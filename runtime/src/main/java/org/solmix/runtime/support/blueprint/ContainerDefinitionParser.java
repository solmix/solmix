/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.runtime.support.blueprint;

import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.extension.ContainerReference;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年11月4日
 */

public class ContainerDefinitionParser extends AbstractBPBeanDefinitionParser
{
    public ContainerDefinitionParser() {
    }
    
    public Metadata parse(Element element, ParserContext context) {
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
       
        MutableBeanMetadata cBean = getContainer(context, cname);
        parseAttributes(element, context, cBean);
        parseChildElements(element, context, cBean);
        context.getComponentDefinitionRegistry().removeComponentDefinition(cname);
        if (!StringUtils.isEmpty(cname)) {
            cBean.addProperty("id", createValue(context, cname));
        }
        return cBean;
    }
    
  
    
    @Override
    protected boolean hasContainerProperty() {
        return false;
    }
    @Override
    protected void parseElement(ParserContext ctx, MutableBeanMetadata bean, Element el, String name) {
    
        if ("properties".equals(name)) {
            bean.addProperty(name, parseMapData(ctx, bean, el));
        } else if ("listeners".equals(name)) {
            bean.addProperty("containerListeners", parseListData(ctx, bean, el));
        }else if ("ref".equals(name)) {
        	MutableBeanMetadata meta = ctx.createMetadata(MutableBeanMetadata.class);
        	NamedNodeMap atts = el.getAttributes();
    		for (int i = 0; i < atts.getLength(); i++) {
    			Attr node = (Attr) atts.item(i);
    			String val = node.getValue();
    			String attrName = node.getLocalName();
    			String prefix = node.getPrefix();
    			if (isNamespace(name, prefix)) {
    				continue;
    			}
    			 String propertyName=attrName;
    			 if ("container-id".equals(attrName)) {
    				 propertyName="id";
    			} 
    			 if (val != null && val.trim().length() > 0) {
    		           bean.addProperty(propertyName, createValue(ctx, val));
    		       }
    		}
            meta.setRuntimeClass(ContainerReference.class);
            bean.addProperty("reference",  meta);
        }
    }
   
}
