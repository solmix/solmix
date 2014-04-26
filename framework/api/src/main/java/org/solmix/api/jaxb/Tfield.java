//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.29 at 03:33:15 下午 CST 
//


package org.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Tfield complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tfield">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valueMap" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}TvalueMap" minOccurs="0"/>
 *         &lt;element name="validators" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}Tvalidators" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Arela_field"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Aso_field"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_control_filed"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_size_field"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_main_field"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tfield", propOrder = {
    "valueMap",
    "validators"
})
public class Tfield {

    protected TvalueMap valueMap;
    protected Tvalidators validators;
    @XmlAttribute
    protected Boolean primaryKey;
    @XmlAttribute
    protected String foreignKey;
    @XmlAttribute
    protected String rootValue;
    @XmlAttribute
    protected String includeFrom;
    @XmlAttribute
    protected Boolean derived;
    @XmlAttribute
    protected String tableName;
    @XmlAttribute
    protected String dbName;
    @XmlAttribute
    protected String sqlStorageStrategy;
    @XmlAttribute
    protected String customInsertExpression;
    @XmlAttribute
    protected String customSelectExpression;
    @XmlAttribute
    protected String customUpdateExpression;
    @XmlAttribute
    protected Boolean canEdit;
    @XmlAttribute
    protected Boolean canExport;
    @XmlAttribute
    protected Boolean canFilter;
    @XmlAttribute
    protected Boolean canSave;
    @XmlAttribute
    protected Boolean canSortClientOnly;
    @XmlAttribute
    protected Boolean canView;
    @XmlAttribute
    protected Boolean detail;
    @XmlAttribute
    protected Boolean escapeHTML;
    @XmlAttribute
    protected String exportTitle;
    @XmlAttribute
    protected String pluralTitle;
    @XmlAttribute
    protected String prompt;
    @XmlAttribute
    protected String sequenceName;
    @XmlAttribute
    protected String valueXPath;
    @XmlAttribute
    protected Boolean multiple;
    @XmlAttribute
    protected Boolean customSQL;
    @XmlAttribute
    protected String storeWithHash;
    @XmlAttribute
    protected String dateFormat;
    @XmlAttribute
    protected Integer imageHeight;
    @XmlAttribute
    protected Integer imageSize;
    @XmlAttribute
    protected Integer imageWidth;
    @XmlAttribute
    protected Integer length;
    @XmlAttribute
    protected Integer maxFileSize;
    @XmlAttribute
    protected Boolean hidden;
    @XmlAttribute
    protected Boolean ignore;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected Efield type;
    @XmlAttribute
    protected Boolean required;
    @XmlAttribute
    protected String nativeName;

    /**
     * Gets the value of the valueMap property.
     * 
     * @return
     *     possible object is
     *     {@link TvalueMap }
     *     
     */
    public TvalueMap getValueMap() {
        return valueMap;
    }

    /**
     * Sets the value of the valueMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link TvalueMap }
     *     
     */
    public void setValueMap(TvalueMap value) {
        this.valueMap = value;
    }

    /**
     * Gets the value of the validators property.
     * 
     * @return
     *     possible object is
     *     {@link Tvalidators }
     *     
     */
    public Tvalidators getValidators() {
        return validators;
    }

    /**
     * Sets the value of the validators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tvalidators }
     *     
     */
    public void setValidators(Tvalidators value) {
        this.validators = value;
    }

    /**
     * Gets the value of the primaryKey property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrimaryKey() {
        if (primaryKey == null) {
            return false;
        } else {
            return primaryKey;
        }
    }

    /**
     * Sets the value of the primaryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrimaryKey(Boolean value) {
        this.primaryKey = value;
    }

    /**
     * Gets the value of the foreignKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForeignKey() {
        return foreignKey;
    }

    /**
     * Sets the value of the foreignKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForeignKey(String value) {
        this.foreignKey = value;
    }

    /**
     * Gets the value of the rootValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootValue() {
        return rootValue;
    }

    /**
     * Sets the value of the rootValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootValue(String value) {
        this.rootValue = value;
    }

    /**
     * Gets the value of the includeFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeFrom() {
        return includeFrom;
    }

    /**
     * Sets the value of the includeFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeFrom(String value) {
        this.includeFrom = value;
    }

    /**
     * Gets the value of the derived property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDerived() {
        return derived;
    }

    /**
     * Sets the value of the derived property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDerived(Boolean value) {
        this.derived = value;
    }

    /**
     * Gets the value of the tableName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the value of the tableName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTableName(String value) {
        this.tableName = value;
    }

    /**
     * Gets the value of the dbName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Sets the value of the dbName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDbName(String value) {
        this.dbName = value;
    }

    /**
     * Gets the value of the sqlStorageStrategy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSqlStorageStrategy() {
        return sqlStorageStrategy;
    }

    /**
     * Sets the value of the sqlStorageStrategy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSqlStorageStrategy(String value) {
        this.sqlStorageStrategy = value;
    }

    /**
     * Gets the value of the customInsertExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomInsertExpression() {
        return customInsertExpression;
    }

    /**
     * Sets the value of the customInsertExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomInsertExpression(String value) {
        this.customInsertExpression = value;
    }

    /**
     * Gets the value of the customSelectExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomSelectExpression() {
        return customSelectExpression;
    }

    /**
     * Sets the value of the customSelectExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomSelectExpression(String value) {
        this.customSelectExpression = value;
    }

    /**
     * Gets the value of the customUpdateExpression property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomUpdateExpression() {
        return customUpdateExpression;
    }

    /**
     * Sets the value of the customUpdateExpression property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomUpdateExpression(String value) {
        this.customUpdateExpression = value;
    }

    /**
     * Gets the value of the canEdit property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanEdit() {
        return canEdit;
    }

    /**
     * Sets the value of the canEdit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanEdit(Boolean value) {
        this.canEdit = value;
    }

    /**
     * Gets the value of the canExport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanExport() {
        return canExport;
    }

    /**
     * Sets the value of the canExport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanExport(Boolean value) {
        this.canExport = value;
    }

    /**
     * Gets the value of the canFilter property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanFilter() {
        return canFilter;
    }

    /**
     * Sets the value of the canFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanFilter(Boolean value) {
        this.canFilter = value;
    }

    /**
     * Gets the value of the canSave property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanSave() {
        return canSave;
    }

    /**
     * Sets the value of the canSave property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanSave(Boolean value) {
        this.canSave = value;
    }

    /**
     * Gets the value of the canSortClientOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanSortClientOnly() {
        return canSortClientOnly;
    }

    /**
     * Sets the value of the canSortClientOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanSortClientOnly(Boolean value) {
        this.canSortClientOnly = value;
    }

    /**
     * Gets the value of the canView property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanView() {
        return canView;
    }

    /**
     * Sets the value of the canView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanView(Boolean value) {
        this.canView = value;
    }

    /**
     * Gets the value of the detail property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDetail(Boolean value) {
        this.detail = value;
    }

    /**
     * Gets the value of the escapeHTML property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEscapeHTML() {
        return escapeHTML;
    }

    /**
     * Sets the value of the escapeHTML property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEscapeHTML(Boolean value) {
        this.escapeHTML = value;
    }

    /**
     * Gets the value of the exportTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportTitle() {
        return exportTitle;
    }

    /**
     * Sets the value of the exportTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportTitle(String value) {
        this.exportTitle = value;
    }

    /**
     * Gets the value of the pluralTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPluralTitle() {
        return pluralTitle;
    }

    /**
     * Sets the value of the pluralTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPluralTitle(String value) {
        this.pluralTitle = value;
    }

    /**
     * Gets the value of the prompt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the value of the prompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrompt(String value) {
        this.prompt = value;
    }

    /**
     * Gets the value of the sequenceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * Sets the value of the sequenceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequenceName(String value) {
        this.sequenceName = value;
    }

    /**
     * Gets the value of the valueXPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValueXPath() {
        return valueXPath;
    }

    /**
     * Sets the value of the valueXPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValueXPath(String value) {
        this.valueXPath = value;
    }

    /**
     * Gets the value of the multiple property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiple() {
        return multiple;
    }

    /**
     * Sets the value of the multiple property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiple(Boolean value) {
        this.multiple = value;
    }

    /**
     * Gets the value of the customSQL property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCustomSQL() {
        return customSQL;
    }

    /**
     * Sets the value of the customSQL property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCustomSQL(Boolean value) {
        this.customSQL = value;
    }

    /**
     * Gets the value of the storeWithHash property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreWithHash() {
        return storeWithHash;
    }

    /**
     * Sets the value of the storeWithHash property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreWithHash(String value) {
        this.storeWithHash = value;
    }

    /**
     * Gets the value of the dateFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets the value of the dateFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateFormat(String value) {
        this.dateFormat = value;
    }

    /**
     * Gets the value of the imageHeight property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImageHeight() {
        return imageHeight;
    }

    /**
     * Sets the value of the imageHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImageHeight(Integer value) {
        this.imageHeight = value;
    }

    /**
     * Gets the value of the imageSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImageSize() {
        return imageSize;
    }

    /**
     * Sets the value of the imageSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImageSize(Integer value) {
        this.imageSize = value;
    }

    /**
     * Gets the value of the imageWidth property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets the value of the imageWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setImageWidth(Integer value) {
        this.imageWidth = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLength(Integer value) {
        this.length = value;
    }

    /**
     * Gets the value of the maxFileSize property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * Sets the value of the maxFileSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxFileSize(Integer value) {
        this.maxFileSize = value;
    }

    /**
     * Gets the value of the hidden property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHidden() {
        if (hidden == null) {
            return false;
        } else {
            return hidden;
        }
    }

    /**
     * Sets the value of the hidden property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHidden(Boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the value of the ignore property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIgnore() {
        return ignore;
    }

    /**
     * Sets the value of the ignore property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIgnore(Boolean value) {
        this.ignore = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Efield }
     *     
     */
    public Efield getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Efield }
     *     
     */
    public void setType(Efield value) {
        this.type = value;
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the nativeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNativeName() {
        return nativeName;
    }

    /**
     * Sets the value of the nativeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNativeName(String value) {
        this.nativeName = value;
    }

}
