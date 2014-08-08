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

package org.solmix.runtime.support.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.Configurable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.beans.factory.wiring.BeanWiringInfo;
import org.springframework.beans.factory.wiring.BeanWiringInfoResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class SpringConfigurer extends BeanConfigurerSupport implements BeanConfigurer, ApplicationContextAware
{

    private static final Logger LOG = LoggerFactory.getLogger(SpringConfigurer.class);

    private final Map<String, List<MatcherHolder>> wildCardBeanDefinitions = new HashMap<String, List<MatcherHolder>>();

    private Set<ApplicationContext> appContexts;

    private BeanFactory beanFactory;

    public SpringConfigurer()
    {
    }

    public SpringConfigurer(ApplicationContext springContext)
    {
        setApplicationContext(springContext);
    }

    static class MatcherHolder
    {

        Matcher matcher;

        String wildCardId;

        public MatcherHolder(String orig, Matcher matcher)
        {
            wildCardId = orig;
            this.matcher = matcher;
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        super.setBeanFactory(beanFactory);
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
     * {@inheritDoc}
     * 
     * @see org.solmix.api.bean.BeanConfigurer#configureBean(java.lang.String, java.lang.Object)
     */
    @Override
    public void configureBean(String name, Object beanInstance) {
        configureBean(name, beanInstance, true);

    }
    public synchronized void configureBean(String bn, Object beanInstance, boolean checkWildcards) {

        if (null == appContexts) {
            return;
        }
        
        if (null == bn) {
            bn = getBeanName(beanInstance);
        }
        
        if (null == bn) {
            return;
        }
        //configure bean with * pattern style.
        if (checkWildcards) {
            configureWithWildCard(bn, beanInstance);
        }
        
        final String beanName = bn;
        setBeanWiringInfoResolver(new BeanWiringInfoResolver() {
            @Override
            public BeanWiringInfo resolveWiringInfo(Object instance) {
                if (!"".equals(beanName)) {
                    return new BeanWiringInfo(beanName);
                }
                return null;
            }
        });
        
        for (ApplicationContext appContext : appContexts) {
            if (appContext.containsBean(bn)) {
                this.setBeanFactory(appContext.getAutowireCapableBeanFactory());
            }
        }
        
        try {
            //this will prevent a call into the AbstractBeanFactory.markBeanAsCreated(...)
            //which saves ALL the names into a HashSet.  For URL based configuration,
            //this can leak memory
            if (beanFactory instanceof AbstractBeanFactory) {
                ((AbstractBeanFactory)beanFactory).getMergedBeanDefinition(bn);
            }
            super.configureBean(beanInstance);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Successfully performed injection,used beanName:{}",beanName);
            }
        } catch (NoSuchBeanDefinitionException ex) {
            // users often wonder why the settings in their configuration files seem
            // to have no effect - the most common cause is that they have been using
            // incorrect bean ids
            if (LOG.isDebugEnabled()) {
                LOG.debug("No matching bean {}",beanName);
            }
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
            return ((Configurable)beanInstance).getConfigueName();
        }else{//only used interface Configurable powered.
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
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        appContexts = new CopyOnWriteArraySet<ApplicationContext>();
        addApplicationContext(ac);
        this.beanFactory = ac.getAutowireCapableBeanFactory();
        super.setBeanFactory(this.beanFactory);

    }

    /**
     * @param ac
     */
    private void addApplicationContext(ApplicationContext ac) {
        if (!appContexts.contains(ac)) {
            appContexts.add(ac);
            List<ApplicationContext> inactiveApplicationContexts = new ArrayList<ApplicationContext>();
            Iterator<ApplicationContext> it = appContexts.iterator();
            while (it.hasNext()) {
                ApplicationContext c = it.next();
                if (c instanceof ConfigurableApplicationContext
                    && !((ConfigurableApplicationContext)c).isActive()) {
                    inactiveApplicationContexts.add(c);
                }
            }
            // Remove the inactive application context here can avoid the UnsupportedOperationException
            for (ApplicationContext context : inactiveApplicationContexts) {
                appContexts.remove(context);
            }
            initWildcardDefinitionMap();
        }

    }
    private void initWildcardDefinitionMap() {
        if (null != appContexts) {
            for (ApplicationContext appContext : appContexts) {
                for (String n : appContext.getBeanDefinitionNames()) {
                    if (isWildcardBeanName(n)) {
                        AutowireCapableBeanFactory bf = appContext.getAutowireCapableBeanFactory();
                        BeanDefinitionRegistry bdr = (BeanDefinitionRegistry) bf;
                        BeanDefinition bd = bdr.getBeanDefinition(n);
                        String className = bd.getBeanClassName();
                        if (null != className) {
                            String orig = n;
                            if (n.charAt(0) == '*') {
                                //old wildcard
                                n = "." + n.replaceAll("\\.", "\\."); 
                            }
                            try {
                                Matcher matcher = Pattern.compile(n).matcher("");
                                List<MatcherHolder> m = wildCardBeanDefinitions.get(className);
                                if (m == null) {
                                    m = new ArrayList<MatcherHolder>();
                                    wildCardBeanDefinitions.put(className, m);
                                }
                                MatcherHolder holder = new MatcherHolder(orig, matcher);
                                m.add(holder);
                            } catch (PatternSyntaxException npe) { 
                                //not a valid patter, we'll ignore
                            }
                        } else {
                            LOG.warn("Wildcars with not class {}",n); 
                        }
                    }
                }
            }
        }
    }
    private boolean isWildcardBeanName(String bn) {
        return bn.indexOf('*') != -1 || bn.indexOf('?') != -1
            || (bn.indexOf('(') != -1 && bn.indexOf(')') != -1);
    }

    @Override
    public void destroy() {
        super.destroy();
        appContexts.clear();
    }

    public Class<?> getRegistrationType() {
        return BeanConfigurer.class;
    }

    protected Set<ApplicationContext> getAppContexts() {
        return appContexts;
    }
}
