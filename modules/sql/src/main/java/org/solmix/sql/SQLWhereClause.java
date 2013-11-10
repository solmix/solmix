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

import static org.solmix.commons.util.DataUtil.booleanValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.util.DataTools;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-24
 */
@SuppressWarnings("unchecked")
public class SQLWhereClause
{

    private boolean qualifyColumnNames;

    private boolean doFilter;

    private String filterStyle;

    private Object whereStructure;

    private final List<SQLDataSource> dataSources;

    private Map field2ColumnMap;

    private boolean isAdvanced;

    private boolean strictSQLFiltering;

    private List<String> customCriteriaFields;

    private List<String> relatedCriterias;

    private List<String> excludeCriteriaFields;

    private boolean keepUndeclaredFields;

    private Map column2TableMap;

    private static final Logger log = LoggerFactory.getLogger(SQLWhereClause.class.getName());

    /**
     * @return the relatedCriterias
     */
    public List<String> getRelatedCriterias() {
        return relatedCriterias;
    }

    /**
     * @param relatedCriterias the relatedCriterias to set
     */
    public void setRelatedCriterias(List<String> relatedCriterias) {
        this.relatedCriterias = relatedCriterias;
    }

    public List<String> getCustomCriteriaFields() {
        return customCriteriaFields;
    }

    public void setCustomCriteriaFields(List<String> customCriteriaFields) {
        this.customCriteriaFields = customCriteriaFields;
    }

    public List<String> getExcludeCriteriaFields() {
        return excludeCriteriaFields;
    }

    public void setExcludeCriteriaFields(List<String> excludeCriteriaFields) {
        this.excludeCriteriaFields = excludeCriteriaFields;
    }

    public SQLWhereClause(boolean qualifyColumnNames, Object whereStructure, SQLDataSource ds) throws SlxException
    {
        this(whereStructure, ds);
        this.qualifyColumnNames = qualifyColumnNames;
    }

    public SQLWhereClause(Object whereStructure, SQLDataSource ds) throws SlxException
    {
        this(whereStructure, DataUtil.makeList(ds));
    }

    /**
     * @param qualifyColumnNames
     * @param req
     * @param dataSources
     * @param isFilter
     * @param textMatchStyle
     * @throws SlxException
     */
    @SuppressWarnings("rawtypes")
    public SQLWhereClause(boolean qualifyColumnNames, DSRequest req, List dataSources, boolean isFilter, String filterStyle) throws SlxException
    {
        this(req.getContext().getCriteria(), dataSources, isFilter, filterStyle);
        this.qualifyColumnNames = qualifyColumnNames;
        Eoperation __opT = req.getContext().getOperationType();
        boolean primaryKeysOnly = !req.getContext().is_allowMultiUpdate()
            && (DataTools.isRemove(__opT) || DataTools.isUpdate(__opT) || DataTools.isReplace(__opT));
        if (dataSources.size() > 1)
            column2TableMap = SQLDataSource.getColumn2TableMap(dataSources, primaryKeysOnly);
        field2ColumnMap = SQLDataSource.getField2ColumnMap(dataSources, primaryKeysOnly);
        if (primaryKeysOnly && (whereStructure instanceof Map)) {
            Map m = new HashMap((Map) whereStructure);
            whereStructure = DataUtil.subsetMap((Map) whereStructure, new ArrayList(field2ColumnMap.keySet()));
            if (((Map) whereStructure).isEmpty()) {
                if(log.isDebugEnabled())
                log.debug((new StringBuilder()).append("primaryKeysOnly forced removal of all criteria.  criteria was: ").append(
                    DataTools.prettyPrint(m)).append(", primaryKeys are: ").append(DataTools.prettyPrint(field2ColumnMap.keySet())).toString());
                whereStructure = null;
            }
        }
        List constraints = (List) req.getContext().getConstraints();
        if (constraints != null)
            field2ColumnMap = DataUtil.subsetMap(field2ColumnMap, constraints);
    }

    /**
     * @param rawCriteria
     * @param dataSources
     * @param isFilter
     * @param filterStyle
     * @throws SlxException
     */
    public SQLWhereClause(Object rawCriteria, List dataSources, boolean isFilter, String filterStyle) throws SlxException
    {
        this(rawCriteria, dataSources);
        this.doFilter = isFilter;
        this.filterStyle = filterStyle;
    }

    /**
     * @param rawCriteria
     * @param dataSources
     * @throws SlxException
     */
    @SuppressWarnings("rawtypes")
    public SQLWhereClause(Object rawCriteria, List<SQLDataSource> dataSources) throws SlxException
    {
        this.doFilter = false;
        this.whereStructure = rawCriteria;
        this.dataSources = dataSources;
        column2TableMap = new HashMap();
        DataSource ds = null;
        if (dataSources != null && dataSources.size() > 0)
            ds = dataSources.get(0);
        field2ColumnMap = SQLDataSource.getField2ColumnMap(dataSources);
        if (whereStructure == null)
            return;
        if (whereStructure instanceof String) {
            if (((String) whereStructure).length() != 0)
                whereStructure = "'1'='1'";
        } else if (whereStructure instanceof Map) {

            if (ds != null && ds.isAdvancedCriteria((Map) whereStructure)) {
                isAdvanced = true;
                Object strictMode = ((Map) whereStructure).get("strictSQLFiltering");
                if (strictMode != null
                    && ((strictMode instanceof Boolean) && strictMode.equals(Boolean.TRUE) || (strictMode instanceof String)
                        && strictMode.equals("true")))
                    strictSQLFiltering = true;
            }

        } else if (whereStructure instanceof List) {
            List clauses = (List) whereStructure;
            if (clauses.isEmpty()) {
                this.whereStructure = null;
                return;
            } else if (clauses.size() == 1 && ((Map) clauses.get(0)).size() == 0)
                this.whereStructure = null;
        } else {
            throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, new StringBuilder().append("Data type: '").append(
                whereStructure.getClass().getName()).append("' is not supported").toString());
        }
    }

    public void setField2ColumnMap(Map field2ColumnMap, boolean keepUndeclaredFields) {
        this.field2ColumnMap = field2ColumnMap;
        this.keepUndeclaredFields = keepUndeclaredFields;
    }

    public boolean isEmpty() {
        return whereStructure == null;
    }

    public int size() {
        return !isEmpty() ? 1 : 0;
    }

    public String toString(SQLDriver driver) throws SlxException {
        String __return;
        if (isEmpty()) {
            log.trace("no data; returning empty string");
            __return = "('1'='1')";
        } else {
            __return = getOutput(whereStructure, driver);
        }
        StringBuffer sbf = new StringBuffer();
        if (this.relatedCriterias != null)
            for (String criteria : relatedCriterias) {
                sbf.append(" ").append(criteria).append(" AND ");
            }

        __return = sbf.toString() + __return;
        return __return;
    }

    /**
     * @param whereStructure2
     * @param driver
     * @return
     * @throws SlxException
     */
    @SuppressWarnings("rawtypes")
    private String getOutput(Object condition, SQLDriver driver) throws SlxException {
        if (condition instanceof String)
            return (new StringBuilder()).append("(").append(condition).append(")").toString();
        if (!isAdvanced) {
            if (condition instanceof java.util.Map.Entry) {
                Object value = ((Entry) condition).getValue();
                if (value instanceof List) {
                    List values = (List) value;
                    List __exp = new ArrayList();
                    for (Iterator i = values.iterator(); i.hasNext();) {
                        __exp.add(DataUtil.buildMap(((Entry) condition).getKey(), i.next()));
                    }
                    return buildCompoundExpression(__exp.iterator(), "OR", driver);
                } else {
                    return buildExpression(((Entry) condition).getKey().toString(), value, driver);
                }
            } else if (condition instanceof Map) {
                return buildCompoundExpression(((Map) condition).entrySet().iterator(), "AND", driver);
            } else if (condition instanceof List) {
                return buildCompoundExpression(((List) condition).iterator(), "OR", driver);
            } else {
                throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, DataUtil.getNoSupportString(condition));
            }
        } else {

            if (condition instanceof Map) {
                String operator = (String) ((Map) condition).get("operator");
                Object criteriaObject;
                List criteria;
                if ("and".equals(operator) || "or".equals(operator) || "not".equals(operator)) {
                    criteriaObject = ((Map) condition).get("criteria");
                    if (!(criteriaObject instanceof List)) {
                        log.debug("Subcriteria of AdvancedCriteria not an instance of List - using empty ArrayList");
                        criteriaObject = new ArrayList();
                    }
                    criteria = (List) criteriaObject;
                    return buildCompoundExpression(criteria.iterator(), operator, driver);
                } else {
                    String fieldName = (String) ((Map) condition).get("fieldName");
                    Object value = ((Map) condition).get("value");
                    Object start = ((Map) condition).get("start");
                    Object end = ((Map) condition).get("end");
                    return buildAdvancedExpression(fieldName, operator, value, start, end, driver);
                }
            } else {
                throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, DataUtil.getNoSupportString(condition));
            }
        }
    }

    /**
     * @param fieldName
     * @param operator
     * @param value
     * @param start
     * @param end
     * @param driver
     * @return
     */
    private String buildAdvancedExpression(String fieldName, String operator, Object value, Object start, Object end, SQLDriver driver) {
        String _overrideTableName = null;
        boolean skipCustomCheck = false;
        // check customer criteria fields.
        if (this.customCriteriaFields != null) {
            for (String cfield : customCriteriaFields) {
                if (cfield.equals(fieldName)) {
                    skipCustomCheck = true;
                    break;
                }
            }
        }
        Tfield __f = null;
        Efield __type = null;
        if (this.dataSources != null) {
            for (SQLDataSource ds : dataSources) {
                __f = ds.getContext().getField(fieldName);
                if (__f != null) {
                    __type = __f.getType();
                    if (!skipCustomCheck && (__f.isCustomSQL() || __f.isCanFilter()))
                        return null;
                    if (__f.getTableName() != null)
                        _overrideTableName = __f.getTableName();
                }
            }
        }
        if (__f == null || !driver.fieldIsSearchable(__f))
            return null;
        // check exclude criteria fields
        if (!skipCustomCheck && this.excludeCriteriaFields != null) {
            for (String cfield : excludeCriteriaFields) {
                if (cfield.equals(fieldName)) {
                    return null;
                }
            }
        }
        String _columnName = getColumnNameForField(fieldName);
        if (_columnName == null && __f != null && __f.getCustomSelectExpression() == null) {
            log.warn("no column name for field named: [" + fieldName + "] , field2ColumnMap: " + DataTools.prettyPrint(field2ColumnMap));
            return "'1'='1'";
        }

        String escapedColumnName = driver.escapeColumnName(_columnName);
        String _tableName = _overrideTableName;
        if (_tableName == null) {
            _tableName = (String) column2TableMap.get(_columnName);
        }
        if (_tableName == null && qualifyColumnNames && (dataSources.get(0)).getTable() != null) {
            _tableName = (dataSources.get(0)).getTable().getName();
        }
        if (!qualifyColumnNames && _overrideTableName != null)
            escapedColumnName = (new StringBuilder()).append(_overrideTableName).append(".").append(escapedColumnName).toString();
        else if (_tableName != null)
            escapedColumnName = (new StringBuilder()).append(_tableName).append(".").append(escapedColumnName).toString();
        String columnOrExpression = escapedColumnName;
        if (__f.getCustomSelectExpression() != null)
            columnOrExpression = __f.getCustomSelectExpression();
        String columnType = __type.value();
        if (operator.equals("contains") || operator.equals("iContains") || operator.equals("startsWith") || operator.equals("iStartsWith")
            || operator.equals("endsWith") || operator.equals("iEndsWith") || operator.equals("iEquals"))
            return stringComparison(columnOrExpression, columnType, operator, value, false, driver);
        if (operator.equals("notContains") || operator.equals("iNotContains") || operator.equals("notStartsWith")
            || operator.equals("iNotStartsWith") || operator.equals("notEndsWith") || operator.equals("iNotEndsWith") || operator.equals("iNotEqual"))
            return stringComparison(columnOrExpression, columnType, operator, value, true, driver);
        if (operator.equals("equals") || operator.equals("notEqual") || operator.equals("greaterThan") || operator.equals("greaterOrEqual")
            || operator.equals("lessThan") || operator.equals("lessOrEqual") || operator.equals("between") || operator.equals("betweenInclusive"))
            return valueComparison(columnOrExpression, columnType, operator, value, start, end, driver, fieldName);
        if (operator.equals("isNull") || operator.equals("notNull"))
            return nullComparison(columnOrExpression, operator, driver);
        if (operator.equals("inSet") || operator.equals("notInSet"))
            return setComparison(columnOrExpression, columnType, operator, value, operator.equals("notInSet"), driver, fieldName);
        if (operator.equals("equalsField") || operator.equals("notEqualField")) {
            String otherColumnType = "";
            if (value != null)
                otherColumnType = getFieldType(value.toString());
            return fieldComparison(columnOrExpression, columnType, operator, (String) value, otherColumnType, operator.equals("notEqualField"),
                driver);
        }
        if (operator.equals("regexp") || operator.equals("iregexp")) {
            log.debug("'regexp' and 'iregexp' conditions are ignored on the server");
            return "('1'='1')";
        } else {
            log.warn((new StringBuilder()).append("Found unknown operator ").append(operator).toString());
            return "('1'='1')";
        }
    }

    private String getFieldType(String fieldName) {
        Tfield field = getField(fieldName);
        return field != null ? field.getType().value() : "text";
    }

    private Tfield getField(String fieldName) {
        if (dataSources == null)
            return null;
        Iterator i = dataSources.iterator();
        Tfield field = null;
        do {
            if (!i.hasNext())
                break;
            SQLDataSource ds = (SQLDataSource) i.next();
            field = ds.getContext().getField(fieldName);
        } while (field == null);
        return field;
    }

    /**
     * @param iterator
     * @param string
     * @param driver
     * @return
     * @throws SlxException
     */
    private String buildCompoundExpression(Iterator conditions, String operator, SQLDriver driver) throws SlxException {
        if (!conditions.hasNext()) {
            log.info("empty condition");
            return "('1'='1')";
        }
        StringBuffer clause = new StringBuffer();
        while (conditions.hasNext()) {
            String subClause = getOutput(conditions.next(), driver);
            if (subClause != null && subClause.length() != 0) {
                if (clause.length() != 0) {
                    if (!operator.equals("not"))
                        clause.append((new StringBuilder()).append(" ").append(operator.toUpperCase()).append(" ").toString());
                    else
                        clause.append(" OR ");
                }
                clause.append(subClause);
            }
        }
        if (DataUtil.isNullOrEmpty(clause))
            return null;
        if (operator.equals("not"))
            return (new StringBuilder()).append("NOT(").append(clause.toString()).append(")").toString();
        else
            return (new StringBuilder()).append("(").append(clause.toString()).append(")").toString();
    }

    /**
     * @param string
     * @param value
     * @param driver
     * @return
     */
    private String buildExpression(String fieldName, Object value, SQLDriver driver) {
        String _overrideTableName = null;
        boolean skipCustomCheck = false;
        // check customer criteria fields.
        if (this.customCriteriaFields != null) {
            for (String cfield : customCriteriaFields) {
                if (cfield.equals(fieldName)) {
                    skipCustomCheck = true;
                    break;
                }
            }
        }
        Tfield __f = null;
        Efield __type = null;
        if (this.dataSources != null) {
            for (SQLDataSource ds : dataSources) {
                __f = ds.getContext().getField(fieldName);
                if (__f != null) {
                    __type = __f.getType();
                    if (!skipCustomCheck && (booleanValue(__f.isCustomSQL()) || booleanValue(__f.isCanFilter())))
                        return null;
                    if (__f.getTableName() != null)
                        _overrideTableName = __f.getTableName();
                }
            }
        }
        if (__f == null || !driver.fieldIsSearchable(__f))
            return null;
        // check exclude criteria fields
        if (!skipCustomCheck && this.excludeCriteriaFields != null) {
            for (String cfield : excludeCriteriaFields) {
                if (cfield.equals(fieldName)) {
                    return null;
                }
            }
        }
        String _columnName = getColumnNameForField(fieldName);
        if (_columnName == null && __f != null && __f.getCustomSelectExpression() == null) {
            log.warn("no column name for field named: [" + fieldName + "] , field2ColumnMap: " + DataTools.prettyPrint(field2ColumnMap));
            return "'1'='1'";
        }

        String escapedColumnName = driver.escapeColumnName(_columnName);
        String _tableName = _overrideTableName;
        if (_tableName == null) {
            _tableName = (String) column2TableMap.get(_columnName);
        }
        if (_tableName == null && qualifyColumnNames && (dataSources.get(0)).getTable() != null) {
            _tableName = (dataSources.get(0)).getTable().getName();
        }
        if (!qualifyColumnNames && _overrideTableName != null)
            escapedColumnName = (new StringBuilder()).append(_overrideTableName).append(".").append(escapedColumnName).toString();
        else if (_tableName != null)
            escapedColumnName = (new StringBuilder()).append(_tableName).append(".").append(escapedColumnName).toString();
        String columnOrExpression = escapedColumnName;
        if (__f.getCustomSelectExpression() != null)
            columnOrExpression = __f.getCustomSelectExpression();
        if (value == null)
            return (new StringBuilder()).append(columnOrExpression).append(" IS NULL").toString();
        if (__type.equals(Efield.TEXT) || __type.equals("string"))
            if (!doFilter)
                return (new StringBuilder()).append(columnOrExpression).append("=").append(driver.sqlInTransform(value, __f)).toString();
            else
                return substringFilter(columnOrExpression, value, driver);
        else if ((__type.equals(Efield.INTEGER) || __type.equals("integer") || __type.equals(Efield.SEQUENCE) || __type.equals(Efield.FLOAT))
            && (value instanceof String)) {
            if (!doFilter) {
                try {
                    if (__type.equals(Efield.FLOAT))
                        value = (new BigDecimal((String) value)).toString();
                    else
                        value = (new BigInteger((String) value)).toString();
                } catch (Exception e) {
                    log.warn((new StringBuilder()).append("Got non-numeric value '").append(value).append("' for numeric column '").append(
                        _columnName).append("', creating literal false expression: ").append(e).toString());
                    return "'0'='1'";
                }
                return (new StringBuilder()).append(columnOrExpression).append("=").append(value.toString()).toString();
            } else {
                return substringFilter(columnOrExpression, value, driver);
            }
        } else if (__type == Efield.INT_ENUM || __type == Efield.INTEGER || __type == Efield.FLOAT || __type == Efield.SEQUENCE) {
            return (new StringBuilder()).append(columnOrExpression).append("=").append(value.toString()).toString();
        } else {
            return (new StringBuilder()).append(columnOrExpression).append("=").append(driver.sqlInTransform(value, __f)).toString();
        }
    }

    private String getColumnNameForField(String fieldName) {
        if (field2ColumnMap == null)
            return fieldName;
        String columnName = (String) field2ColumnMap.get(fieldName);
        if (columnName != null)
            return columnName;
        if (keepUndeclaredFields)
            return fieldName;
        else
            return null;
    }

    /**
     * @param columnOrExpression
     * @param value
     * @param driver
     * @return
     */
    private String substringFilter(String lvalue, Object rvalue, SQLDriver driver) {
        if ((driver instanceof OracleDriver) || (driver instanceof SQLServerDriver) || (driver instanceof PostgresDriver))
            return (new StringBuilder()).append("LOWER(").append(lvalue).append(") LIKE ").append(
                driver.escapeValueForFilter(rvalue.toString().toLowerCase(), filterStyle)).append(driver.escapeClause()).toString();
        else
            return (new StringBuilder()).append(lvalue).append(" LIKE ").append(driver.escapeValueForFilter(rvalue.toString(), filterStyle)).toString();
    }

    public String getSQLString() throws SlxException {
        String whereClause = toString((dataSources.get(0)).getDriver());
        if (whereClause == null)
            whereClause = "('1'='1')";
        return whereClause;
    }

    private String stringComparison(String fieldName, String columnType, String operator, Object objVal, boolean negate, SQLDriver driver) {
        if (fieldName == null) {
            log.error("Found a null fieldName");
            return "('1'='1')";
        }
        if (!columnType.equals("text") && !columnType.equals("string") && !columnType.equals("integer") && !columnType.equals("float")
            && !columnType.equals("sequence") && !columnType.equals("number"))
            if (negate)
                return "('1'='1')";
            else
                return "'0'='1'";
        if (objVal == null)
            objVal = "";
        String value = objVal.toString();
        value = driver.escapeValueUnquoted(value, true);
        StringBuffer sql = new StringBuffer("(");
        if ((driver instanceof OracleDriver) || (driver instanceof SQLServerDriver) || (driver instanceof PostgresDriver)) {
            if (operator.startsWith("i"))
                sql.append((new StringBuilder()).append("LOWER(").append(fieldName).append(") LIKE ").toString());
            else
                sql.append((new StringBuilder()).append(fieldName).append(" LIKE ").toString());
        } else if (operator.startsWith("i"))
            sql.append((new StringBuilder()).append(fieldName).append(" LIKE ").toString());
        else
            sql.append((new StringBuilder()).append(fieldName).append(" LIKE BINARY ").toString());
        if (operator.startsWith("i"))
            sql.append("LOWER(");
        sql.append("'");
        if (operator.indexOf("ndsWith") != -1 || operator.indexOf("tains") != -1)
            sql.append("%");
        if (operator.startsWith("i"))
            sql.append(value.toLowerCase());
        else
            sql.append(value);
        if (operator.indexOf("rtsWith") != -1 || operator.indexOf("tains") != -1)
            sql.append("%");
        sql.append("'");
        if (operator.startsWith("i"))
            sql.append(")");
        sql.append(driver.escapeClause());
        if (!strictSQLFiltering)
            sql.append((new StringBuilder()).append(" AND ").append(fieldName).append(" IS NOT NULL").toString());
        sql.append(")");
        if (negate)
            sql.insert(0, "NOT");
        return sql.toString();
    }

    private String valueComparison(String fieldName, String columnType, String operator, Object value, Object start, Object end, SQLDriver driver,
        String realFieldName) {
        if (fieldName == null) {
            log.error("Found a null fieldName");
            return "('1'='1')";
        }
        if (("text".equals(columnType) || "string".equals(columnType))
            && ((value instanceof Date) || (start instanceof Date) || (end instanceof Date)))
            if (operator.equals("notEqual"))
                return "('1'='1')";
            else
                return "('0'='1')";
        value = castValue(value, columnType);
        start = castValue(start, columnType);
        end = castValue(end, columnType);
        if ("integer".equals(columnType) && value != null && !(value instanceof Number) || "float".equals(columnType) && value != null
            && !(value instanceof Number) || "date".equals(columnType) && value != null && !(value instanceof Date) || "integer".equals(columnType)
            && start != null && !(start instanceof Number) || "float".equals(columnType) && start != null && !(start instanceof Number)
            || "date".equals(columnType) && start != null && !(start instanceof Date) || "integer".equals(columnType) && end != null
            && !(end instanceof Number) || "float".equals(columnType) && end != null && !(end instanceof Number) || "date".equals(columnType)
            && end != null && !(end instanceof Date)) {
            if (operator.equals("notEqual"))
                if (strictSQLFiltering)
                    return (new StringBuilder()).append("(").append(fieldName).append(" = ").append(fieldName).append(")").toString();
                else
                    return "('1'='1')";
            if (operator.equals("lessThan") || operator.equals("lessOrEqual"))
                if (strictSQLFiltering)
                    return (new StringBuilder()).append("(").append(fieldName).append(" <> ").append(fieldName).append(" AND ").append(fieldName).append(
                        " IS NULL)").toString();
                else
                    return (new StringBuilder()).append("(").append(fieldName).append(" IS NULL)").toString();
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" <> ").append(fieldName).append(" AND ").append(fieldName).append(
                    " IS NULL)").toString();
            else
                return "('0'='1')";
        }
        Tfield field = null;
        DataSource firstDS = dataSources.get(0);
        if (firstDS != null)
            field = firstDS.getContext().getField(realFieldName);
        if (columnType.equals("number") || columnType.equals("integer") || columnType.equals("sequence") || columnType.equals("float")) {
            if (value != null)
                value = value.toString();
            if (start != null)
                start = start.toString();
            if (end != null)
                end = end.toString();
        } else if (columnType.equals("date")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            if (value != null)
                if (driver instanceof OracleDriver)
                    value = (new StringBuilder()).append("To_Date('").append(df.format((Date) value)).append("','yyyy-mm-dd')").toString();
                // else if (driver instanceof DB2iSeriesDriver) {
                // if (field != null && !driver.shouldUseSQLDateType(field))
                // value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("-00.00.00'")
                // .toString();
                // else
                // value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("'").toString();
                // }
                else {
                    value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("'").toString();
                }
            if (start != null)
                if (driver instanceof OracleDriver)
                    start = (new StringBuilder()).append("To_Date('").append(df.format((Date) start)).append("','yyyy-mm-dd')").toString();
                /*
                 * else if (driver instanceof DB2iSeriesDriver) { if (field != null &&
                 * !driver.shouldUseSQLDateType(field)) value = (new
                 * StringBuilder()).append("'").append(df.format((Date) value)).append("-00.00.00'") .toString(); else
                 * value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("'").toString(); }
                 */else {
                    start = (new StringBuilder()).append("'").append(df.format((Date) start)).append("'").toString();
                }
            if (end != null)
                if (driver instanceof OracleDriver)
                    end = (new StringBuilder()).append("To_Date('").append(df.format((Date) end)).append("','yyyy-mm-dd')").toString();
                /*
                 * else if (driver instanceof DB2iSeriesDriver) { if (field != null &&
                 * !driver.shouldUseSQLDateType(field)) value = (new
                 * StringBuilder()).append("'").append(df.format((Date) value)).append("-00.00.00'") .toString(); else
                 * value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("'").toString(); }
                 */else {
                    end = (new StringBuilder()).append("'").append(df.format((Date) end)).append("'").toString();
                }
        } else {
            if (value != null)
                value = (new StringBuilder()).append("'").append(driver.escapeValueUnquoted(value.toString(), false)).append("'").toString();
            if (start != null)
                start = (new StringBuilder()).append("'").append(driver.escapeValueUnquoted(start.toString(), false)).append("'").toString();
            if (end != null)
                end = (new StringBuilder()).append("'").append(driver.escapeValueUnquoted(end.toString(), false)).append("'").toString();
        }
        if (operator.equals("equals")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" = ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" = ").append(value).append(" AND ").append(fieldName).append(
                    " IS NOT NULL)").toString();
            else
                return (new StringBuilder()).append("(").append(fieldName).append(" IS NULL)").toString();
        }
        if (operator.equals("notEqual")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" <> ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" <> ").append(value).append(" OR ").append(fieldName).append(
                    " IS NULL)").toString();
            else
                return (new StringBuilder()).append("(").append(fieldName).append(" IS NOT NULL)").toString();
        }
        if (operator.equals("greaterThan")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(value).append(" AND ").append(fieldName).append(
                    " IS NOT NULL)").toString();
            else
                return "('1'='1')";
        }
        if (operator.equals("greaterOrEqual")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" >= ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" >= ").append(value).append(" AND ").append(fieldName).append(
                    " IS NOT NULL)").toString();
            else
                return "('1'='1')";
        }
        if (operator.equals("lessThan")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" < ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" < ").append(value).append(" OR ").append(fieldName).append(
                    " IS NULL)").toString();
            else
                return "('1'='1')";
        }
        if (operator.equals("lessOrEqual")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" <= ").append(value).append(")").toString();
            if (value != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" <= ").append(value).append(" OR ").append(fieldName).append(
                    " IS NULL)").toString();
            else
                return "('1'='1')";
        }
        if (operator.equals("between")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(start).append(" AND ").append(fieldName).append(" < ").append(
                    end).append(")").toString();
            if (start == null && end != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" < ").append(end).append(" OR ").append(fieldName).append(
                    " IS NULL)").toString();
            if (start != null && end == null)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(start).append(")").toString();
            if (start != null && end != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(start).append(" AND ").append(fieldName).append(" < ").append(
                    end).append(" AND ").append(fieldName).append(" IS NOT NULL)").toString();
            else
                return "('1'='1')";
        }
        if (operator.equals("betweenInclusive")) {
            if (strictSQLFiltering)
                return (new StringBuilder()).append("(").append(fieldName).append(" >= ").append(start).append(" AND ").append(fieldName).append(
                    " <= ").append(end).append(")").toString();
            if (start == null && end != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" <= ").append(end).append(" OR ").append(fieldName).append(
                    " IS NULL)").toString();
            if (start != null && end == null)
                return (new StringBuilder()).append("(").append(fieldName).append(" > ").append(start).append(")").toString();
            if (start != null && end != null)
                return (new StringBuilder()).append("(").append(fieldName).append(" >= ").append(start).append(" AND ").append(fieldName).append(
                    " <= ").append(end).append(" AND ").append(fieldName).append(" IS NOT NULL)").toString();
            else
                return "('1'='1')";
        } else {
            return "('1'='1')";
        }
    }

    private String nullComparison(String fieldName, String operator, SQLDriver driver) {
        if (fieldName == null) {
            log.error("Found a null fieldName");
            return "('1'='1')";
        }
        if (operator.equals("isNull"))
            return (new StringBuilder()).append("(").append(fieldName).append(" IS NULL)").toString();
        else
            return (new StringBuilder()).append("(").append(fieldName).append(" IS NOT NULL)").toString();
    }

    @SuppressWarnings("rawtypes")
    private String setComparison(String fieldName, String columnType, String operator, Object value, boolean negate, SQLDriver driver,
        String realFieldName) {
        if (fieldName == null) {
            log.error("Found a null fieldName");
            return "('1'='1')";
        }
        Tfield field = null;
        DataSource firstDS = dataSources.get(0);
        if (firstDS != null)
            field = firstDS.getContext().getField(realFieldName);
        if (value == null)
            value = new ArrayList();
        if (!(value instanceof List)) {
            Object val = value;
            value = new ArrayList();
            ((List) value).add(val);
        }
        int size = ((List) value).size();
        for (int i = 0; i < size; i++) {
            Object work = ((List) value).get(i);
            work = castValue(work, columnType);
            if ("integer".equals(columnType) && work != null && !(work instanceof Integer) || "float".equals(columnType) && work != null
                && !(work instanceof Double) || "date".equals(columnType) && work != null && !(work instanceof Date)) {
                ((List) value).set(i, null);
                continue;
            }
            if (columnType.equals("number") || columnType.equals("integer") || columnType.equals("sequence") || columnType.equals("float"))
                work = work.toString();
            else if (columnType.equals("date")) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                if (driver instanceof OracleDriver)
                    work = (new StringBuilder()).append("To_Date('").append(df.format((Date) work)).append("','yyyy-mm-dd')").toString();
                /*
                 * else if (driver instanceof DB2iSeriesDriver) { if (field != null &&
                 * !driver.shouldUseSQLDateType(field)) value = (new
                 * StringBuilder()).append("'").append(df.format((Date) value)).append("-00.00.00'") .toString(); else
                 * value = (new StringBuilder()).append("'").append(df.format((Date) value)).append("'").toString(); }
                 */
                else {
                    work = (new StringBuilder()).append("'").append(df.format((Date) work)).append("'").toString();
                }
            } else {
                work = (new StringBuilder()).append("'").append(driver.escapeValueUnquoted(work.toString(), false)).append("'").toString();
            }
            ((List) value).set(i, work);
        }

        for (; ((List) value).contains(null); ((List) value).remove(null))
            ;
        StringBuffer sql = new StringBuffer();
        if (((List) value).isEmpty()) {
            sql.append("('0'='1')");
        } else {
            sql.append((new StringBuilder()).append("((").append(fieldName).append(" IN (").toString());
            int setSize = driver.getMaximumSetSize();
            if (setSize == 0)
                setSize = ((List) value).size();
            int j = 0;
            for (int i = 0; i < ((List) value).size(); i++) {
                if (j >= setSize) {
                    sql.append((new StringBuilder()).append(") OR ").append(fieldName).append(" IN (").toString());
                    j = 0;
                }
                if (i > 0 && j > 0)
                    sql.append(", ");
                sql.append(((List) value).get(i));
                j++;
            }

            sql.append((new StringBuilder()).append(")) AND ").append(fieldName).append(" IS NOT NULL)").toString());
        }
        if (negate)
            sql.insert(0, "NOT");
        return sql.toString();
    }

    private String fieldComparison(String fieldName, String columnType, String operator, String otherFieldName, String otherColumnType,
        boolean negate, SQLDriver driver) {
        if (fieldName == null) {
            log.error("Found a null fieldName");
            return "('1'='1')";
        }
        if (otherFieldName == null) {
            log.error("Found a null 'other' fieldName in a field comparison");
            return "('1'='1')";
        }
        String columnName = getColumnNameForField(otherFieldName);
        if (columnName == null) {
            log.warn((new StringBuilder()).append("no column name for field named: ").append(otherFieldName).toString());
            return "('1'='1')";
        }
        String tableName = (String) column2TableMap.get(columnName);
        otherFieldName = driver.escapeColumnName(columnName);
        if (tableName != null)
            otherFieldName = (new StringBuilder()).append(tableName).append(".").append(otherFieldName).toString();
        if (!columnType.equals(otherColumnType))
            if (negate)
                return "('1'='1')";
            else
                return "('0'='1')";
        StringBuffer sql = new StringBuffer();
        sql.append((new StringBuilder()).append("((").append(fieldName).append(" IS NULL AND ").append(otherFieldName).append(" IS NULL)").toString());
        sql.append((new StringBuilder()).append(" OR (").append(fieldName).append(" = ").append(otherFieldName).append(" AND ").toString());
        sql.append((new StringBuilder()).append(fieldName).append(" IS NOT NULL AND ").append(otherFieldName).append(" IS NOT NULL))").toString());
        if (negate)
            sql.insert(0, "NOT");
        return sql.toString();
    }

    private Object castValue(Object value, String columnType) {
        if (value instanceof String)
            try {
                if ("integer".equals(columnType)) {
                    Integer temp = new Integer((String) value);
                    value = temp;
                }
                if ("float".equals(columnType)) {
                    Double temp = new Double((String) value);
                    value = temp;
                }
            } catch (Exception e) {
                log.warn((new StringBuilder()).append("String '").append(value).append("' was passed as filter criteria for a ").append(
                    "numeric field. We could not parse it.").toString());
            }
        if (value != null && !(value instanceof String) && (columnType.equals("text") || columnType.equals("string")))
            value = value.toString();
        if (value instanceof Long)
            value = new Integer(((Long) value).intValue());
        return value;
    }
}
