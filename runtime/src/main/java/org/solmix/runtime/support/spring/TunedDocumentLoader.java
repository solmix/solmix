/*
 * Copyright 2013 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.runtime.support.spring;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月29日
 */

public class TunedDocumentLoader extends DefaultDocumentLoader
{
 private static boolean hasFastInfoSet;
 private static final Logger LOG= LoggerFactory.getLogger(TunedDocumentLoader.class);

    static {
        try { 
            ClassLoaderUtils
                .loadClass("com.sun.xml.fastinfoset.stax.StAXDocumentParser", 
                           TunedDocumentLoader.class); 
            hasFastInfoSet = true;
        } catch (Throwable e) { 
            LOG.info("FastInfoset not found on classpath. Disabling context load optimizations.");
            hasFastInfoSet = false;
        } 
    }
    private SAXParserFactory saxParserFactory;
    private SAXParserFactory nsasaxParserFactory;
    
    TunedDocumentLoader() {
        try {
            Class<?> cls = ClassLoaderUtils.loadClass("com.ctc.wstx.sax.WstxSAXParserFactory",
                                                      TunedDocumentLoader.class);
            saxParserFactory = (SAXParserFactory)cls.newInstance();
            nsasaxParserFactory = (SAXParserFactory)cls.newInstance();
        } catch (Throwable e) {
            //woodstox not found, use any other Stax parser
            saxParserFactory = SAXParserFactory.newInstance();
            nsasaxParserFactory = SAXParserFactory.newInstance();
        }

        try {
            nsasaxParserFactory.setFeature("http://xml.org/sax/features/namespaces", true); 
            nsasaxParserFactory.setFeature("http://xml.org/sax/features/namespace-prefixes", 
                                           true);
        } catch (Throwable e) {
            //ignore
        }
        
    }
    
    public static boolean hasFastInfoSet() {
        return hasFastInfoSet;
    }

    @Override
    public Document loadDocument(InputSource inputSource, EntityResolver entityResolver,
                                 ErrorHandler errorHandler, int validationMode, boolean namespaceAware)
        throws Exception {
        if (validationMode == XmlBeanDefinitionReader.VALIDATION_NONE) {
            SAXParserFactory parserFactory = 
                namespaceAware ? nsasaxParserFactory : saxParserFactory;
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setEntityResolver(entityResolver);
            reader.setErrorHandler(errorHandler);
//            SAXSource saxSource = new SAXSource(reader, inputSource);
//            W3CDOMStreamWriter writer = new W3CDOMStreaXMLStreamWriterImplmWriter();
//            StaxUtils.copy(saxSource, writer);
//            return writer.getDocument();
            return null;
        } else {
            return super.loadDocument(inputSource, entityResolver, errorHandler, validationMode,
                                      namespaceAware);
        }
    }

    @Override
    protected DocumentBuilderFactory createDocumentBuilderFactory(int validationMode, boolean namespaceAware)
        throws ParserConfigurationException {
        DocumentBuilderFactory factory = super.createDocumentBuilderFactory(validationMode, namespaceAware);
        try {
            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        } catch (Throwable e) {
            // we can get all kinds of exceptions from this
            // due to old copies of Xerces and whatnot.
        }
        
        return factory;
    }
    
    static Document loadFastinfosetDocument(URL url) 
        throws IOException, ParserConfigurationException, XMLStreamException {
        /*InputStream is = url.openStream();
        InputStream in = new BufferedInputStream(is);
        XMLStreamReader staxReader = new StAXDocumentParser(in);
        W3CDOMStreamWriter writer = new W3CDOMStreamWriter();
        StaxUtils.copy(staxReader, writer);
        in.close();
        return writer.getDocument();*/
        return null;
    }
}
