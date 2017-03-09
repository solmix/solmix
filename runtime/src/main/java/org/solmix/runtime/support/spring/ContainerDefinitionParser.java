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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.extension.ContainerReference;
import org.solmix.runtime.extension.ExtensionContainer;
import org.solmix.runtime.transaction.TxProxyRule;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年9月11日
 */

public class ContainerDefinitionParser extends AbstractBeanDefinitionParser
    implements BeanDefinitionParser {

    private static AtomicInteger counter = new AtomicInteger(0);

    public ContainerDefinitionParser() {
        super();
        setBeanClass(ContainerType.class);
    }

    @Override
    protected void doParse(Element element, ParserContext ctx,
        BeanDefinitionBuilder bean) {
        String cname = element.getAttribute("name");
        String id = element.getAttribute("id");
       //container name 为空
        if (StringUtils.isEmpty(cname)) {
            // 使用id作为name
            if (!StringUtils.isEmpty(id)) {
                cname = id;
            } else {
                cname = "solmix";
            }
        }
        super.doParse(element, ctx, bean);
        if (ctx.getRegistry().containsBeanDefinition(cname)) {
            BeanDefinition def = ctx.getRegistry().getBeanDefinition(cname);
            copyProps(bean, def);
            bean.addConstructorArgValue(cname);
        } else if (!"solmix".equals(cname)) {
            bean.getRawBeanDefinition().setBeanClass(SpringContainer.class);
            bean.setDestroyMethodName("close");
            try {
                element.setAttribute("ID", cname);
            } catch (Throwable t) {
                //ignore
            }
        } else {
            addContainerWiringAttribute(bean, true, cname, ctx);
            bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_CREATE,
                resolveId(element, null, ctx));
            bean.addConstructorArgValue(cname);
        }

    }

    private void copyProps(BeanDefinitionBuilder src, BeanDefinition def) {
        for (PropertyValue v : src.getBeanDefinition().getPropertyValues().getPropertyValues()) {
            if (!"name".equals(v.getName())) {
                def.getPropertyValues().addPropertyValue(v.getName(),
                    v.getValue());
            }
            src.getBeanDefinition().getPropertyValues().removePropertyValue(v);
        }

    }

    @Override
    protected String resolveId(Element element,
        AbstractBeanDefinition definition, ParserContext ctx) {
        String container = null;
        try {
            container = element.getAttribute("ID");
        } catch (Throwable t) {
            // ignore
        }
        if (DataUtils.isNullOrEmpty(container)) {
            container = element.getAttribute("name");

            if (StringUtils.isEmpty(container)) {
                container = Container.DEFAULT_CONTAINER_ID + ".config"
                    + counter.getAndIncrement();
            } else {
                container = container + ".config";
            }
            try {
                element.setAttribute("ID", container);
            } catch (Throwable t) {
                t.printStackTrace();
                // maybe no DOM level 3, ignore, but, may have issues with the
                // counter
            }
        }
        return container;
    }

    @Override
    protected void parseElement(ParserContext ctx, BeanDefinitionBuilder bean,
        Element e, String name) {
        if ("properties".equals(name)) {
            Map<?, ?> map = ctx.getDelegate().parseMapElement(e,
                bean.getBeanDefinition());
            bean.addPropertyValue("properties", map);
        } else if ("listeners".equals(name)) {
            List<?> lis = ctx.getDelegate().parseListElement(e,
                bean.getBeanDefinition());
            bean.addPropertyValue("containerListeners", lis);
        } else if ("bindings".equals(name)) {
            List<?> lis = ctx.getDelegate().parseListElement(e,
                bean.getBeanDefinition());
            bean.addPropertyValue("extensionBindings", lis);
        } else if ("ref".equals(name)) {
        	BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(ContainerReference.class);
        	parseRefAttributes(e, ctx, component);
        	bean.addPropertyValue("reference", component.getBeanDefinition());
        } else if ("tx".equals(name)) {
        	BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(TxProxyRule.class);
        	parseAttribute(component,"proxy-target-class","proxyTargetClass",e);
        	parseAttribute(component,"filter",e);
        	parseAttribute(component,"optimize",e);
        	parseAttribute(component,"expose","exposeProxy",e);
        	String managerId = e.getAttribute("manager");
        	if(DataUtils.isNotNullAndEmpty(managerId)){
        		component.addPropertyReference("transactionManager", managerId);
        	}
        	bean.addPropertyValue("proxyRule", component.getBeanDefinition());
        }
    }
    
    private void parseAttribute(BeanDefinitionBuilder component,String name,String property,Element e){
    	String value = e.getAttribute(name);
		if (value != null && value.length() > 0) {
			component.addPropertyValue(property, value);
		}
    }
    
    private void parseAttribute(BeanDefinitionBuilder component,String name,Element e){
    	parseAttribute(component,name,name,e);
    }
    
    @Override
	protected void parseIdAttribute(BeanDefinitionBuilder bean, Element element,
            String name, String val, ParserContext ctx) {
    	bean.addPropertyValue(BeanDefinitionParserDelegate.ID_ATTRIBUTE, val);
        }
	protected boolean parseRefAttributes(Element element, ParserContext ctx,BeanDefinitionBuilder bean) {
		NamedNodeMap atts = element.getAttributes();
		boolean setContainer = false;
		for (int i = 0; i < atts.getLength(); i++) {
			Attr node = (Attr) atts.item(i);
			String val = node.getValue();
			String name = node.getLocalName();
			String prefix = node.getPrefix();
			if (isNamespace(name, prefix)) {
				continue;
			}
			 String propertyName=name;
			 if ("container-id".equals(name)) {
				 propertyName="id";
			} 
			 if (val != null && val.trim().length() > 0) {
		            bean.addPropertyValue(propertyName, val);
		       }
		}
		return setContainer;
	}
    public static class ContainerType implements ApplicationContextAware {

        Container container;

        String name;

        String id;
        
        Boolean production;

        Map<String, Object> properties;
        private List<ContainerReference> references;
        public ContainerType(String name) {
            this.name = name;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {

        }

        public void setContainer(Container bb) {
            if (container == bb) {
                return;
            }
            container = bb;
            if (properties != null) {
                container.setProperties(properties);
                properties = null;
            }

            if (!StringUtils.isEmpty(id)) {
                container.setId(id);
            }else{
            	if(!StringUtils.isEmpty(name)){
            		container.setId(name);
            	}
            }
            if(production!=null){
                container.setProduction(production.booleanValue());
            }
           if(references!=null){
        	   ((ExtensionContainer)container).setReferences(references);
           }
        }

        public Map<String, Object> getProperties() {
            if (container != null) {
                return container.getProperties();
            }
            return properties;
        }

        public void setProperties(Map<String, Object> s) {
            if (container != null) {
                container.setProperties(s);
            } else {
                this.properties = s;
            }
        }

        public void setId(String s) {
            id = s;
        }
        
        public void setProduction(Boolean production){
            this.production=production;
        }
        public void setReference(ContainerReference ref){
        	if(references==null){
        		references = new ArrayList<ContainerReference>();
        	}
        	references.add(ref);
        }
    }
}
