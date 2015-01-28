//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.29 at 03:33:15 下午 CST 
//


package org.solmix.api.jaxb;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * 
 * This is the XML Schema for the Solmix DataSource service 1.0.0 development
 * descriptor.DataSource Configuration files using this schema must indicate 
 *  the schema using the version 1.0.0 namespace.for example,
 * <DataSource xmlns="http://www.solmix.org/xmlns/datasource/v1.0.0">
 *   if used as a qualified namespace,"slx" is the recommended namespace prefix.
 *   
 * 
 * <p>Java class for TdataSource complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TdataSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}Tdescription" minOccurs="0"/>
 *         &lt;element name="fields" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}Tfields" minOccurs="0"/>
 *         &lt;element name="operationBindings" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}ToperationBindings" minOccurs="0"/>
 *         &lt;group ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Gc_client_i"/>
 *         &lt;group ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Gs_with_operation"/>
 *         &lt;element name="sqlPara" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}TsqlPara" minOccurs="0"/>
 *         &lt;group ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Gs_extention"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Atitle"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Acommon_datasource"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Aidentity"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Aco_datasource"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_client"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Avalidate"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Afield_ds"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Aso_datasource"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_desc"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Atransaction"/>
 *       &lt;attGroup ref="{http://www.solmix.org/xmlns/datasource/v1.0.1}Ac_client_i"/>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TdataSource", propOrder = {
    "description",
    "fields",
    "operationBindings",
    "callbackParam",
    "testData",
    "service",
    "security",
    "sqlPara",
    "cache",
    "configuration"
})
public class TdataSource {

    protected Tdescription description;
    protected Tfields fields;
    protected ToperationBindings operationBindings;
    protected String callbackParam;
    protected Tobject testData;
    protected Tservice service;
    protected Tsecurity security;
    protected TsqlPara sqlPara;
    protected Tobject cache;
    protected Tobject configuration;
    @XmlAttribute
    protected String pluralTitle;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    protected String titleField;
    @XmlAttribute
    protected Boolean autoDeriveTitles;
    @XmlAttribute
    protected Boolean autoConvertRelativeDates;
    @XmlAttribute
    protected Boolean canMultiSort;
    @XmlAttribute
    protected EcriteriaPolicy criteriaPolicy;
    @XmlAttribute
    protected String jsonPrefix;
    @XmlAttribute
    protected String jsonSuffix;
    @XmlAttribute
    protected Boolean preventHTTPCaching;
    @XmlAttribute
    protected Boolean qualifyColumnNames;
    @XmlAttribute
    protected Boolean showPrompt;
    @XmlAttribute(name = "ID", required = true)
    protected String id;
    @XmlAttribute
    protected String addGlobalId;
    @XmlAttribute
    protected EserverType serverType;
    @XmlAttribute
    protected Boolean clientOnly;
    @XmlAttribute
    protected Boolean autoCacheAllData;
    @XmlAttribute
    protected Boolean cacheAllData;
    @XmlAttribute
    protected String cacheData;
    @XmlAttribute
    protected Integer cacheMaxAge;
    @XmlAttribute
    protected Boolean useLocalValidators;
    @XmlAttribute
    protected String inheritsFrom;
    @XmlAttribute
    protected Boolean useFlatFields;
    @XmlAttribute
    protected Boolean showLocalFieldsOnly;
    @XmlAttribute
    protected Boolean useParentFieldOrder;
    @XmlAttribute
    protected Boolean autoDeriveSchema;
    @XmlAttribute
    protected Boolean validateRecords;
    @XmlAttribute
    protected String schemaClass;
    @XmlAttribute
    protected String persistenceUnit;
    @XmlAttribute
    protected String bean;
    @XmlAttribute
    protected String tableName;
    @XmlAttribute
    protected String dbName;
    @XmlAttribute
    protected String sqlSchema;
    @XmlAttribute
    protected Boolean simpleReturn;
    @XmlAttribute
    protected String iconField;
    @XmlAttribute
    protected String infoField;
    @XmlAttribute
    protected String dataField;
    @XmlAttribute
    protected String descriptionField;
    @XmlAttribute
    protected Boolean autoJoinTransactions;
    @XmlAttribute
    protected EdataProtocol dataProtocol;
    @XmlAttribute
    protected String dataURL;
    @XmlAttribute
    protected String dropUnknownCriteria;
    @XmlAttribute
    protected Boolean dropExtraFields;
    @XmlAttribute
    protected ErequestTransport dataTransport;
    @XmlAttribute
    protected String recordXPath;
    @XmlAttribute
    protected String tagName;
    @XmlAttribute
    protected Boolean useHttpProxy;
    @XmlAttribute
    protected EdataFormat dataFormat;
    @XmlAttribute
    protected String testFileName;
    @XmlAnyAttribute
    private final Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Tdescription }
     *     
     */
    public Tdescription getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tdescription }
     *     
     */
    public void setDescription(Tdescription value) {
        this.description = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * @return
     *     possible object is
     *     {@link Tfields }
     *     
     */
    public Tfields getFields() {
        return fields;
    }

    /**
     * Sets the value of the fields property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tfields }
     *     
     */
    public void setFields(Tfields value) {
        this.fields = value;
    }

    /**
     * Gets the value of the operationBindings property.
     * 
     * @return
     *     possible object is
     *     {@link ToperationBindings }
     *     
     */
    public ToperationBindings getOperationBindings() {
        return operationBindings;
    }

    /**
     * Sets the value of the operationBindings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ToperationBindings }
     *     
     */
    public void setOperationBindings(ToperationBindings value) {
        this.operationBindings = value;
    }

    /**
     * Gets the value of the callbackParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallbackParam() {
        return callbackParam;
    }

    /**
     * Sets the value of the callbackParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallbackParam(String value) {
        this.callbackParam = value;
    }

    /**
     * Gets the value of the testData property.
     * 
     * @return
     *     possible object is
     *     {@link Tobject }
     *     
     */
    public Tobject getTestData() {
        return testData;
    }

    /**
     * Sets the value of the testData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tobject }
     *     
     */
    public void setTestData(Tobject value) {
        this.testData = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link Tservice }
     *     
     */
    public Tservice getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tservice }
     *     
     */
    public void setService(Tservice value) {
        this.service = value;
    }

    /**
     * Gets the value of the security property.
     * 
     * @return
     *     possible object is
     *     {@link Tsecurity }
     *     
     */
    public Tsecurity getSecurity() {
        return security;
    }

    /**
     * Sets the value of the security property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tsecurity }
     *     
     */
    public void setSecurity(Tsecurity value) {
        this.security = value;
    }

    /**
     * Gets the value of the sqlPara property.
     * 
     * @return
     *     possible object is
     *     {@link TsqlPara }
     *     
     */
    public TsqlPara getSqlPara() {
        return sqlPara;
    }

    /**
     * Sets the value of the sqlPara property.
     * 
     * @param value
     *     allowed object is
     *     {@link TsqlPara }
     *     
     */
    public void setSqlPara(TsqlPara value) {
        this.sqlPara = value;
    }

    /**
     * Gets the value of the cache property.
     * 
     * @return
     *     possible object is
     *     {@link Tobject }
     *     
     */
    public Tobject getCache() {
        return cache;
    }

    /**
     * Sets the value of the cache property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tobject }
     *     
     */
    public void setCache(Tobject value) {
        this.cache = value;
    }

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link Tobject }
     *     
     */
    public Tobject getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Tobject }
     *     
     */
    public void setConfiguration(Tobject value) {
        this.configuration = value;
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
     * Gets the value of the titleField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitleField() {
        return titleField;
    }

    /**
     * Sets the value of the titleField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitleField(String value) {
        this.titleField = value;
    }

    /**
     * Gets the value of the autoDeriveTitles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoDeriveTitles() {
        return autoDeriveTitles;
    }

    /**
     * Sets the value of the autoDeriveTitles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoDeriveTitles(Boolean value) {
        this.autoDeriveTitles = value;
    }

    /**
     * Gets the value of the autoConvertRelativeDates property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoConvertRelativeDates() {
        return autoConvertRelativeDates;
    }

    /**
     * Sets the value of the autoConvertRelativeDates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoConvertRelativeDates(Boolean value) {
        this.autoConvertRelativeDates = value;
    }

    /**
     * Gets the value of the canMultiSort property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanMultiSort() {
        return canMultiSort;
    }

    /**
     * Sets the value of the canMultiSort property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanMultiSort(Boolean value) {
        this.canMultiSort = value;
    }

    /**
     * Gets the value of the criteriaPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link EcriteriaPolicy }
     *     
     */
    public EcriteriaPolicy getCriteriaPolicy() {
        return criteriaPolicy;
    }

    /**
     * Sets the value of the criteriaPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link EcriteriaPolicy }
     *     
     */
    public void setCriteriaPolicy(EcriteriaPolicy value) {
        this.criteriaPolicy = value;
    }

    /**
     * Gets the value of the jsonPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonPrefix() {
        return jsonPrefix;
    }

    /**
     * Sets the value of the jsonPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonPrefix(String value) {
        this.jsonPrefix = value;
    }

    /**
     * Gets the value of the jsonSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonSuffix() {
        return jsonSuffix;
    }

    /**
     * Sets the value of the jsonSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonSuffix(String value) {
        this.jsonSuffix = value;
    }

    /**
     * Gets the value of the preventHTTPCaching property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isPreventHTTPCaching() {
        return preventHTTPCaching;
    }

    /**
     * Sets the value of the preventHTTPCaching property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPreventHTTPCaching(Boolean value) {
        this.preventHTTPCaching = value;
    }

    /**
     * Gets the value of the qualifyColumnNames property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isQualifyColumnNames() {
        return qualifyColumnNames;
    }

    /**
     * Sets the value of the qualifyColumnNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setQualifyColumnNames(Boolean value) {
        this.qualifyColumnNames = value;
    }

    /**
     * Gets the value of the showPrompt property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowPrompt() {
        return showPrompt;
    }

    /**
     * Sets the value of the showPrompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowPrompt(Boolean value) {
        this.showPrompt = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the addGlobalId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddGlobalId() {
        return addGlobalId;
    }

    /**
     * Sets the value of the addGlobalId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddGlobalId(String value) {
        this.addGlobalId = value;
    }

    /**
     * Gets the value of the serverType property.
     * 
     * @return
     *     possible object is
     *     {@link EserverType }
     *     
     */
    public EserverType getServerType() {
        return serverType;
    }

    /**
     * Sets the value of the serverType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EserverType }
     *     
     */
    public void setServerType(EserverType value) {
        this.serverType = value;
    }

    /**
     * Gets the value of the clientOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isClientOnly() {
        return clientOnly;
    }

    /**
     * Sets the value of the clientOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setClientOnly(Boolean value) {
        this.clientOnly = value;
    }

    /**
     * Gets the value of the autoCacheAllData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoCacheAllData() {
        return autoCacheAllData;
    }

    /**
     * Sets the value of the autoCacheAllData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoCacheAllData(Boolean value) {
        this.autoCacheAllData = value;
    }

    /**
     * Gets the value of the cacheAllData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCacheAllData() {
        return cacheAllData;
    }

    /**
     * Sets the value of the cacheAllData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCacheAllData(Boolean value) {
        this.cacheAllData = value;
    }

    /**
     * Gets the value of the cacheData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCacheData() {
        return cacheData;
    }

    /**
     * Sets the value of the cacheData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCacheData(String value) {
        this.cacheData = value;
    }

    /**
     * Gets the value of the cacheMaxAge property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCacheMaxAge() {
        return cacheMaxAge;
    }

    /**
     * Sets the value of the cacheMaxAge property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCacheMaxAge(Integer value) {
        this.cacheMaxAge = value;
    }

    /**
     * Gets the value of the useLocalValidators property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseLocalValidators() {
        return useLocalValidators;
    }

    /**
     * Sets the value of the useLocalValidators property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseLocalValidators(Boolean value) {
        this.useLocalValidators = value;
    }

    /**
     * Gets the value of the inheritsFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInheritsFrom() {
        return inheritsFrom;
    }

    /**
     * Sets the value of the inheritsFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInheritsFrom(String value) {
        this.inheritsFrom = value;
    }

    /**
     * Gets the value of the useFlatFields property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseFlatFields() {
        return useFlatFields;
    }

    /**
     * Sets the value of the useFlatFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseFlatFields(Boolean value) {
        this.useFlatFields = value;
    }

    /**
     * Gets the value of the showLocalFieldsOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isShowLocalFieldsOnly() {
        return showLocalFieldsOnly;
    }

    /**
     * Sets the value of the showLocalFieldsOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowLocalFieldsOnly(Boolean value) {
        this.showLocalFieldsOnly = value;
    }

    /**
     * Gets the value of the useParentFieldOrder property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseParentFieldOrder() {
        return useParentFieldOrder;
    }

    /**
     * Sets the value of the useParentFieldOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseParentFieldOrder(Boolean value) {
        this.useParentFieldOrder = value;
    }

    /**
     * Gets the value of the autoDeriveSchema property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoDeriveSchema() {
        return autoDeriveSchema;
    }

    /**
     * Sets the value of the autoDeriveSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoDeriveSchema(Boolean value) {
        this.autoDeriveSchema = value;
    }

    /**
     * Gets the value of the validateRecords property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isValidateRecords() {
        return validateRecords;
    }

    /**
     * Sets the value of the validateRecords property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setValidateRecords(Boolean value) {
        this.validateRecords = value;
    }

    /**
     * Gets the value of the schemaClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaClass() {
        return schemaClass;
    }

    /**
     * Sets the value of the schemaClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaClass(String value) {
        this.schemaClass = value;
    }

    /**
     * Gets the value of the persistenceUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersistenceUnit() {
        return persistenceUnit;
    }

    /**
     * Sets the value of the persistenceUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersistenceUnit(String value) {
        this.persistenceUnit = value;
    }

    /**
     * Gets the value of the bean property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBean() {
        return bean;
    }

    /**
     * Sets the value of the bean property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBean(String value) {
        this.bean = value;
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
     * Gets the value of the sqlSchema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSqlSchema() {
        return sqlSchema;
    }

    /**
     * Sets the value of the sqlSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSqlSchema(String value) {
        this.sqlSchema = value;
    }

    /**
     * Gets the value of the simpleReturn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSimpleReturn() {
        if (simpleReturn == null) {
            return false;
        } else {
            return simpleReturn;
        }
    }

    /**
     * Sets the value of the simpleReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSimpleReturn(Boolean value) {
        this.simpleReturn = value;
    }

    /**
     * Gets the value of the iconField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIconField() {
        return iconField;
    }

    /**
     * Sets the value of the iconField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIconField(String value) {
        this.iconField = value;
    }

    /**
     * Gets the value of the infoField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoField() {
        return infoField;
    }

    /**
     * Sets the value of the infoField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoField(String value) {
        this.infoField = value;
    }

    /**
     * Gets the value of the dataField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataField() {
        return dataField;
    }

    /**
     * Sets the value of the dataField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataField(String value) {
        this.dataField = value;
    }

    /**
     * Gets the value of the descriptionField property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptionField() {
        return descriptionField;
    }

    /**
     * Sets the value of the descriptionField property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptionField(String value) {
        this.descriptionField = value;
    }

    /**
     * Gets the value of the autoJoinTransactions property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAutoJoinTransactions() {
        return autoJoinTransactions;
    }

    /**
     * Sets the value of the autoJoinTransactions property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAutoJoinTransactions(Boolean value) {
        this.autoJoinTransactions = value;
    }

    /**
     * Gets the value of the dataProtocol property.
     * 
     * @return
     *     possible object is
     *     {@link EdataProtocol }
     *     
     */
    public EdataProtocol getDataProtocol() {
        return dataProtocol;
    }

    /**
     * Sets the value of the dataProtocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link EdataProtocol }
     *     
     */
    public void setDataProtocol(EdataProtocol value) {
        this.dataProtocol = value;
    }

    /**
     * Gets the value of the dataURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataURL() {
        return dataURL;
    }

    /**
     * Sets the value of the dataURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataURL(String value) {
        this.dataURL = value;
    }

    /**
     * Gets the value of the dropUnknownCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDropUnknownCriteria() {
        return dropUnknownCriteria;
    }

    /**
     * Sets the value of the dropUnknownCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDropUnknownCriteria(String value) {
        this.dropUnknownCriteria = value;
    }

    /**
     * Gets the value of the dropExtraFields property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDropExtraFields() {
        return dropExtraFields;
    }

    /**
     * Sets the value of the dropExtraFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDropExtraFields(Boolean value) {
        this.dropExtraFields = value;
    }

    /**
     * Gets the value of the dataTransport property.
     * 
     * @return
     *     possible object is
     *     {@link ErequestTransport }
     *     
     */
    public ErequestTransport getDataTransport() {
        return dataTransport;
    }

    /**
     * Sets the value of the dataTransport property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErequestTransport }
     *     
     */
    public void setDataTransport(ErequestTransport value) {
        this.dataTransport = value;
    }

    /**
     * Gets the value of the recordXPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecordXPath() {
        return recordXPath;
    }

    /**
     * Sets the value of the recordXPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecordXPath(String value) {
        this.recordXPath = value;
    }

    /**
     * Gets the value of the tagName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Sets the value of the tagName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTagName(String value) {
        this.tagName = value;
    }

    /**
     * Gets the value of the useHttpProxy property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isUseHttpProxy() {
        return useHttpProxy;
    }

    /**
     * Sets the value of the useHttpProxy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseHttpProxy(Boolean value) {
        this.useHttpProxy = value;
    }

    /**
     * Gets the value of the dataFormat property.
     * 
     * @return
     *     possible object is
     *     {@link EdataFormat }
     *     
     */
    public EdataFormat getDataFormat() {
        return dataFormat;
    }

    /**
     * Sets the value of the dataFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link EdataFormat }
     *     
     */
    public void setDataFormat(EdataFormat value) {
        this.dataFormat = value;
    }

    /**
     * Gets the value of the testFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTestFileName() {
        return testFileName;
    }

    /**
     * Sets the value of the testFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTestFileName(String value) {
        this.testFileName = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}