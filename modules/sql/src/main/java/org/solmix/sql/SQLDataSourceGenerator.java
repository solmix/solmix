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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.ToperationBindings;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.datasource.AutoDeriver;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.velocity.Velocity;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-9-9
 */

public class SQLDataSourceGenerator implements DataSourceGenerator
{

    private static final Logger log = LoggerFactory.getLogger(SQLDataSourceGenerator.class.getName());

    private Connection connection;

    private final ConnectionManager connectionManager;

    private final SQLDataSource sqlDatasource;

    private static Map<Integer, String> jdbcTypes;
    static {
        jdbcTypes = new HashMap<Integer, String>();
        jdbcTypes.put(new Integer(-7), "bit");
        jdbcTypes.put(new Integer(-6), "tinyint");
        jdbcTypes.put(new Integer(5), "smallint");
        jdbcTypes.put(new Integer(4), "integer");
        jdbcTypes.put(new Integer(-5), "bigint");
        jdbcTypes.put(new Integer(6), "float");
        jdbcTypes.put(new Integer(7), "real");
        jdbcTypes.put(new Integer(8), "double");
        jdbcTypes.put(new Integer(2), "numeric");
        jdbcTypes.put(new Integer(3), "decimal");
        jdbcTypes.put(new Integer(1), "char");
        jdbcTypes.put(new Integer(12), "varchar");
        jdbcTypes.put(new Integer(-1), "longvarchar");
        jdbcTypes.put(new Integer(91), "date");
        jdbcTypes.put(new Integer(92), "time");
        jdbcTypes.put(new Integer(93), "timestamp");
        jdbcTypes.put(new Integer(-2), "binary");
        jdbcTypes.put(new Integer(-3), "varbinary");
        jdbcTypes.put(new Integer(-4), "longvarbinary");
        jdbcTypes.put(new Integer(0), "null");
        jdbcTypes.put(new Integer(1111), "other");
        jdbcTypes.put(new Integer(2000), "java_object");
        jdbcTypes.put(new Integer(2001), "distinct");
        jdbcTypes.put(new Integer(2002), "struct");
        jdbcTypes.put(new Integer(2003), "array");
        jdbcTypes.put(new Integer(2004), "blob");
        jdbcTypes.put(new Integer(2005), "clob");
        jdbcTypes.put(new Integer(2006), "ref");
        jdbcTypes.put(new Integer(70), "datalink");
        jdbcTypes.put(new Integer(16), "boolean");
    }

    public SQLDataSourceGenerator(ConnectionManager connectionManager, SQLDataSource sql)
    {
        this.connectionManager = connectionManager;
        this.sqlDatasource = sql;
    }

    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {

        return sqlDatasource.instance(context);
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceGenerator#deriveSchema(org.solmix.api.datasource.DataSourceData)
     */
    @Override
    public DataSource deriveSchema(DataSourceData context) throws SlxException {
        TdataSource tds = context.getTdataSource();
        String serverType = AutoDeriver.getServerType(tds).value();
        String dbName = AutoDeriver.getDbName(tds);
        SchemaBuilder sqlgen = new SchemaBuilder(dbName, tds.getSqlSchema(), tds.getTableName(), serverType, "datetime", false);
        TdataSource data = sqlgen.build();
        DataSourceData schemaContext = new DataSourceData(data);
        BasicDataSource schema = new BasicDataSource(SlxContext.getThreadSystemContext());
        schema.init(schemaContext);
        return schema;
    }

    class SchemaBuilder
    {

        private final String dbName;

        private final String schema;

        private final String tableName;

        private final String serverType;

        private final String timestampType;

        private final boolean returnSqlType;

        private String discoveredSchema;

        private String ID;

        private final ToperationBinding autoDeriveSchemaOperation;

        SchemaBuilder(String dbName, String schema, String tableName, String serverType, String timestampType, boolean returnSqlType)
        {
            autoDeriveSchemaOperation = null;
            this.tableName = tableName;
            this.dbName = dbName;
            this.schema = schema;
            this.serverType = serverType;
            this.timestampType = timestampType;
            this.returnSqlType = returnSqlType;
        }

        public TdataSource build() throws SlxException {
            boolean freeConnection = false;
            List<Tfield> fields = null;
            ObjectFactory factory = new ObjectFactory();
            TdataSource data = factory.createTdataSource();
            if (dbName != null)
                data.setDbName(dbName);
            if (serverType != null)
                data.setServerType(EserverType.fromValue(serverType));
            if (ID == null)
                ID = tableName;
            data.setID(ID);
            if (tableName != null)
                data.setTableName(tableName);
            if (schema != null)
                data.setSqlSchema(schema);
            data.setTitle("Auto Gernerated " + ID);
            if (connection == null) {
                freeConnection = true;
                connection = connectionManager.getConnection(dbName);
            }
            try {
                fields = this.autoDeriveSchemaOperation != null ? this.getFieldsFromOperation(autoDeriveSchemaOperation) : getFieldsFromTable();
            } catch (SQLException e) {
                throw new SlxException(Tmodule.DATASOURCE, Texception.SQL_SQLEXCEPTION, e.getMessage(), e);
            }
            if (discoveredSchema != null)
                data.setSqlSchema(discoveredSchema);
            if (fields != null) {
                Tfields tfields = factory.createTfields();
                tfields.getField().addAll(fields);
                data.setFields(tfields);
            }
            if (this.autoDeriveSchemaOperation != null) {
                ToperationBindings binds = factory.createToperationBindings();
                binds.getOperationBinding().add(autoDeriveSchemaOperation);
                data.setOperationBindings(binds);
            }
            if (freeConnection)
                connectionManager.freeConnection(connection);
            return data;

        }

        /**
         * @return
         * @throws SlxException
         * @throws SQLException
         */
        public List<Tfield> getFieldsFromTable() throws SlxException, SQLException {
            boolean freeConnection = false;
            List<Tfield> fields = null;
            if (connection == null) {
                freeConnection = true;
                connection = connectionManager.getConnection(dbName);
            }
            SQLMetaData md = new SQLMetaData(connection, connectionManager);
            String catalog = connection.getCatalog();
            log.warn((new StringBuilder()).append("Fetching column metadata for table: ").append(tableName).toString());
            log.warn((new StringBuilder()).append("=============Using catalog: ").append(catalog).toString());
            List columns = md.getColumnMetaData(catalog, schema, tableName);
            log.info((new StringBuilder()).append("Fetching column metadata for ").append(tableName).append(" complete").toString());
            if (columns.size() == 0)
                throw new SlxException(
                    (new StringBuilder()).append("table ").append(tableName).append(" does not exist or contains no columns.").toString());
            List pks = md.getPrimaryKeys(catalog, tableName);
            Iterator i = columns.iterator();
            do {
                if (!i.hasNext())
                    break;
                Map metaData = (Map) i.next();
                String fieldName = (String) metaData.get("COLUMN_NAME");
                Object typeObj = metaData.get("DATA_TYPE");
                if (typeObj != null) {
                    Integer type = new Integer(typeObj.toString());
                    if (type.intValue() == 1111) {
                        String typeName = (String) metaData.get("TYPE_NAME");
                        if ("FLOAT".equals(typeName) || "DOUBLE".equals(typeName))
                            type = new Integer(6);
                    }
                    if (type.intValue() != -2) {
                        String dsType = overrideTypeConversion(type.intValue(), fieldName, md);
                        if (dsType == null)
                            dsType = dsTypeForDBType(type, null);
                        Tfield dsField = new Tfield();
                        dsField.setName(fieldName);
                        dsField.setType(Efield.fromValue(dsType));
                        if (returnSqlType) {
                            // dsField.setSqlType(jdbcTypes.get(type));
                            // if (metaData.get("COLUMN_SIZE") != null)
                            // dsField.setSqlLength(metaData.get("COLUMN_SIZE").toString());
                        }
                        if (dsType.equals("text")) {
                            Object lengthObj = metaData.get("COLUMN_SIZE");
                            if (lengthObj != null) {
                                Integer length = new Integer(lengthObj.toString());
                                dsField.setLength(length);
                            }
                        }
                        Object commentsObj = metaData.get("REMARKS");
                        if (commentsObj != null && !commentsObj.equals(""))
                            dsField.setTitle(commentsObj.toString());
                        if (pks.contains(fieldName)) {
                            dsField.setPrimaryKey(new Boolean(true));
                            if (dsType.equals("number"))
                                dsField.setType(Efield.fromValue("sequence"));
                        }
                        if (fields == null)
                            fields = new ArrayList<Tfield>();
                        fields.add(dsField);
                        String discoveredSchema = (String) metaData.get("TABLE_SCHEM");
                        if (discoveredSchema != null)
                            this.discoveredSchema = discoveredSchema;
                    }
                }
            } while (true);
            if (freeConnection)
                connectionManager.freeConnection(connection);
            return fields;
        }

        public List<Tfield> getFieldsFromOperation(ToperationBinding operation) throws SlxException, SQLException {
            boolean freeConnection = false;
            List<Tfield> fields = null;
            if (connection == null) {
                freeConnection = true;
                connection = connectionManager.getConnection(dbName);
            }
            String query = operation.getCommand();
            if (query == null)
                query = DataSourceData.getCustomSQL(operation);
            if (query == null) {
                String selectClause = DataSourceData.getSelectClause(operation);
                if (selectClause == null)
                    selectClause = "$defaultSelectClause";
                String tableClause = DataSourceData.getTableClause(operation);
                if (tableClause == null)
                    throw new SlxException("command, customSQL or tableClause is required for auto-deriving schema from an operationBinding");
                String whereClause = DataSourceData.getWhereClause(operation);
                if (whereClause == null)
                    whereClause = "$defaultWhereClause";
                query = (new StringBuilder()).append("SELECT ").append(selectClause).append(" FROM ").append(tableClause).append(" WHERE ").append(
                    whereClause).toString();
            }
            Map<String, String> params = new HashMap<String, String>();
            params.put("defaultWhereClause", "1=0");
            params.put("defaultSelectClause", "*");
            query = Velocity.evaluateAsString(query, params);
            log.info((new StringBuilder()).append("Auto-deriving datasource using query: ").append(query).toString());
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            if (numCols == 0)
                throw new SlxException((new StringBuilder()).append("autoDeriveSchema query produced zero columns: ").append(query).toString());
            for (int i = 1; i <= numCols; i++) {
                String fieldName = rsmd.getColumnName(i);
                log.warn((new StringBuilder()).append("processing field: ").append(fieldName).toString());
                Integer type = new Integer(rsmd.getColumnType(i));
                String typeName = rsmd.getColumnTypeName(i);
                if (type.intValue() == 1111 && ("FLOAT".equals(typeName) || "DOUBLE".equals(typeName)))
                    type = new Integer(6);
                String dsType = overrideTypeConversion(type.intValue(), fieldName, rsmd);
                if (dsType == null)
                    dsType = dsTypeForDBType(type, null);
                Tfield dsField = new Tfield();
                dsField.setName(fieldName);
                dsField.setType(Efield.fromValue(dsType));
                // if(returnSqlType)
                // dsField.setSqlType(jdbcTypes.get(type));
                if (dsType.equals("text")) {
                    Integer length = new Integer(rsmd.getColumnDisplaySize(i));
                    dsField.setLength(length);
                }
                String columnLabel = rsmd.getColumnLabel(i);
                if (columnLabel != null && !columnLabel.equals(""))
                    dsField.setTitle(columnLabel);
                if (fields == null)
                    fields = new ArrayList<Tfield>();
                fields.add(dsField);
            }
            if (freeConnection)
                connectionManager.freeConnection(connection);
            return fields;

        }

        /**
         * @param intValue
         * @param fieldName
         * @param rsmd
         * @return
         */
        private String overrideTypeConversion(int intValue, String fieldName, ResultSetMetaData rsmd) {
            // TODO Auto-generated method stub
            return null;
        }

        public String dsTypeForDBType(Number dbType, String dbName) throws SlxException {
            switch (dbType.intValue()) {
                case -1:
                case 1: // '\001'
                case 12: // '\f'
                case 2005:
                    return "text";

                case -7:
                case -6:
                case -5:
                case 4: // '\004'
                case 5: // '\005'
                    return "integer";

                case 2: // '\002'
                case 3: // '\003'
                    return "number";

                case 6: // '\006'
                case 7: // '\007'
                case 8: // '\b'
                    return "float";

                case 91: // '['
                    return "date";

                case 92: // '\\'
                    return "time";

                case 93: // ']'
                    return timestampType;
            }
            return "text";
        }

        public String overrideTypeConversion(int javaSQLType, String columnName, SQLMetaData md) {
            return null;
        }
    }
}
