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

package com.solmix.fmk.spring;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.solmix.fmk.filter.ContextFilter;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-3
 */

public class ManagedContextFilter extends ContextFilter
{

    // private WebApplicationContext ctx;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        // ctx = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        // ContextFactory factory = ctx.getBean("context_factory", ContextFactory.class);
        // this.setContextFactory(factory);
    }
}
