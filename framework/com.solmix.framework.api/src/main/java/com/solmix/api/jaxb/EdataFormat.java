//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.28 at 12:07:19 上午 CST 
//


package com.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EdataFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EdataFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="json"/>
 *     &lt;enumeration value="xml"/>
 *     &lt;enumeration value="soap"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EdataFormat")
@XmlEnum
public enum EdataFormat {

    @XmlEnumValue("json")
    JSON("json"),
    @XmlEnumValue("xml")
    XML("xml"),
    @XmlEnumValue("soap")
    SOAP("soap");
    private final String value;

    EdataFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EdataFormat fromValue(String v) {
        for (EdataFormat c: EdataFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
