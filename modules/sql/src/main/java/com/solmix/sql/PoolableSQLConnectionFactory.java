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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.PoolableConnection;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.exception.SlxException;
import com.solmix.api.pool.IPoolableObjectFactory;
import com.solmix.api.pool.SlxPoolableObjectFactory;
import com.solmix.commons.collections.DataTypeMap;
import com.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solmix
 * @version 111039 0.1.1，1039
 * @since 0.0.1
 * 
 * 
 */
public class PoolableSQLConnectionFactory extends SlxPoolableObjectFactory
{

    private static Logger log = LoggerFactory.getLogger(PoolableSQLConnectionFactory.class.getName());

    private final boolean autoDeriveConfig;

    private EInterfaceType interfaceType;

    private String serverName;

    private DataTypeMap sqlConfig;

    private String pingTest;

    private DataSource ds;

    public PoolableSQLConnectionFactory()
    {
        autoDeriveConfig = false;
        interfaceType = EInterfaceType.DATASOURCE;
    }

    public PoolableSQLConnectionFactory(String serverName) throws SlxException
    {
        this(serverName, SQLConfigManager.getConfig().getSubtree((new StringBuilder()).append(serverName).toString()));
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
     * {@inheritDoc}
     * 
     * @see com.solmix.api.pool.SlxPoolableObjectFactory#makeUnpooledObject()
     */
    @Override
    public Object makeUnpooledObject() throws Exception {
        switch (interfaceType) {
            case DATASOURCE: {
                if (ds == null) {
                    ds = ConnectionManager.getInternalDs(serverName, sqlConfig);
                }
                return ds.getConnection();
            }
            default:
                return ConnectionManager.getInternalConn(serverName, interfaceType, sqlConfig);

        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
     */
    @Override
    public void activateObject(Object obj) throws Exception {
        numActivateObjectCalls++;
       
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
     */
    @Override
    public void destroyObject(Object arg0) throws Exception {
        numDestroyObjectCalls++;
       
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
     */
    @Override
    public Object makeObject() throws Exception {
        numMakeObjectCalls++;
        Connection conn = (Connection) makeUnpooledObject();
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
    public void passivateObject(Object arg0) throws Exception {
        numPassivateObjectCalls++;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
     */
    @Override
    public boolean validateObject(Object obj) {
        Connection conn = (Connection) obj;
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
     * @see com.solmix.api.pool.IPoolableObjectFactory#newInstance(java.lang.Object)
     */
    @Override
    public IPoolableObjectFactory newInstance(Object key) throws SlxException {
        return new PoolableSQLConnectionFactory(key.toString());
    }

}
