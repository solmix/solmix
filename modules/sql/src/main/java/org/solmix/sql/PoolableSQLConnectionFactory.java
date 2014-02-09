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

package org.solmix.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.PoolableConnection;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.IPoolableObjectFactory;
import org.solmix.api.pool.SlxPoolableObjectFactory;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix
 * @version 111039 0.1.1，1039
 * @since 0.0.1
 * 
 * 
 */
public class PoolableSQLConnectionFactory extends SlxPoolableObjectFactory<Connection>
{

    private static Logger log = LoggerFactory.getLogger(PoolableSQLConnectionFactory.class.getName());

    private final boolean autoDeriveConfig;

    private EInterfaceType interfaceType;

    private String serverName;

    private DataTypeMap sqlConfig;

    private String pingTest;

    private DataSource ds;

    private SystemContext sc;


    public PoolableSQLConnectionFactory()
    {
        autoDeriveConfig = false;
        interfaceType = EInterfaceType.DATASOURCE;
    }

    public PoolableSQLConnectionFactory(String serverName,final SystemContext sc) throws SlxException
    {
        this(sc);
       DataTypeMap serconfig= getConfig().getSubtree(serverName);
       interfaceType = EInterfaceType.DATASOURCE;
       this.serverName = serverName;
       this.sqlConfig = serconfig;
       EInterfaceType _interfaceType = EInterfaceType.fromValue(sqlConfig.getString("interface.type"));
       if (_interfaceType == null)
           log.warn((new StringBuilder()).append("sql.").append(serverName).append(".interface.type not set - assuming ").append(
               this.interfaceType).toString());
       else
           this.interfaceType = _interfaceType;
       if (!sqlConfig.getBoolean("autoDeriveConfig", false)) {
           pingTest = sqlConfig.getString("pingTest");
       }
    }

    /**
     * @param serverName
     * @param subtree
     */
    public PoolableSQLConnectionFactory(String serverName, DataTypeMap sqlConfig)
    {
        autoDeriveConfig = false;
        interfaceType = EInterfaceType.DATASOURCE;
        this.serverName = serverName;
        this.sqlConfig = sqlConfig;
        EInterfaceType _interfaceType = EInterfaceType.fromValue(sqlConfig.getString("interface.type"));
        if (_interfaceType == null)
            log.warn((new StringBuilder()).append("sql.").append(serverName).append(".interface.type not set - assuming ").append(
                this.interfaceType).toString());
        else
            this.interfaceType = _interfaceType;
        if (!sqlConfig.getBoolean("autoDeriveConfig", false)) {
            pingTest = sqlConfig.getString("pingTest");
        }
    }

    /**
     * @param sc
     */
    public PoolableSQLConnectionFactory(final SystemContext sc)
    {
        autoDeriveConfig = false;
        interfaceType = EInterfaceType.DATASOURCE;
        this.sc=sc;
    }
    protected DataTypeMap getConfig()  {
        ConfigureUnitManager cum = sc.getBean(org.solmix.api.cm.ConfigureUnitManager.class);
        ConfigureUnit cu=null;
        try {
            cu = cum.getConfigureUnit(SQLDataSource.SERVICE_PID);
        } catch (IOException e) {
            //ignore
        }
        if (cu != null)
            return cu.getProperties();
        else
            return new DataTypeMap();
    }

  

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.SlxPoolableObjectFactory#makeUnpooledObject()
     */
    @Override
    public Connection makeUnpooledObject() throws Exception {
        switch (interfaceType) {
            case DATASOURCE: {
                if (ds == null) {
                    ds = ConnectionManagerImpl.getInternalDs(serverName, sqlConfig);
                }
                return ds.getConnection();
            }
            default:
                return ConnectionManagerImpl.getInternalConn(serverName, interfaceType, sqlConfig);

        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
     */
    @Override
    public void activateObject(Connection obj) throws Exception {
        numActivateObjectCalls++;
       
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
     */
    @Override
    public void destroyObject(Connection arg0) throws Exception {
        numDestroyObjectCalls++;
       
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
     */
    @Override
    public Connection makeObject() throws Exception {
        numMakeObjectCalls++;
        Connection conn = makeUnpooledObject();
        if (pool == null) {
            log.debug("Returning unpooled Connection");
            return conn;
        } else {
            log.debug("Returning pooled Connection");
            return new PoolableConnection(conn,(ObjectPool)pool);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
     */
    @Override
    public void passivateObject(Connection arg0) throws Exception {
        numPassivateObjectCalls++;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
     */
    @Override
    public boolean validateObject(Connection obj) {
        Connection conn = obj;
        /**
         * This method generally cannot be called to determine whether a connection to a database is valid or invalid. A typical client can determine that a connection is invalid by catching any exceptions that might be thrown when an operation is attempted.
         */
       /* boolean isClosed = false;
        try {
            isClosed = conn.isClosed();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (isClosed)
            return false;*/
        try {
            if (pingTest != null) {
                Statement s = conn.createStatement();
                s.execute(pingTest);
            }
            return true;
        } catch (SQLException e) {
            try {
                conn.close();
            } catch (Exception ignored) {
            }
        }
      
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.IPoolableObjectFactory#newInstance(java.lang.Object)
     */
    @Override
    public IPoolableObjectFactory newInstance(Object key) throws SlxException {
        return new PoolableSQLConnectionFactory(key.toString(),sc);
    }

}
