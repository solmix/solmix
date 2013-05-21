//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.12 at 10:21:58 下午 CST 
//


package com.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EcriteriaPolicy.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EcriteriaPolicy">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="dropOnChange"/>
 *     &lt;enumeration value="dropOnShortening"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EcriteriaPolicy")
@XmlEnum
public enum EcriteriaPolicy {

    @XmlEnumValue("dropOnChange")
    DROP_ON_CHANGE("dropOnChange"),
    @XmlEnumValue("dropOnShortening")
    DROP_ON_SHORTENING("dropOnShortening");
    private final String value;

    EcriteriaPolicy(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EcriteriaPolicy fromValue(String v) {
        for (EcriteriaPolicy c: EcriteriaPolicy.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
