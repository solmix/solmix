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

package com.solmix.fmk.engine.internel;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.solmix.fmk.engine.MimeTypeService;
import com.solmix.fmk.engine.internel.mime.MimeTypeServiceImpl;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class SlxHttpContext implements HttpContext
{

    /** default log */
    private static final Logger log = LoggerFactory.getLogger(SlxHttpContext.class);

    private MimeTypeService mimeTypeService;

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.http.HttpContext#handleSecurity(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String name) {
        System.out.println(name);
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
     */
    @Override
    public String getMimeType(String name) {
        if (mimeTypeService == null) {
            mimeTypeService = new MimeTypeServiceImpl();
        }
        MimeTypeService mtservice = mimeTypeService;
        if (mtservice != null) {
            return mtservice.getMimeType(name);
        }

        log.debug("getMimeType: MimeTypeService not available, cannot resolve mime type for {}", name);
        return null;
    }

}
