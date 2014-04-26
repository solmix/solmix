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

import org.solmix.api.context.SystemContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-4
 */

public class SystemContextPostProcessor implements BeanFactoryPostProcessor
{

    private SystemContext context;

    private String contextName;

    public SystemContextPostProcessor()
    {

    }

    public SystemContextPostProcessor(SystemContext context)
    {
        this.context = context;
    }

    public SystemContextPostProcessor(String name)
    {
        this.contextName = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Object inject = context;
        if(inject==null){
            inject = getContextByName(SystemContext.DEFAULT_CONTEXT_ID, beanFactory, true, null);
        }else{
            if (!beanFactory.containsBeanDefinition(SystemContext.DEFAULT_CONTEXT_ID)
                && !beanFactory.containsSingleton(SystemContext.DEFAULT_CONTEXT_ID)) {
                beanFactory.registerSingleton(SystemContext.DEFAULT_CONTEXT_ID, context);
            }
        }
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            
        }//end loop bean.

    }

    /**
     * @param name
     * @param factory
     * @param b
     * @param object
     * @return
     */
    private Object getContextByName(String name, ConfigurableListableBeanFactory factory, boolean create, String cn) {
        if (!factory.containsBeanDefinition(name) && (create || SystemContext.DEFAULT_CONTEXT_ID.equals(name))) {
            DefaultListableBeanFactory df = (DefaultListableBeanFactory)factory;
            RootBeanDefinition rbd = new RootBeanDefinition(SpringSystemContext.class);
            if (cn != null) {
                rbd.setAttribute("busConfig", new RuntimeBeanReference(cn));
            }
            df.registerBeanDefinition(name, rbd);
        } else if (cn != null) {
            BeanDefinition bd = factory.getBeanDefinition(name);
            bd.getPropertyValues().addPropertyValue("busConfig", new RuntimeBeanReference(cn));
        }
        return new RuntimeBeanReference(name);
    }

}
