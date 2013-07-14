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
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.DateUtil;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.util.DataTools;
import org.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solomon
 * @version 110035 2011-3-20
 */
@SuppressWarnings("unchecked")
public abstract class SQLDriver
{

    protected static Logger log = LoggerFactory.getLogger(SQLDriver.class.getName());

    protected static Perl5Util globalPerl = new Perl5Util();

    protected boolean quoteColumnNames;

    protected SQLTable table;

    protected String dbName;

    public Connection dbConnection;

    private final boolean useColumnLabelInMetadata;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    protected static DataTypeMap thisConfig;

    public static final DataTypeMap buildInDriverImpl;
    static {

        buildInDriverImpl = new DataTypeMap(new HashMap<String, String>());
        buildInDriverImpl.put("oracle", "org.solmix.sql.OracleDriver");
        buildInDriverImpl.put("mysql", "org.solmix.sql.MysqlDriver");
        buildInDriverImpl.put("postgresql", "org.solmix.sql.PostgresDriver");
        buildInDriverImpl.put("sqlserver", "org.solmix.sql.SQLServerDriver");
        buildInDriverImpl.put("db2", "org.solmix.sql.DB2Driver");
        buildInDriverImpl.put("db2iSeries", "org.solmix.sql.DB2iSeriesDriver");
        buildInDriverImpl.put("hsqldb", "org.solmix.sql.HSQLDBDriver");
        buildInDriverImpl.put("h2db", "org.solmix.sql.H2DBDriver");
        buildInDriverImpl.put("generic", "orgorg.solmix.sql.H2DBDriver.solmix.sql.GenericDriver");
        buildInDriverImpl.put("cache", "org.solmix.sql.CacheDriver");
        buildInDriverImpl.put("::hibernate::", "org.solmix.sql.HibernateDriver");
    }

    public SQLDriver(String dbName) throws SlxException
    {
        this(dbName, null);
    }

    public SQLDriver(String dbName, SQLTable table) throws SlxException
    {
        this.dbName = null;
        this.table = null;
        this.dbConnection = null;
        this.dbName = dbName;
        this.table = table;
        thisConfig = SQLConfigManager.getConfig();
        String dbType = thisConfig.getString(dbName + ".database.type", "");
        quoteColumnNames = thisConfig.getBoolean(dbType + ".quoteColumnNames", false);
        useColumnLabelInMetadata = thisConfig.getBoolean(dbType + ".useColumnLabelInMetadata", false);
    }

    public static SQLDriver instance() throws SlxException {
        return instance(thisConfig.getString("defaultDatabase"));
    }

    public static SQLDriver instance(String dbName) throws SlxException {
        try {
            return (SQLDriver) Reflection.invokeStaticMethod(buildInDriverForDB(dbName), "instance", dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SQLDriver instance(String dbName, String dbType) throws Exception {
        return (SQLDriver) Reflection.invokeStaticMethod(buildInDriverForDBType(dbType), "instance", dbName);
    }

    public static SQLDriver instance(String dbName, String dbType, SQLTable table) throws Exception {
        return (SQLDriver) Reflection.invokeStaticMethod(buildInDriverForDBType(dbType), "instance", dbName, table);
    }

    /**
     * @param databaseName
     * @param table
     * @return
     * @throws Exception
     * @throws SlxException
     */
    public static SQLDriver instance(String databaseName, SQLTable table) throws SlxException {
        try {
            return (SQLDriver) Reflection.invokeStaticMethod(buildInDriverForDB(databaseName), "instance", databaseName, table);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static String buildInDriverForDB(String dbName) throws SlxException {
        String _dbType = null;
        String prop_db_key = dbName + ".database.type";
        _dbType = SQLConfigManager.getConfig().getString(prop_db_key, "hsqldb");
        if (_dbType == null) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_DEFINED_DBTYPE, "no sql type defined");
        }
        return buildInDriverForDBType(_dbType);
    }

    protected static String buildInDriverForDBType(String dbType) throws SlxException {
        String impl = buildInDriverImpl.getString(dbType);
        if (impl == null) {
            throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, "unable to find implementer for database type" + dbType);
        } else {
            return impl;
        }
    }

    protected String getSequenceName(String columnName, SQLDataSource dataSource) throws SlxException {
        return getSequenceName(columnName, dataSource.getSequences(), table.getName());
    }

    public static String getSequenceName(String columnName, Map sequences, String tableName) throws SlxException {
        if (columnName == null || sequences == null || tableName == null)
            return null;
        String sequenceName = (String) sequences.get(columnName);
        if (sequenceName == null)
            return null;
        if (sequenceName.equals("__default")) {
            sequenceName = (new StringBuilder()).append(tableName).append("_").append(columnName).toString();
            int sequenceLength = sequenceName.length();
            if (sequenceLength > 30) {
                int columnNameLength = columnName.length();
                if (columnNameLength + 1 > 29)
                    throw new SlxException(
                        (new StringBuilder()).append("can't create a unique sequence name for column name: '").append(columnName).append(
                            "' - unable to continue").toString());
                String truncatedTableName = tableName.substring(0, 29 - columnNameLength);
                sequenceName = (new StringBuilder()).append(truncatedTableName).append("_").append(columnName).toString();
            }
        }
        return sequenceName;

    }

    public boolean shouldUseSQLDateType(Tfield field) {
        return false;
    }

    public abstract boolean isSupportsNativeReplace();

    public abstract String sqlOutTransform(String s, String s1, String s2) throws SlxException;

    protected abstract String getExpressionForSortBy(String s, Map map);

    public abstract Map fetchLastPrimaryKeys(Map map, List list, SQLDataSource sqldatasource, DSRequest dsrequest) throws SlxException;

    public abstract String escapeValue(Object obj);

    public abstract String escapeValueForFilter(Object obj, String s);

    public abstract String formatValue(Object obj);

    /**
     * escape some column from table.
     * 
     * @param columnName
     * @return
     */
    public String escapeColumnName(String columnName) {
        return escapeColumnName(columnName, false);
    }

    public String escapeColumnName(String columnName, boolean forceQuoteColumn) {
        if (columnName == null)
            return null;
        if (forceQuoteColumn || quoteColumnNames || table != null && table.quoteColumnNames != null && table.quoteColumnNames.equals("true"))
            return (new StringBuilder()).append(openQuote()).append(globalPerl.substitute("s'\"'\"\"'g", columnName.toString())).append(closeQuote()).toString();
        else
            return columnName.toString();
    }

    public String openQuote() {
        return "\"";
    }

    public String closeQuote() {
        return "\"";
    }

    public String getQualifiedSchemaSeparator() {
        return ".";
    }

    /**
     * @param columnValue
     * @param field
     * @return
     */
    public String sqlInTransform(Object columnValue, Tfield field) {
        if (columnValue instanceof Date) {
            if (field != null) {
                String strategy = field.getSqlStorageStrategy();
                if ("number".equals(strategy) || "text".equals(strategy)) {
                    String sqlFormat = field.getDateFormat();
                    if (sqlFormat == null)
                        sqlFormat = "yyyyMMdd";
                    SimpleDateFormat sdf = new SimpleDateFormat(sqlFormat);
                    String formatted = sdf.format(columnValue);
                    if ("text".equals(strategy))
                        formatted = (new StringBuilder()).append("'").append(formatted).append("'").toString();
                    return formatted;
                } else if (DataTools.typeIsBoolean(columnValue.toString())) {
                    return columnValue.equals(Boolean.TRUE) ? "'Y'" : "'N'";
                }
            }
            long timeStamp = ((Date) columnValue).getTime();
            return escapeValue((new Timestamp(timeStamp)).toString());
        } else
            return escapeValue(columnValue);
    }

    /**
     * @return
     */
    public Object escapeClause() {
        return "";
    }

    /**
     * execute SQL query.
     * 
     * @param statement query string.
     * @param dataSources list of datasource.
     * @param opConfig datasource operation configuration.
     * @param req datasource request.
     * @return
     * @throws SlxException
     */
    public List executeQuery(String query, List dataSources, ToperationBinding opConfig, DSRequest req) throws SlxException {
        return getTransformedResults(query, dbConnection, dbName, this, dataSources, opConfig, req);
    }

    public static List getTransformedResults(String query, DSRequest req) throws Exception {
        return getTransformedResults(query, null, req);
    }

    public static List getTransformedResults(String query, String dbName, DSRequest req) throws Exception {
        return getTransformedResults(query, null, dbName, null, req);
    }

    protected static List getTransformedResults(String query, Connection conn, String dbName, SQLDriver sqlDriver, DSRequest req) throws SlxException {
        return getTransformedResults(query, conn, dbName, sqlDriver, null, req);
    }

    protected static List getTransformedResults(String query, Connection conn, String dbName, SQLDriver sqlDriver, List dataSources, DSRequest req)
        throws SlxException {
        return getTransformedResults(query, conn, dbName, sqlDriver, dataSources, null, req);
    }

    /**
     * @param query
     * @param dbConnection2
     * @param dbName2
     * @param sqlDriver
     * @param dataSources
     * @param opConfig
     * @param reqContext
     * @return
     * @throws SlxException
     */
    protected static List getTransformedResults(String query, Connection conn, String dbName, SQLDriver driver, List dataSources,
        ToperationBinding opConfig, DSRequest req) throws SlxException {
        boolean __colseConn = false;
        boolean __userOrAutoTransaction = true;
        Connection __currentConn;
        Connection __userOrAutoConn = null;
        if (req != null) {
            RPCManager rpc = req.getRpc();
            // is rpc
            if (rpc != null) {
                __userOrAutoConn = ((SQLDataSource) req.getDataSource()).getTransactionalConnection(req);
            }
        }
        // not find datasource connection,used default connection.
        if (__userOrAutoConn == null) {
            if (dbName == null)
                dbName = thisConfig.getString("defaultDatabase", null);
            if (conn == null) {
                conn = ConnectionManager.getConnection(dbName);
                __colseConn = true;
                if (driver != null && driver.dbConnection == null) {
                    driver.dbConnection = conn;
                    __colseConn = false;
                }
            }
            __userOrAutoTransaction = false;
            __currentConn = conn;
        } else {
            __currentConn = __userOrAutoConn;
        }
        Statement statement = null;
        ResultSet resultSet = null;
        List __return = null;
        try {
            Boolean printSQL = thisConfig.getBoolean("printSQL", false);
            if (printSQL) {
                log.debug("Executing SQL query on {} :\n {}",dbName, query);
            }
            if (driver != null) {
                statement = driver.createFetchStatement(__currentConn);
            } else {
                statement = __currentConn.createStatement();
            }
            long _$ = System.currentTimeMillis();
            resultSet = statement.executeQuery(query);
            long $_ = System.currentTimeMillis();
            SQLDataSource.createAndFireTMEvent($_ - _$, "SQL Query Time", query);
            __return = SQLTransform.toListOfMapsOrBeans(resultSet, SQLTransform.hasBrokenCursorAPIs(dbName), dataSources, opConfig);
        } catch (SQLException e) {
            String __info = "Execute of select :\n" + query + " on db: [" + dbName + "] throw exception  ErrorCode:[" + e.getErrorCode()
                + "] Message: " + e.toString();
            log.debug(__info);
            if (__userOrAutoTransaction) {
                log.info("  - assuming the connection is staled and retrying query.");
                try {
                    ConnectionManager.freeConnection(conn);
                    conn = ConnectionManager.getNewConnection(dbName);
                    __currentConn = conn;
                    if (driver != null) {
                        driver.dbConnection = conn;
                        statement = driver.createFetchStatement(__currentConn);
                    } else {
                        statement = __currentConn.createStatement();
                    }
                    resultSet = statement.executeQuery(query);
                    __return = SQLTransform.toListOfMapsOrBeans(resultSet, SQLTransform.hasBrokenCursorAPIs(dbName), dataSources, opConfig);
                } catch (SQLException sql) {
                    throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
                }
            } else {
                throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, __info, e);
            }

        } finally {
            try {
                resultSet.close();
                statement.close();
            } catch (Exception ignored) {
            }
            if (!__userOrAutoTransaction && __colseConn)
                ConnectionManager.freeConnection(conn);
        }
        return __return;
    }

    /**
     * @param currentConn
     * @return
     * @throws SQLException
     */
    public Statement createFetchStatement(Connection conn) throws SQLException {
        return conn.createStatement();
    }

    /**
     * @param statement
     * @param req
     * @return
     * @throws SlxException
     */
    public int executeUpdate(String update, DSRequest req) throws SlxException {
        return update(update, null, dbConnection, dbName, this, req);
    }

    public static int update(String update, DSRequest req) throws Exception {
        return update(update, null, req);
    }

    public static int update(String update, String dbName, DSRequest req) throws Exception {
        return update(update, null, dbName, req);
    }

    /**
     * After used this method ,must free db connection.
     * 
     * @param update
     * @param data
     * @param dbName
     * @param req
     * @return
     * @throws Exception
     */
    public static int update(String update, List data, String dbName, DSRequest req) throws Exception {
        return update(update, data, ConnectionManager.getConnection(dbName), dbName, null, req);
    }

    /**
     * @param update Update SQL string
     * @param data binary stream or byte stream
     * @param conn database connection
     * @param dbName database name
     * @param sqlDriver database SQL driver name
     * @param req datasource request
     * @return success or failed flag
     */
    public static int update(String update, List data, Connection conn, String dbName, SQLDriver driver, DSRequest req) throws SlxException {
        boolean __userOrAutoTransaction = true;
        Connection __currentConn;
        if (dbName == null)
            dbName = thisConfig.getString("defaultDatabase", "");
        Connection __userOrAutoConn = null;
        if (req != null) {
            RPCManager rpc = req.getRpc();
            if (rpc != null) {
                SQLDataSource ds = (SQLDataSource) req.getDataSource();
                __userOrAutoConn = ds.getTransactionalConnection(req);
            }
        }
        // NO set RPC transaction ,no global transaction
        if (__userOrAutoConn == null) {
            if (conn == null) {
                conn = ConnectionManager.getConnection(dbName);
                if (driver != null)
                    driver.dbConnection = conn;
            }
            __userOrAutoTransaction = false;
            __currentConn = conn;
        } else {
            __currentConn = __userOrAutoConn;
        }
        Boolean printSQL = thisConfig.getBoolean("printSQL", false);
        if (printSQL) {
            log.debug((new StringBuilder()).append("Executing SQL update on '").append(dbName).append("'").toString(), update);
        }
        int __return = 0;
        try {
            __return = doUpdate(update, data, __currentConn, req, driver);
        } catch (SlxException e) {
            if (__userOrAutoTransaction) {
                ConnectionManager.freeConnection(conn);
                conn = ConnectionManager.getNewConnection(dbName);
                if (driver != null)
                    driver.dbConnection = conn;
                __return = doUpdate(update, data, conn, req,driver);
            }else{
                String __info = "Execute of select :\n" + update + " on db: [" + dbName + "] throw exception  Message: " + e.getMessage();
                throw new SlxException(Tmodule.SQL,Texception.SQL_SQLEXCEPTION,__info,e);
            }
        } finally {
            if (!__userOrAutoTransaction && driver == null)
                ConnectionManager.freeConnection(conn);
        }
        return __return;
    }

    public static int doUpdate(String update, List data, Connection conn, DSRequest req) throws SlxException {
        return doUpdate(update, data, conn, req, null);
    }

    /**
     * @param update
     * @param data binary stream or byte stream
     * @param currentConn
     * @param req
     * @param driver
     * @return
     * @throws SlxException
     */
    private static int doUpdate(String update, List data, Connection conn, DSRequest req, SQLDriver driver) throws SlxException {
        long _$ = System.currentTimeMillis();
        PreparedStatement s=null;
        int __return = 0;
        try {
            s = driver.getPreparedStatement(conn, update);
            if (data != null && !DataTools.isRemove(req.getContext().getOperationType())) {
                int position = 1;
                for (Object o : data) {
                    if (o instanceof InputStream) {
                        InputStream is = (InputStream) o;
                        s.setBinaryStream(position, is, is.available());
                    } else if (o instanceof StringBuffer) {
                        StringBuffer sb = (StringBuffer) o;
                        s.setCharacterStream(position, new StringReader(sb.toString()), sb.length());
                    }
                    position++;
                }
            }
            __return = s.executeUpdate();
            long $_ = System.currentTimeMillis();
            SQLDataSource.createAndFireTMEvent(($_ - _$), "SQL modification operation success,return value is" +__return);
        } catch (Exception e) {
            log.error("SQL exception", e);

            if (driver != null)
                driver.saveGeneratedKeys(s, req);
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e.getMessage());
        } finally {
            if (s != null)
                try {
                    s.close();
                } catch (Exception ignored) {
                    log.warn("Exception thrown whilst processing InputStream", ignored);
                    return -1;
                }
        }
        return __return;
    }

    /**
     * @param conn
     * @param update
     * @return
     * @throws SlxException
     */
    protected PreparedStatement getPreparedStatement(Connection conn, String update) throws SlxException {
        try {
            return conn.prepareStatement(update);
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
        }
    }

    /**
     * support special database,if sub driver used this method would override this.
     * 
     * @param s A object that represents a precompiled SQL statement
     * @param req
     */
    public void saveGeneratedKeys(PreparedStatement s, DSRequest req) throws SlxException {

    }

    public int executeUpdate(String update, List data, DSRequest req) throws SlxException {
        return update(update, data, dbConnection, dbName, this, req);
    }

    /**
     * @param selectClause
     * @param tableClause
     * @param whereClause
     * @param groupClause
     * @param groupWhereClause
     * @param context Velocity SQL building context.
     * @return
     */
    public String getRowCountQueryString(String selectClause, String tableClause, String whereClause, String groupClause, String groupWhereClause,
        Map context) {
        String __return = "SELECT COUNT(*) FROM ";
        if (!groupClause.equals("$defaultGroupClause"))
            __return = (new StringBuilder()).append(__return).append("(SELECT ").append(selectClause).append(" FROM ").toString();
        __return = (new StringBuilder()).append(__return).append(tableClause).toString();
        if (!whereClause.equals("$defaultWhereClause") || context.get("defaultWhereClause") != null)
            __return = (new StringBuilder()).append(__return).append(" WHERE ").append(whereClause).toString();
        if (!groupClause.equals("$defaultGroupClause")) {
            __return = (new StringBuilder()).append(__return).append(" GROUP BY ").append(groupClause).append(") work").toString();
            if (!groupWhereClause.equals("$defaultGroupWhereClause"))
                __return = (new StringBuilder()).append(__return).append(" WHERE ").append(groupWhereClause).toString();
        }
        return __return;
    }

    public String getRowCountQueryString(String customSql) {
        return "SELECT COUNT(*) FROM (" + customSql + ")";
    }

    /**
     * @param eQuery
     * @param req
     * @throws SlxException
     */
    public Object executeScalar(String query, DSRequest req) throws SlxException {
        return getScalarResult(query, dbConnection, dbName, this, req);

    }

    /**
     * @param query
     * @param dbConnection2
     * @param dbName2
     * @param sqlDriver
     * @param req
     * @return
     * @throws SlxException
     */
    public static Object getScalarResult(String query, Connection conn, String dbName, SQLDriver sqlDriver, DSRequest req) throws SlxException {
        List list = getTransformedResults(query, conn, dbName, sqlDriver, req);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            //
            Map map = (Map) list.get(0);
            return map.get(DataUtil.getSingle(map));
        }
    }

    /**
     * @return
     */
    public boolean hasBrokenCursorAPIs() {
        return thisConfig.getBoolean(dbName + ".database.brokenCursorAPIs", false);
    }

    /**
     * output data with column title.
     * 
     * @return
     */
    public boolean useColumnLabelInMetadata() {
        return useColumnLabelInMetadata;
    }

    /**
     * support special database,if sub driver used this method would override this.
     * 
     * @return
     */
    public boolean supportsSQLLimit() {
        return false;
    }

    /**
     * support special database,if sub driver used this method would override this.
     * 
     * @return
     */
    public boolean limitRequiresSQLOrderClause() {
        return false;
    }

    public abstract String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns, String orderClause)
        throws SlxException;

    public abstract String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns) throws SlxException;

    /**
    * 
    */
    public void clearState() {
        freeConnection();
    }

    public synchronized void freeConnection() {
        if (dbConnection != null) {
            try {
                ConnectionManager.freeConnection(dbConnection);
            } catch (Exception ignored) {
            }
            dbConnection = null;
        }
    }

    public Connection getConnection() {
        return dbConnection;
    }

    /**
     * @param f
     * @return
     */
    public boolean fieldIsSearchable(Tfield f) {
        Efield type = f.getType();
        return type != Efield.BINARY && !type.value().equals("clob");
    }

    /**
     * @return
     */
    public int getMaximumSetSize() {
        return 0;
    }

    /**
     * @param value
     * @param b
     * @return
     */
    protected abstract String escapeValueUnquoted(Object value, boolean b);

    /**
     * @param f
     * @return
     */
    public boolean fieldAssignableInline(Tfield field) {
        String fieldType = field.getType().value();
        return (!DataTools.isBinary(field)) && !"blob".equals(fieldType) && !"clob".equals(fieldType);
    }

    public abstract String getNextSequenceValue(String s, SQLDataSource sqldatasource) throws SlxException;

    /**
     * @param field
     * @param obj
     * @return
     */
    public Object transformFieldValue(Tfield field, Object obj) {
        return obj;
    }

    /**
     * @param statement
     * @param valueMap
     * @param valueSets
     * @param req
     */
    public int executeBatchUpdate(String statement, List<String> valueMap, List valueSets, DSRequest req) throws SlxException {
        if (DataUtil.isNullOrEmpty(valueSets) || DataUtil.isNullOrEmpty(valueMap))
            return -1;
        Connection conn = this.dbConnection;
        boolean __colseConn = false;
        if (conn == null) {
            conn = ConnectionManager.getConnection(dbName);
            __colseConn = true;
            if (this.dbConnection == null) {
                this.dbConnection = conn;
                __colseConn = false;
            }
        }
        Map<String, Tfield> types = columnValueType(valueSets, req);
        try {
            PreparedStatement pre = conn.prepareStatement(statement);
            for (int i = 0; i < valueSets.size(); i++) {
                Object obj = valueSets.get(i);
                if (obj instanceof Map) {
                    Map map = (Map) obj;
                    for (int j = 1; j <= valueMap.size(); j++) {
                        String key = valueMap.get(j - 1);
                        Object objValue = map.get(key);
                        objValue = adaptValue(key, objValue, types);
                        if (objValue instanceof String) {
                            pre.setString(j, objValue.toString());
                        } else if (objValue instanceof Double) {
                            pre.setDouble(j, (Double) objValue);
                        } else if (objValue instanceof Long) {
                            pre.setLong(j, (Long) objValue);
                        } else if (objValue instanceof Short) {
                            pre.setShort(j, (Short) objValue);
                        } else if (objValue instanceof Integer) {
                            pre.setInt(j, (Integer) objValue);
                        } else if (objValue instanceof Float) {
                            pre.setFloat(j, (Float) objValue);
                        } else if (objValue instanceof BigDecimal) {
                            pre.setBigDecimal(j, (BigDecimal) objValue);
                        } else if (objValue instanceof java.sql.Timestamp) {
                            pre.setTimestamp(j, (java.sql.Timestamp) objValue);
                        } else if (objValue instanceof InputStream) {
                            InputStream is = (InputStream) pre;
                            pre.setBinaryStream(j, is, is.available());
                        } else if (objValue instanceof StringBuffer) {
                            StringBuffer sb = (StringBuffer) objValue;
                            pre.setCharacterStream(j, new StringReader(sb.toString()), sb.length());
                        } else if (objValue instanceof Date) {
                            java.sql.Date date = new java.sql.Date(((Date) objValue).getTime());
                            pre.setDate(j, date);
                        } else if (objValue instanceof java.sql.Time) {
                            pre.setTime(j, (java.sql.Time) objValue);
                        }
                    }
                    pre.addBatch();
                    if (i % 100 == 0) {
                        pre.executeBatch();
                        pre.clearBatch();
                    }
                }
            }
        } catch (SQLException e) {
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
        } catch (IOException e) {
            throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
        } finally {
            if (__colseConn)
                ConnectionManager.freeConnection(conn);
        }

        return 0;

    }

    /**
     * @param key
     * @param objValue
     * @param types
     * @return
     */
    private Object adaptValue(String key, Object objValue, Map<String, Tfield> types) {
        Efield _type = types.get(key) != null ? types.get(key).getType() : null;
        if (_type == Efield.DATE && !(objValue instanceof Date)) {
            String dateFormat = types.get(key).getDateFormat() != null ? types.get(key).getDateFormat() : "yyyyMMdd";
            objValue = DateUtil.getDateFromString(objValue.toString(), dateFormat);
        }
        return objValue;
    }

    /**
     * @param key
     * @param objValue
     * @param req
     * @return
     * @throws SlxException
     */
    private Map<String, Tfield> columnValueType(List<String> keys, DSRequest req) throws SlxException {
        DataSource _ds = req.getDataSource();
        List<Tfield> list = _ds.getContext().getFields();
        Eoperation __opType = req.getContext().getOperationType();
        Tfield found = null;
        Map<String, Tfield> __return = new HashMap<String, Tfield>();
        for (String key : keys) {
            for (Tfield field : list) {
                if (field.getCustomInsertExpression() != null && __opType == Eoperation.ADD
                    && field.getCustomInsertExpression().equalsIgnoreCase(key)) {
                    found = field;
                    break;
                }
                if (field.getCustomUpdateExpression() != null && __opType == Eoperation.UPDATE
                    && field.getCustomInsertExpression().equalsIgnoreCase(key)) {
                    found = field;
                    break;
                }
                if (field.getName().equalsIgnoreCase(key)) {
                    found = field;
                    break;
                }
            }
            if (found != null) {
                __return.put(key, found);
            }
        }

        return __return;
    }

}
