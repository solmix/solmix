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

package org.solmix.fmk.engine.internal.request;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.request.RequestProcessor;
import org.solmix.api.request.RequestProgressTracker;
import org.solmix.api.request.SlxHttpServletRequest;
import org.solmix.api.request.SlxHttpServletResponse;
import org.solmix.api.servlet.FilterManager;
import org.solmix.api.servlet.ServletManager;
import org.solmix.fmk.engine.internal.filter.RequestFilterChain;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class SlxRequestProcessor implements RequestProcessor
{

    /** default log */
    private final Logger log = LoggerFactory.getLogger(SlxRequestProcessor.class);

    private ServletManager servletManager;

    private FilterManager filterManager;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.request.RequestProcessor#processRequest(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // setting the Sling request and response
        final RequestData requestData = new RequestData(this, request, response);
        SlxHttpServletRequest slxRequest = requestData.getSlxRequest();
        SlxHttpServletResponse slxResponse = requestData.getSlxResponse();
        try {
            // TODO recoreds Requests.
            final ServletManager servletManager = this.servletManager;
            if (servletManager == null) {
                throw new UnavailableException("servletManager");
            }
            requestData.initServlet(servletManager);
            Filter[] filters = filterManager.getFilters();
            if (filters != null&&filters.length>0) {
                FilterChain processor = new RequestFilterChain(this, filters);
                slxRequest.getRequestProgressTracker().log("Applying filters");
                processor.doFilter(slxRequest, slxResponse);
            } else {
                RequestData.service(slxRequest, slxResponse);
            }

        } catch (AccessControlException ace) {

            log.info("service: Authenticated user {} does not have enough rights to executed requested action", request.getRemoteUser());
            // handleError(HttpServletResponse.SC_FORBIDDEN, null, request, response);

        } catch (UnavailableException ue) {

            // exception is thrown before the SlingHttpServletRequest/Response
            // is properly set up due to missing dependencies. In this case
            // we must not use the Sling error handling infrastructure but
            // just return a 503 status response handled by the servlet
            // container environment

            final int status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
            final String errorMessage = ue.getMessage() + " service missing, cannot service requests";
            log.error("{} , sending status {}", errorMessage, status);
            // servletResponse.sendError(status, errorMessage);

        } catch (Throwable t) {

            // if we have request data and a non-null active servlet name
            // we assume, that this is the name of the causing servlet
            if (requestData.getActiveServletName() != null) {
                request.setAttribute("error.servlet.name", requestData.getActiveServletName());
            }

            log.error("service: Uncaught Throwable", t);
            // handleError(t, request, response);

        } finally {

            if (log.isDebugEnabled()) {
                RequestProgressTracker logTracker = requestData.getRequestProgressTracker();
                if (logTracker != null) {
                    Iterator<String> it = logTracker.getMessages();
                    while (it.hasNext()) {
                        log.debug(it.next());
                    }
                }
            }
        }

    }

    /**
     * @param servletManager the servletManager to set
     */
    public void setServletManager(ServletManager servletManager) {
        this.servletManager = servletManager;
    }

    /**
     * @param filterManager the filterManager to set
     */
    public void setFilterManager(FilterManager filterManager) {
        this.filterManager = filterManager;
    }

    /**
     * 
     */
    public void unsetServletManager(ServletManager servletManager) {
        if (this.servletManager == servletManager) {
            this.servletManager = null;
        }

    }

    /**
     * @param serverInfo
     */
    public void setServerInfo(String serverInfo) {
        // TODO Auto-generated method stub

    }

}
