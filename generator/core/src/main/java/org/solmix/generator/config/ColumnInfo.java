
package org.solmix.generator.config;

import static org.solmix.commons.util.StringUtils.stringHasValue;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;

public class ColumnInfo extends PropertyHolder
{

    private String column;
    
    private String property;

    private String title;

    private String jdbcType;

    /** The java type. */
    private String javaType;

    /** The type handler. */
    private String typeHandler;

    /** The is column name delimited. */
    private boolean isColumnNameDelimited;

    /** The configured delimited column name. */
    private String configuredDelimitedColumnName;

    /**
     * If true, the column is a GENERATED ALWAYS column which means that it should not be used in insert or update
     * statements.
     */
    private boolean isGeneratedAlways;

    private boolean override;

    private boolean ignore;

    private boolean primaryKey;

    private String desc;

    
    public String getColumn() {
        return column;
    }

    
    public void setColumn(String column) {
        this.column = column;
    }

    
    public String getProperty() {
        return property;
    }

    
    public void setProperty(String property) {
        this.property = property;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public boolean isColumnNameDelimited() {
        return isColumnNameDelimited;
    }

    public void setColumnNameDelimited(boolean isColumnNameDelimited) {
        this.isColumnNameDelimited = isColumnNameDelimited;
    }

    public String getConfiguredDelimitedColumnName() {
        return configuredDelimitedColumnName;
    }

    public void setConfiguredDelimitedColumnName(String configuredDelimitedColumnName) {
        this.configuredDelimitedColumnName = configuredDelimitedColumnName;
    }

    public boolean isGeneratedAlways() {
        return isGeneratedAlways;
    }

    public void setGeneratedAlways(boolean isGeneratedAlways) {
        this.isGeneratedAlways = isGeneratedAlways;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void validate(List<String> errors, String tableName) {
        if (!StringUtils.stringHasValue(column)) {
            errors.add(Messages.getString("ValidationError.22", //$NON-NLS-1$
                    tableName));
        }
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("columnOverride"); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("column", column)); //$NON-NLS-1$
        if (stringHasValue(property)) {
            xmlElement.addAttribute(new Attribute("property", property)); //$NON-NLS-1$
        }
        if (stringHasValue(title)) {
            xmlElement.addAttribute(new Attribute("title", title)); //$NON-NLS-1$
        }

        if (stringHasValue(javaType)) {
            xmlElement.addAttribute(new Attribute("javaType", javaType)); //$NON-NLS-1$
        }

        if (stringHasValue(jdbcType)) {
            xmlElement.addAttribute(new Attribute("jdbcType", jdbcType)); //$NON-NLS-1$
        }

        if (stringHasValue(typeHandler)) {
            xmlElement.addAttribute(new Attribute("typeHandler", typeHandler)); //$NON-NLS-1$
        }

        if (stringHasValue(configuredDelimitedColumnName)) {
            xmlElement.addAttribute(new Attribute(
                    "delimitedColumnName", configuredDelimitedColumnName)); //$NON-NLS-1$
        }

        addPropertyXmlElements(xmlElement);
        if (stringHasValue(desc)) {
            XmlElement desc = new XmlElement("desc");
            xmlElement.addElement(desc);
        }

        return xmlElement;
    }
}
