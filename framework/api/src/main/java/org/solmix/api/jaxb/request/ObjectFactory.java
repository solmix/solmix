//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.13 at 12:58:28 上午 CST 
//

package org.solmix.api.jaxb.request;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.solmix.api.jaxb.request package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 */
@XmlRegistry
public class ObjectFactory
{

    private final static QName _Transaction_QNAME = new QName("http://www.solmix.org/xmlns/requestdata/v1.0.1", "transaction");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.solmix.api.jaxb.request
     * 
     */
    public ObjectFactory()
    {
    }

    /**
     * Create an instance of {@link Roperation }
     * 
     */
    public Roperation createRoperation() {
        return new Roperation();
    }

    /**
     * Create an instance of {@link Roperations }
     * 
     */
    public Roperations createRoperations() {
        return new Roperations();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link Rdata }
     * 
     */
    public Rdata createRdata() {
        return new Rdata();
    }

    /**
     * Create an instance of {@link Rmap }
     * 
     */
    public Rmap createRmap() {
        return new Rmap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Request }{@code >}
     * 
     */
    @XmlElementDecl(namespace = "http://www.solmix.org/xmlns/requestdata/v1.0.1", name = "transaction")
    public JAXBElement<Request> createTransaction(Request value) {
        return new JAXBElement<Request>(_Transaction_QNAME, Request.class, null, value);
    }

}
