/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.commons.xml;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月17日
 */

public class XMLParser
{

    private Document document;

    private boolean validation;

    private EntityResolver entityResolver;

    private Map<String, Object> variables;

    private XPath xpath;
    
    private String namespacePrefix;

    public XMLParser(String xml)
    {
        this(false, null, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    public XMLParser(InputStream inputStream)
    {
        this(false, null, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    public XMLParser(Document document)
    {
        this(false, null, null);
        this.document = document;
    }

    public XMLParser(String xml, boolean validation)
    {
        this(validation, null, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    public XMLParser(Reader reader, boolean validation)
    {
        this(validation, null, null);
        this.document = createDocument(new InputSource(reader));
    }

    public XMLParser(InputStream inputStream, boolean validation)
    {
        this(validation, null, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    public XMLParser(Document document, boolean validation)
    {
        this(validation, null, null);
        this.document = document;
    }

    public XMLParser(String xml, boolean validation, Map<String, Object> variables)
    {
        this(validation, variables, null);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    public XMLParser(Reader reader, boolean validation, Map<String, Object> variables)
    {
        this(validation, variables, null);
        this.document = createDocument(new InputSource(reader));
    }

    public XMLParser(InputStream inputStream, boolean validation, Map<String, Object> variables)
    {
        this(validation, variables, null);
        this.document = createDocument(new InputSource(inputStream));
    }

    public XMLParser(Document document, boolean validation, Map<String, Object> variables)
    {
        this(validation, variables, null);
        this.document = document;
    }

    public XMLParser(String xml, boolean validation, Map<String, Object> variables, EntityResolver entityResolver)
    {
        this(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(new StringReader(xml)));
    }

    public XMLParser(Reader reader, boolean validation, Map<String, Object> variables, EntityResolver entityResolver)
    {
        this(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(reader));
    }

    public XMLParser(InputStream inputStream, boolean validation, Map<String, Object> variables, EntityResolver entityResolver,String namespacePrefix)
    {
        this(validation, variables, entityResolver);
        this.document = createDocument(new InputSource(inputStream));
        this.namespacePrefix = namespacePrefix;
    }
    
    public XMLParser(InputSource inputsource, boolean validation, Map<String, Object> variables, EntityResolver entityResolver,String namespacePrefix)
    {
        this(validation, variables, entityResolver);
        this.document = createDocument(inputsource);
        this.namespacePrefix = namespacePrefix;
    }

    public XMLParser(Document document, boolean validation, Map<String, Object> variables, EntityResolver entityResolver)
    {
        this(validation, variables, entityResolver);
        this.document = document;
        finishXpath(this.document);
    }

    private XMLParser(boolean validation, Map<String, Object> variables, EntityResolver entityResolver)
    {
        this.validation = validation;
        this.variables = variables;
        this.entityResolver = entityResolver;
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();
    }
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
      }
    public String evalString(String expression) {
        return evalString(document, expression);
      }

      public String evalString(Object root, String expression) {
        String result = (String) evaluate(expression, root, XPathConstants.STRING);
        result = VariablesParser.parse(result, variables);
        return result;
      }

      public Boolean evalBoolean(String expression) {
        return evalBoolean(document, expression);
      }

      public Boolean evalBoolean(Object root, String expression) {
        return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
      }

      public Short evalShort(String expression) {
        return evalShort(document, expression);
      }

      public Short evalShort(Object root, String expression) {
        return Short.valueOf(evalString(root, expression));
      }

      public Integer evalInteger(String expression) {
        return evalInteger(document, expression);
      }

      public Integer evalInteger(Object root, String expression) {
        return Integer.valueOf(evalString(root, expression));
      }

      public Long evalLong(String expression) {
        return evalLong(document, expression);
      }

      public Long evalLong(Object root, String expression) {
        return Long.valueOf(evalString(root, expression));
      }

      public Float evalFloat(String expression) {
        return evalFloat(document, expression);
      }

      public Float evalFloat(Object root, String expression) {
        return Float.valueOf(evalString(root, expression));
      }

      public Double evalDouble(String expression) {
        return evalDouble(document, expression);
      }

      public Double evalDouble(Object root, String expression) {
        return (Double) evaluate(expression, root, XPathConstants.NUMBER);
      }

      public List<XMLNode> evalNodes(String expression) {
        return evalNodes(document, expression);
      }

      public List<XMLNode> evalNodes(Object root, String expression) {
        List<XMLNode> xnodes = new ArrayList<XMLNode>();
        NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
          xnodes.add(new XMLNode(this, nodes.item(i), variables));
        }
        return xnodes;
      }

      public XMLNode evalNode(String expression) {
        return evalNode(document, expression);
      }

      public XMLNode evalNode(Object root, String expression) {
        Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
        if (node == null) {
          return null;
        }
        return new XMLNode(this, node, variables);
      }

      private Object evaluate(String expression, Object root, QName returnType) {
        try {
            if(namespacePrefix!=null && expression.indexOf(":")==-1){
                StringBuffer sb = new StringBuffer();
                if(expression.indexOf("/")!=-1){
                    if(!expression.startsWith("/")){
                        sb.append(namespacePrefix);
                    }
                    int index = expression.indexOf("/");
                    while(index!=-1){
                        String t=expression.substring(0, index+1);
                        sb.append(t);
                        sb.append(namespacePrefix);
                        expression=expression.substring(index+1);
                        index=expression.indexOf("/");
                    }
                    sb.append(processOrSplit(expression));
                }else{
                    sb.append(namespacePrefix);
                    sb.append(processOrSplit(expression));
                }
                expression= sb.toString();
            }
          return xpath.evaluate(expression, root, returnType);
        } catch (Exception e) {
          throw new XMLParsingException("Error evaluating XPath.  Cause: " + e, e);
        }
      }
      private StringBuffer processOrSplit(String expression){
          StringBuffer sb = new StringBuffer();
          int index = expression.indexOf("|");
          while(index!=-1){
              String t=expression.substring(0, index+1);
              sb.append(t);
              sb.append(namespacePrefix);
              expression=expression.substring(index+1);
              index=expression.indexOf("|");
          }
          sb.append(expression);
          return sb;
      }
      private Document createDocument(InputSource inputSource) {
          return createDocument(inputSource,"xsd");
      }
    private Document createDocument(InputSource inputSource,String validationMode) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validation);

            factory.setNamespaceAware(false);
            if("xsd".equals(validationMode)){
                factory.setNamespaceAware(true);
                factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(new ErrorHandler() {

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                }
            });
            Document doc= builder.parse(inputSource);
            if(doc!=null){
                finishXpath(doc);
            }
            return doc;
        } catch (Exception e) {
            throw new XMLParsingException("Error creating document instance.  Cause: " + e, e);
        }
    }

    
    private void finishXpath(Document doc) {
        String prefix=doc.getDocumentElement().getPrefix();
        String ns= doc.getDocumentElement().getNamespaceURI();
        this.xpath.setNamespaceContext(new SimpleContext(prefix,ns));
        
    }
    class SimpleContext implements NamespaceContext {
        final String      prefix;
        final String      ns;

        SimpleContext(String prefix, String ns) {
              this.prefix = prefix;
              this.ns = ns;
        }

        @Override
        public String getNamespaceURI(String prefix) {
              if (this.prefix.equals(prefix))
                    return ns;
              return null;
        }

        @Override
        public String getPrefix(String namespaceURI) {
              if (namespaceURI.equals(ns))
                    return prefix;
              return prefix;
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
              return Arrays.asList(prefix).iterator();
        }

  }
}
