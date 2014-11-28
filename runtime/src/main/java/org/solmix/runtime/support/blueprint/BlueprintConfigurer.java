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

package org.solmix.runtime.support.blueprint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Reflection;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.Configurable;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class BlueprintConfigurer implements BeanConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(BlueprintConfigurer.class);

    private final BlueprintContainer blueprintContainer;

    private final Map<String, List<MatcherHolder>> wildCardBeanDefinitions = new HashMap<String, List<MatcherHolder>>();

    static class MatcherHolder {

        Matcher matcher;

        String wildCardId;

        public MatcherHolder(String orig, Matcher matcher) {
            wildCardId = orig;
            this.matcher = matcher;
        }
    }

    public BlueprintConfigurer(BlueprintContainer container) {
        this.blueprintContainer = container;
        initializeWildcardMap();
    }

    private void initializeWildcardMap() {
        for (String s : blueprintContainer.getComponentIds()) {
            if (isWildcardBeanName(s)) {
                ComponentMetadata cmd = blueprintContainer.getComponentMetadata(s);
                Class<?> cls = BlueprintBeanProvider.getClassForMetaData(blueprintContainer, cmd);
                if (cls != null) {
                    String orig = s;
                    if (s.charAt(0) == '*') {
                        // old wildcard
                        s = "." + s.replaceAll("\\.", "\\.");
                    }
                    Matcher matcher = Pattern.compile(s).matcher("");
                    List<MatcherHolder> m = wildCardBeanDefinitions.get(cls.getName());
                    if (m == null) {
                        m = new ArrayList<MatcherHolder>();
                        wildCardBeanDefinitions.put(cls.getName(), m);
                    }
                    MatcherHolder holder = new MatcherHolder(orig, matcher);
                    m.add(holder);
                }
            }
        }
    }

    private boolean isWildcardBeanName(String bn) {
        return bn.indexOf('*') != -1
            || bn.indexOf('?') != -1 
            || (bn.indexOf('(') != -1 
            && bn.indexOf(')') != -1);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.BeanConfigurer#configureBean(java.lang.Object)
     */
    @Override
    public void configureBean(Object beanInstance) {
        configureBean(null, beanInstance, true);

    }

    /**
     * @param object
     * @param beanInstance
     * @param b
     */
    public synchronized void configureBean(String bname, Object bean, boolean checkWildcards) {
        if (bean == null) {
            bname = getBeanName(bean);
        }
        if (bname == null) {
            return;
        }
        if (checkWildcards) {
            configureWithWildCard(bname, bean);
        }
        Method m = Reflection.findMethod(blueprintContainer.getClass(),
            "injectBeanInstance", BeanMetadata.class, Object.class);

        try {
            if (m != null) {
                ComponentMetadata cm = null;
                try {
                    cm = blueprintContainer.getComponentMetadata(bname);
                } catch (NoSuchComponentException nsce) {
                    cm = null;
                }
                if (cm instanceof BeanMetadata) {

                    m.invoke(blueprintContainer, cm, bean);
                }
            }
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new RuntimeException(t);
            }
        } catch (Exception e) {
            LOG.warn("Can't configured object {}", bname);
        }
    }

    private void configureWithWildCard(String bn, Object beanInstance) {
        if (!wildCardBeanDefinitions.isEmpty()) {
            Class<?> clazz = beanInstance.getClass();            
            while (!Object.class.equals(clazz)) {
                String className = clazz.getName();
                List<MatcherHolder> matchers = wildCardBeanDefinitions.get(className);
                if (matchers != null) {
                    for (MatcherHolder m : matchers) {
                        synchronized (m.matcher) {
                            m.matcher.reset(bn);
                            if (m.matcher.matches()) {
                                configureBean(m.wildCardId, beanInstance, false);
                                return;
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    protected String getBeanName(Object beanInstance) {
        if (beanInstance instanceof Configurable) {
            return ((Configurable) beanInstance).getConfigueName();
        } else {
            return null;
        }
        /*String beanName = null;
        Method m = null;
        try {
            m = beanInstance.getClass().getDeclaredMethod("getConfigureName", (Class[])null);
        } catch (NoSuchMethodException ex) {
            try {
                m = beanInstance.getClass().getMethod("getConfigureName", (Class[])null);
            } catch (NoSuchMethodException e) {
                //ignore
            }
        }
        if (m != null) {
            try {
                beanName = (String)(m.invoke(beanInstance));
            } catch (Exception ex) {
                LOG.warn("Error determining bean name",ex);
            }
        }
        
        if (null == beanName) {
            LOG.warn("Could not determining bean name {}",beanInstance.getClass().getName());
        }
      
        return beanName;*/
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.BeanConfigurer#configureBean(java.lang.String, java.lang.Object)
     */
    @Override
    public void configureBean(String name, Object beanInstance) {
        configureBean(name, beanInstance, true);

    }

}
