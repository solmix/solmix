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

import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.support.ext.ContainerAdaptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-4
 */

public class SpringContainer extends ContainerAdaptor implements ApplicationContextAware
{

//    private final static Logger LOG = LoggerFactory.getLogger(SpringContainer.class);
    private AbstractApplicationContext applicationContext;

    private boolean closeContext;

    public SpringContainer()
    {

    }
    
    public void setConfig(ContainerDefinitionParser.ContainerType config){
        config.setContainer(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (AbstractApplicationContext) applicationContext;
        ApplicationListener listener = new ApplicationListener() {

            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                SpringContainer.this.onApplicationEvent(event);
            }
        };
        this.applicationContext.addApplicationListener(listener);
        ApplicationContext ac = applicationContext.getParent();
        while (ac != null) {
            if (ac instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) ac).addApplicationListener(listener);
            }
            ac = ac.getParent();
        }
        setExtension(applicationContext.getClassLoader(), ClassLoader.class);
        setExtension(new SpringConfigurer(applicationContext), BeanConfigurer.class);
        //
        setExtension(applicationContext, ApplicationContext.class);
//        setBean(new SpringConfigureUnitManager(), ConfigureUnitManager.class);
        ResourceManager m = getExtension(ResourceManager.class);
        m.addResourceResolver(new SpringResourceResolver(applicationContext));
        //at last add the spring bean provider.
        ConfiguredBeanProvider provider = getExtension(ConfiguredBeanProvider.class);
        if (!(provider instanceof SpringBeanProvider)) {
            setExtension(new SpringBeanProvider(applicationContext, this), ConfiguredBeanProvider.class);
        }
        if (getStatus() != ContainerStatus.CREATED) {
            initialize();
        }
    }

    /**
     * @param em
     */

    /**
     * @param event
     */
    protected void onApplicationEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        boolean doIt = false;
        ApplicationContext ac = applicationContext;
        while (ac != null && !doIt) {
            if (event.getSource() == ac) {
                doIt = true;
                break;
            }
            ac = ac.getParent();
        }
        if (doIt) {
            if (event instanceof ContextRefreshedEvent) {
                if (getStatus() != ContainerStatus.CREATED) {
                    initialize();
                }
            } else if (event instanceof ContextClosedEvent) {
                // getBean(ContextLifeCycleManager.class).postShutdown();
            }
        }

    }

    @Override
    public void destroyBeans() {
        if (closeContext) {
            applicationContext.close();
        }
        super.destroyBeans();
    }
    @Override
    public String getId() {
        if (id == null) {
            try {
                Class<?> clsbc = Class.forName("org.osgi.framework.BundleContext");
                Class<?> clsb = Class.forName("org.osgi.framework.Bundle");
                Object o = getExtension(clsbc);
                Object o2 = clsbc.getMethod("getBundle").invoke(o);
                String s = (String)clsb.getMethod("getSymbolicName").invoke(o2);
                id = s + "-" + DEFAULT_CONTAINER_ID + Integer.toString(this.hashCode());
            } catch (Throwable t) {
                id = super.getId();
            }
        }
        return id;
    }

    /**
     * @param b
     */
    public void setCloseContext(boolean b) {
        this.closeContext = b;

    }
}
