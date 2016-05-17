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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.solmix.commons.util.DOMUtils;
import org.solmix.commons.util.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Spring Root bean,适用于定义单独的bean,没有子元素
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年9月6日
 */

public class AbstractRootBeanDefinitionParser implements BeanDefinitionParser
{

    private final Class<?> type;
    public AbstractRootBeanDefinitionParser(Class<?> type){
        this.type=type;
    }
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        return parse(element,parserContext,type);
    }
    protected BeanDefinition parse(Element element, ParserContext parserContext,Class<?> type) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(type);
        beanDefinition.setLazyInit(false);
        String id = checkId(element, parserContext, beanDefinition);
        parseAttributes(element, parserContext, id, beanDefinition);
        parseChildElements(element, parserContext, beanDefinition);
        return beanDefinition;
    }
   
    protected void parseChildElements(Element element,
        ParserContext parserContext, RootBeanDefinition beanDefinition) {
        Element el = DOMUtils.getFirstElement(element);
        while (el != null) {
            String name = el.getLocalName();
            mapElement(parserContext, beanDefinition, el, name);
            el = DOMUtils.getNextElement(el);
        }
        
    }
    protected void mapElement(ParserContext ctx, RootBeanDefinition bean,
        Element e, String name) {
    }
    protected void parseAttributes(Element element, ParserContext parserContext,
        String id, RootBeanDefinition beanDefinition) {
        Set<String> props = new HashSet<String>();
        ManagedMap<String,Object> parameters = null;
        for (Method setter : this.type.getMethods()) {
            String name = setter.getName();
            if (name.length() > 3 && name.startsWith("set")
                && Modifier.isPublic(setter.getModifiers())
                && setter.getParameterTypes().length == 1) {
                Class<?> type = setter.getParameterTypes()[0];
                String property = StringUtils.camelToSplitName(
                    name.substring(3, 4).toLowerCase() + name.substring(4), "-");
                props.add(property);
                Method getter = null;
                try {
                    getter =this.type.getMethod(
                        "get" + name.substring(3), new Class<?>[0]);
                } catch (NoSuchMethodException e) {
                    try {
                        getter = this.type.getMethod(
                            "is" + name.substring(3), new Class<?>[0]);
                    } catch (NoSuchMethodException e2) {
                    }
                }
                if (getter == null || !Modifier.isPublic(getter.getModifiers())
                    || !type.equals(getter.getReturnType())) {
                    continue;
                }
                if ("properties".equals(property)) {
                    parameters = parseProperties(element.getChildNodes(),beanDefinition);
                } else if ("arguments".equals(property)) {
                    parseArguments(id, element.getChildNodes(), beanDefinition,parserContext);
                } else {
                    String value = element.getAttribute(property);
                    if (value != null&&value.trim().length()>0) {
                        value=value.trim();
                        parserValue(beanDefinition,property,type,value, parserContext);
                    }
                }
            }
        }
        NamedNodeMap attributes = element.getAttributes();
        int len = attributes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = attributes.item(i);
            String name = node.getLocalName();
            if (!props.contains(name)) {
                if (parameters == null) {
                    parameters = new ManagedMap<String,Object>();
                }
                String value = node.getNodeValue();
                parameters.put(name, new TypedStringValue(value, String.class));
            }
        }
        if (parameters != null) {
            beanDefinition.getPropertyValues().addPropertyValue("properties",
                parameters);
        }
    }

    
    protected void parserValue(RootBeanDefinition beanDefinition,
        String property, Class<?> propertyType,String value,ParserContext parserContext) {
        
    }
    protected void parseArguments(String id, NodeList nodeList,
        RootBeanDefinition beanDefinition, ParserContext parserContext) {
        if (nodeList != null && nodeList.getLength() > 0) {
            ManagedList arguments = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    Element element = (Element) node;
                    if ("argument".equals(node.getNodeName()) || "argument".equals(node.getLocalName())) {
                        String argumentIndex = element.getAttribute("index");
                        if (arguments == null) {
                            arguments = new ManagedList();
                        }
                        BeanDefinition argumentBeanDefinition = parse(((Element) node),
                                parserContext, /*ArgumentConfig.class*/null);
                        String name = id + "." + argumentIndex;
                        BeanDefinitionHolder argumentBeanDefinitionHolder = new BeanDefinitionHolder(
                                argumentBeanDefinition, name);
                        arguments.add(argumentBeanDefinitionHolder);
                    }
                }
            }
            if (arguments != null) {
                beanDefinition.getPropertyValues().addPropertyValue("arguments", arguments);
            }
        }
        
    }

    /**
     * @param childNodes
     * @param beanDefinition
     * @return
     */
    protected ManagedMap<String,Object> parseProperties(NodeList nodeList,
        RootBeanDefinition beanDefinition) {
        if (nodeList != null && nodeList.getLength() > 0) {
            ManagedMap<String,Object> parameters = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node instanceof Element) {
                    if ("parameter".equals(node.getNodeName())
                            || "parameter".equals(node.getLocalName())) {
                        if (parameters == null) {
                            parameters = new ManagedMap<String,Object>();
                        }
                        String key = ((Element) node).getAttribute("key");
                        String value = ((Element) node).getAttribute("value");
                        parameters.put(key, new TypedStringValue(value, String.class));
                    }
                }
            }
            return parameters;
        }
        return null;
    }

    /**验证ID的合法性*/
    protected String checkId(Element element, ParserContext parserContext,
        RootBeanDefinition beanDefinition) {
        String id = element.getAttribute("id");
        if (id == null || id.length() == 0) {
            String name = element.getAttribute("name");
            if (name != null && name.length() != 0) {
                id = name;
            } else {
                id = type.getSimpleName() + "-"
                    + Integer.toString(Math.abs(this.hashCode()));
            }
        }
        if (id != null && id.length() > 0) {
            if (parserContext.getRegistry().containsBeanDefinition(id)) {
                throw new IllegalStateException("Duplicate spring bean id "
                    + id);
            }
            parserContext.getRegistry().registerBeanDefinition(id,
                beanDefinition);
            beanDefinition.getPropertyValues().addPropertyValue("id", id);
        }
        return id;
    }

    protected static void parseMultiRef(String property, String value,
        RootBeanDefinition beanDefinition, ParserContext parserContext) {
        String[] values = value.split("\\s*[,]+\\s*");
        ManagedList<Object> list = null;
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            if (v != null && v.length() > 0) {
                if (list == null) {
                    list = new ManagedList<Object>();
                }
                list.add(new RuntimeBeanReference(v));
            }
        }
        beanDefinition.getPropertyValues().addPropertyValue(property, list);
    }
    protected static boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == Boolean.class || cls == Byte.class
                || cls == Character.class || cls == Short.class || cls == Integer.class
                || cls == Long.class || cls == Float.class || cls == Double.class
                || cls == String.class || cls == Date.class || cls == Class.class;
    }
}
