/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package com.solmix.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.commons.dbcp.PoolableConnection;

import com.solmix.api.exception.SlxException;
import com.solmix.api.pool.PoolService;
import com.solmix.api.pool.PoolServiceFactory;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.collections.DataTypeMap;
import com.solmix.commons.logs.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.base.Reflection;
import com.solmix.fmk.util.ServiceUtil;
import com.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110037 2011-3-20
 */
@SuppressWarnings("unchecked")
public class ConnectionManager
{

    public PoolService manager;

    private static ConnectionManager instance;

    private static final Logger log = new Logger(ConnectionManager.class.getName());

    public static Map referencedDatabases = new HashMap();

    private DataTypeMap thisConfig = null;

    private static PoolServiceFactory poolServiceFactory;

    protected boolean monitorConnections;

    protected long recycleMillis;

    protected static Map<Connection, Map<String, Object>> openConnections;

    @Deprecated
    public ConnectionManager()
    {

    }

    public void init() {
        manager = poolServiceFactory.createPoolService("sql", new PoolableSQLConnectionFactory());
        thisConfig = SQLConfigManager.getConfig();
        monitorConnections = thisConfig.getBoolean("monitorOpenConnections", false);
        recycleMillis = thisConfig.getLong("forceConnectionClosedPeriod", 30000);
        openConnections = new ConcurrentHashMap<Connection, Map<String, Object>>();
        if (monitorConnections) {
            Runnable monitor = new Runnable() {

                Lock lock = new java.util.concurrent.locks.ReentrantLock();

                @Override
                public void run() {
                    Map<Connection, Map<String, Object>> localCopy = null;
                    do {
                        lock.lock();
                        localCopy = new ConcurrentHashMap<Connection, Map<String, Object>>(openConnections);
                        lock.unlock();
                        long now = System.currentTimeMillis();
                        Iterator<Connection> i = localCopy.keySet().iterator();
                        do {
                            if (!i.hasNext())
                                break;
                            Connection conn = (Connection) i.next();
                            Map<String, Object> info = localCopy.get(conn);
                            long ms = ((Long) info.get("time")).longValue();
                            if (now - ms > recycleMillis) {
                                String error = (new StringBuilder()).append("Connection '").append(conn.hashCode()).append("', of type '").append(
                                    info.get("type")).append("', borrowed by the ").append("following call stack, has been open for ").append(
                                    "more than ").append(recycleMillis).append("ms; it will ").append("now be forcibly closed").toString();
                                StackTraceElement elements[] = (StackTraceElement[]) (StackTraceElement[]) info.get("stacktrace");
                                boolean start = false;
                                for (int j = 0; j < elements.length; j++) {
                                    if (!start && elements[j].toString().indexOf("writeOpenConnectionEntry") != -1) {
                                        start = true;
                                        continue;
                                    }
                                    if (start)
                                        error = (new StringBuilder()).append(error).append("\n").append(elements[j].toString()).toString();
                                }

                                log.warn(error);
                                try {
                                    free(conn);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } while (true);
                        try {
                            Thread.sleep(2000L);
                        } catch (Exception ignored) {
                        }
                    } while (true);
                }
            };
            (new Thread(monitor)).start();
        }
    }

    public synchronized static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
            instance.init();
        }
        return instance;
    }

    public static Connection getConnection() throws SlxException {
        return getConnection(null);
    }

    public Connection get() throws SlxException {
        return get(null);
    }

    protected static DataSource getInternalDs(String dbName, DataTypeMap dbConfig) throws SlxException {
        Lock l = new ReentrantLock();
        DataSource ds = null;
        l.lock();
        try {
            if (ds == null) {
                String impl = dbConfig.getString("driver");
                if (log.isDebugEnabled())
                    log.debug("Initializing sql config for [" + dbName + "] from system config -using DataSource: " + impl);
                ds = (DataSource) Reflection.newInstance(impl);
                DataTypeMap driverConfig = dbConfig.getSubtree("driver");
                DataUtil.setProperties(driverConfig, ds);
                if (ds != null && dbConfig.getBoolean("log.enabled", false))
                    ds.setLogWriter(new PrintWriter(System.out));
            }
            if (ds == null) {
                String __info = "Unable to instantiate JDBC DataSource for database [" + dbName + "] - check your config.";
                throw new SlxException(Tmodule.POOL, Texception.CAN_NOT_INSTANCE, __info);
            } else {
                return ds;
            }
        } catch (Exception e) {
            throw new SlxException(Tmodule.SQL, Texception.DEFAULT, "can not  initial datasource");
        } finally {
            l.unlock();
        }

    }

    protected static Connection getInternalConn(String dbName, EInterfaceType type, DataTypeMap dbConfig) throws SlxException {
        if (type == null || dbConfig == null)
            return null;
        Connection __return = null;
        try {
            switch (type) {
                case JNDIOSGI: {
                    String filterName = dbConfig.getString("jndiosgi");

                    DataSource ds = lookupDataSource(filterName);
                    __return = ds.getConnection();
                    break;
                }
                case OSGI: {
                    String filterName = dbConfig.getString("osgi");
                    List<DataSource> objs = ServiceUtil.getOSGIServices(DataSource.class, filterName);
                    DataSource obj = null;
                    if (objs != null && objs.size() == 1)
                        obj = objs.get(0);
                    if (obj != null) {
                        __return = ((DataSource) obj).getConnection();
                    } else {
                        throw new SlxException(Tmodule.SQL, Texception.OBJECT_TYPE_NOT_ADAPTED, "can not find a adapter for datasource");
                    }
                    break;
                }
                case DATASOURCE: {

                    DataSource ds = getInternalDs(dbName, dbConfig);
                    try {

                        __return = ds.getConnection();
                    } catch (Exception e) {
                        throw new SlxException(Tmodule.SQL, Texception.DEFAULT, "can not  initial datasource");
                    }
                    break;
                }

                case DRIVERMANAGER: {
                    Boolean credentialsSetting = dbConfig.getBoolean("interface.credentialsInURL");
                    DataTypeMap driverConfig = dbConfig.getSubtree("driver");
                    String username = driverConfig.getString("username");
                    String password = driverConfig.getString("password");
                    String jdbcURL = driverConfig.getString("url");
                    boolean credentialsInURL = credentialsSetting != null && credentialsSetting.booleanValue();
                    if (jdbcURL == null)
                        jdbcURL = (new StringBuilder()).append("jdbc:").append(driverConfig.getString("driverName")).append("://").append(
                            driverConfig.getString("serverName")).append(":").append(driverConfig.get("portNumber")).append("/").append(
                            driverConfig.get("databaseName")).append(
                            credentialsInURL ? (new StringBuilder()).append("?user=").append(username).append("&password=").append(password).toString()
                                : "").toString();
                    Class<?> driver = null;
                    String dmImplementer = dbConfig.getString("driver");
                    if (log.isDebugEnabled()) {
                        log.debug((new StringBuilder()).append("Initializing SQL config for '").append(dbName).append("' from system config").append(
                            " - using DriverManager:  ").append(dmImplementer).toString());
                    }
                    try {
                        driver = Reflection.classForName(dmImplementer);
                    } catch (Exception e) {
                        throw new SlxException(Tmodule.SQL, Texception.REFLECTION_EXCEPTION, "can't find class :" + dmImplementer);
                    }
                    if (driver != null) {
                        log.debug((new StringBuilder()).append(dmImplementer).append(" lookup successful").toString());
                    }
                    if (!credentialsInURL) {
                        log.debug("Passing credentials getConnection separately from JDBC URL");
                        __return = DriverManager.getConnection(jdbcURL, username, password);
                    } else {
                        log.debug("Passing JDBC URL only to getConnection");
                        __return = DriverManager.getConnection(jdbcURL);
                    }

                    break;
                }
                case JNDI: {
                    String jndi = dbConfig.getString("jndi");
                    Object dataSource = ServiceUtil.getJNDIService(jndi);
                    if (dataSource instanceof DataSource) {
                        __return = ((DataSource) dataSource).getConnection();
                    } else {
                        throw new SlxException(Tmodule.SQL, Texception.OBJECT_TYPE_NOT_ADAPTED, "can not find a adapter for datasource");
                    }
                    break;
                }
                default: {
                    throw new SlxException(Tmodule.POOL, Texception.NO_SUPPORT, "Unsupported interface type " + type + " for database " + dbName
                        + " - check your config.");
                }
            }
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e.getMessage());
        }
        return __return;

    }

    public Connection get(String dbName) throws SlxException {
        if (DataUtil.isNullOrEmpty(dbName))
            dbName = SQLConfigManager.defaultDatabase;
        Connection __return = null;
        DataTypeMap dbConfig = thisConfig.getSubtree(dbName);
        Boolean usedPool = dbConfig.getBoolean(SQLDataSource.USED_POOL);
        if (usedPool == null || usedPool.booleanValue())
            __return = (Connection) manager.borrowObject(dbName);
        else {
            String _interfaceType = dbConfig.getString(SQLDataSource.INTERFACE_TYPE);
            EInterfaceType type = EInterfaceType.fromValue(_interfaceType);
            __return = getInternalConn(dbName, type, dbConfig);
        }
        // markAsReferenced(dbName);
        writeOpenConnectionEntry(__return, "get");
        return __return;
    }

    /**
     * @param dbName
     * @return
     */
    public static Connection getConnection(String dbName) throws SlxException {
        ConnectionManager m = ConnectionManager.getInstance();
        return m.get(dbName);

    }

    /**
     * for JNDI lookup datasource in the context.
     * 
     * @return
     */
    private static DataSource lookupDataSource(String filterName) throws SlxException {
        Object obj = ServiceUtil.getOsgiJndiService(DataSource.class.getName(), filterName);
        if (obj instanceof DataSource) {
            return (DataSource) obj;
        } else {
            throw new SlxException(Tmodule.SQL, Texception.OBJECT_TYPE_NOT_ADAPTED, "can not find a adapter for datasource");
        }

    }

    /**
     * @param return1
     * @param string
     */

    private void writeOpenConnectionEntry(Connection conn, String type) {
        if (monitorConnections) {
            Map<String, Object> openConnEntry = new HashMap<String, Object>();
            openConnEntry.put("type", type);
            openConnEntry.put("time", Long.valueOf(System.currentTimeMillis()));
            openConnEntry.put("stacktrace", Thread.currentThread().getStackTrace());
            openConnections.put(conn, openConnEntry);
        }

    }

    public static void freeConnection(Connection conn) throws SlxException {
        ConnectionManager m = ConnectionManager.getInstance();
        m.free(conn);
    }

    /**
     * @param connection
     * @throws SlxException
     */
    public void free(Connection conn) throws SlxException {
        try {
            if (conn == null)
                return;
            if (conn != null && !conn.isClosed() && !conn.getAutoCommit())
                conn.commit();
            if (conn instanceof PoolableConnection) {
                conn.close();
            } else if (conn != null && !conn.isClosed())
                conn.close();
            if (monitorConnections)
                openConnections.remove(conn);
        } catch (SQLException e) {
            log.error("Error attempting to commit and close a connection");
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
        }

    }

    public static Connection getNewConnection() throws SlxException {
        return getNewConnection(null);
    }

    public Connection getNew() throws SlxException {
        return getNew(null);
    }

    public Connection getNew(String dbName) throws SlxException {
        Connection __return;
        if (dbName == null)
            dbName = SQLConfigManager.defaultDatabase;
        try {
            __return = (Connection) manager.borrowNewObject(dbName);
        } catch (Exception e) {
            throw new SlxException(Tmodule.SQL, Texception.POOL_BORROW_OBJECT_FAILD, e);
        }
        writeOpenConnectionEntry(__return, "getNew");
        return __return;
    }

    /**
     * @return
     * @throws SlxException
     */
    public static Connection getNewConnection(String dbName) throws SlxException {
        ConnectionManager m = ConnectionManager.getInstance();
        return m.getNew(dbName);
    }

    /**
     * @return the poolServiceFactory
     */
    public PoolServiceFactory getPoolServiceFactory() {
        return poolServiceFactory;
    }

    /**
     * @param poolServiceFactory the poolServiceFactory to set
     */
    public void setPoolServiceFactory(PoolServiceFactory poolServiceFactory) {
        ConnectionManager.poolServiceFactory = poolServiceFactory;
    }
}