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

package org.solmix.runtime.adapter.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerExtension;
import org.solmix.runtime.adapter.AdapterFactory;
import org.solmix.runtime.adapter.AdapterManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年4月24日
 */

public class AdapterManagerImpl implements AdapterManager, ContainerExtension
{

    private Map<String, Map<String, AdapterFactory>> factoryCache;

    private Map<Class<?>, Class<?>[]> classSearchCache;
    final Map<String,List<AdapterFactory>> factories;
    private Container container;
    protected  final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public AdapterManagerImpl(){
        factories=new HashMap<String,List<AdapterFactory>>();
    }
    public AdapterManagerImpl(final Container systemContext)
    {
        this();
        setContainer(systemContext);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#getAdapter(java.lang.Object,
     *      java.lang.Class)
     */
    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable,
        Class<AdapterType> type) {
        Assert.isNotNull(adaptable);
        Assert.isNotNull(type);
        Map<String, AdapterFactory> factories = getAdapterFactories(adaptable.getClass());
        AdapterFactory factory = factories.get(type.getName());
        AdapterType result = null;
        if (factory != null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Using adapter factory " + factory + " to map "
                    + adaptable + " to " + type);
            }
            result = factory.getAdapter(adaptable, type);
        }
        return result;
    }

    /**
     * @param class1
     * @return
     */
    private Map<String, AdapterFactory> getAdapterFactories(Class<?> clazz) {
        Map<String, Map<String, AdapterFactory>> cache = factoryCache;
        if (cache == null)
            factoryCache = cache = Collections.synchronizedMap(new HashMap<String, Map<String, AdapterFactory>>(
                30));
        Map<String, AdapterFactory> table = cache.get(clazz.getName());
        if (table != null) {
            table = new HashMap<String, AdapterFactory>(4);
            Class<?>[] classes = computeClassOrder(clazz);
            for (int i = 0; i < classes.length; i++)
                addFactoriesForClass(classes[i].getName(), table);
                // cache the table
                cache.put(clazz.getName(), table);
        }
        return table;
    }

    private void addFactoriesForClass(String typeName,
        Map<String, AdapterFactory> table) {
        List<AdapterFactory> factoryList = getFactories().get(typeName);
        if (factoryList == null)
            return;
        for (int i = 0, imax = factoryList.size(); i < imax; i++) {
            AdapterFactory factory = factoryList.get(i);

            Class<?>[] adapters = factory.getAdapterList();
            for (int j = 0; j < adapters.length; j++) {
                String adapterName = adapters[j].getName();
                if (table.get(adapterName) == null)
                    table.put(adapterName, factory);
            }
        }
    }

    /**
     * @return
     */
    protected Map<String, List< AdapterFactory>> getFactories() {
        return factories;
    }

    /**
     * @param clazz
     * @return
     */
    private Class<?>[] computeClassOrder(Class<?> clazz) {
        Class<?>[] classes = null;
        Map<Class<?>, Class<?>[]> cache = classSearchCache;
        if (classSearchCache == null)
            classSearchCache = cache = Collections.synchronizedMap(new HashMap<Class<?>, Class<?>[]>());
        else
            classes = cache.get(clazz);
        if (classes == null) {
            classes = doComputeClassOrder(clazz);
            cache.put(clazz, classes);
        }
        return classes;
    }

    private Class<?>[] doComputeClassOrder(Class<?> adaptable) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        Class<?> clazz = adaptable;
        Set<Class<?>> seen = new HashSet<Class<?>>(4);
        // first traverse class hierarchy
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        // now traverse interface hierarchy for each class
        Class<?>[] classHierarchy = classes.toArray(new Class[classes.size()]);
        for (int i = 0; i < classHierarchy.length; i++)
            computeInterfaceOrder(classHierarchy[i].getInterfaces(), classes,
                seen);
        return classes.toArray(new Class[classes.size()]);
    }

    private void computeInterfaceOrder(Class<?>[] interfaces,
        Collection<Class<?>> classes, Set<Class<?>> seen) {
        List<Class<?>> newInterfaces = new ArrayList<Class<?>>(
            interfaces.length);
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> interfac = interfaces[i];
            if (seen.add(interfac)) {
                // note we cannot recurse here without changing the resulting
                // interface order
                classes.add(interfac);
                newInterfaces.add(interfac);
            }
        }
        for (Iterator<Class<?>> it = newInterfaces.iterator(); it.hasNext();)
            computeInterfaceOrder(it.next().getInterfaces(), classes, seen);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#hasAdapter(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public boolean hasAdapter(Object adaptable, String adapterTypeName) {
       return getAdapterFactories(adaptable.getClass()).get(adapterTypeName)!=null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#registerAdapters(org.solmix.runtime.adapter.AdapterFactory,
     *      java.lang.Class)
     */
    @Override
    public void registerAdapters(AdapterFactory factory, Class<?> adaptable) {
        List<AdapterFactory> list =  factories.get(adaptable.getName());
        if (list == null) {
              list = new ArrayList<AdapterFactory>(5);
              factories.put(adaptable.getName(), list);
        }
        list.add(factory);
        flush();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#unregisterAdapters(org.solmix.runtime.adapter.AdapterFactory)
     */
    @Override
    public void unregisterAdapters(AdapterFactory factory) {
        for (Iterator<List<AdapterFactory>> it = factories.values().iterator(); it.hasNext();)
            it.next().remove(factory);
        flush();
    }
    public synchronized void flush() {
        this.factoryCache=null;
        this.classSearchCache=null;
  }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#unregisterAdapters(org.solmix.runtime.adapter.AdapterFactory,
     *      java.lang.Class)
     */
    @Override
    public void unregisterAdapters(AdapterFactory factory, Class<?> adaptable) {
        List<AdapterFactory> lf = factories.get(adaptable.getName());
        if (lf != null) {
            lf.remove(factory);
        }
        flush();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.ContainerExtension#setContainer(org.solmix.runtime.Container)
     */
    @Override
    public void setContainer(Container container) {
        this.container=container;
        container.setBean(this,AdapterManager.class);
    }

   protected Container getContainer(){
        return container;
    }
}
