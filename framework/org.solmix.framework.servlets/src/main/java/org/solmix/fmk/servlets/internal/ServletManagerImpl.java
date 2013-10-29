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

package org.solmix.fmk.servlets.internal;

import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.solmix.api.request.SlxHttpServletRequest;
import org.solmix.api.servlets.ServletManager;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class ServletManagerImpl implements ServletManager
{

    private BundleContext bundleContext;

    private Map<Servlet, String> servletMap;

    private ServletContext servletContext;

    private ServletManagerTracker tracker;

    private String servletPrifix;

    /** The list of property names checked by {@link #getName(ServiceReference)} */
    private static final String[] NAME_PROPERTIES = { SERVLET_NAME, SERVICE_PID, SERVICE_ID };

    public void init() {
        tracker = new ServletManagerTracker(bundleContext, this.servletContext);
        tracker.open();
    }

    public void destroy() {
        if (tracker != null)
            tracker.close();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.servlets.ServletManager#resolveServlet(org.solmix.api.request.SlxHttpServletRequest)
     */
    @Override
    public Servlet resolveServlet(SlxHttpServletRequest request) {
        String url = request.getPathInfo();
        if (url != null && url.startsWith(servletPrifix)) {
            url = url.substring(servletPrifix.length());
        }
        Servlet servlet = tracker.getServlet(url);
        return servlet == null ? tracker.getDefaultServlet() : servlet;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.servlets.ServletManager#resolveServlet(java.lang.String)
     */
    @Override
    public Servlet resolveServlet(String requestStr) {
        Servlet servlet = tracker.getServlet(requestStr);
        return servlet == null ? tracker.getDefaultServlet() : servlet;
    }

    /**
     * @return the servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @param servletContext the servletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    
    /**
     * @return the bundleContext
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    
    /**
     * @param bundleContext the bundleContext to set
     */
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Looks for a name value in the service reference properties. See the class comment at the top for the list of
     * properties checked by this method.
     */
    public static String getName(ServiceReference reference) {
        String servletName = null;
        for (int i = 0; i < NAME_PROPERTIES.length && (servletName == null || servletName.length() == 0); i++) {
            Object prop = reference.getProperty(NAME_PROPERTIES[i]);
            if (prop != null) {
                servletName = String.valueOf(prop);
            }
        }
        return servletName;
    }

    /**
     * @return the servletPrifix
     */
    public String getServletPrifix() {
        return servletPrifix;
    }

    /**
     * @param servletPrifix the servletPrifix to set
     */
    public void setServletPrifix(String servletPrifix) {
        this.servletPrifix = servletPrifix;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.servlets.ServletManager#getDefault()
     */
    @Override
    public Servlet getDefault() {
        return tracker.getDefaultServlet();
    }

}
