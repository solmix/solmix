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
import java.util.Iterator;
import java.util.List;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.rpc.RPCManager;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.logs.Logger;
import com.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solomon
 * @version 110035 2011-3-27
 */

public class SQLTransaction
{

    public static final String CONNECTION_ATTR_KEY = "_slx_sql_connection_key";

    public static final String DBNAME_ATTR = "_slx_default_dbName";

    private static Logger log = new Logger(SQLTransaction.class.getName());

    private static boolean autoEndTransactions;

    public static boolean startTransaction(RPCManager rpc) throws SlxException {
        String dbName = null;
        List dsReqs = rpc.getRequests();
        for (Object req : dsReqs) {
            if (req instanceof DSRequest) {
                DSRequest dsReq = (DSRequest) req;
                DataSource ds = dsReq.getDataSource();
                if (ds instanceof SQLDataSource) {
                    dbName = ((SQLDataSource) ds).getDriver().dbName;
                    break;
                }
            }
        }
        if (dbName == null)
            dbName = SQLConfigManager.defaultDatabase;
        rpc.getContext().setAttribute(DBNAME_ATTR, dbName);
        return startTransaction(rpc, dbName);

    }

    /**
     * @param rpc
     * @param dbName
     * @throws SlxException
     */
    public static boolean startTransaction(RPCManager rpc, String dbName) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection conn = (Connection) rpc.getContext().getAttribute(connectionKey);
        if (conn == null) {
            conn = ConnectionManager.getConnection(dbName);
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
            }
            rpc.getContext().setAttribute(DBNAME_ATTR, dbName);
            rpc.getContext().setAttribute(connectionKey, conn);
            log.debug("Started new transaction [ " + conn.hashCode() + " ]");
            return true;
        } else {
            log.debug((new StringBuilder()).append("startTransaction called but transaction \"").append(conn.hashCode()).append(
                "\" was already active - ignoring the startTransaction request").toString());
            return true;
        }

    }

    public static Connection getConnection(RPCManager rpc) throws SlxException {
        List dsReqs = rpc.getRequests();
        for (Iterator i = dsReqs.iterator(); i.hasNext();) {
            Object req = i.next();
            if (req instanceof DSRequest) {
                DSRequest dsReq = (DSRequest) req;
                DataSource ds = dsReq.getDataSource();
                if (ds instanceof SQLDataSource) {
                    String dbName = ((SQLDataSource) ds).getDriver().dbName;
                    if (dbName == null)
                        dbName = SQLConfigManager.defaultDatabase;
                    rpc.getContext().setAttribute("_isc_default_dbName", dbName);
                    return getConnection(rpc, dbName);
                }
            }
        }

        throw new SlxException("Could not find a DSRequest for a SQLDataSource in getConnection");
    }

    /**
     * @param rpc
     * @param dbName
     * @return
     */
    private static Connection getConnection(RPCManager rpc, String dbName) {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getContext().getAttribute(connectionKey);
        return connection;
    }

    public static void rollbackTransaction(RPCManager rpc) throws SlxException {
        String dbName = (String) rpc.getContext().getAttribute(DBNAME_ATTR);
        rollbackTransaction(rpc, dbName);
    }

    public static void rollbackTransaction(RPCManager rpc, String dbName) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getContext().getAttribute(connectionKey);
        if (connection == null)
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, (new StringBuilder()).append("No current connection for '").append(
                dbName).append("'").toString());
        log.debug((new StringBuilder()).append("Rolling back transaction \"").append(connection.hashCode()).append("\"").toString());
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_ROLLBACK_EXCEPTION, e);
        }
        if (autoEndTransactions)
            endTransaction(rpc, dbName);
    }

    public static void commitTransaction(RPCManager rpc) throws SlxException {
        String dbName = (String) rpc.getContext().getAttribute(DBNAME_ATTR);
        commitTransaction(rpc, dbName);
    }

    public static void commitTransaction(RPCManager rpc, String dbName) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getContext().getAttribute(connectionKey);
        if (connection == null)
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, (new StringBuilder()).append("No current connection for '").append(
                dbName).append("'").toString());
        log.debug((new StringBuilder()).append("Committing transaction \"").append(connection.hashCode()).append("\"").toString());
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_COMMIT_EXCEPTION, e);
        }
        if (autoEndTransactions)
            endTransaction(rpc, dbName);
    }

    public static void endTransaction(RPCManager rpc) throws SlxException {
        String dbName = (String) rpc.getContext().getAttribute(DBNAME_ATTR);
        endTransaction(rpc, dbName);
    }

    public static void endTransaction(RPCManager rpc, String dbName) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getContext().getAttribute(connectionKey);
        if (connection == null) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, (new StringBuilder()).append("No current connection for '").append(
                dbName).append("'").toString());
        } else {
            log.debug((new StringBuilder()).append("Ending transaction \"").append(connection.hashCode()).append("\"").toString());
            ConnectionManager.freeConnection(connection);
            rpc.getContext().removeAttribute(DBNAME_ATTR);
            rpc.getContext().removeAttribute(connectionKey);
            return;
        }
    }
}