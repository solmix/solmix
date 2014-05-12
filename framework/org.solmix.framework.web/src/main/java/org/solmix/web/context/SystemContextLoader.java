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

package org.solmix.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.SystemContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.4.12 2012-12-2
 */

public class SystemContextLoader extends ContextLoaderListener
{

    public static final String SYSTEM_CONTEXT_CONFIG = "systemContextConfig";

    private static final Logger LOG = LoggerFactory.getLogger(SystemContextLoader.class);
    @Override
    public void contextInitialized(ServletContextEvent event) {
        //initial web configured spring applicationContext
        super.contextInitialized(event);
        ServletContext servletContext = event.getServletContext();
        String root = servletContext.getRealPath("/");
        if(LOG.isInfoEnabled()){
            LOG.info("Initial Web context,used path:"+root+" As [solmix.base]");
        }
        System.setProperty("solmix.base", root);
        String userConfig = event.getServletContext().getInitParameter("solmix.cfg.file");
        if (userConfig != null) {
            if(LOG.isInfoEnabled()){
                LOG.info("Initial Web context,used configure:"+userConfig);
            }
            System.setProperty("solmix.cfg.file", userConfig);
        }
        initialSystemContext(servletContext, ContextLoader.getCurrentWebApplicationContext());

    }

    void initialSystemContext(ServletContext servletContext, ApplicationContext applicationContext) {
        WebSystemContextFactory scfactory = new WebSystemContextFactory(servletContext);
        scfactory.setParent(ContextLoader.getCurrentWebApplicationContext());
        SystemContextFactory.possiblySetDefaultSystemContext(scfactory.createContext());

    }
}
