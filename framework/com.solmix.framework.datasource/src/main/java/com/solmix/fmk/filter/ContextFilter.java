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

package com.solmix.fmk.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.context.Context;
import com.solmix.api.context.ContextFactory;
import com.solmix.api.context.WebContext;
import com.solmix.api.exception.SlxException;
import com.solmix.fmk.context.ContextFactoryImpl;
import com.solmix.fmk.context.SlxContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-10
 */

public class ContextFilter extends AbstractFilter implements Filter
{

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private ContextFactory contextFactory;

    private ServletContext servletContext;

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();

    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final Context originalContext = SlxContext.hasContext() ? SlxContext.getContext() : null;
        boolean initializedContext = false;
        request.setCharacterEncoding("UTF-8");
        contextFactory = ContextFactoryImpl.getInstance();
        if (SlxContext.isSystemContext() || !SlxContext.hasContext()) {
            final WebContext context = contextFactory.createWebContext(request, response, servletContext);
            SlxContext.setContext(context);
            initializedContext = true;
           /* try {
                String uri = request.getRequestURI();
                if (uri != null) {
                    MDC.put("requesturi", uri);
                }
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    MDC.put("Referer", referer);
                }

                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null) {
                    MDC.put("User-Agent", userAgent);
                }

                String remoteHost = request.getRemoteHost();
                if (remoteHost != null) {
                    MDC.put("Remote-Host", remoteHost);
                }

                HttpSession session = request.getSession(false);
                if (session != null) {
                    MDC.put("SessionId", session.getId());
                }
            } catch (Throwable e) {
                // if for any reason the MDC couldn't be set, just ignore it.
                log.debug(e.getMessage(), e);
            }*/
        } else {
            try {
                WebContext context = SlxContext.getWebContext();
                context.init(request, response, servletContext);
                SlxContext.setContext(context);
            } catch (SlxException e) {
                log.error(e.getFullMessage());
            }
        }
        try {
            doProcess(request, response);
            chain.doFilter(request, response);
        } finally {
            if (initializedContext) {
//                SlxContext.release();
                if (originalContext != null)
                    SlxContext.setContext(originalContext);

                // cleanup
//                MDC.clear();
            }
        }
    }

    /**
     * 
     */
    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // nothing to do.

    }

}
