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

package org.solmix.web.internal;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.solmix.api.servlet.FilterManager;

/**
 * 
 * @version 110035 2012-10-9
 */

public class FilterManagerImpl implements FilterManager
{

    
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

    private BundleContext bundleContext;

    private FilterManagerTracker tracker;

    private ServletContext servletContext;

    @Override
    public Filter[] getFilters() {
        return tracker.getFilterChain().getFilters();
    }

    public void init() {
        tracker = new FilterManagerTracker(bundleContext, this.servletContext);
        tracker.open();
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

    public void destroy() {
        if (tracker != null)
            tracker.close();
    }
}
