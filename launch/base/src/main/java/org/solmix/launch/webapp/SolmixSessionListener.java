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

package org.solmix.launch.webapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.solmix.launch.base.shared.LaunchConstants;

/**
 * 
 * @author ffz
 * @version 0.0.1 2012-3-16
 * @since 0.0.4
 */

public class SolmixSessionListener implements HttpSessionAttributeListener, HttpSessionListener, ServletContextListener
{

    private static ServletContext servletContext;

    private static ServletContextListener delegateeContextListener;

    private static HttpSessionListener delegateeSessionListener;

    private static HttpSessionAttributeListener delegateeSessionAttributeListener;

    static void startDelegate(final ClassLoader classLoader) {
        if (servletContext == null) {
            return;
        }
        Object delegatee = null;
        try {
            Class<?> delegateeClass = classLoader.loadClass(LaunchConstants.DEFAULT_DELEGATE_CLASS);
            delegatee = delegateeClass.newInstance();
        } catch (Exception e) {
            servletContext.log("Delegatee Event Listener class " + LaunchConstants.DEFAULT_DELEGATE_CLASS
                + " cannot be loaded or instantiated; Http Session Event forwarding is disabled", e);
        }
        if (delegatee instanceof ServletContextListener) {
            delegateeContextListener = (ServletContextListener) delegatee;
            delegateeContextListener.contextInitialized(new ServletContextEvent(servletContext));

            delegateeSessionListener = (HttpSessionListener) delegatee;
            delegateeSessionAttributeListener = (HttpSessionAttributeListener) delegatee;
        }
    }

    static void stopDelegatee() {
        if (delegateeContextListener != null) {
            delegateeContextListener.contextDestroyed(new ServletContextEvent(servletContext));
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        stopDelegatee();
        servletContext = null;

    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        final HttpSessionListener delegateeSessionListener = SolmixSessionListener.delegateeSessionListener;
        if (delegateeSessionListener != null) {
            delegateeSessionListener.sessionCreated(se);
        }

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSessionListener delegateeSessionListener = SolmixSessionListener.delegateeSessionListener;
        if (delegateeSessionListener != null) {
            delegateeSessionListener.sessionDestroyed(se);
        }

    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        final HttpSessionAttributeListener delegateeSessionAttributeListener = SolmixSessionListener.delegateeSessionAttributeListener;
        if (delegateeSessionAttributeListener != null) {
            delegateeSessionAttributeListener.attributeAdded(se);
        }

    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
        final HttpSessionAttributeListener delegateeSessionAttributeListener = SolmixSessionListener.delegateeSessionAttributeListener;
        if (delegateeSessionAttributeListener != null) {
            delegateeSessionAttributeListener.attributeRemoved(se);
        }

    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
        final HttpSessionAttributeListener delegateeSessionAttributeListener = SolmixSessionListener.delegateeSessionAttributeListener;
        if (delegateeSessionAttributeListener != null) {
            delegateeSessionAttributeListener.attributeReplaced(se);
        }
    }

}
