//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.28 at 04:57:48 下午 CST 
//


package com.solmix.api.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EdataProtocol.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EdataProtocol">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="getParams"/>
 *     &lt;enumeration value="postParams"/>
 *     &lt;enumeration value="postXML"/>
 *     &lt;enumeration value="clientCustom"/>
 *     &lt;enumeration value="soap"/>
 *     &lt;enumeration value="postMessage"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EdataProtocol")
@XmlEnum
public enum EdataProtocol {

    @XmlEnumValue("getParams")
    GET_PARAMS("getParams"),
    @XmlEnumValue("postParams")
    POST_PARAMS("postParams"),
    @XmlEnumValue("postXML")
    POST_XML("postXML"),
    @XmlEnumValue("clientCustom")
    CLIENT_CUSTOM("clientCustom"),
    @XmlEnumValue("soap")
    SOAP("soap"),
    @XmlEnumValue("postMessage")
    POST_MESSAGE("postMessage");
    private final String value;

    EdataProtocol(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EdataProtocol fromValue(String v) {
        for (EdataProtocol c: EdataProtocol.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
