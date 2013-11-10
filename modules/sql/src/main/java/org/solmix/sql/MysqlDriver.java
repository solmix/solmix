/*
 * SOLMIX PROJECT
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-16
 */

public class MysqlDriver extends SQLDriver
{

 

    public static SQLDriver instance(String dbName, SQLTable table, DataTypeMap config, SQLDataSource ds) throws SlxException {
        return new MysqlDriver(dbName, table, config, ds);
    }

    private final boolean supportsSQLLimit;
    /**
     * @param dbName
     * @param table
     * @throws SlxException 
     */
    public MysqlDriver(String dbName, SQLTable table, DataTypeMap config, SQLDataSource ds) throws SlxException
    {
        super(dbName, table,config,ds);
        supportsSQLLimit = true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#isSupportsNativeReplace()
     */
    @Override
    public boolean isSupportsNativeReplace() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#sqlOutTransform(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String sqlOutTransform(String columnName, String remapName, String tableName) throws SlxException {
        String output = escapeColumnName(columnName);
        if (remapName != null && !columnName.equals(remapName))
            output = (new StringBuilder()).append(output).append(" AS ").append(escapeColumnName(remapName)).toString();
        if (tableName != null)
            output = (new StringBuilder()).append(tableName).append(".").append(output).toString();
        return output;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#getExpressionForSortBy(java.lang.String, java.util.Map)
     */
    @Override
    protected String getExpressionForSortBy(String s, Map map) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#fetchLastPrimaryKeys(java.util.Map, java.util.List, org.solmix.sql.SQLDataSource,
     *      org.solmix.api.datasource.DSRequest)
     */
    @Override
    public Map fetchLastPrimaryKeys(Map primaryKeysPresent, List list, SQLDataSource ds, DSRequest req) throws SlxException {
        if (connection == null && req == null)
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, "no existing db connection exists for last row fetch");
        Map primaryKeys = primaryKeysPresent;
        for (Object key : primaryKeys.keySet()) {
            String sequenceName = (String) key;
            String sequence = getCurrentSequenceValue(sequenceName, ds);
            if (sequence != null) {
                Object obj = getScalarResult((new StringBuilder()).append("SELECT ").append(sequence).append(" FROM DUAL").toString(), connection,
                    dbName, this, req);
                BigDecimal value = new BigDecimal(obj.toString());
                primaryKeys.put(sequenceName, value.toString());
            }
        }
        return primaryKeys;
    }

    /**
     * @param sequenceName
     * @param ds
     * @return
     * @throws SlxException
     */
    protected String getCurrentSequenceValue(String columnName, SQLDataSource ds) throws SlxException {
        String sequenceName = getSequenceName(columnName);
        if (sequenceName == null)
            return null;
        String schema = "";
        if (ds != null) {
            // schema = (String)ds.getContext().getTdataSource().getSchema();
            if (schema == null)
                schema = "";
            else
                schema = (new StringBuilder()).append(schema).append(".").toString();
        }
        return (new StringBuilder()).append(schema).append(sequenceName).append(".CurrVal").toString();
    }

    protected String getSequenceName(String columnName) throws SlxException {
        return getSequenceName(columnName, table.getSequences(), table.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#escapeValue(java.lang.Object)
     */
    @Override
    public String escapeValue(Object value) {
        if (value == null)
            return null;
        else
            return (new StringBuilder()).append("'").append(escapeValueUnquoted(value.toString(), false)).append("'").toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#escapeValueForFilter(java.lang.Object, java.lang.String)
     */
    @Override
    public String escapeValueForFilter(Object value, String filterStyle) {
        if (value == null)
            return null;
        String rtn = "'";
        if (!"startsWith".equals(filterStyle))
            rtn = (new StringBuilder()).append(rtn).append("%").toString();
        return (new StringBuilder()).append(rtn).append(escapeValueUnquoted(value, true)).append("%'").toString();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List, java.lang.String)
     */
    @Override
    public String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns, String orderClause) throws SlxException {
        throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, "Not supported");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List)
     */
    @Override
    public String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns) throws SlxException {
         return (new StringBuilder()).append(query).append("limit").append(" "  ).append(startRow).append(", ").append(totalRows).toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#escapeValueUnquoted(java.lang.Object, boolean)
     */
    @Override
    protected String escapeValueUnquoted(Object value, boolean escapeForFilter) {
        if (value == null)
            return null;
        String escaped = globalPerl.substitute("s/'/''/g", value.toString());
        if (escapeForFilter) {
            escaped = globalPerl.substitute("s'\\\\'\\\\'g", escaped);
            escaped = globalPerl.substitute("s'%'\\%'g", escaped);
            escaped = globalPerl.substitute("s'_'\\_'g", escaped);
        }
        return escaped;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.sql.SQLDriver#getNextSequenceValue(java.lang.String, org.solmix.sql.SQLDataSource)
     */
    @Override
    public String getNextSequenceValue(String columnName, SQLDataSource dataSource) throws SlxException {
        String sequenceName = getSequenceName(columnName, dataSource);
        if (sequenceName == null)
            return null;
        String schema = "";
        if (dataSource != null) {
            schema = dataSource.getContext().getTdataSource().getSqlSchema();
            if (schema == null)
                schema = "";
            else
                schema = (new StringBuilder()).append(schema).append(".").toString();
        }
        return (new StringBuilder()).append(schema).append(sequenceName).append(".NextVal").toString();
    }

}
