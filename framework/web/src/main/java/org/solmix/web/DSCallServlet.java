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
package org.solmix.web;

import org.solmix.api.call.DSCallInterceptor;
import org.solmix.web.interceptor.DefaultRestInterceptor;
import org.solmix.web.interceptor.DownloadInterceptor;
import org.solmix.web.interceptor.ExportInterceptor;
import org.solmix.web.interceptor.UploadInterceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-27
 */

public class DSCallServlet extends AbstractDSCallServlet
{

    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.AbstractDSCallServlet#configuredInterceptors()
     */
    @Override
   protected DSCallInterceptor[] configuredInterceptors() {
        DSCallInterceptor[] re={
            new DownloadInterceptor(), 
            new UploadInterceptor(), 
            new ExportInterceptor(), 
            new DefaultRestInterceptor()};
        return  re;
    }

}
