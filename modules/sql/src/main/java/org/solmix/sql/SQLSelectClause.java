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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.commons.util.DataUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-22
 */
@SuppressWarnings("unchecked")
public class SQLSelectClause
{

    private static Logger log = LoggerFactory.getLogger(SQLSelectClause.class.getName());

    private final List<SQLDataSource> dataSources;

    private Map remapTable;

    private Map<String, String> column2TableMap;

    /**
     * Qualify column to specific Table
     */
    private boolean qualifyColumnNames;

    private List<String> customValueFields;

    public List<String> getCustomValueFields() {
        return customValueFields;
    }

    public void setCustomValueFields(List<String> customValueFields) {
        this.customValueFields = customValueFields;
    }

    public SQLSelectClause(SQLDataSource dataSource)
    {
        this(DataUtil.makeList(dataSource));
    }

    public SQLSelectClause(List<SQLDataSource> dataSources)
    {
        column2TableMap = new HashMap<String, String>();
        qualifyColumnNames = true;
        customValueFields = null;
        this.dataSources = dataSources;
        remapTable = SQLDataSource.getField2ColumnMap(dataSources);
        if (dataSources.size() > 1)
            column2TableMap = SQLDataSource.getColumn2TableMap(dataSources);
    }

    public SQLSelectClause(DSRequest request, SQLDataSource ds, boolean qualifyColumnNames)
    {
        this(request, DataUtil.makeList(ds), qualifyColumnNames);
    }

    public SQLSelectClause(DSRequest request, List<SQLDataSource> dataSources, boolean qualifyColumnNames)
    {
        this(dataSources);
        List<String> outputColumns = computeOutputColumns(request);
        if (outputColumns != null)
            remapTable = DataUtil.subsetMap(remapTable, outputColumns);
        this.qualifyColumnNames = qualifyColumnNames;
    }

    /**
     * @param request
     * @return
     */
    protected List<String> computeOutputColumns(DSRequest request) {
        List<String> __return = null;
        if (request == null)
            return null;
        Eoperation __type = request.getContext().getOperationType();
        String __id = request.getContext().getOperationId();
        SQLDataSource ds = dataSources.get(0);
        ToperationBinding __bind = ds.getContext().getOperationBinding(__type, __id);
        if (__bind != null && __bind.getOutputs() != null) {
            String __out = __bind.getOutputs();
            String __outArray[] = __out.split(",");
            __return = new ArrayList<String>();
            for (String str : __outArray) {
                __return.add(str);
            }
        }
        // request's outputs
        List<String> __RO = request.getContext().getOutputs();
        if (__RO != null) {
            if (__return != null) {
                if (__return.containsAll(__RO))
                    __return = __RO;
            } else if (ds.getContext().getFieldNames().containsAll(__RO))
                __return = __RO;

        }
        return __return;
    }

    public String getSQLString() throws SlxException {
        return getSQLString((dataSources.get(0)).getDriver());
    }

    public String getSQLString(SQLDriver driver) throws SlxException {

        if (remapTable == null || remapTable.size() == 0) {
            log.debug("SQLSelectClause is null or zero size, remap and conversions won't work!");
            return "*";
        }
        StringBuffer __result = new StringBuffer();
        Iterator e = remapTable.keySet().iterator();
        while (e.hasNext()) {
            boolean qualifyColumnNames = this.qualifyColumnNames;
            boolean _skipCusSQLCheck = false;
            String _rsName = (String) e.next();
            String _columnName = (String) remapTable.get(_rsName);
            String _tableName = qualifyColumnNames ? (String) column2TableMap.get(_columnName) : null;
            if (_tableName == null && qualifyColumnNames) {
                DataSource firstDS = dataSources.get(0);
                if (firstDS instanceof SQLDataSource)
                    _tableName = ((SQLDataSource) firstDS).getTable().getName();
            }
            if (customValueFields != null) {
                for (String field : customValueFields) {
                    if (field.equalsIgnoreCase(_rsName)) {
                        _skipCusSQLCheck = true;
                        break;
                    }
                }

            } else {
                _skipCusSQLCheck = true;
            }
            Tfield __f = null;
            boolean _exclude = false;
            for (SQLDataSource ds : dataSources) {
                __f = ds.getContext().getField(_rsName);
                if (__f != null) {
                    if (!_skipCusSQLCheck)
                        _exclude = true;
                    else if (__f.getTableName() != null) {
                        _tableName = __f.getTableName();
                        qualifyColumnNames = true;
                    }
                    break;
                }
            }// END datasource.
            if (!_exclude) {
                if (__result.length() != 0)
                    __result.append(", ");
                if (__f != null && __f.getType().value().equals("relatedCount"))
                    __result.append(aggregationSubSelect(driver, _columnName, _rsName, _tableName, __f));
                else if (__f != null && __f.getCustomSelectExpression() != null) {
                    __result.append(customSQLExpression(driver, _columnName, _rsName, _tableName, __f, qualifyColumnNames));
                } else
                    __result.append(driver.sqlOutTransform(_columnName, _rsName, _tableName));
            }
        }
        return __result.toString();
    }

    /**
     * @param driver
     * @param columnName
     * @param rsName
     * @param tableName
     * @param f
     * @return
     */
    private String aggregationSubSelect(SQLDriver driver, String columnName, String rsName, String tableName, Tfield f) {
        // TODO Auto-generated method stub
        // String relatedTable = field.getProperty("relatedTable");
        // String relatedColumn = field.getProperty("relatedColumn");
        // String localField = field.getProperty("localField");
        // String subselect = " (SELECT ";
        // subselect = (new StringBuilder()).append(subselect).append("COUNT").toString();
        // subselect = (new StringBuilder()).append(subselect).append("(").toString();
        // subselect = (new StringBuilder()).append(subselect).append("*").toString();
        // subselect = (new StringBuilder()).append(subselect).append(") FROM ").toString();
        // subselect = (new StringBuilder()).append(subselect).append(field.getProperty("relatedTable")).toString();
        // subselect = (new StringBuilder()).append(subselect).append(" WHERE ").toString();
        // subselect = (new StringBuilder()).append(subselect).append(driver.sqlOutTransform(localField, localField,
        // tableName)).toString();
        // subselect = (new StringBuilder()).append(subselect).append(" = ").toString();
        // subselect = (new StringBuilder()).append(subselect).append(driver.sqlOutTransform(relatedColumn,
        // relatedColumn,
        // relatedTable)).toString();
        // subselect = (new StringBuilder()).append(subselect).append(") AS ").toString();
        // subselect = (new StringBuilder()).append(subselect).append(field.getName()).toString();
        // return subselect;
        return null;
    }

    /**
     * @param driver
     * @param columnName
     * @param rsName
     * @param tableName
     * @param f
     * @return
     */
    private String customSQLExpression(SQLDriver driver, String columnName, String rsName, String tableName, Tfield field, boolean qualifyColumnNames) {
        String custom = field.getCustomSelectExpression();
        if (DataUtil.isNullOrEmpty(custom) && custom.substring(0, 1).equals("$")) {
            if (custom.substring(0, custom.indexOf(":")).equalsIgnoreCase("$value"))
                custom = custom.substring(custom.indexOf(":") + 1);
        } else {
            if (qualifyColumnNames)
                custom = tableName + "." + custom;
        }
        custom = custom + " AS " + field.getName();
        return custom;
    }
}
