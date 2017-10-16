
package org.solmix.generator.config;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.HashUtils;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;

public class TableInfo extends PropertyHolder
{

    private boolean insertStatementEnabled;

    private boolean selectByPrimaryKeyStatementEnabled;

    private boolean selectByExampleStatementEnabled;

    private boolean updateByPrimaryKeyStatementEnabled;

    private boolean deleteByPrimaryKeyStatementEnabled;

    private boolean deleteByExampleStatementEnabled;

    private boolean countByExampleStatementEnabled;

    private boolean updateByExampleStatementEnabled;

    private GeneratedKey generatedKey;

    private String selectByPrimaryKeyQueryId;

    private String selectByExampleQueryId;

    private String catalog;

    private String schema;

    private String tableName;

    private String domainObjectName;

    private String alias;

    private ModelType modelType;

    private boolean wildcardEscapingEnabled;

    private String configuredModelType;

    private boolean delimitIdentifiers;

    private DomainObjectRenamingRule domainObjectRenamingRule;

    private ColumnRenamingRule columnRenamingRule;

    private boolean isAllColumnDelimitingEnabled;

    private String mapperName;

    private String sqlProviderName;
    
    private String remark;

    private List<IgnoredColumnPattern> ignoredColumnPatterns = new ArrayList<IgnoredColumnPattern>();

    protected Map<String, ColumnInfo> columns = new LinkedHashMap<String, ColumnInfo>();

    public TableInfo(DomainInfo context)
    {
        super();

        this.modelType = context.getDefaultModelType();
        insertStatementEnabled = true;
        selectByPrimaryKeyStatementEnabled = true;
        
        updateByPrimaryKeyStatementEnabled = true;
        deleteByPrimaryKeyStatementEnabled = true;
        selectByExampleStatementEnabled = false;
        deleteByExampleStatementEnabled = false;
        countByExampleStatementEnabled = false;
        updateByExampleStatementEnabled = false;
    }

    public boolean isDeleteByPrimaryKeyStatementEnabled() {
        return deleteByPrimaryKeyStatementEnabled;
    }

    public void setDeleteByPrimaryKeyStatementEnabled(boolean deleteByPrimaryKeyStatementEnabled) {
        this.deleteByPrimaryKeyStatementEnabled = deleteByPrimaryKeyStatementEnabled;
    }

    public boolean isInsertStatementEnabled() {
        return insertStatementEnabled;
    }

    public void setInsertStatementEnabled(boolean insertStatementEnabled) {
        this.insertStatementEnabled = insertStatementEnabled;
    }

    public boolean isSelectByPrimaryKeyStatementEnabled() {
        return selectByPrimaryKeyStatementEnabled;
    }

    public void setSelectByPrimaryKeyStatementEnabled(boolean selectByPrimaryKeyStatementEnabled) {
        this.selectByPrimaryKeyStatementEnabled = selectByPrimaryKeyStatementEnabled;
    }

    public boolean isUpdateByPrimaryKeyStatementEnabled() {
        return updateByPrimaryKeyStatementEnabled;
    }

    public void setUpdateByPrimaryKeyStatementEnabled(boolean updateByPrimaryKeyStatementEnabled) {
        this.updateByPrimaryKeyStatementEnabled = updateByPrimaryKeyStatementEnabled;
    }

    public boolean isSelectByExampleStatementEnabled() {
        return selectByExampleStatementEnabled;
    }

    public void setSelectByExampleStatementEnabled(boolean selectByExampleStatementEnabled) {
        this.selectByExampleStatementEnabled = selectByExampleStatementEnabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TableInfo)) {
            return false;
        }

        TableInfo other = (TableInfo) obj;

        return ObjectUtils.isEquals(this.catalog, other.catalog) && ObjectUtils.isEquals(this.schema, other.schema)
            && ObjectUtils.isEquals(this.tableName, other.tableName);
    }

    public List<ColumnInfo> getColumns() {
        return new ArrayList<ColumnInfo>(columns.values());
    }

    @Override
    public int hashCode() {
        int result = HashUtils.SEED;
        result = HashUtils.hash(result, catalog);
        result = HashUtils.hash(result, schema);
        result = HashUtils.hash(result, tableName);

        return result;
    }

    public GeneratedKey getGeneratedKey() {
        return generatedKey;
    }

    public String getSelectByExampleQueryId() {
        return selectByExampleQueryId;
    }

    public void setSelectByExampleQueryId(String selectByExampleQueryId) {
        this.selectByExampleQueryId = selectByExampleQueryId;
    }

    public String getSelectByPrimaryKeyQueryId() {
        return selectByPrimaryKeyQueryId;
    }

    public void setSelectByPrimaryKeyQueryId(String selectByPrimaryKeyQueryId) {
        this.selectByPrimaryKeyQueryId = selectByPrimaryKeyQueryId;
    }

    public boolean isDeleteByExampleStatementEnabled() {
        return deleteByExampleStatementEnabled;
    }

    public void setDeleteByExampleStatementEnabled(boolean deleteByExampleStatementEnabled) {
        this.deleteByExampleStatementEnabled = deleteByExampleStatementEnabled;
    }

    public boolean areAnyStatementsEnabled() {
        return selectByExampleStatementEnabled || selectByPrimaryKeyStatementEnabled || insertStatementEnabled || updateByPrimaryKeyStatementEnabled
            || deleteByExampleStatementEnabled || deleteByPrimaryKeyStatementEnabled || countByExampleStatementEnabled
            || updateByExampleStatementEnabled;
    }

    public void setGeneratedKey(GeneratedKey generatedKey) {
        this.generatedKey = generatedKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getDomainObjectName() {
        return domainObjectName;
    }

    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setConfiguredModelType(String configuredModelType) {
        this.configuredModelType = configuredModelType;
        this.modelType = ModelType.getModelType(configuredModelType);
    }

    public boolean isWildcardEscapingEnabled() {
        return wildcardEscapingEnabled;
    }

    public void setWildcardEscapingEnabled(boolean wildcardEscapingEnabled) {
        this.wildcardEscapingEnabled = wildcardEscapingEnabled;
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("table");
        xmlElement.addAttribute(new Attribute("tableName", tableName));

        if (stringHasValue(catalog)) {
            xmlElement.addAttribute(new Attribute("catalog", catalog));
        }
        if(stringHasValue(remark)){
            xmlElement.addAttribute(new Attribute("remark", remark));
        }

        if (stringHasValue(schema)) {
            xmlElement.addAttribute(new Attribute("schema", schema));
        }

        if (stringHasValue(alias)) {
            xmlElement.addAttribute(new Attribute("alias", alias));
        }

        if (stringHasValue(domainObjectName)) {
            xmlElement.addAttribute(new Attribute("domainObjectName", domainObjectName));
        }

        if (!insertStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableInsert", "false")); //$NON-NLS-2$
        }

        if (!selectByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableSelectByPrimaryKey", "false")); //$NON-NLS-2$
        }

        if (!selectByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableSelectByExample", "false")); //$NON-NLS-2$
        }

        if (!updateByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableUpdateByPrimaryKey", "false")); //$NON-NLS-2$
        }

        if (!deleteByPrimaryKeyStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableDeleteByPrimaryKey", "false")); //$NON-NLS-2$
        }

        if (!deleteByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableDeleteByExample", "false")); //$NON-NLS-2$
        }

        if (!countByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableCountByExample", "false")); //$NON-NLS-2$
        }

        if (!updateByExampleStatementEnabled) {
            xmlElement.addAttribute(new Attribute("enableUpdateByExample", "false")); //$NON-NLS-2$
        }

        if (stringHasValue(selectByPrimaryKeyQueryId)) {
            xmlElement.addAttribute(new Attribute("selectByPrimaryKeyQueryId", selectByPrimaryKeyQueryId));
        }

        if (stringHasValue(selectByExampleQueryId)) {
            xmlElement.addAttribute(new Attribute("selectByExampleQueryId", selectByExampleQueryId));
        }

        if (configuredModelType != null) {
            xmlElement.addAttribute(new Attribute("modelType", configuredModelType));
        }

        if (wildcardEscapingEnabled) {
            xmlElement.addAttribute(new Attribute("escapeWildcards", "true")); //$NON-NLS-2$
        }

        if (isAllColumnDelimitingEnabled) {
            xmlElement.addAttribute(new Attribute("delimitAllColumns", "true")); //$NON-NLS-2$
        }

        if (delimitIdentifiers) {
            xmlElement.addAttribute(new Attribute("delimitIdentifiers", "true")); //$NON-NLS-2$
        }

        if (stringHasValue(mapperName)) {
            xmlElement.addAttribute(new Attribute("mapperName", mapperName));
        }

        if (stringHasValue(sqlProviderName)) {
            xmlElement.addAttribute(new Attribute("sqlProviderName", sqlProviderName));
        }

        addPropertyXmlElements(xmlElement);

        if (generatedKey != null) {
            xmlElement.addElement(generatedKey.toXmlElement());
        }

        if (domainObjectRenamingRule != null) {
            xmlElement.addElement(domainObjectRenamingRule.toXmlElement());
        }

        if (columnRenamingRule != null) {
            xmlElement.addElement(columnRenamingRule.toXmlElement());
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            xmlElement.addElement(ignoredColumnPattern.toXmlElement());
        }

        if (columns.size() > 0) {
            for (ColumnInfo ci : columns.values()) {
                xmlElement.addElement(ci.toXmlElement());
            }
        }
        return xmlElement;
    }

    public boolean isDelimitIdentifiers() {
        return delimitIdentifiers;
    }

    public void setDelimitIdentifiers(boolean delimitIdentifiers) {
        this.delimitIdentifiers = delimitIdentifiers;
    }

    public boolean isCountByExampleStatementEnabled() {
        return countByExampleStatementEnabled;
    }

    public void setCountByExampleStatementEnabled(boolean countByExampleStatementEnabled) {
        this.countByExampleStatementEnabled = countByExampleStatementEnabled;
    }

    public boolean isUpdateByExampleStatementEnabled() {
        return updateByExampleStatementEnabled;
    }

    public void setUpdateByExampleStatementEnabled(boolean updateByExampleStatementEnabled) {
        this.updateByExampleStatementEnabled = updateByExampleStatementEnabled;
    }

    public static String composeFullyQualifiedTableName(String catalog, String schema, String tableName, char separator) {
        StringBuilder sb = new StringBuilder();

        if (stringHasValue(catalog)) {
            sb.append(catalog);
            sb.append(separator);
        }

        if (stringHasValue(schema)) {
            sb.append(schema);
            sb.append(separator);
        } else {
            if (sb.length() > 0) {
                sb.append(separator);
            }
        }

        sb.append(tableName);

        return sb.toString();
    }

    public void validate(List<String> errors, int listPosition) {
        if (!stringHasValue(tableName)) {
            errors.add(getString("ValidationError.6", Integer.toString(listPosition)));
        }

        String fqTableName = composeFullyQualifiedTableName(catalog, schema, tableName, '.');

        if (generatedKey != null) {
            generatedKey.validate(errors, fqTableName);
        }

        // when using column indexes, either both or neither query ids
        // should be set
        if (DataUtils.asBoolean(getProperty(PropertyRegistry.TABLE_USE_COLUMN_INDEXES)) && selectByExampleStatementEnabled
            && selectByPrimaryKeyStatementEnabled) {
            boolean queryId1Set = stringHasValue(selectByExampleQueryId);
            boolean queryId2Set = stringHasValue(selectByPrimaryKeyQueryId);

            if (queryId1Set != queryId2Set) {
                errors.add(getString("ValidationError.13", fqTableName));
            }
        }

        if (domainObjectRenamingRule != null) {
            domainObjectRenamingRule.validate(errors, fqTableName);
        }

        if (columnRenamingRule != null) {
            columnRenamingRule.validate(errors, fqTableName);
        }

        for (ColumnInfo column : columns.values()) {
            column.validate(errors, fqTableName);
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            ignoredColumnPattern.validate(errors, fqTableName);
        }
    }

    public DomainObjectRenamingRule getDomainObjectRenamingRule() {
        return domainObjectRenamingRule;
    }

    public void setDomainObjectRenamingRule(DomainObjectRenamingRule domainObjectRenamingRule) {
        this.domainObjectRenamingRule = domainObjectRenamingRule;
    }

    public ColumnRenamingRule getColumnRenamingRule() {
        return columnRenamingRule;
    }

    public void setColumnRenamingRule(ColumnRenamingRule columnRenamingRule) {
        this.columnRenamingRule = columnRenamingRule;
    }

    public boolean isAllColumnDelimitingEnabled() {
        return isAllColumnDelimitingEnabled;
    }

    public void setAllColumnDelimitingEnabled(boolean isAllColumnDelimitingEnabled) {
        this.isAllColumnDelimitingEnabled = isAllColumnDelimitingEnabled;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getSqlProviderName() {
        return sqlProviderName;
    }

    public void setSqlProviderName(String sqlProviderName) {
        this.sqlProviderName = sqlProviderName;
    }

    public void addIgnoredColumnPattern(IgnoredColumnPattern ignoredColumnPattern) {
        ignoredColumnPatterns.add(ignoredColumnPattern);
    }

    public void addColumn(ColumnInfo ci) {
        columns.put(ci.getColumn(), ci);
    }

    public boolean isColumnIgnored(String column) {
        ColumnInfo ci = columns.get(column);
        return ci.isIgnore();
    }

    public ColumnInfo getColumnOverride(String actualColumnName) {
        ColumnInfo ci = columns.get(actualColumnName);
        return (ci!=null&&ci.isIgnore())?ci:null;
    }

    
    public String getRemark() {
        return remark;
    }

    
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
