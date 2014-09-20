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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.support.ContainerFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-3
 */

public class SpringContainerFactory extends ContainerFactory
{
    private  ApplicationContext parent;
    private NamespaceHandlerResolver resolver;
    public SpringContainerFactory() {
        this.parent = null;
    }
    public SpringContainerFactory(ApplicationContext context) {
        this.parent = context;
    }
  
    public SpringContainerFactory(NamespaceHandlerResolver r) {
        parent = null;
        this.resolver = r;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.ContainerFactory#createContainer()
     */
    @Override
    public Container createContainer() {
        return createContainer((String)null);
    }

    /**
     * @param string
     * @return
     */
    public Container createContainer(String configFile) {
        return createContainer(configFile,defaultContextNotExists());
    }
    /**
     * @param configFile
     * @param defaultContextNotExists
     * @return
     */
    public Container createContainer(String configFile, boolean includeDefaults) {
        if (configFile == null) {
            return createContainer((String[])null, includeDefaults);
        }
        return createContainer(new String[] {configFile}, includeDefaults);
    }

    /**
     * @param strings
     * @param includeDefaults
     * @return
     */
    public Container createContainer(String[] cfgFiles, boolean includeDefaults) {
        try {
            final Resource r = ContainerApplicationContext.findResource(ContainerApplicationContext.DEFAULT_USER_CFG_FILE);
            boolean exists = true;
            if (r != null) {
                exists = AccessController .doPrivileged(new PrivilegedAction<Boolean>() {
                        @Override
                        public Boolean run() {
                            return r.exists();
                        }
                    });
            }
            if (parent == null && includeDefaults&&(r==null||!exists)) {
                return new ContainerFactoryImpl().createContainer();
            }
            ConfigurableApplicationContext cac = createApplicationContext(cfgFiles, includeDefaults, parent);
            return completeCreating(cac);
        } catch (BeansException ex) {
            throw new java.lang.RuntimeException(ex);
        }
    }



    /**
     * @param cac
     * @return
     */
    private Container completeCreating(ConfigurableApplicationContext spring) {
        Container system=(Container)spring.getBean(Container.DEFAULT_CONTAINER_ID);
        system.setExtension(spring, ApplicationContext.class);
        possiblySetDefaultContainer(system);
        initializeContainer(system);
        if (system instanceof SpringContainer && defaultContextNotExists()) {
            ((SpringContainer)system).setCloseContext(true);
        }
        return system;
    }

    /**
     * @param cfgFiles
     * @param includeDefaults
     * @param springContext2
     * @return
     */
    private ConfigurableApplicationContext createApplicationContext(String[] cfgFiles, boolean includeDefaults, ApplicationContext parent) {
        try {  
        return new ContainerApplicationContext(cfgFiles,parent,includeDefaults);
        } catch (BeansException ex) {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != ContainerApplicationContext.class.getClassLoader()) {
                Thread.currentThread().setContextClassLoader(
                    ContainerApplicationContext.class.getClassLoader());
                try {
                    return new ContainerApplicationContext(cfgFiles, parent,includeDefaults ,resolver);        
                } finally {
                    Thread.currentThread().setContextClassLoader(contextLoader);
                }
            } else {
                throw ex;
            }
        }
    }

    private boolean defaultContextNotExists() {
        if (null != parent) {
            return !parent.containsBean(Container.DEFAULT_CONTAINER_ID);
        }
        return true;
    }
    
    /**
     * @return the parent
     */
    public ApplicationContext getParent() {
        return parent;
    }

    
    /**
     * @param parent the parent to set
     */
    public void setParent(ApplicationContext parent) {
        this.parent = parent;
    }

}
