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

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.solmix.fmk.spring.ManagedContextFilter;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-3
 */

public class SGTContextFilter extends ManagedContextFilter
{



    Servlet sgtServlet;

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
    }

    @Override
    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(sgtServlet==null){
            sgtServlet = new SGTServlet();
            sgtServlet.init(new FilterServletConfig(filterConfig));
        }
        sgtServlet.service(request, response);
       
    }
   
}
