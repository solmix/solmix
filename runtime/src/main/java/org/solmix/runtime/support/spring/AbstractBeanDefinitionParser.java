/**
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

import java.util.StringTokenizer;

import org.solmix.commons.util.DOMUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年9月10日
 */

public class AbstractBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
{

    /** 当springContext中没有container时,但是实际却需要一个Container,
     * 这时在spring定义中预先设置一个到container的引线(wire),
     * 当container准备好后在为其设置上 */
    public static final String WIRE_CONTAINER_ATTRIBUTE = 
        AbstractBeanDefinitionParser.class.getName() + ".wireCT";

    public static final String WIRE_CONTAINER_NAME = 
        AbstractBeanDefinitionParser.class.getName() + ".wireCTName";

    /** container名称   */
    public static final String WIRE_CONTAINER_CREATE = 
        AbstractBeanDefinitionParser.class.getName() + ".wireCTCreate";

    public static final String WIRE_CONTAINER_HANDLER = ContainerPostProcessor.class.getName();

    private Class<?> beanClass;

    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder bean) {
        //属性中包含了container属性,如果已经通过spring设置上了-返回true,否则返回false.
        boolean setContainer = parseAttributes(element, ctx, bean);
        if (!setContainer && hasContainerProperty()) {
            addContainerWiringAttribute(bean, true);
        }
        parseChildElements(element, ctx, bean);
    }

    /**处理子元素*/
    protected void parseChildElements(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean) {
        Element el = DOMUtils.getFirstElement(element);
        while (el != null) {
            String name = el.getLocalName();
            parseElement(ctx, bean, el, name);
            el = DOMUtils.getNextElement(el);
        }
    }

    protected void parseElement(ParserContext ctx, BeanDefinitionBuilder bean,
        Element e, String name) {
    }

    /** 指明元素中是否包含设置container的属性*/
    protected boolean hasContainerProperty() {
        return false;
    }

    /**处理Element的属性,返回是否设置了Container*/
    protected boolean parseAttributes(Element element, 
                                      ParserContext ctx,
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
                parseNameAttribute(element, ctx, bean, val);
            } else if ("container".equals(name)) {
                setBus = parseContainerAttribute(element, ctx, bean, val);
            } else if ("id".equals(name)) {
                parseIdAttribute(bean, element, name, val, ctx);
            } else if (isAttribute(pre, name)) {
                parseAttribute(bean, element, name, val, ctx);
            }
        }
        return setBus;
    }

    protected void parseIdAttribute(BeanDefinitionBuilder bean, Element element,
        String name, String val, ParserContext ctx) {
        
    }

    protected void parseAttribute(BeanDefinitionBuilder bean, Element e,
        String name, String val, ParserContext ctx) {
        parseAttribute(bean, name, val, ctx);
    }

    protected void parseAttribute(BeanDefinitionBuilder bean, String name,
        String val, ParserContext ctx) {
        attributeToProperty(bean, name, val, ctx);
    }

    protected void attributeToProperty(BeanDefinitionBuilder bean,
        String propertyName, String val, ParserContext ctx) {
        if (ID_ATTRIBUTE.equals(propertyName)) {
            return;
        }

        if (val != null && val.trim().length() > 0) {
            bean.addPropertyValue(propertyName, val);
        }
    }

    /**Container注入处理*/
    protected boolean parseContainerAttribute(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean, String val) {
        if (val != null && val.trim().length() > 0) {
            //属性中包含Container,并且Spring中包含以val为id的Container.
            if (ctx.getRegistry().containsBeanDefinition(val)) {
                bean.addPropertyReference("container", val);
            } else {
                //spring中没有,但是需要一个Container.
                addContainerWiringAttribute(bean, true, val, ctx);
            }
            return true;
        }
        return false;
    }

    protected void parseNameAttribute(Element element, ParserContext ctx,
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

    protected void addContainerWiringAttribute(BeanDefinitionBuilder bean, boolean type) {
        addContainerWiringAttribute(bean, type, null, null);
    }

    protected void addContainerWiringAttribute(BeanDefinitionBuilder bean,
        boolean type, String containerName, ParserContext ctx) {
        bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_ATTRIBUTE, type);
        if (containerName != null && containerName.trim().length() > 0) {
            bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_NAME,
                containerName);
        }

        if (ctx != null
            && !ctx.getRegistry().containsBeanDefinition(WIRE_CONTAINER_HANDLER)) {
            //注册后置处理器,
            BeanDefinitionBuilder b = BeanDefinitionBuilder.rootBeanDefinition(WIRE_CONTAINER_HANDLER);
            ctx.getRegistry().registerBeanDefinition(WIRE_CONTAINER_HANDLER,
                b.getBeanDefinition());
        }
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }
    
    /**spring bean实例class*/
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    protected Class<?> getBeanClass(Element e) {
        return beanClass;
    }
    /**重写这个方法以解决,不设置id时,spring加载报错*/
    @Override
    protected String resolveId(Element elem, 
        AbstractBeanDefinition definition, 
        ParserContext ctx) throws BeanDefinitionStoreException {
        //自定义加载id
        String id = getIdOrName(elem);
        
        if (null == id || "".equals(id)) {
            return super.resolveId(elem, definition, ctx);
        } 
        return id;        
    }
    
    /**id 生成策略*/
    protected String getIdOrName(Element elem) {
        String id = elem.getAttribute(BeanDefinitionParserDelegate.ID_ATTRIBUTE);
        //如果ID没有设置,那么使用name
        if (null == id || "".equals(id)) {
            String names = elem.getAttribute(BeanDefinitionParserDelegate.NAME_ATTRIBUTE);
            if (null != names) {
                StringTokenizer st = new StringTokenizer(names, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
               //处理一下分隔符
                if (st.countTokens() > 0) {
                    id = st.nextToken();
                }
            }
        }
        return id;
    }
    protected Element getFirstChild(Element element) {
        return DOMUtils.getFirstElement(element);
    }
    protected void firstChildAsProperty(Element element, ParserContext ctx, 
        BeanDefinitionBuilder bean, String propertyName) {
        Element first = getFirstChild(element);
        if (first == null) {
            throw new IllegalStateException(propertyName + " property must have child elements!");
        }
        String id;
        BeanDefinition child;
        if (first.getNamespaceURI().equals(BeanDefinitionParserDelegate.BEANS_NAMESPACE_URI)) {
            String name = first.getLocalName();
            if ("ref".equals(name)) {
                id = first.getAttribute("bean");
                if (id == null) {
                    throw new IllegalStateException("<ref> elements must have a \"bean\" attribute!");
                }
                bean.addPropertyReference(propertyName, id);
                return;
            } else if ("bean".equals(name)) {
                BeanDefinitionHolder bdh = ctx.getDelegate().parseBeanDefinitionElement(first);
                child = bdh.getBeanDefinition();
                bean.addPropertyValue(propertyName, child);
                return;
            } else {
                throw new UnsupportedOperationException("Elements with the name " + name  
                                                        + " are not currently "
                                                        + "supported as sub elements of " 
                                                        + element.getLocalName());
            }
        }
        child = ctx.getDelegate().parseCustomElement(first, bean.getBeanDefinition());
        bean.addPropertyValue(propertyName, child);
    }
    /**字符串作为多值引用*/
    protected  void parseMultiRef(String property, String value,
        BeanDefinitionBuilder bean, ParserContext parserContext) {
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
        bean.getBeanDefinition().getPropertyValues().addPropertyValue(property, list);
    }
}
