/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.sql;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.dbcp.PoolableConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.PoolManager;
import org.solmix.api.pool.PoolManagerFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.util.ServiceUtil;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;
import org.solmix.sql.internal.SqlCM;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110037 2011-3-20
 */
public class ConnectionManagerImpl implements ConnectionManager
{

    public PoolManager manager;

    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class.getName());

    private DataTypeMap thisConfig = null;

    private static PoolManagerFactory poolManagerFactory;

    protected boolean monitorConnections;

    protected long recycleMillis;

    private boolean connectionMonitorIsOpen;

    protected static Map<Connection, Map<String, Object>> openConnections;

    private SystemContext sc;

    public ConnectionManagerImpl(final SystemContext sc)
    {
        setSystemContext(sc);
    }

    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
        if(sc!=null)
            sc.setExtension(this, ConnectionManager.class);
    }

    private synchronized DataTypeMap getSqlConfig() throws SlxException {
        if (thisConfig == null) {
            ConfigureUnitManager cum = sc.getExtension(ConfigureUnitManager.class);
            ConfigureUnit cu = null;
            try {
                cu = cum.getConfigureUnit(SQLDataSource.SERVICE_PID);
            } catch (IOException e) {
                throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
            }
            if (cu != null)
                thisConfig = cu.getProperties();
            else
                thisConfig = new DataTypeMap();
        }
        return thisConfig;
    }

    public void init() {

    }


    /**
     * @return the manager
     */
    public synchronized PoolManager getManager() {
        if (manager == null) {
            if (poolManagerFactory == null) {
                poolManagerFactory = sc.getExtension(PoolManagerFactory.class);
            }
            manager = poolManagerFactory.createPoolManager("sql", new PoolableSQLConnectionFactory(this,sc));
        }
        return manager;
    }

    /**
     * @param manager the manager to set
     */
    public void setManager(PoolManager manager) {
        this.manager = manager;
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
                DataUtils.setProperties(driverConfig, ds);
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

    protected  Connection getInternalConn(String dbName, DataTypeMap dbConfig) throws SlxException {
       DataSource ds= getDataSource(dbName, dbConfig);
       try{
       if(ds!=null)
           return ds.getConnection();
       else
           throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, "");
       
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e.getMessage(), e);
        }

    }

    @Override
    public Connection getConnection(String dbName) throws SlxException {
        if (dbName==null)
            dbName = getDefaultDbName();
        Connection __return = null;
        DataTypeMap dbConfig = getSqlConfig().getSubtree(dbName);
        Boolean usedPool = dbConfig.getBoolean(SQLDataSource.USED_POOL);
        if (usedPool == null || usedPool.booleanValue())
            __return = (Connection) getManager().borrowObject(dbName);
        else {
            __return = getInternalConn(dbName,  dbConfig);
        }
        // markAsReferenced(dbName);
        writeOpenConnectionEntry(__return, "get");
        return __return;
    }

    protected String getDefaultDbName() throws SlxException {
        return getSqlConfig().getString(SqlCM.P_DEFAULT_DATABASE,
            SqlCM.DEFAULT_DATABASE);
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
     * @throws SlxException
     */

    private void writeOpenConnectionEntry(Connection conn, String type) throws SlxException {
        if (monitorConnections) {
            if (connectionMonitorIsOpen)
                startupMonitor();
            Map<String, Object> openConnEntry = new HashMap<String, Object>();
            openConnEntry.put("type", type);
            openConnEntry.put("time", Long.valueOf(System.currentTimeMillis()));
            openConnEntry.put("stacktrace", Thread.currentThread().getStackTrace());
            openConnections.put(conn, openConnEntry);
        }

    }

 

    /**
     * @throws SlxException
     * 
     */
    private synchronized void startupMonitor() throws SlxException {
        connectionMonitorIsOpen = true;
        monitorConnections = getSqlConfig().getBoolean("monitorOpenConnections", false);
        recycleMillis = getSqlConfig().getLong("forceConnectionClosedPeriod", 30000);
        openConnections = new ConcurrentHashMap<Connection, Map<String, Object>>();
        log.info("Startup Sql connection Monitor,the recycle check time is " + recycleMillis);
        Runnable monitor = new Runnable() {

            @Override
            public void run() {
                Map<Connection, Map<String, Object>> localCopy = null;
                do {
                    localCopy = new ConcurrentHashMap<Connection, Map<String, Object>>(openConnections);
                    long now = System.currentTimeMillis();
                    Iterator<Connection> i = localCopy.keySet().iterator();
                    do {
                        if (!i.hasNext())
                            break;
                        Connection conn = i.next();
                        Map<String, Object> info = localCopy.get(conn);
                        long ms = ((Long) info.get("time")).longValue();
                        if (now - ms > recycleMillis) {
                            String error = (new StringBuilder()).append("Connection '").append(conn.hashCode()).append("', of type '").append(
                                info.get("type")).append("', borrowed by the ").append("following call stack, has been open for ").append(
                                "more than ").append(recycleMillis).append("ms; it will ").append("now be forcibly closed").toString();
                            StackTraceElement elements[] = (StackTraceElement[]) info.get("stacktrace");
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

    /**
     * @param connection
     * @throws SlxException
     */
    @Override
    public void freeConnection(Connection conn) throws SlxException {
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


    @Override
    public Connection getNewConnection(String dbName) throws SlxException {
        Connection __return;
        if (dbName == null)
            dbName = getSqlConfig().getString(SqlCM.P_DEFAULT_DATABASE, SqlCM.DEFAULT_DATABASE);
        try {
            DataTypeMap dbConfig = getSqlConfig().getSubtree(dbName);
            Boolean usedPool = dbConfig.getBoolean(SQLDataSource.USED_POOL);
            if (usedPool == null || usedPool.booleanValue())
                __return = (Connection) getManager().borrowObject(dbName);
            else {
                __return = getInternalConn(dbName, dbConfig);
            }
        } catch (Exception e) {
            throw new SlxException(Tmodule.SQL, Texception.POOL_BORROW_OBJECT_FAILD, e);
        }
        writeOpenConnectionEntry(__return, "getNew");
        return __return;
    }

   

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getPoolManagerFactory()
     */
    @Override
    public PoolManagerFactory getPoolManagerFactory() {
        return poolManagerFactory;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#freeConnection(java.sql.Connection)
     */
    @Override
    public void free(Connection conn) throws SlxException {
       freeConnection(conn);
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getConnection()
     */
    @Override
    public Connection get() throws SlxException {
        return getConnection(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getConnection(java.lang.String)
     */
    @Override
    public Connection get(String dbName) throws SlxException {
        return getConnection(dbName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getNewConnection(java.lang.String)
     */
    @Override
    public Connection getNew(String dbName) throws SlxException {
        return getNewConnection(dbName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getNewConnection()
     */
    @Override
    public Connection getNew() throws SlxException {
        return getNewConnection(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.ConnectionManager#getDataSource(java.lang.String)
     */
    @Override
    public DataSource getDataSource(String dbName) throws SlxException {
        if (dbName==null)
            dbName = getDefaultDbName();
        DataTypeMap dbConfig = getSqlConfig().getSubtree(dbName);
        if(dbConfig==null||dbConfig.isEmpty())
            throw new SlxException(Tmodule.SQL,Texception.NO_FOUND,"No found configure for jdbc datasource name:"+dbName+ " config");
        return getDataSource(dbName,dbConfig);
    }

    private DataSource getDataSource(String dbName, DataTypeMap dbConfig) throws SlxException {
        Assert.isNotNull(dbConfig);
        String _interfaceType = dbConfig.getString(SQLDataSource.INTERFACE_TYPE);
        EInterfaceType type = EInterfaceType.fromValue(_interfaceType);
        DataSource __return = null;
            switch (type) {
                case JNDIOSGI: {
                    String filterName = dbConfig.getString("jndiosgi");
                    __return = lookupDataSource(filterName);
                    break;
                }
                case OSGI: {
                    String filter = dbConfig.getString("osgi");
                    List<DataSource> objs = ServiceUtil.getOSGIServices(
                        DataSource.class, filter);
                    if (objs != null && objs.size() == 1)
                        __return = objs.get(0);
                    if (objs.size() > 1) {
                        log.warn("found more than one Jdbc DataSource for filter:"
                            + filter + ",Just select first one");
                        __return = objs.get(0);
                    }
                    break;
                }
                case DATASOURCE: {

                    __return = getInternalDs(dbName, dbConfig);
                    break;
                }

                case DRIVERMANAGER: {
                    boolean credentialsInURL = dbConfig.getBoolean(
                        "interface.credentialsInURL", false);
                    DataTypeMap driverConfig = dbConfig.getSubtree("driver");
                    String username = driverConfig.getString("username");
                    String password = driverConfig.getString("password");
                    String jdbcURL = driverConfig.getString("url");
                    if (jdbcURL == null)
                        jdbcURL = new StringBuilder().append("jdbc:").append(
                            driverConfig.getString("driverName")).append("://").append(
                            driverConfig.getString("serverName")).append(":").append(
                            driverConfig.get("portNumber")).append("/").append(
                            driverConfig.get("databaseName")).append(
                            credentialsInURL ? (new StringBuilder()).append(
                                "?user=").append(username).append("&password=").append(
                                password).toString()
                                : "").toString();
                    Class<?> driver = null;
                    String dmImplementer = dbConfig.getString("driver");
                    if (log.isDebugEnabled()) {
                        log.debug((new StringBuilder()).append(
                            "Initializing SQL config for '").append(dbName).append(
                            "' from system config").append(
                            " - using DriverManager:  ").append(dmImplementer).toString());
                    }
                    try {
                        driver = Reflection.classForName(dmImplementer);
                    } catch (Exception e) {
                        throw new SlxException(Tmodule.SQL,
                            Texception.REFLECTION_EXCEPTION,
                            "can't find class :" + dmImplementer);
                    }
                    if (driver != null) {
                        log.debug((new StringBuilder()).append(dmImplementer).append(
                            " lookup successful").toString());
                    }
                    __return= new DriverManagerDataSource(jdbcURL, username, password, credentialsInURL);
                   
                    break;
                }
                case JNDI: {
                    String jndi = dbConfig.getString("jndi");
                    Object dataSource = ServiceUtil.getJNDIService(jndi);
                    if (dataSource instanceof DataSource) {
                        __return = (DataSource) dataSource;
                    } else {
                        throw new SlxException(Tmodule.SQL,
                            Texception.OBJECT_TYPE_NOT_ADAPTED,
                            "can not find a adapter for datasource");
                    }
                    break;
                }
                default: {
                    throw new SlxException(Tmodule.POOL, Texception.NO_SUPPORT,
                        "Unsupported interface type " + type + " for database "
                            + dbName + " - check your config.");
                }
            }
        return __return;
    }
}
