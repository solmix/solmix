//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.28 at 04:57:48 下午 CST 
//


package com.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TqueryClauses complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TqueryClauses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customSQL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="selectClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valuesClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tableClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="whereClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCaluse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="groupWhereClause" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TqueryClauses", propOrder = {
    "customSQL",
    "selectClause",
    "valuesClause",
    "tableClause",
    "whereClause",
    "groupClause",
    "orderCaluse",
    "groupWhereClause"
})
public class TqueryClauses {

    protected String customSQL;
    protected String selectClause;
    protected String valuesClause;
    protected String tableClause;
    protected String whereClause;
    protected String groupClause;
    protected String orderCaluse;
    protected String groupWhereClause;

    /**
     * Gets the value of the customSQL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomSQL() {
        return customSQL;
    }

    /**
     * Sets the value of the customSQL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomSQL(String value) {
        this.customSQL = value;
    }

    /**
     * Gets the value of the selectClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSelectClause() {
        return selectClause;
    }

    /**
     * Sets the value of the selectClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSelectClause(String value) {
        this.selectClause = value;
    }

    /**
     * Gets the value of the valuesClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValuesClause() {
        return valuesClause;
    }

    /**
     * Sets the value of the valuesClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValuesClause(String value) {
        this.valuesClause = value;
    }

    /**
     * Gets the value of the tableClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTableClause() {
        return tableClause;
    }

    /**
     * Sets the value of the tableClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTableClause(String value) {
        this.tableClause = value;
    }

    /**
     * Gets the value of the whereClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhereClause() {
        return whereClause;
    }

    /**
     * Sets the value of the whereClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhereClause(String value) {
        this.whereClause = value;
    }

    /**
     * Gets the value of the groupClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupClause() {
        return groupClause;
    }

    /**
     * Sets the value of the groupClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupClause(String value) {
        this.groupClause = value;
    }

    /**
     * Gets the value of the orderCaluse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderCaluse() {
        return orderCaluse;
    }

    /**
     * Sets the value of the orderCaluse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderCaluse(String value) {
        this.orderCaluse = value;
    }

    /**
     * Gets the value of the groupWhereClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupWhereClause() {
        return groupWhereClause;
    }

    /**
     * Sets the value of the groupWhereClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupWhereClause(String value) {
        this.groupWhereClause = value;
    }

}
