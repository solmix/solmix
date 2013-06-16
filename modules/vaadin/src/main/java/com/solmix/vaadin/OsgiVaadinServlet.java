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

package com.solmix.vaadin;

import java.util.Properties;

import javax.servlet.ServletException;

import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-3
 */

public class OsgiVaadinServlet extends VaadinServlet
{

    public static final String UI_PROVIDER_CLASS = OsgiUIProvider.class.getName();
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(javax.servlet.ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        getService().addSessionInitListener(new SessionInitListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                VaadinSession session =event.getSession();
                session.addUIProvider(new OsgiUIProvider());
                
            }
            
        });

    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        VaadinServletService service = new OsgiVaadinService(this, deploymentConfiguration);
        return service;
    }

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
        return new DefaultDeploymentConfiguration(getClass(), initParameters);
    }
    
}
