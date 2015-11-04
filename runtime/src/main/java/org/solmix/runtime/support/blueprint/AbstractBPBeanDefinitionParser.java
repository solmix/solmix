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

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.aries.blueprint.ComponentDefinitionRegistry;
import org.apache.aries.blueprint.ParserContext;
import org.apache.aries.blueprint.PassThroughMetadata;
import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.apache.aries.blueprint.mutable.MutableCollectionMetadata;
import org.apache.aries.blueprint.mutable.MutablePassThroughMetadata;
import org.apache.aries.blueprint.mutable.MutableRefMetadata;
import org.apache.aries.blueprint.mutable.MutableValueMetadata;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.service.blueprint.reflect.MapMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.osgi.service.blueprint.reflect.RefMetadata;
import org.osgi.service.blueprint.reflect.ValueMetadata;
import org.solmix.commons.util.DOMUtils;
import org.solmix.commons.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年11月3日
 */

public abstract class AbstractBPBeanDefinitionParser
{
    private static final String XMLNS_BLUEPRINT = "http://www.osgi.org/xmlns/blueprint/v1.0.0";
    private static final String COMPONENT_ID = "component-id";

    protected boolean hasContainerProperty() {
        return false;
    }

    public Metadata createValue(ParserContext context, QName qName) {
        MutableBeanMetadata v = context.createMetadata(MutableBeanMetadata.class);
        v.setRuntimeClass(QName.class);
        v.addArgument(createValue(context, qName.getNamespaceURI()), null, 0);
        v.addArgument(createValue(context, qName.getLocalPart()), null, 1);
        return v;
    }

    protected Metadata parseListData(ParserContext context, 
                                     ComponentMetadata enclosingComponent, 
                                     Element element) {
        MutableCollectionMetadata m 
            = (MutableCollectionMetadata) context.parseElement(CollectionMetadata.class, 
                                                               enclosingComponent, element);
        m.setCollectionClass(List.class);
        return m;
    }

    protected Metadata parseMapData(ParserContext context, 
                                    ComponentMetadata enclosingComponent, 
                                    Element element) {
        return context.parseElement(MapMetadata.class, enclosingComponent, element);
    }

    protected void firstChildAsProperty(Element element, 
                                           ParserContext ctx, 
                                           MutableBeanMetadata bean, 
                                           String propertyName) {

        Element first = DOMUtils.getFirstElement(element);

        if (first == null) {
            throw new IllegalStateException(propertyName + " property must have child elements!");
        }

        String id;
        if (first.getNamespaceURI().equals(XMLNS_BLUEPRINT)) {
            String name = first.getLocalName();
            if ("ref".equals(name)) {
                id = first.getAttribute(COMPONENT_ID);
                if (id == null) {
                    throw new IllegalStateException("<ref> elements must have a \"component-id\" attribute!");
                }
                bean.addProperty(propertyName, createRef(ctx, id));
            } else {
                //Rely on BP to handle these ones.
                bean.addProperty(propertyName, ctx.parseElement(Metadata.class, bean, first));
            }
        } else {
            bean.addProperty(propertyName, ctx.parseElement(Metadata.class, bean, first));
        }
    }

    public QName parseQName(Element element, String t) {
        String t1 = t;
        String ns = null;
        String pre = null;
        String local = null;

        if (t1.startsWith("{")) {
            int i = t1.indexOf('}');
            if (i == -1) {
                throw new RuntimeException("Namespace bracket '{' must having a closing bracket '}'.");
            }

            ns = t1.substring(1, i);
            t1 = t1.substring(i + 1);
        }

        int colIdx = t1.indexOf(':');
        if (colIdx == -1) {
            local = t1;
            pre = "";

            ns = DOMUtils.getNamespace(element, "");
        } else {
            pre = t1.substring(0, colIdx);
            local = t1.substring(colIdx + 1);

            ns = DOMUtils.getNamespace(element, pre);
        }

        return new QName(ns, local, pre);
    }

    protected boolean parseAttributes(Element element, ParserContext ctx, MutableBeanMetadata bean) {
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

            if ("createdFromAPI".equals(name) || "abstract".equals(name)) {
                bean.setScope(BeanMetadata.SCOPE_PROTOTYPE);
            } else {
                if ("depends-on".equals(name)) {
                    bean.addDependsOn(val);
                } else if ("name".equals(name)) {
                    processNameAttribute(element, ctx, bean, val);
                } else if ("container".equals(name)) {
                    parseContainerAttribute(element, ctx, bean, val);
                } else if (!"id".equals(name) && isAttribute(pre, name)) {
                    mapAttribute(bean, element, name, val, ctx);
                }
            }
        }
        return setBus;
    }
    protected void parseContainerAttribute(Element element, ParserContext ctx, 
                                       MutableBeanMetadata bean, String val) {
        if (this.hasContainerProperty()) {
            bean.addProperty("container", getContainerRef(ctx, val));
        } else {
            bean.addArgument(getContainerRef(ctx, val), null, 0);
        }
    }

    protected void processNameAttribute(Element element,
                                        ParserContext ctx,
                                        MutableBeanMetadata bean,
                                        String val) {
        //nothing
    }
    protected void mapAttribute(MutableBeanMetadata bean, Element e, 
                                String name, String val, ParserContext context) {
        mapToProperty(bean, name, val, context);
    }

    protected boolean isAttribute(String pre, String name) {
        return !"xmlns".equals(name) && (pre == null || !pre.equals("xmlns")) 
            && !"abstract".equals(name) && !"lazy-init".equals(name) 
            && !"id".equals(name);
    }

    protected boolean isNamespace(String name, String prefix) {
        return "xmlns".equals(prefix) || prefix == null && "xmlns".equals(name);
    }

    protected void parseElement(ParserContext ctx, MutableBeanMetadata bean, Element el, String name) {
    }

    protected void mapToProperty(MutableBeanMetadata bean, 
                                 String propertyName, 
                                 String val, 
                                 ParserContext context) {
        if ("id".equals(propertyName)) {
            return;
        }

        if (!StringUtils.isEmpty(val)) {
            if (val.startsWith("#")) {
                bean.addProperty(propertyName, createRef(context, val.substring(1)));
            } else {
                bean.addProperty(propertyName, createValue(context, val));
            }
        }
    }

    public static ValueMetadata createValue(ParserContext context, String value) {
        MutableValueMetadata v = context.createMetadata(MutableValueMetadata.class);
        v.setStringValue(value);
        return v;
    }

    public static RefMetadata createRef(ParserContext context, String value) {
        MutableRefMetadata r = context.createMetadata(MutableRefMetadata.class);
        r.setComponentId(value);
        return r;
    }

    public static PassThroughMetadata createPassThrough(ParserContext context, Object value) {
        MutablePassThroughMetadata v = context.createMetadata(MutablePassThroughMetadata.class);
        v.setObject(value);
        return v;
    }

    public static MutableBeanMetadata createObjectOfClass(ParserContext context, String value) {
        MutableBeanMetadata v = context.createMetadata(MutableBeanMetadata.class);
        v.setClassName(value);
        return v;
    }

    protected MutableBeanMetadata getContainer(ParserContext context, String name) {
        ComponentDefinitionRegistry cdr = context.getComponentDefinitionRegistry();
        ComponentMetadata meta = cdr.getComponentDefinition("blueprintBundle");
        if (!cdr.containsComponentDefinition(name)) {
            //Create a bus

            MutableBeanMetadata container = context.createMetadata(MutableBeanMetadata.class);
            container.setId(name);
            container.setRuntimeClass(BpContainer.class);
            if (meta != null) {
                //blueprint-no-osgi does not provide a bundleContext
                container.addProperty("bundleContext", createRef(context, "blueprintBundleContext"));
            }
            container.addProperty("blueprintContainer", createRef(context, "blueprintContainer"));
            container.setDestroyMethod("close");
            container.setInitMethod("initialize");

            context.getComponentDefinitionRegistry().registerComponentDefinition(container);

            return container;
        }
        return (MutableBeanMetadata) cdr.getComponentDefinition(name);
    }

    protected RefMetadata getContainerRef(ParserContext context, String name) {
        if ("solmix".equals(name)) {
            getContainer(context, name);
        }
        return createRef(context, name);
    }

    protected void parseChildElements(Element element, ParserContext ctx, MutableBeanMetadata bean) {
        Element el = DOMUtils.getFirstElement(element);
        while (el != null) {
            String name = el.getLocalName();
            parseElement(ctx, bean, el, name);
            el = DOMUtils.getNextElement(el);
        }
    }
}
