//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.28 at 12:07:19 上午 CST 
//


package com.solmix.api.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for Tservice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tservice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="methodArguments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="lookupStyle" type="{http://www.solmix.com/xmlns/datasource/v1.0.1}EserviceStyle" />
 *       &lt;attribute name="interface" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="filter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="curdName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetXPath" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tservice", propOrder = {
    "methodArguments"
})
public class Tservice {

    protected String methodArguments;
    @XmlAttribute
    protected EserviceStyle lookupStyle;
    @XmlAttribute(name = "interface")
    protected String _interface;
    @XmlAttribute
    protected String filter;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute
    protected String method;
    @XmlAttribute
    protected String curdName;
    @XmlAttribute
    protected String targetXPath;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the methodArguments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodArguments() {
        return methodArguments;
    }

    /**
     * Sets the value of the methodArguments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodArguments(String value) {
        this.methodArguments = value;
    }

    /**
     * Gets the value of the lookupStyle property.
     * 
     * @return
     *     possible object is
     *     {@link EserviceStyle }
     *     
     */
    public EserviceStyle getLookupStyle() {
        return lookupStyle;
    }

    /**
     * Sets the value of the lookupStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link EserviceStyle }
     *     
     */
    public void setLookupStyle(EserviceStyle value) {
        this.lookupStyle = value;
    }

    /**
     * Gets the value of the interface property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterface() {
        return _interface;
    }

    /**
     * Sets the value of the interface property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterface(String value) {
        this._interface = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilter(String value) {
        this.filter = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Gets the value of the method property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the value of the method property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethod(String value) {
        this.method = value;
    }

    /**
     * Gets the value of the curdName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurdName() {
        return curdName;
    }

    /**
     * Sets the value of the curdName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurdName(String value) {
        this.curdName = value;
    }

    /**
     * Gets the value of the targetXPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetXPath() {
        return targetXPath;
    }

    /**
     * Sets the value of the targetXPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetXPath(String value) {
        this.targetXPath = value;
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
