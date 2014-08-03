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
package org.solmix.runtime.support.spring;

import org.solmix.runtime.Container;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月29日
 */

public class ResourceBeanPostProcessor implements
    DestructionAwareBeanPostProcessor, Ordered, ApplicationContextAware
{
    private ResourceManager resourceManager;
    private ApplicationContext context;
    private boolean isProcessing;
    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        if (!isProcessing) {
            return bean;
        }
        if (bean instanceof Container) {
            getResourceManager(bean);
        }
        if (bean != null 
            && injectable(bean, beanName)) {
            new ResourceInjector(getResourceManager(bean)).inject(bean);
        }
        return bean;
    }

    /**
     * @param bean
     * @return
     */
    private ResourceManager getResourceManager(Object bean) {
        if (resourceManager == null) {
            boolean temp = isProcessing;
            isProcessing = false;
            if (bean instanceof ResourceManager) {
                resourceManager = (ResourceManager)bean;
                resourceManager.addResourceResolver(new SpringResourceResolver(context));
            } else if (bean instanceof Container) {
                Container b = (Container)bean;
                ResourceManager m = b.getExtension(ResourceManager.class);
                if (m != null) {
                    resourceManager = m;
                    if (!(b instanceof SpringContainer)) {
                        resourceManager
                            .addResourceResolver(new SpringResourceResolver(context));
                    }
                }
            } else {
                ResourceManager m = null;
                Container b = null;
                try {
                    m = (ResourceManager)context.getBean(ResourceManager.class.getName());
                } catch (NoSuchBeanDefinitionException t) {
                    //ignore - no resource manager
                }
                if (m == null) {
                    b = (Container)context.getBean("cxf");
                    m = b.getExtension(ResourceManager.class);
                }
                if (m != null) {
                    resourceManager = m;
                    if (!(b instanceof SpringContainer)) {
                        resourceManager
                            .addResourceResolver(new SpringResourceResolver(context));
                    }
                }
            }
            isProcessing = temp;
        }
        return resourceManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
        if (!isProcessing) {
            if (resourceManager == null && bean instanceof ResourceManager) {
                resourceManager = (ResourceManager)bean;
                resourceManager.addResourceResolver(new SpringResourceResolver(context));
            }
            return bean;
        }
        if (bean != null 
            && injectable(bean, beanName)) {
            new ResourceInjector(getResourceManager(bean)).construct(bean);
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
       this.context=applicationContext;
       try {
           Class<?> cls = Class
               .forName("org.springframework.context.annotation.CommonAnnotationBeanPostProcessor");
           isProcessing = context.getBeanNamesForType(cls, true, false).length == 0;
       } catch (ClassNotFoundException e) {
           isProcessing = true;
       }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        return 1200;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor#postProcessBeforeDestruction(java.lang.Object, java.lang.String)
     */
    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName)
        throws BeansException {
        if (!isProcessing) {
            return;
        }
        if (bean != null 
            && injectable(bean, beanName)) {
            new ResourceInjector(getResourceManager(bean)).destroy(bean);
        }

    }
    private boolean injectable(Object bean, String beanId) {
        return !"cxf".equals(beanId) && ResourceInjector.processable(bean.getClass(), bean);
    }
}
