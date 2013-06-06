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

package com.solmix.fmk.servlets.internel.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-9
 */

public class SlxFilterConfig implements FilterConfig
{

    private final ServletContext context;

    /** The <code>ServiceReference</code> providing the properties */
    private final ServiceReference<Filter> reference;

    private final String name;

    public SlxFilterConfig(ServletContext context, final ServiceReference<Filter> reference, String name)
    {
        this.context = context;
        this.name = name;
        this.reference = reference;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.FilterConfig#getFilterName()
     */
    @Override
    public String getFilterName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.FilterConfig#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return this.context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.FilterConfig#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        Object prop = reference.getProperty(name);
        return (prop == null) ? null : String.valueOf(prop);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.FilterConfig#getInitParameterNames()
     */
    @Override
    public Enumeration getInitParameterNames() {
        List<?> keys = Arrays.asList(reference.getPropertyKeys());
        return Collections.enumeration(keys);
    }

}
