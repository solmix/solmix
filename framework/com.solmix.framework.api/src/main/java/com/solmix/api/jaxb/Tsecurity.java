//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.28 at 04:57:48 下午 CST 
//


package com.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Tsecurity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tsecurity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.solmix.com/xmlns/datasource/v1.0.1}Gs_security"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requireAuth" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="operator" type="{http://www.solmix.com/xmlns/datasource/v1.0.1}Eoperator" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tsecurity", propOrder = {
    "requires",
    "requireRoles"
})
public class Tsecurity {

    protected String requires;
    protected String requireRoles;
    @XmlAttribute
    protected Boolean requireAuth;
    @XmlAttribute
    protected Eoperator operator;

    /**
     * Gets the value of the requires property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequires() {
        return requires;
    }

    /**
     * Sets the value of the requires property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequires(String value) {
        this.requires = value;
    }

    /**
     * Gets the value of the requireRoles property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequireRoles() {
        return requireRoles;
    }

    /**
     * Sets the value of the requireRoles property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequireRoles(String value) {
        this.requireRoles = value;
    }

    /**
     * Gets the value of the requireAuth property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRequireAuth() {
        if (requireAuth == null) {
            return true;
        } else {
            return requireAuth;
        }
    }

    /**
     * Sets the value of the requireAuth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequireAuth(Boolean value) {
        this.requireAuth = value;
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link Eoperator }
     *     
     */
    public Eoperator getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Eoperator }
     *     
     */
    public void setOperator(Eoperator value) {
        this.operator = value;
    }

}
