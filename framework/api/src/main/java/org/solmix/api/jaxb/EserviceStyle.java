//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.29 at 03:33:15 下午 CST 
//


package org.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EserviceStyle.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EserviceStyle">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="new"/>
 *     &lt;enumeration value="osgi"/>
 *     &lt;enumeration value="jndi"/>
 *     &lt;enumeration value="bean"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EserviceStyle")
@XmlEnum
public enum EserviceStyle {

    @XmlEnumValue("new")
    NEW("new"),
    @XmlEnumValue("osgi")
    OSGI("osgi"),
    @XmlEnumValue("jndi")
    JNDI("jndi"),
    @XmlEnumValue("bean")
    BEAN("bean");
    private final String value;

    EserviceStyle(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EserviceStyle fromValue(String v) {
        for (EserviceStyle c: EserviceStyle.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
