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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.WebContext;
import org.solmix.api.context.WebContextFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.runtime.SystemContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-4
 */

public class WebContextFactoryImpl implements WebContextFactory
{

    private static Logger log = LoggerFactory.getLogger(WebContextFactoryImpl.class.getName());

    private static WebContextFactory instance;
    private SystemContext systemContext;

    public synchronized static WebContextFactory getInstance() {
        if (instance == null) {
            instance = new WebContextFactoryImpl();
        }
        return instance;
    }

    private WebContextFactoryImpl()
    {

    }

    @Override
    public WebContext createWebContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        WebContextImpl context = new WebContextImpl();
        try {
            context.init(request, response, servletContext); 
            if(systemContext==null){
                systemContext=SlxContext.getThreadSystemContext();
            }
            assert systemContext!=null;
            //every context shared a single systemContext.
            context.setSystemContext(systemContext);
        } catch (SlxException e) {
            log.error("Create webContext failed,message :{}", e.getMessage());
        }
        return context;

    }

}
