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
 * <p>Java class for Texport complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Texport">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="exportFields" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="exportResults" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="exportAs" type="{http://www.solmix.org/xmlns/datasource/v1.0.1}EexportAs" />
 *       &lt;attribute name="exportFilename" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lineBreakStyle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exportDelimiter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exportTitleSeparatorChar" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exportDisplay" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exportHeader" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exportFooter" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Texport", propOrder = {
    "exportFields"
})
public class Texport {

    protected String exportFields;
    @XmlAttribute
    protected Boolean exportResults;
    @XmlAttribute
    protected EexportAs exportAs;
    @XmlAttribute
    protected String exportFilename;
    @XmlAttribute
    protected String lineBreakStyle;
    @XmlAttribute
    protected String exportDelimiter;
    @XmlAttribute
    protected String exportTitleSeparatorChar;
    @XmlAttribute
    protected String exportDisplay;
    @XmlAttribute
    protected String exportHeader;
    @XmlAttribute
    protected String exportFooter;

    /**
     * Gets the value of the exportFields property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportFields() {
        return exportFields;
    }

    /**
     * Sets the value of the exportFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportFields(String value) {
        this.exportFields = value;
    }

    /**
     * Gets the value of the exportResults property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isExportResults() {
        if (exportResults == null) {
            return true;
        } else {
            return exportResults;
        }
    }

    /**
     * Sets the value of the exportResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExportResults(Boolean value) {
        this.exportResults = value;
    }

    /**
     * Gets the value of the exportAs property.
     * 
     * @return
     *     possible object is
     *     {@link EexportAs }
     *     
     */
    public EexportAs getExportAs() {
        return exportAs;
    }

    /**
     * Sets the value of the exportAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link EexportAs }
     *     
     */
    public void setExportAs(EexportAs value) {
        this.exportAs = value;
    }

    /**
     * Gets the value of the exportFilename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportFilename() {
        return exportFilename;
    }

    /**
     * Sets the value of the exportFilename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportFilename(String value) {
        this.exportFilename = value;
    }

    /**
     * Gets the value of the lineBreakStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineBreakStyle() {
        return lineBreakStyle;
    }

    /**
     * Sets the value of the lineBreakStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineBreakStyle(String value) {
        this.lineBreakStyle = value;
    }

    /**
     * Gets the value of the exportDelimiter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportDelimiter() {
        return exportDelimiter;
    }

    /**
     * Sets the value of the exportDelimiter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportDelimiter(String value) {
        this.exportDelimiter = value;
    }

    /**
     * Gets the value of the exportTitleSeparatorChar property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportTitleSeparatorChar() {
        return exportTitleSeparatorChar;
    }

    /**
     * Sets the value of the exportTitleSeparatorChar property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportTitleSeparatorChar(String value) {
        this.exportTitleSeparatorChar = value;
    }

    /**
     * Gets the value of the exportDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportDisplay() {
        return exportDisplay;
    }

    /**
     * Sets the value of the exportDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportDisplay(String value) {
        this.exportDisplay = value;
    }

    /**
     * Gets the value of the exportHeader property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportHeader() {
        return exportHeader;
    }

    /**
     * Sets the value of the exportHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportHeader(String value) {
        this.exportHeader = value;
    }

    /**
     * Gets the value of the exportFooter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExportFooter() {
        return exportFooter;
    }

    /**
     * Sets the value of the exportFooter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExportFooter(String value) {
        this.exportFooter = value;
    }

}
