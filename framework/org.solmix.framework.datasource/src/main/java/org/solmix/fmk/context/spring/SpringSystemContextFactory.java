/*
 * SOLMIX PROJECT
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

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.solmix.api.context.SystemContext;
import org.solmix.api.context.SystemContextFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-3
 */

public class SpringSystemContextFactory extends SystemContextFactory
{
    private  ApplicationContext springContext;
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.SystemContextFactory#createContext()
     */
    @Override
    public SystemContext createContext() {
        return createContext((String)null);
    }

    /**
     * @param string
     * @return
     */
    public SystemContext createContext(String configFile) {
        return createContext(configFile,defaultContextNotExists());
    }
    /**
     * @param configFile
     * @param defaultContextNotExists
     * @return
     */
    public SystemContext createContext(String configFile, boolean includeDefaults) {
        if (configFile == null) {
            return createContext((String[])null, includeDefaults);
        }
        return createContext(new String[] {configFile}, includeDefaults);
    }

    /**
     * @param strings
     * @param includeDefaults
     * @return
     */
    public SystemContext createContext(String[] cfgFiles, boolean includeDefaults) {
        try {
            final Resource r = SystemApplicationContext.findResource(SystemApplicationContext.DEFAULT_CFG_FILE);
            boolean exists = true;
            if (r != null) {
                exists = AccessController .doPrivileged(new PrivilegedAction<Boolean>() {
                        @Override
                        public Boolean run() {
                            return r.exists();
                        }
                    });
            }
            if (springContext == null && includeDefaults&&(r==null||!exists)) {
                return new org.solmix.fmk.context.SystemContextFactoryImpl().createContext();
            }
            ConfigurableApplicationContext cac = createApplicationContext(cfgFiles, includeDefaults, springContext);
            return completeCreating(cac);
        } catch (BeansException ex) {
            throw new java.lang.RuntimeException(ex);
        }
    }



    /**
     * @param cac
     * @return
     */
    private SystemContext completeCreating(ConfigurableApplicationContext spring) {
        SystemContext system=(SystemContext)spring.getBean(SystemContext.DEFAULT_CONTEXT_ID);
        system.setBean(spring, ApplicationContext.class);
        possiblySetDefaultSystemContext(system);
        initializeContext(system);
        if (system instanceof SpringSystemContext && defaultContextNotExists()) {
            ((SpringSystemContext)system).setCloseContext(true);
        }
        return system;
    }

    /**
     * @param cfgFiles
     * @param includeDefaults
     * @param springContext2
     * @return
     */
    private ConfigurableApplicationContext createApplicationContext(String[] cfgFiles, boolean includeDefaults, ApplicationContext springContext2) {
        try {  
        return new SystemApplicationContext(cfgFiles,springContext2,includeDefaults);
        } catch (BeansException ex) {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != SystemApplicationContext.class.getClassLoader()) {
                Thread.currentThread().setContextClassLoader(
                    SystemApplicationContext.class.getClassLoader());
                try {
                    return new SystemApplicationContext(cfgFiles, springContext2,includeDefaults );        
                } finally {
                    Thread.currentThread().setContextClassLoader(contextLoader);
                }
            } else {
                throw ex;
            }
        }
    }

    private boolean defaultContextNotExists() {
        if (null != springContext) {
            return !springContext.containsBean(SystemContext.DEFAULT_CONTEXT_ID);
        }
        return true;
    }

}
