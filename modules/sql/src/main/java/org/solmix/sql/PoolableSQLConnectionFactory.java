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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.PoolableConnection;
import org.apache.commons.pool.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.IPoolableObjectFactory;
import org.solmix.api.pool.SlxPoolableObjectFactory;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;

/**
 * 
 * @author solmix
 * @version 111039 0.1.1，1039
 * @since 0.0.1
 * 
 * 
 */
public class PoolableSQLConnectionFactory extends
    SlxPoolableObjectFactory<Connection>
{

    private static Logger log = LoggerFactory.getLogger(PoolableSQLConnectionFactory.class.getName());

    private final ConnectionManagerImpl connectionManager;

    private String serverName;

    private DataTypeMap sqlConfig;

    private String pingTest;

    private final SystemContext sc;

    PoolableSQLConnectionFactory(String serverName, final SystemContext sc,
        ConnectionManagerImpl connectionManager) throws SlxException
    {
        this(connectionManager, sc);
        DataTypeMap serconfig = getConfig().getSubtree(serverName);
        this.serverName = serverName;
        init(serverName, serconfig);
    }

    /**
     * @param serverName
     * @param subtree
     */
    private void init(String serverName, DataTypeMap sqlConfig) {
        this.serverName = serverName;
        this.sqlConfig = sqlConfig;
        if (!sqlConfig.getBoolean("autoDeriveConfig", false)) {
            pingTest = sqlConfig.getString("pingTest");
        }
    }

    /**
     * @param sc
     */
    public PoolableSQLConnectionFactory(
        final ConnectionManagerImpl connectionManager, final SystemContext sc)
    {
        this.connectionManager = connectionManager;
        this.sc = sc;
    }

    protected DataTypeMap getConfig() {
        ConfigureUnitManager cum = sc.getExtension(ConfigureUnitManager.class);
        ConfigureUnit cu = null;
        try {
            cu = cum.getConfigureUnit(SQLDataSource.SERVICE_PID);
        } catch (IOException e) {
            // ignore
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

        return connectionManager.getInternalConn(serverName, sqlConfig);
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
            return new PoolableConnection(conn, (ObjectPool) pool);
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
         * This method generally cannot be called to determine whether a
         * connection to a database is valid or invalid. A typical client can
         * determine that a connection is invalid by catching any exceptions
         * that might be thrown when an operation is attempted.
         */
        /*
         * boolean isClosed = false; try { isClosed = conn.isClosed(); } catch
         * (SQLException e1) { e1.printStackTrace(); } if (isClosed) return
         * false;
         */
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
        return new PoolableSQLConnectionFactory(key.toString(), sc,
            connectionManager);
    }

}
