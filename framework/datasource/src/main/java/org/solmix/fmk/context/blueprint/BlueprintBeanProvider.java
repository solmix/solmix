/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.fmk.context.blueprint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.fmk.base.Reflection;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class BlueprintBeanProvider implements ConfiguredBeanProvider
{

    private static final Logger LOG = LoggerFactory.getLogger(BlueprintConfigurer.class);

    BundleContext bundleContext;

    BlueprintContainer blueprintContainer;

    ConfiguredBeanProvider original;

    public BlueprintBeanProvider(ConfiguredBeanProvider original, BlueprintContainer blueprintContainer, BundleContext bundleContext)
    {
        this.bundleContext = bundleContext;
        this.blueprintContainer = blueprintContainer;
        this.original = original;

    }

    private ComponentMetadata getComponentMetadata(String id) {
        try {
            return blueprintContainer.getComponentMetadata(id);
        } catch (NoSuchComponentException nsce) {
            return null;
        }
    }

    static Class<?> getClassForMetaData(BlueprintContainer container, ComponentMetadata cmd) {
        Class<?> cls = null;
        if (cmd instanceof BeanMetadata) {
            BeanMetadata bm = (BeanMetadata) cmd;
            // XXX for aries
            /*
             * if (bm instanceof ExtendedBeanMetadata) { cls = ((ExtendedBeanMetadata)bm).getRuntimeClass(); }
             */
            if (cls == null) {
                try {
                    Method m = Reflection.findMethod(container.getClass().getName(), "loadClass", String.class);
                    cls = (Class<?>) m.invoke(container, bm.getClassName());
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return cls;
    }

    private Class<?> getClassForMetaData(ComponentMetadata cmd) {
        return getClassForMetaData(blueprintContainer, cmd);
    }

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        Set<String> names = new LinkedHashSet<String>();
        for (String s : blueprintContainer.getComponentIds()) {
            ComponentMetadata cmd = blueprintContainer.getComponentMetadata(s);
            Class<?> cls = getClassForMetaData(cmd);
            if (cls != null && type.isAssignableFrom(cls)) {
                names.add(s);
            }
        }
        if (original != null) {
            List<String> origs = original.getBeanNamesOfType(type);
            if (origs != null)
                names.addAll(original.getBeanNamesOfType(type));
        }

        return new ArrayList<String>(names);
    }

    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        ComponentMetadata cmd = getComponentMetadata(name);
        Class<?> cls = getClassForMetaData(cmd);
        if (cls != null && type.isAssignableFrom(cls)) {
            return type.cast(blueprintContainer.getComponentInstance(name));
        }
        if(original!=null){
            return original.getBeanOfType(name, type);
        }
        return null;
    }

    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        List<T> list = new ArrayList<T>();

        for (String s : blueprintContainer.getComponentIds()) {
            ComponentMetadata cmd = blueprintContainer.getComponentMetadata(s);
            Class<?> cls = getClassForMetaData(cmd);
            if (cls != null && type.isAssignableFrom(cls)) {
                list.add(type.cast(blueprintContainer.getComponentInstance(s)));
            }
        }
        if(original!=null){
            Collection<? extends T> origs=  original.getBeansOfType(type);
            if(origs!=null)
                list.addAll(origs);
        }
        if (list.isEmpty()) {
            try {
                ServiceReference<?> refs[] = bundleContext.getServiceReferences(type.getName(), null);
                if (refs != null) {
                    for (ServiceReference<?> r : refs) {
                        list.add(type.cast(bundleContext.getService(r)));
                    }
                }
            } catch (Exception ex) {
                // ignore, just don't support the OSGi services
                LOG.info("Try to find the Bean with type:" + type + " from OSGi services and get error: " + ex);
            }
        }

        return list;
    }

    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
        List<String> names = new ArrayList<String>();
        boolean loaded = false;
        for (String s : blueprintContainer.getComponentIds()) {
            ComponentMetadata cmd = blueprintContainer.getComponentMetadata(s);
            Class<?> cls = getClassForMetaData(cmd);
            if (cls != null && type.isAssignableFrom(cls)) {
                names.add(s);
            }
        }
        Collections.reverse(names);
        for (String s : names) {
            ComponentMetadata cmd = blueprintContainer.getComponentMetadata(s);
            Class<?> beanType = getClassForMetaData(cmd);
            Class<? extends T> t = beanType.asSubclass(type);
            if (listener.loadBean(s, t)) {
                Object o = blueprintContainer.getComponentInstance(s);
                if (listener.beanLoaded(s, type.cast(o))) {
                    return true;
                }
                loaded = true;
            }
        }
        return loaded || original.loadBeansOfType(type, listener);
    }

    @Override
    public boolean hasBeanOfName(String name) {
        ComponentMetadata cmd = getComponentMetadata(name);
        if (cmd instanceof BeanMetadata) {
            return true;
        }
        return original.hasBeanOfName(name);
    }

}
