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

package org.solmix.fmk.context.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.runtime.SystemContext;
import org.springframework.beans.Mergeable;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-4
 */

public class SpringBeanProvider implements ConfiguredBeanProvider
{

    private final ApplicationContext context;

    private ConfiguredBeanProvider original;

    private final Set<String> passThroughs = new HashSet<String>();

    public SpringBeanProvider(ApplicationContext context)
    {
        this(context, null);
    }

    /**
     * @param applicationContext
     * @param systemContext
     */
    public SpringBeanProvider(ApplicationContext applicationContext, SystemContext system)
    {
        this.context = applicationContext;
        if (system != null) {
            original = system.getBean(ConfiguredBeanProvider.class);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.ConfiguredBeanProvider#getBeanNamesOfType(java.lang.Class)
     */
    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        Set<String> s = new LinkedHashSet<String>(Arrays.asList(context.getBeanNamesForType(type, false, false)));
        if(context.getParent()!=null){
            s.addAll(_doGetBeanNamesOfType(context.getParent(),type));
        }
        s.removeAll(passThroughs);
        if (original != null) {
            List<String> origs = original.getBeanNamesOfType(type);
            if (origs != null)
                s.addAll(original.getBeanNamesOfType(type));
        }
        return new ArrayList<String>(s);
    }

    private Set<String> _doGetBeanNamesOfType(ApplicationContext context,Class<?> type) {
        
        Set<String> s = new LinkedHashSet<String>(Arrays.asList(context.getBeanNamesForType(type, false, false)));
        if(context.getParent()!=null){
            s.addAll(_doGetBeanNamesOfType(context.getParent(),type));
        }
        return s;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.ConfiguredBeanProvider#getBeanOfType(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        T t = null;
        try {
            t = type.cast(context.getBean(name, type));
        } catch (NoSuchBeanDefinitionException nsbde) {
            // ignore
        }
        if(t==null&&context.getParent()!=null){
            t=_dogetBeanOfType(context.getParent(),name,type);
        }
        if (t == null && original != null) {
            t = original.getBeanOfType(name, type);
        }
        return t;
    }
    //get bean from parent;
    private <T> T _dogetBeanOfType(ApplicationContext context,String name, Class<T> type){
        T t = null;
        if(context!=null){
            t = type.cast(context.getBean(name, type));
            if(t==null&&context.getParent()!=null)
                t=_dogetBeanOfType(context.getParent(),name,type);
        }
        return t;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.ConfiguredBeanProvider#getBeansOfType(java.lang.Class)
     */
    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        Set<String> s = new LinkedHashSet<String>(Arrays.asList(context.getBeanNamesForType(type, false, false)));
        s.removeAll(passThroughs);
        List<T> lst = new LinkedList<T>();
        for (String n : s) {
            lst.add(type.cast(context.getBean(n, type)));
        }
        if(context.getParent()!=null){
            lst.addAll(_doGetBeansOfType(context.getParent(),type));
        }
        if(original!=null){
            Collection<? extends T> origs=  original.getBeansOfType(type);
            if(origs!=null)
                lst.addAll(origs);
        }
        return lst;
    }
    //get bean from parent;
    private <T> Collection<? extends T> _doGetBeansOfType(ApplicationContext context,Class<T> type){
        if(context!=null){
            Set<String> s = new LinkedHashSet<String>(Arrays.asList(context.getBeanNamesForType(type, false, false)));
            s.removeAll(passThroughs);
            List<T> lst = new LinkedList<T>();
            for (String n : s) {
                lst.add(type.cast(context.getBean(n, type)));
            }    
            if(context.getParent()!=null){
                lst.addAll(_doGetBeansOfType(context.getParent(),type));
            }
            return lst;
        }
        return null;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.ConfiguredBeanProvider#loadBeansOfType(java.lang.Class,
     *      org.solmix.api.bean.ConfiguredBeanProvider.BeanLoaderListener)
     */
    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
        List<String> list = new ArrayList<String>(Arrays.asList(context.getBeanNamesForType(type, false, false)));
        list.removeAll(passThroughs);
        Collections.reverse(list);
        boolean loaded = false;
        for (String s : list) {
            Class<?> beanType = context.getType(s);
            Class<? extends T> t = beanType.asSubclass(type);
            if (listener.loadBean(s, t)) {
                Object o = context.getBean(s);
                if (listener.beanLoaded(s, type.cast(o))) {
                    return true;
                }
                loaded = true;
            }
        }
        return loaded || original.loadBeansOfType(type, listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.ConfiguredBeanProvider#hasBeanOfName(java.lang.String)
     */
    @Override
    public boolean hasBeanOfName(String name) {
        if (context.containsBean(name)) {
            return true;
        }
        return original.hasBeanOfName(name);
    }
    
    public boolean hasConfiguredPropertyValue(String beanName, String propertyName, String searchValue) {
        if (context.containsBean(beanName) && !passThroughs.contains(beanName)) {
            ConfigurableApplicationContext ctxt = (ConfigurableApplicationContext)context;
            BeanDefinition def = ctxt.getBeanFactory().getBeanDefinition(beanName);
            if (!ctxt.getBeanFactory().isSingleton(beanName) || def.isAbstract()) {
                return false;
            }
            Collection<?> ids = null;
            PropertyValue pv = def.getPropertyValues().getPropertyValue(propertyName);
            
            if (pv != null) {
                Object value = pv.getValue();
                if (!(value instanceof Collection)) {
                    throw new RuntimeException("The property " + propertyName + " must be a collection!");
                }
    
                if (value instanceof Mergeable) {
                    if (!((Mergeable)value).isMergeEnabled()) {
                        ids = (Collection<?>)value;
                    }
                } else {
                    ids = (Collection<?>)value;
                }
            } 
            
            if (ids != null) {
                for (Iterator<?> itr = ids.iterator(); itr.hasNext();) {
                    Object o = itr.next();
                    if (o instanceof TypedStringValue) {
                        if (searchValue.equals(((TypedStringValue) o).getValue())) {
                            return true;
                        }
                    } else {
                        if (searchValue.equals(o)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
//        return orig.hasConfiguredPropertyValue(beanName, propertyName, searchValue);
    }

}
