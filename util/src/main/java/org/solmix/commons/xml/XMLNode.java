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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月17日
 */

public class XMLNode
{

    private Node node;

    private String name;

    private String body;

    private Map<String, Object> attributes;

    private Map<String, Object> variables;

    private XMLParser xpathParser;
   
    public XMLNode(XMLParser xpathParser, Node node, Map<String, Object> variables) {
        this.xpathParser = xpathParser;
        this.node = node;
        this.name = node.getNodeName();
        this.variables = variables;
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
      }
    public XMLNode newXMLNode(Node node) {
        return new XMLNode(xpathParser, node, variables);
      }
    public XMLNode getParent() {
        Node parent = node.getParentNode();
        if (parent == null || !(parent instanceof Element)) {
          return null;
        } else {
          return new XMLNode(xpathParser, parent, variables);
        }
      }

    public String getValueBasedIdentifier() {
      StringBuilder builder = new StringBuilder();
      XMLNode current = this;
      while (current != null) {
        if (current != this) {
          builder.insert(0, "_");
        }
        String value = current.getStringAttribute("id",
            current.getStringAttribute("value",
                current.getStringAttribute("property", null)));
        if (value != null) {
          value = value.replace('.', '_');
          builder.insert(0, "]");
          builder.insert(0,
              value);
          builder.insert(0, "[");
        }
        builder.insert(0, current.getName());
        current = current.getParent();
      }
      return builder.toString();
    }

    public String evalString(String expression) {
      return xpathParser.evalString(node, expression);
    }

    public Boolean evalBoolean(String expression) {
      return xpathParser.evalBoolean(node, expression);
    }

    public Double evalDouble(String expression) {
      return xpathParser.evalDouble(node, expression);
    }

    public List<XMLNode> evalNodes(String expression) {
      return xpathParser.evalNodes(node, expression);
    }

    public XMLNode evalNode(String expression) {
      return xpathParser.evalNode(node, expression);
    }

    public Node getNode() {
      return node;
    }

    public String getName() {
      return name;
    }

    public String getStringBody() {
      return getStringBody(null);
    }

    public String getStringBody(String def) {
      if (body == null) {
        return def;
      } else {
        return body;
      }
    }

    public Boolean getBooleanBody() {
      return getBooleanBody(null);
    }

    public Boolean getBooleanBody(Boolean def) {
      if (body == null) {
        return def;
      } else {
        return Boolean.valueOf(body);
      }
    }

    public Integer getIntBody() {
      return getIntBody(null);
    }

    public Integer getIntBody(Integer def) {
      if (body == null) {
        return def;
      } else {
        return Integer.parseInt(body);
      }
    }

    public Long getLongBody() {
      return getLongBody(null);
    }

    public Long getLongBody(Long def) {
      if (body == null) {
        return def;
      } else {
        return Long.parseLong(body);
      }
    }

    public Double getDoubleBody() {
      return getDoubleBody(null);
    }

    public Double getDoubleBody(Double def) {
      if (body == null) {
        return def;
      } else {
        return Double.parseDouble(body);
      }
    }

    public Float getFloatBody() {
      return getFloatBody(null);
    }

    public Float getFloatBody(Float def) {
      if (body == null) {
        return def;
      } else {
        return Float.parseFloat(body);
      }
    }

    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name) {
      return getEnumAttribute(enumType, name, null);
    }

    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name, T def) {
      String value = getStringAttribute(name);
      if (value == null) {
        return def;
      } else {
        return Enum.valueOf(enumType, value);
      }
    }

    public String getStringAttribute(String name) {
      return getStringAttribute(name, null);
    }

    public String getStringAttribute(String name, String def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return value.toString();
        }
    }

    public Boolean getBooleanAttribute(String name) {
        return getBooleanAttribute(name, null);
    }

    public Boolean getBooleanAttribute(String name, Boolean def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return Boolean.valueOf(value.toString());
        }
    }

    public Integer getIntAttribute(String name) {
        return getIntAttribute(name, null);
    }

    public Integer getIntAttribute(String name, Integer def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return Integer.parseInt(value.toString());
        }
    }

    public Long getLongAttribute(String name) {
        return getLongAttribute(name, null);
    }

    public Long getLongAttribute(String name, Long def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return Long.parseLong(value.toString());
        }
    }

    public Double getDoubleAttribute(String name) {
        return getDoubleAttribute(name, null);
    }

    public Double getDoubleAttribute(String name, Double def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return Double.parseDouble(value.toString());
        }
    }

    public Float getFloatAttribute(String name) {
        return getFloatAttribute(name, null);
    }

    public Float getFloatAttribute(String name, Float def) {
        Object value = attributes.get(name);
        if (value == null) {
            return def;
        } else {
            return Float.parseFloat(value.toString());
        }
    }

    public List<XMLNode> getChildren() {
        List<XMLNode> children = new ArrayList<XMLNode>();
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0, n = nodeList.getLength(); i < n; i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(new XMLNode(xpathParser, node, variables));
                }
            }
        }
        return children;
    }

    public Properties getChildrenAsProperties() {
        Properties properties = new Properties();
        for (XMLNode child : getChildren()) {
            String name = child.getStringAttribute("name");
            String value = child.getStringAttribute("value");
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<");
      builder.append(name);
      for (String key : attributes.keySet()) {
        builder.append(" ");
        builder.append(key);
        builder.append("=\"");
        builder.append(attributes.get(key));
        builder.append("\"");
      }
      List<XMLNode> children = getChildren();
      if (children.size() > 0) {
        builder.append(">\n");
        for (XMLNode node : children) {
          builder.append(node.toString());
        }
        builder.append("</");
        builder.append(name);
        builder.append(">");
      } else if (body != null) {
        builder.append(">");
        builder.append(body);
        builder.append("</");
        builder.append(name);
        builder.append(">");
      } else {
        builder.append("/>");
      }
      builder.append("\n");
      return builder.toString();
    }
    
    public String getPath() {
        StringBuilder builder = new StringBuilder();
        Node current = node;
        while (current != null && current instanceof Element) {
          if (current != node) {
            builder.insert(0, "/");
          }
          builder.insert(0, current.getNodeName());
          current = current.getParentNode();
        }
        return builder.toString();
      }

    private Map<String, Object> parseAttributes(Node n) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
          for (int i = 0; i < attributeNodes.getLength(); i++) {
            Node attribute = attributeNodes.item(i);
            String value = VariablesParser.parse(attribute.getNodeValue(), variables);
            attributes.put(attribute.getNodeName(), value);
          }
        }
        return attributes;
      }

      private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
          NodeList children = node.getChildNodes();
          for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            data = getBodyData(child);
            if (data != null) break;
          }
        }
        return data;
      }
      
      private String getBodyData(Node child) {
          if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((org.w3c.dom.CharacterData) child).getData();
            data = VariablesParser.parse(data, variables);
            return data;
          }
          return null;
        }
}
