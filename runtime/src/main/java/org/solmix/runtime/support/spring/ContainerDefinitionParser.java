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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Element;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年9月11日
 */

public class ContainerDefinitionParser extends AbstractBeanDefinitionParser
    implements BeanDefinitionParser
{
    private static AtomicInteger counter = new AtomicInteger(0);

    public ContainerDefinitionParser(){
        super();
        setBeanClass(ContainerType.class);
    }
    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder bean) {
        String cname=element.getAttribute("name");
        String id = element.getAttribute("id");
        if (!StringUtils.isEmpty(id)) {
            bean.addPropertyValue("id", id);
        }
        if(StringUtils.isEmpty(cname)){
            cname="solmix";
        }
        super.doParse(element, ctx, bean);
        if(ctx.getRegistry().containsBeanDefinition(cname)){
            BeanDefinition def = ctx.getRegistry().getBeanDefinition(cname);
            copyProps(bean, def);
            bean.addConstructorArgValue(cname);
        }else if(!"solmix".equals(cname)){
            bean.getRawBeanDefinition().setBeanClass(SpringContainer.class);
            bean.setDestroyMethodName("close");
            try {
                element.setUserData("ID", cname, null);
            } catch (Throwable t) {
            }
        }else{
            addContainerWiringAttribute(bean, true, cname, ctx);
            bean.getRawBeanDefinition().setAttribute(WIRE_CONTAINER_CREATE, 
                resolveId(element, null, ctx));
            bean.addConstructorArgValue(cname);
        }
        
        
    }
    private void copyProps(BeanDefinitionBuilder src, BeanDefinition def) {
        for (PropertyValue v : src.getBeanDefinition().getPropertyValues().getPropertyValues()) {
            if (!"name".equals(v.getName())) {
                def.getPropertyValues().addPropertyValue(v.getName(), v.getValue());
            }
            src.getBeanDefinition().getPropertyValues().removePropertyValue(v);
        }
        
    }
    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, 
                               ParserContext ctx) {
        String container = null;
        try {
            container = (String)element.getUserData("ID");
        } catch (Throwable t) {
            //ignore
        }
        if (container == null) {
            container = element.getAttribute("name");        
            
            if (StringUtils.isEmpty(container)) {
                container = Container.DEFAULT_CONTAINER_ID + ".config" + counter.getAndIncrement();
            } else {
                container =container + ".config";
            }
            try {
                element.setUserData("ID", container, null);
            } catch (Throwable t) {
                //maybe no DOM level 3, ignore, but, may have issues with the counter 
            }
        }
        return container;
    }
    @Override
    protected void parseElement(ParserContext ctx, BeanDefinitionBuilder bean,
        Element e, String name) {
        if ("properties".equals(name)) {
            Map<?, ?> map = ctx.getDelegate().parseMapElement(e, bean.getBeanDefinition());
            bean.addPropertyValue("properties", map);
        }
    }
   public static class ContainerType implements ApplicationContextAware
    {

        Container container;

        String name;

        String id;

        Map<String, Object> properties;

        public ContainerType(String name)
        {
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
            }
            /*
             * if (features != null) { container.setFeatures(features); features
             * = null; }
             */
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
    }
}
