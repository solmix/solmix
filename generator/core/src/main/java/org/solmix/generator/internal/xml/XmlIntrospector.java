package org.solmix.generator.internal.xml;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.generator.api.FullyQualifiedTable;
import org.solmix.generator.api.IntrospectedColumn;
import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.JavaTypeResolver;
import org.solmix.generator.api.java.FullyQualifiedJavaType;
import org.solmix.generator.api.java.JavaReservedWords;
import org.solmix.generator.config.ColumnInfo;
import org.solmix.generator.config.DomainInfo;
import org.solmix.generator.config.GeneratedKey;
import org.solmix.generator.config.PropertyRegistry;
import org.solmix.generator.config.TableInfo;
import org.solmix.generator.internal.ObjectFactory;
import org.solmix.generator.internal.db.ActualTableName;
import org.solmix.generator.internal.db.SqlReservedWords;
import org.solmix.generator.util.JavaBeansUtil;


public class XmlIntrospector
{

    private JavaTypeResolver javaTypeResolver;

    private List<String> warnings;

    private DomainInfo domain;

    private Logger logger;
    
    public XmlIntrospector(DomainInfo domain, JavaTypeResolver javaTypeResolver, List<String> warnings)
    {
        super();
        this.domain = domain;
        this.javaTypeResolver = javaTypeResolver;
        this.warnings = warnings;
        logger = LoggerFactory.getLogger(getClass());
    }

    public List<IntrospectedTable> introspectTables(TableInfo tc) {
        Map<ActualTableName, List<IntrospectedColumn>> columns = getColumns(tc);
        removeIgnoredColumns(tc, columns);
        calculateExtraColumnInformation(tc, columns);
        calculateIdentityColumns(tc, columns);

        List<IntrospectedTable> introspectedTables = calculateIntrospectedTables(tc, columns);

        // now introspectedTables has all the columns from all the
        // tables in the configuration. Do some validation...

        Iterator<IntrospectedTable> iter = introspectedTables.iterator();
        while (iter.hasNext()) {
            IntrospectedTable introspectedTable = iter.next();

            if (!introspectedTable.hasAnyColumns()) {
                // add warning that the table has no columns, remove from the
                // list
                String warning = getString("Warning.1", introspectedTable.getFullyQualifiedTable().toString());
                warnings.add(warning);
                iter.remove();
            } else if (!introspectedTable.hasPrimaryKeyColumns() && !introspectedTable.hasBaseColumns()) {
                // add warning that the table has only BLOB columns, remove from
                // the list
                String warning = getString("Warning.18", introspectedTable.getFullyQualifiedTable().toString());
                warnings.add(warning);
                iter.remove();
            } else {
                // now make sure that all columns called out in the
                // configuration
                // actually exist
                reportIntrospectionWarnings(introspectedTable, tc, introspectedTable.getFullyQualifiedTable());
            }
        }

        return introspectedTables;
    }
    
    private List<IntrospectedTable> calculateIntrospectedTables(TableInfo tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        boolean delimitIdentifiers = tc.isDelimitIdentifiers() || stringContainsSpace(tc.getCatalog()) || stringContainsSpace(tc.getSchema())
            || stringContainsSpace(tc.getTableName());

        List<IntrospectedTable> answer = new ArrayList<IntrospectedTable>();

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
            ActualTableName atn = entry.getKey();

            // we only use the returned catalog and schema if something was
            // actually
            // specified on the table configuration. If something was returned
            // from the DB for these fields, but nothing was specified on the
            // table
            // configuration, then some sort of DB default is being returned
            // and we don't want that in our SQL
            FullyQualifiedTable table = new FullyQualifiedTable(stringHasValue(tc.getCatalog()) ? atn.getCatalog() : null,
                stringHasValue(tc.getSchema()) ? atn.getSchema() : null, atn.getTableName(), tc.getDomainObjectName(), tc.getAlias(),
                DataUtils.asBoolean(tc.getProperty(PropertyRegistry.TABLE_IGNORE_QUALIFIERS_AT_RUNTIME)),
                tc.getProperty(PropertyRegistry.TABLE_RUNTIME_CATALOG), tc.getProperty(PropertyRegistry.TABLE_RUNTIME_SCHEMA),
                tc.getProperty(PropertyRegistry.TABLE_RUNTIME_TABLE_NAME), delimitIdentifiers, tc.getDomainObjectRenamingRule(), domain);

            IntrospectedTable introspectedTable = ObjectFactory.createIntrospectedTable(tc, table, domain);

            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                introspectedTable.addColumn(introspectedColumn);
            }
            introspectedTable.setRemarks(tc.getRemark());
            for(ColumnInfo c:tc.getColumns()){
                if(c.isPrimaryKey()){
                    introspectedTable.addPrimaryKeyColumn(c.getColumn());
                }
            }
            answer.add(introspectedTable);
        }

        return answer;
    }
    
    
    
    
    
    
    private void reportIntrospectionWarnings(IntrospectedTable introspectedTable, TableInfo tableConfiguration, FullyQualifiedTable table) {
        // make sure that every column listed in column overrides
        // actually exists in the table
        for (ColumnInfo c : tableConfiguration.getColumns()) {
            if (c.isOverride()&&introspectedTable.getColumn(c.getColumn()) == null) {
                warnings.add(getString("Warning.3", c.getColumn(), table.toString()));
            }else if(c.isIgnore()&&introspectedTable.getColumn(c.getColumn()) == null){
                warnings.add(getString("Warning.4", c.getColumn(), table.toString()));
            }
            
        }

        GeneratedKey generatedKey = tableConfiguration.getGeneratedKey();
        if (generatedKey != null && introspectedTable.getColumn(generatedKey.getColumn()) == null) {
            if (generatedKey.isIdentity()) {
                warnings.add(getString("Warning.5", generatedKey.getColumn(), table.toString()));
            } else {
                warnings.add(getString("Warning.6", generatedKey.getColumn(), table.toString()));
            }
        }

        for (IntrospectedColumn ic : introspectedTable.getAllColumns()) {
            if (JavaReservedWords.containsWord(ic.getJavaProperty())) {
                warnings.add(getString("Warning.26", ic.getActualColumnName(), table.toString()));
            }
        }
    }

    private void calculateIdentityColumns(TableInfo tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        GeneratedKey gk = tc.getGeneratedKey();
        if (gk == null) {
            // no generated key, then no identity or sequence columns
            return;
        }

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                if (isMatchedColumn(introspectedColumn, gk)) {
                    if (gk.isIdentity() || gk.isJdbcStandard()) {
                        introspectedColumn.setIdentity(true);
                        introspectedColumn.setSequenceColumn(false);
                    } else {
                        introspectedColumn.setIdentity(false);
                        introspectedColumn.setSequenceColumn(true);
                    }
                }
            }
        }
    }
    private boolean isMatchedColumn(IntrospectedColumn introspectedColumn, GeneratedKey gk) {
        if (introspectedColumn.isColumnNameDelimited()) {
            return introspectedColumn.getActualColumnName().equals(gk.getColumn());
        } else {
            return introspectedColumn.getActualColumnName().equalsIgnoreCase(gk.getColumn());
        }
    }
    
    private void calculateExtraColumnInformation(TableInfo tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = null;
        String replaceString = null;
        if (tc.getColumnRenamingRule() != null) {
            pattern = Pattern.compile(tc.getColumnRenamingRule().getSearchString());
            replaceString = tc.getColumnRenamingRule().getReplaceString();
            replaceString = replaceString == null ? "" : replaceString;
        }

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                String calculatedColumnName;
                if (pattern == null) {
                    calculatedColumnName = introspectedColumn.getActualColumnName();
                } else {
                    Matcher matcher = pattern.matcher(introspectedColumn.getActualColumnName());
                    calculatedColumnName = matcher.replaceAll(replaceString);
                }

                if (DataUtils.asBoolean(tc.getProperty(PropertyRegistry.TABLE_USE_ACTUAL_COLUMN_NAMES))) {
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getValidPropertyName(calculatedColumnName));
                } else if (DataUtils.asBoolean(tc.getProperty(PropertyRegistry.TABLE_USE_COMPOUND_PROPERTY_NAMES))) {
                    sb.setLength(0);
                    sb.append(calculatedColumnName);
                    sb.append('_');
                    sb.append(JavaBeansUtil.getCamelCaseString(introspectedColumn.getRemarks(), true));
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getValidPropertyName(sb.toString()));
                } else {
                    introspectedColumn.setJavaProperty(JavaBeansUtil.getCamelCaseString(calculatedColumnName, false));
                }

                FullyQualifiedJavaType fullyQualifiedJavaType = javaTypeResolver.calculateJavaType(introspectedColumn);

                if (fullyQualifiedJavaType != null) {
                    introspectedColumn.setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    introspectedColumn.setJdbcTypeName(javaTypeResolver.calculateJdbcTypeName(introspectedColumn));
                } else {
                    // type cannot be resolved. Check for ignored or overridden
                    boolean warn = true;
                    if (tc.isColumnIgnored(introspectedColumn.getActualColumnName())) {
                        warn = false;
                    }

//                    ColumnOverride co = tc.getColumnOverride(introspectedColumn.getActualColumnName());
//                    if (co != null && stringHasValue(co.getJavaType()) && stringHasValue(co.getJavaType())) {
//                        warn = false;
//                    }

                    // if the type is not supported, then we'll report a warning
                    if (warn) {
                        introspectedColumn.setFullyQualifiedJavaType(FullyQualifiedJavaType.getObjectInstance());
                        introspectedColumn.setJdbcTypeName("OTHER");

                        String warning = getString("Warning.14", Integer.toString(introspectedColumn.getJdbcType()), entry.getKey().toString(),
                            introspectedColumn.getActualColumnName());

                        warnings.add(warning);
                    }
                }

                if (domain.autoDelimitKeywords() && SqlReservedWords.containsWord(introspectedColumn.getActualColumnName())) {
                    introspectedColumn.setColumnNameDelimited(true);
                }

                if (tc.isAllColumnDelimitingEnabled()) {
                    introspectedColumn.setColumnNameDelimited(true);
                }
            }
        }
    }
    private void removeIgnoredColumns(TableInfo tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
            Iterator<IntrospectedColumn> tableColumns = entry.getValue().iterator();
            while (tableColumns.hasNext()) {
                IntrospectedColumn introspectedColumn = tableColumns.next();
                if (tc.isColumnIgnored(introspectedColumn.getActualColumnName())) {
                    tableColumns.remove();
                    if (logger.isDebugEnabled()) {
                        logger.debug(getString("Tracing.3", introspectedColumn.getActualColumnName(), entry.getKey().toString()));
                    }
                }
            }
        }
    }
    public static boolean stringContainsSpace(String s) {
        return s != null && s.indexOf(' ') != -1;
    }
    private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableInfo tc) {
        String localCatalog = tc.getCatalog();
        String localSchema= tc.getSchema();
        String localTableName = tc.getTableName();
        Map<ActualTableName, List<IntrospectedColumn>> answer = new HashMap<ActualTableName, List<IntrospectedColumn>>();

        if (logger.isDebugEnabled()) {
            String fullTableName = TableInfo.composeFullyQualifiedTableName(localCatalog, localSchema, localTableName, '.');
            logger.debug(getString("Tracing.1", fullTableName));
        }
        List<ColumnInfo> columnInfos=tc.getColumns();
        if(ObjectUtils.isEmptyObject(columnInfos)){
            warnings.add("table column is empty");
            return null;
        }
        ActualTableName atn = new ActualTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName());

        for(ColumnInfo c:columnInfos){
            IntrospectedColumn introspectedColumn = ObjectFactory.createIntrospectedColumn(domain);

            introspectedColumn.setTableAlias(tc.getAlias());
            String jdbcType = c.getJdbcType();
            if(jdbcType==null){
                jdbcType=c.getNativeType();
            }
            introspectedColumn.setJdbcType(javaTypeResolver.calculateJdbcType(jdbcType));
            introspectedColumn.setLength(c.getColumnSize());
            introspectedColumn.setActualColumnName(c.getColumn());
            introspectedColumn.setNullable(c.isNullable());
            introspectedColumn.setScale(c.getScale());
            introspectedColumn.setRemarks(c.getRemark());
            introspectedColumn.setDefaultValue(c.getDefaultValue());
            introspectedColumn.setColumnInfo(c);

            List<IntrospectedColumn> columns = answer.get(atn);
            if (columns == null) {
                columns = new ArrayList<IntrospectedColumn>();
                answer.put(atn, columns);
            }
            columns.add(introspectedColumn);

            if (logger.isDebugEnabled()) {
                logger.debug(getString("Tracing.2", introspectedColumn.getActualColumnName(), Integer.toString(introspectedColumn.getJdbcType()),
                    atn.toString()));
            }
        }
        return answer;
    }

}
