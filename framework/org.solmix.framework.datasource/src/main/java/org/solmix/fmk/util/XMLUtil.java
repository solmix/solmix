/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Tobject;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.serialize.XMLParserFactoryImpl;

/**
 * 
 * @author solomon
 * @version 110035 2011-4-18
 */
@SuppressWarnings("unchecked")
public class XMLUtil {

	public static Set<String> ignoreNamespaceAttributes = new HashSet<String>();
	static {
		ignoreNamespaceAttributes.add("http://www.w3.org/2000/10/XMLSchema-instance");
		ignoreNamespaceAttributes.add("http://www.solmix.org/xmlns/requestdata/v");
	}

	/**
	 * @param element
	 * @return
	 */
	public static Map attributesToMap(Element element) {
		return addAttributesToMap(element, null);
	}

	public static boolean isIgnoreNS(String nameSpace) {
		for (String ns : ignoreNamespaceAttributes) {
			if (nameSpace.startsWith(ns))
				return true;
		}
		return false;
	}

	/**
	 * @param element
	 *            the giving Element
	 * @param valueMap
	 *            the return Map
	 */
	public static Map<String, String> addAttributesToMap(Element element, Map<String, String> map) {
		if (map == null)
			map = new HashMap<String, String>();
		if (element.hasAttributes()) {
			NamedNodeMap attrs = element.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Node attr = attrs.item(i);
				String namespace = attr.getNamespaceURI();
				if (namespace != null && isIgnoreNS(namespace))
					continue;
				String attrName = attr.getLocalName();
				if (attrName == null)
					attrName = attr.getNodeName();
				if (attrName.equals("_BLANK_"))
					attrName = "";
				if (!map.containsKey(attrName))
					map.put(attrName, attr.getNodeValue());
			}
		}
		return map;
	}

	public static Map getElementChildrenMap(Element element) {
		Map elementChildren = new HashMap();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				DataUtil.putMultiple(elementChildren, childElement.getTagName(), childElement);
			}
		}

		return elementChildren;
	}

	public static List<Element> getElementChildren(Element element) {
		return getElementChildren(element, (Map) null);
	}

	public static List<Element> getElementChildren(Element element, String tagName) {
		return getElementChildren(element, DataUtil.buildMap(tagName, new Object()));
	}

	public static List<Element> getElementChildren(Element element, Map tags) {
		List<Element> elementChildren = new ArrayList<Element>();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (!(child instanceof Element))
				continue;
			Element childElement = (Element) child;
			if (tags == null || tags.containsKey(childElement.getTagName()))
				elementChildren.add(childElement);
		}

		return elementChildren;
	}

	public static String toSimpleValue(Element element) {
		if (!element.hasChildNodes())
			return getAttribute(element, "value");
		Writer out = new StringWriter();
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
			try {
				Node child = children.item(i);
				if (child instanceof Text) {
					out.write(((Text) child).getData());
					continue;
				}
			} catch (IOException impossible) {
			}

		return out.toString();
	}

	/**
	 * @param element
	 * @param string
	 * @return
	 */
	public static String getAttribute(Element element, String eleName) {
		return element.getAttribute(eleName);
	}

	public static void toFile(String location, String fileName, Object jaxbObject) throws SlxException {
		XMLParserFactory factory = XMLParserFactoryImpl.getInstance();
		XMLParser parser = factory.get();
		String absolutePath = null;
		if (location != null) {
			absolutePath = location.endsWith("/") ? location : location + "/";
			File dir = new File(absolutePath);
			if (!dir.exists() && !dir.isDirectory())
				dir.mkdirs();
			absolutePath = DataUtil.isNullOrEmpty(fileName) ? absolutePath : absolutePath + fileName;
		} else {
			absolutePath = fileName;
		}
		Writer out = null;
		FileOutputStream outStream = null;
		try {
			File f = new File(absolutePath);
			try {
				if (!f.exists()) {
					f.createNewFile();
				}
				outStream = new FileOutputStream(f);
				out = new BufferedWriter(new OutputStreamWriter(outStream));
			} catch (Exception e) {
				throw new SlxException(Tmodule.XML, Texception.IO_EXCEPTION, e);
			}
			parser.marshalDS(out, jaxbObject);
		} finally {
			try {
				out.close();
				outStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static void toFile(String fileName, Object jaxbObject) throws SlxException {
		toFile(null, fileName, jaxbObject);
	}

	/**
	 * convert xml to map ,the {@link Tobject} is a list of Element object,and
	 * the element must like this <code>&lt;key&gt;value&lt;/key&gt;</code> and
	 * no like this <code>&lt;key a="value"&gt;&lt;/key> </code>
	 * 
	 * @param to
	 * @return
	 */
	public static Map<String, Object> toMap(Tobject to) {
		if (to == null) {
			return Collections.emptyMap();
		}
		List<Object> objs = to.getAny();
		return toMap(objs);
	}

	public static Map<String, Object> toMap(List<Object> objs) {
		Map<String, Object> _return = new HashMap<String, Object>();
		for (Object obj : objs) {
			if (obj instanceof Element) {
				Element e = (Element) obj;
				Object value = getValue(e);
				_return.put(e.getNodeName(), value);
			}
		}

		return _return;
	}

	public static Object getValue(Element e) {
		if (e.getChildNodes() == null)
			return null;
		if (e.getChildNodes().getLength() == 1 && e.getFirstChild().getNodeType() != Node.ELEMENT_NODE) {
			return e.getFirstChild().getNodeValue();
		} else {
			Object _return = null;
			Node node = e.getFirstChild();
			String tempName = null;
			Object tempValue = null;
			int i = 0;
			boolean sure = false;
			List<Object> re=null;
			Map<String, Object> rem=null;
			for (; node != null; node = node.getNextSibling()) {
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					if (i == 0) {
						tempName = node.getNodeName();
						tempValue = getValue((Element) node);
					} else {
						if (node.getNodeName().equals(tempName)) {

							if (!sure) {
								rem = new HashMap<String, Object>();
								re = new ArrayList<Object>();
								rem.put(tempName, re);
								re.add(tempValue);
								_return = rem;
							}

							re.add(getValue((Element) node));
							sure = true;
						} else {
							
							if (!sure) {
								rem = new HashMap<String, Object>();
								rem.put(tempName, tempValue);
								_return = rem;
							}

							rem.put(node.getNodeName(), getValue((Element) node));
							sure = true;
						}
					}
					i++;
				}
			
			}
			if(i==1){
				Map<String, Object> _r = new HashMap<String, Object>();
				_r.put(tempName, tempValue);
				return _r;
			}
			return _return;
		}
	}

	public static Element toElement(String key, Object value, boolean mapAsAttribute) throws SlxException {
		Document doc = null;
		Element element = null;
		try {
			doc = getTempDocument();
			element = doc.createElement(key);
			if (value instanceof Map ) {
			    _buildMap(element, (Map)value, mapAsAttribute);
			} else if(value instanceof List){
			    _buildList(element,(List)value,mapAsAttribute);
			}else {
				element.setTextContent(value.toString());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SlxException(Tmodule.XML, Texception.XML_CREATE_DOCUMENT, e);
		}
		return element;
	}

	/**
     * @param element
     * @param value
     * @param mapAsAttribute
	 * @throws ParserConfigurationException 
     */
    private static void _buildList(Element element, List value, boolean mapAsAttribute) throws ParserConfigurationException {
        for (Object _v : value) {
            
            if (_v instanceof Map ) {
                _buildMap(element,(Map) _v, mapAsAttribute);
            } else if(_v instanceof List){
                _buildList(element, (List)_v, mapAsAttribute);
            }else {
                  Document doc = getTempDocument();
                  Element child = doc.createElement(autoBuildTags(element.getNodeName()));
                  child.setTextContent(_v.toString());
                  element.appendChild(child);
            }

      }
        
    }

    public static Element toElement(String key, Object value) throws SlxException {
		return toElement(key, value, false);
	}

	
	private static void _buildMap(Element element, Map mvalue, boolean mapAsAttribute) throws ParserConfigurationException{
          for (Object _key : mvalue.keySet()) {
                Document doc = getTempDocument();
                Element child = doc.createElement(_key.toString());
                Object _value = mvalue.get(_key);
                if (_value instanceof Map ) {
                      _buildMap(child, (Map)_value, mapAsAttribute);
                }else if(_value instanceof List){ 
                    _buildList(child, (List)_value, mapAsAttribute);
                }else {
                      if (mapAsAttribute)
                            child.setAttribute((String) _key, _value.toString());
                      else
                            child.setTextContent(_value.toString());
                      element.appendChild(child);
                }
          }
	    
	}
	private static String autoBuildTags(String parent){
	    if(parent.endsWith("s")||parent.endsWith("S")){
	        parent.subSequence(0, parent.length()-1);
	    }
	    return parent;
	}

	private static Document getTempDocument() throws ParserConfigurationException {
		if (tempDoc == null) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			tempDoc = builder.newDocument();
		}
		return tempDoc;
	}

	private static Document tempDoc;
}
