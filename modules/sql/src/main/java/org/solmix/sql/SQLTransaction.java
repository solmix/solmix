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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.sql.internal.SqlCM;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-27
 */

public class SQLTransaction
{

    public static final String CONNECTION_ATTR_KEY = "_slx_sql_connection_key";

    public static final String DBNAME_ATTR = "_slx_default_dbName";

    private static Logger log = LoggerFactory.getLogger(SQLTransaction.class.getName());

    private static boolean autoEndTransactions;

    public static boolean startTransaction(DSCall rpc) throws SlxException {
        String dbName = null;
        List dsReqs = rpc.getRequests();
        ConnectionManager connectionManager=null;
        for (Object req : dsReqs) {
            if (req instanceof DSRequest) {
                DSRequest dsReq = (DSRequest) req;
                DataSource ds = dsReq.getDataSource();
                if (ds instanceof SQLDataSource) {
                    dbName = ((SQLDataSource) ds).getDriver().dbName;
                    connectionManager=((SQLDataSource) ds).connectionManager;
                    break;
                }
            }
        }
        if (dbName == null)
            dbName = SqlCM.DEFAULT_DATABASE;
        rpc.setAttribute(DBNAME_ATTR, dbName);
        return startTransaction(rpc, dbName,connectionManager);

    }

    /**
     * @param rpc
     * @param dbName
     * @throws SlxException
     */
    public static boolean startTransaction(DSCall rpc, String dbName,ConnectionManager connectionManager) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection conn = (Connection) rpc.getAttribute(connectionKey);
        if (conn == null) {
            conn = connectionManager.get(dbName);
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
            }
            rpc.setAttribute(DBNAME_ATTR, dbName);
            rpc.setAttribute(connectionKey, conn);
            log.debug("Started new transaction [ " + conn.hashCode() + " ]");
            return true;
        } else {
            log.debug((new StringBuilder()).append("startTransaction called but transaction \"").append(conn.hashCode()).append(
                "\" was already active - ignoring the startTransaction request").toString());
            return true;
        }

    }

    public static Connection getConnection(DSCall rpc) throws SlxException {
        List dsReqs = rpc.getRequests();
        for (Iterator i = dsReqs.iterator(); i.hasNext();) {
            Object req = i.next();
            if (req instanceof DSRequest) {
                DSRequest dsReq = (DSRequest) req;
                DataSource ds = dsReq.getDataSource();
                if (ds instanceof SQLDataSource) {
                    String dbName = ((SQLDataSource) ds).getDriver().dbName;
                    rpc.setAttribute("_isc_default_dbName", dbName);
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
    private static Connection getConnection(DSCall rpc, String dbName) {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getAttribute(connectionKey);
        return connection;
    }

    public static void rollbackTransaction(DSCall rpc,ConnectionManager connectionManager) throws SlxException {
        String dbName = (String) rpc.getAttribute(DBNAME_ATTR);
        rollbackTransaction(rpc, dbName,connectionManager);
    }

    public static void rollbackTransaction(DSCall rpc, String dbName,ConnectionManager connectionManager) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getAttribute(connectionKey);
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
            endTransaction(rpc, dbName,connectionManager);
    }

    public static void commitTransaction(DSCall rpc,ConnectionManager connectionManager) throws SlxException {
        String dbName = (String) rpc.getAttribute(DBNAME_ATTR);
        commitTransaction(rpc, dbName,connectionManager);
    }

    public static void commitTransaction(DSCall rpc, String dbName,ConnectionManager connectionManager) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getAttribute(connectionKey);
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
            endTransaction(rpc, dbName,connectionManager);
    }

    public static void endTransaction(DSCall rpc,ConnectionManager connectionManager) throws SlxException {
        String dbName = (String) rpc.getAttribute(DBNAME_ATTR);
        endTransaction(rpc, dbName,connectionManager);
    }

    public static void endTransaction(DSCall rpc, String dbName,ConnectionManager connectionManager) throws SlxException {
        String connectionKey = CONNECTION_ATTR_KEY + "_" + dbName;
        Connection connection = (Connection) rpc.getAttribute(connectionKey);
        if (connection == null) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, (new StringBuilder()).append("No current connection for '").append(
                dbName).append("'").toString());
        } else {
            log.debug((new StringBuilder()).append("Ending transaction \"").append(connection.hashCode()).append("\"").toString());
            connectionManager.free(connection);
            rpc.removeAttribute(DBNAME_ATTR);
            rpc.removeAttribute(connectionKey);
            return;
        }
    }
}
