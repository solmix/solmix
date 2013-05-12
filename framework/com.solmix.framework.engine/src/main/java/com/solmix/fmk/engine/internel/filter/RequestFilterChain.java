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

package com.solmix.fmk.engine.internel.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.request.RequestProcessor;
import com.solmix.api.request.SlxHttpServletRequest;
import com.solmix.api.request.SlxHttpServletResponse;
import com.solmix.fmk.engine.MainServlet;
import com.solmix.fmk.engine.internel.request.RequestData;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-9
 */

public class RequestFilterChain implements FilterChain
{

    private final RequestProcessor processor;

    private Filter[] filters;

    /** default log */
    private final Logger log = LoggerFactory.getLogger(MainServlet.class);

    private int current;

    public RequestFilterChain(RequestProcessor processor, Filter[] filters)
    {
        this.processor = processor;
        this.filters = filters;

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        current++;
        SlxHttpServletRequest slxRequest = toSlxRequest(request);
        SlxHttpServletResponse slxResponse = toSlxResponse(response);
        if (this.current < this.filters.length) {
            Filter filter = this.filters[this.current];
            trackFilter(request, filter);
            filter.doFilter(slxRequest, slxResponse, this);
        } else {
            RequestData.service(slxRequest, slxResponse);
        }
    }

    /**
     * @param response
     * @return
     */
    private SlxHttpServletResponse toSlxResponse(ServletResponse response) {
        if (response instanceof SlxHttpServletResponse) {
            return (SlxHttpServletResponse) response;
        }
        return RequestData.toSlxHttpServletResponse(response);
    }

    /**
     * @param request
     * @return
     */
    private SlxHttpServletRequest toSlxRequest(ServletRequest request) {
        if (request instanceof SlxHttpServletRequest) {
            return (SlxHttpServletRequest) request;
        }
        return RequestData.toSlxHttpServletRequest(request);
    }

    /**
     * @param request
     * @param filter
     */
    private void trackFilter(ServletRequest request, Filter filter) {
        log.debug("Calling filter: {0}", this.filters[this.current].getClass().getName());

    }

}
