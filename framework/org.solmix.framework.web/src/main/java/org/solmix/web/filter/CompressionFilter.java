/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.WebContext;
import org.solmix.api.context.WebContextFactory;
import org.solmix.api.interfaces.CompressionService;
import org.solmix.web.ServletTools;
import org.solmix.web.compression.CompressionServiceImpl;
import org.solmix.web.context.WebContextFactoryImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-6-7
 */

public class CompressionFilter implements Filter
{
    private static final Logger log = LoggerFactory.getLogger(CompressionFilter.class.getName());

    private CompressionService compression;
    private WebContextFactory contextFactory;
    private ServletContext servletContext;
    private List<String> compressableMimeTypes;

    protected int compressThreshold = 250;

    /**
     * @return the compressableMimeTypes
     */
    public List<String> getCompressableMimeTypes() {
        return compressableMimeTypes;
    }

    /**
     * @param compressableMimeTypes the compressableMimeTypes to set
     */
    public void setCompressableMimeTypes(List<String> compressableMimeTypes) {
        this.compressableMimeTypes = compressableMimeTypes;
    }


    CompressionFilter()
    {
        compressableMimeTypes = null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        compression = new CompressionServiceImpl();
        contextFactory=WebContextFactoryImpl.getInstance();
        this.servletContext = filterConfig.getServletContext();
    }

    /**
     * @return the compressThreshold
     */
    public int getCompressThreshold() {
        return compressThreshold;
    }

    /**
     * @param compressThreshold the compressThreshold to set
     */
    public void setCompressThreshold(int compressThreshold) {
        this.compressThreshold = compressThreshold;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     *      javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (compression == null)
            return;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        WebContext context = null;
        String mimeType = null;
        boolean compress = false;
        try {
            context = contextFactory.createWebContext(req, resp, servletContext);
            mimeType = ServletTools.mimeTypeForContext(context);
            compress = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean mimeTypeCompressable;
        if (mimeType == null || ServletTools.compressionEnabledForMimeType(mimeType, compressableMimeTypes))
            if (ServletTools.compressionWorksForMimeType(context, mimeType))
                mimeTypeCompressable = true;
        if (!ServletTools.compressionEnabled()) {
            compress = false;
            log.debug("Compression is disabled in config - not compressing");
        }
        if (compress && !ServletTools.compressionWorksForContext(context))
            compress = false;
        if (compress && !ServletTools.browserClaimsGZSupport(req) && !ServletTools.alwaysCompressMimeType(mimeType)) {
            compress = false;
            log.debug("Browser is not compression-capable - not compressing");
        }
        if (compress) {
            chain.doFilter(req, resp);
        }
        ByteArrayOutputStream wrapBuf = new ByteArrayOutputStream();
        OutputStream out = response.getOutputStream();
        wrapBuf.writeTo(out);
        try {
            this.compression.compressAndSend(context, wrapBuf, compressThreshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.flushBuffer();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        compression=null;
    }

}
