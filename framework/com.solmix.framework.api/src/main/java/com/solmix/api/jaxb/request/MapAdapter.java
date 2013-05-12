/*
 * SOLMIX PROJECT
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

package com.solmix.api.jaxb.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-13
 */

public class MapAdapter extends XmlAdapter<AdaptedMap, Map<String, String>>
{

    @Override
    public AdaptedMap marshal(Map<String, String> map) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element rootElement = document.createElement("map");
        document.appendChild(rootElement);

        for (Entry<String, String> entry : map.entrySet()) {
            Element mapElement = document.createElement(entry.getKey());
            mapElement.setTextContent(entry.getValue());
            rootElement.appendChild(mapElement);
        }

        AdaptedMap adaptedMap = new AdaptedMap();
        adaptedMap.setValue(document);
        return adaptedMap;
    }

    @Override
    public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        Element rootElement = (Element) adaptedMap.getValue();
        NodeList childNodes = rootElement.getChildNodes();
        for (int x = 0, size = childNodes.getLength(); x < size; x++) {
            Node childNode = childNodes.item(x);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                map.put(childNode.getLocalName(), childNode.getTextContent());
            }
        }
        return map;
    }

}
