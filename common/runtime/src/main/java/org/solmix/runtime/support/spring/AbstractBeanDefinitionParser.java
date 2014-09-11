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

package org.solmix.runtime.support.spring;

import org.solmix.commons.util.DOMUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年9月10日
 */

public class AbstractBeanDefinitionParser extends
    AbstractSingleBeanDefinitionParser
{

    public static final String WIRE_CONTAINER_ATTRIBUTE = 
        AbstractBeanDefinitionParser.class.getName()+ ".wireBus";

    public static final String WIRE_CONTAINER_NAME = 
        AbstractBeanDefinitionParser.class.getName()+ ".wireBusName";

    public static final String WIRE_CONTAINER_CREATE = 
        AbstractBeanDefinitionParser.class.getName()+ ".wireBusCreate";

    public static final String WIRE_CONTAINER_HANDLER = ContainerPostProcessor.class.getName();

    private Class<?> beanClass;

    @Override
    protected void doParse(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean) {
        boolean setBus = parseAttributes(element, ctx, bean);
        if (!setBus && hasBusProperty()) {
            addBusWiringAttribute(bean, true);
        }
        parseChildElements(element, ctx, bean);
    }

    protected void parseChildElements(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean) {
        Element el = DOMUtils.getFirstElement(element);
        while (el != null) {
            String name = el.getLocalName();
            mapElement(ctx, bean, el, name);
            el = DOMUtils.getNextElement(el);
        }
    }

    protected void mapElement(ParserContext ctx, BeanDefinitionBuilder bean,
        Element e, String name) {
    }

    protected boolean hasBusProperty() {
        return false;
    }

    protected boolean parseAttributes(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean) {
        NamedNodeMap atts = element.getAttributes();
        boolean setBus = false;
        for (int i = 0; i < atts.getLength(); i++) {
            Attr node = (Attr) atts.item(i);
            String val = node.getValue();
            String pre = node.getPrefix();
            String name = node.getLocalName();
            String prefix = node.getPrefix();

            // Don't process namespaces
            if (isNamespace(name, prefix)) {
                continue;
            }

            if ("createdFromAPI".equals(name)) {
                bean.setAbstract(true);
            } else if ("abstract".equals(name)) {
                bean.setAbstract(true);
            } else if ("depends-on".equals(name)) {
                bean.addDependsOn(val);
            } else if ("name".equals(name)) {
                processNameAttribute(element, ctx, bean, val);
            } else if ("container".equals(name)) {
                setBus = processBusAttribute(element, ctx, bean, val);
            } else if (!"id".equals(name) && isAttribute(pre, name)) {
                mapAttribute(bean, element, name, val);
            }
        }
        return setBus;
    }

    protected void mapAttribute(BeanDefinitionBuilder bean, Element e,
        String name, String val) {
        mapAttribute(bean, name, val);
    }

    protected void mapAttribute(BeanDefinitionBuilder bean, String name,
        String val) {
        mapToProperty(bean, name, val);
    }

    protected void mapToProperty(BeanDefinitionBuilder bean,
        String propertyName, String val) {
        if (ID_ATTRIBUTE.equals(propertyName)) {
            return;
        }

        if (val != null && val.trim().length() > 0) {
                bean.addPropertyValue(propertyName, val);
        }
    }

    protected boolean processBusAttribute(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean, String val) {
        if (val != null && val.trim().length() > 0) {
            if (ctx.getRegistry().containsBeanDefinition(val)) {
                bean.addPropertyReference("container", val);
            } else {
                addBusWiringAttribute(bean, true, val, ctx);
            }
            return true;
        }
        return false;
    }

    protected void processNameAttribute(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean, String val) {
        // nothing
    }

    protected boolean isAttribute(String pre, String name) {
        return !"xmlns".equals(name) && (pre == null || !pre.equals("xmlns"))
            && !"abstract".equals(name) && !"lazy-init".equals(name)
            && !"id".equals(name);
    }

    private boolean isNamespace(String name, String prefix) {
        return "xmlns".equals(prefix) || prefix == null && "xmlns".equals(name);
    }

    protected void addBusWiringAttribute(BeanDefinitionBuilder bean,
        boolean type) {
        addBusWiringAttribute(bean, type, null, null);
    }

    protected void addBusWiringAttribute(BeanDefinitionBuilder bean,
        boolean type, String containerName, ParserContext ctx) {
        bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_ATTRIBUTE, type);
        if (containerName != null && containerName.trim().length() > 0) {
            bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_NAME,
                containerName);
        }

        if (ctx != null
            && !ctx.getRegistry().containsBeanDefinition(WIRE_CONTAINER_HANDLER)) {
            BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(WIRE_CONTAINER_HANDLER);
            ctx.getRegistry().registerBeanDefinition(WIRE_CONTAINER_HANDLER,
                b.getBeanDefinition());
        }
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    protected Class<?> getBeanClass(Element e) {
        return beanClass;
    }

}
