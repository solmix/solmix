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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Efield;
import com.solmix.api.jaxb.Tfield;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.logs.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.util.DataTools;
import com.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solomon
 * @version 110035 2011-3-26
 */

public class OracleDriver extends SQLDriver
{

    private static Logger log = new Logger(SQLDriver.class.getName());

    boolean driverSupportsSQLLimit;

    boolean databaseSupportsSQLLimit;

    /**
     * @param dbName
     * @throws Exception
     */
    public OracleDriver(String dbName) throws SlxException
    {
        super(dbName);
        driverSupportsSQLLimit = false;
        databaseSupportsSQLLimit = false;
        init(dbName);
    }

    public OracleDriver(String dbName, SQLTable table) throws SlxException
    {
        super(dbName, table);
        driverSupportsSQLLimit = false;
        databaseSupportsSQLLimit = false;
        init(dbName);
    }

    public void init(String dbName) throws SlxException {
        driverSupportsSQLLimit = SQLConfigManager.getConfig().getBoolean(dbName + "driver.supportsSQLLimit", false);
        databaseSupportsSQLLimit = SQLConfigManager.getConfig().getBoolean(dbName + "oracle.supportsSQLLimit", false);
    }

    public static SQLDriver instance(String dbName, SQLTable table) throws SlxException {
        return new OracleDriver(dbName, table);
    }

    public static SQLDriver instance(String dbName) throws SlxException {
        return new OracleDriver(dbName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#escapeValue(java.lang.Object)
     */
    @Override
    public String escapeValue(Object value) {
        if (value == null)
            return null;
        else
            return (new StringBuilder()).append("'").append(escapeValueUnquoted(value.toString(), false)).append("'").toString();
    }

    /**
     * @param string
     * @param b
     * @return
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
     * @see com.solmix.sql.SQLDriver#escapeValueForFilter(java.lang.Object, java.lang.String)
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
     * @see com.solmix.sql.SQLDriver#fetchLastPrimaryKeys(java.util.Map, java.util.List, com.solmix.sql.SQLDataSource,
     *      com.solmix.api.datasource.DSRequest)
     */
    @Override
    public Map fetchLastPrimaryKeys(Map primaryKeysPresent, List list, SQLDataSource ds, DSRequest req) throws SlxException {
        if (dbConnection == null && req == null)
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_CONNECTION, "no existing db connection exists for last row fetch");
        Map primaryKeys = primaryKeysPresent;
        for (Object key : primaryKeys.keySet()) {
            String sequenceName = (String) key;
            String sequence = getCurrentSequenceValue(sequenceName, ds);
            if (sequence != null) {
                Object obj = getScalarResult((new StringBuilder()).append("SELECT ").append(sequence).append(" FROM DUAL").toString(), dbConnection,
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

    /**
     * @param columnName
     * @return
     * @throws SlxException
     */
    protected String getSequenceName(String columnName) throws SlxException {
        return getSequenceName(columnName, table.getSequences(), table.getName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#getExpressionForSortBy(java.lang.String, java.lang.Object)
     */
    @Override
    protected String getExpressionForSortBy(String column, Map valueMap) {
        if (valueMap == null || valueMap.size() == 0)
            return column;
        String expr = (new StringBuilder()).append("DECODE(").append(column).toString();
        for (Iterator e = valueMap.keySet().iterator(); e.hasNext();) {
            String actualValue = (String) e.next();
            String displayValue = (String) valueMap.get(actualValue);
            expr = (new StringBuilder()).append(expr).append(", '").append(actualValue).append("', '").append(displayValue).append("'").toString();
        }

        expr = (new StringBuilder()).append(expr).append(", ").append(column).append(")").toString();
        return expr;
    }

    @Override
    public boolean supportsSQLLimit() {
        return databaseSupportsSQLLimit || driverSupportsSQLLimit;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#isSupportsNativeReplace()
     */
    @Override
    public boolean isSupportsNativeReplace() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#sqlOutTransform(java.lang.String, java.lang.String, java.lang.String)
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

    @Override
    public String sqlInTransform(Object value, Tfield field) {
    	if(field!=null&&(field.getType()==Efield.DATE||field.getType()==Efield.DATETIME)){
    		String dateTime=null;
    		String format=null;
    		if(field.getType()==Efield.DATE)
    			format=SQLConfigManager.defaultDateFormat;
    		else
    			format = SQLConfigManager.defaultDateTimeFormat;
    		if(value instanceof Date){
    			 long timeStamp = ((Date) value).getTime();
    	             dateTime = (new Timestamp(timeStamp)).toString();
    	            int periodIndex;
    	            if ((periodIndex = dateTime.lastIndexOf(".")) != -1)
    	                dateTime = dateTime.substring(0, periodIndex);
    		}else{
    			dateTime=value.toString();
    		}
    		return (new StringBuilder()).append("TO_DATE(").append(escapeValue(dateTime)).append(",'").append(format).append("')").toString();
    	}else if (value instanceof Boolean || DataTools.typeIsBoolean(value.toString())) {
            return value.equals(Boolean.TRUE) || value.equals("true") ? "'1'" : "'0'";
        } else {
            return super.sqlInTransform(value, field);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List, java.lang.String)
     */
    @Override
    public String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns, String orderClause) throws SlxException {
        throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, "Not supported");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List)
     */
    @Override
    public String limitQuery(String query, long startRow, long totalRows, List<String> outputColumns) throws SlxException {
    	StringBuilder out = new StringBuilder();
        if (DataUtil.isNotNullAndEmpty(outputColumns)) {
            for (int i = 0; i < outputColumns.size(); i++) {
            	out.append( outputColumns.get(i));
                if (i < outputColumns.size() - 1)
                	out.append(", ");
            }

        }
        query = (new StringBuilder()).append("SELECT ").append(out.length()==0 ? "*" : out.toString()).append(" FROM (SELECT /*+ FIRST_ROWS(").append(totalRows).append(
            ") */ a.*, rownum myrownum FROM ").append("(").append(query).append(") a where rownum <=").append(startRow + totalRows).append(")").append(
            " WHERE myrownum > ").append(startRow).toString();
        return query;
    }

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
