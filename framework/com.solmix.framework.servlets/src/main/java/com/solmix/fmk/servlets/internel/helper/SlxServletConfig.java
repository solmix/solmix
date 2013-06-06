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

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-17
 */

public class SlxServletConfig implements ServletConfig
{

    private final ServletContext context;

    /** The <code>ServiceReference</code> providing the properties */
    private final ServiceReference<Servlet> reference;

    private final String name;

    public SlxServletConfig(ServletContext context, final ServiceReference<Servlet> reference, String name)
    {
        this.context = context;
        this.name = name;
        this.reference = reference;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getServletName()
     */
    @Override
    public String getServletName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {

        return context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {

        Object prop = null;
        if (reference != null)
            prop = reference.getProperty(name);
        return (prop == null) ? null : String.valueOf(prop);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getInitParameterNames()
     */
    @Override
    public Enumeration getInitParameterNames() {
        if (reference != null) {
            List<?> keys = Arrays.asList(reference.getPropertyKeys());
            return Collections.enumeration(keys);
        } else {
            return null;
        }
    }

}
