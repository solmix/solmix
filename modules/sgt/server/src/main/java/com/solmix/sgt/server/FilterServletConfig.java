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

package com.solmix.sgt.server;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-23
 */

public class FilterServletConfig implements ServletConfig
{

    private final FilterConfig config;

    public FilterServletConfig(FilterConfig config)
    {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String arg0) {
        return config.getInitParameter(arg0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getInitParameterNames()
     */
    @Override
    public Enumeration getInitParameterNames() {
        return this.config.getInitParameterNames();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return this.config.getServletContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletConfig#getServletName()
     */
    @Override
    public String getServletName() {
        return this.config.getFilterName();
    }

}
