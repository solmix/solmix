//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.13 at 12:58:28 上午 CST 
//


package com.solmix.api.jaxb.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Request Data
 * 
 * <p>Java class for Request complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Request">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionNum" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="operations" type="{http://www.solmix.com/xmlns/requestdata/v1.0.1}Roperations" minOccurs="0"/>
 *         &lt;element name="jscallback" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="omitNullMapValuesInResponse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Request", propOrder = {
    "transactionNum",
    "operations",
    "jscallback",
    "omitNullMapValuesInResponse"
})
public class Request {

    protected Long transactionNum;
    protected Roperations operations;
    protected String jscallback;
    protected Boolean omitNullMapValuesInResponse;

    /**
     * Gets the value of the transactionNum property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTransactionNum() {
        return transactionNum;
    }

    /**
     * Sets the value of the transactionNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTransactionNum(Long value) {
        this.transactionNum = value;
    }

    /**
     * Gets the value of the operations property.
     * 
     * @return
     *     possible object is
     *     {@link Roperations }
     *     
     */
    public Roperations getOperations() {
        return operations;
    }

    /**
     * Sets the value of the operations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Roperations }
     *     
     */
    public void setOperations(Roperations value) {
        this.operations = value;
    }

    /**
     * Gets the value of the jscallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJscallback() {
        return jscallback;
    }

    /**
     * Sets the value of the jscallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJscallback(String value) {
        this.jscallback = value;
    }

    /**
     * Gets the value of the omitNullMapValuesInResponse property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOmitNullMapValuesInResponse() {
        return omitNullMapValuesInResponse;
    }

    /**
     * Sets the value of the omitNullMapValuesInResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOmitNullMapValuesInResponse(Boolean value) {
        this.omitNullMapValuesInResponse = value;
    }

}
