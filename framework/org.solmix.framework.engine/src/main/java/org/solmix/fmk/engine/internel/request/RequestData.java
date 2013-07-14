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

package org.solmix.fmk.engine.internel.request;

import static org.solmix.SlxConstants.CURRENT_SERVLET_NAME;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.api.request.RequestProgressTracker;
import org.solmix.api.request.RequestUtil;
import org.solmix.api.request.SlxHttpServletRequest;
import org.solmix.api.request.SlxHttpServletResponse;
import org.solmix.api.servlets.ServletManager;
import org.solmix.fmk.engine.MainServlet;

/**
 * Wrapper Httprequest and httpresponse.
 */

public class RequestData
{

    private MainServlet MAIN_SERVLET;

    private RequestProgressTracker requestProgressTracker;

    private SlxRequestProcessor requestProcessor;

    private HttpServletRequest servletRequest;

    private HttpServletResponse servletResponse;

    private SlxHttpServletRequestImpl slxRequest;

    private SlxHttpServletResponseImpl slxResponse;

    private String activeServletName;

    private Servlet currentServlet;

    private int servletCallCounter;

    public RequestData(SlxRequestProcessor requestProcessor, HttpServletRequest request, HttpServletResponse response)
    {
        this.requestProcessor = requestProcessor;
        this.servletRequest = request;
        this.servletResponse = response;
        this.slxRequest = new SlxHttpServletRequestImpl(this, request);
        this.slxResponse = new SlxHttpServletResponseImpl(this, response);

        this.requestProgressTracker = new SlxRequestProgressTracker();
        this.requestProgressTracker.log("Method={0}, PathInfo={1}", this.slxRequest.getMethod(), this.slxRequest.getPathInfo());

    }

    @Deprecated
    public <Type> Type adaptTo(Object object, Class<Type> type) {
        return MAIN_SERVLET.adaptTo(object, type);
    }

    /**
     * @return
     */
    public RequestProgressTracker getRequestProgressTracker() {
        return requestProgressTracker;
    }

    /**
     * @param requestProgressTracker the requestProgressTracker to set
     */
    public void setRequestProgressTracker(RequestProgressTracker requestProgressTracker) {
        this.requestProgressTracker = requestProgressTracker;
    }

    /**
     * @return the slxRequest
     */
    public SlxHttpServletRequestImpl getSlxRequest() {
        return slxRequest;
    }

    /**
     * @return the slxResponse
     */
    public SlxHttpServletResponseImpl getSlxResponse() {
        return slxResponse;
    }

    /**
     * Sets the name of the currently active servlet and returns the name of the previously active servlet.
     */
    public String setActiveServletName(String servletName) {
        String old = activeServletName;
        activeServletName = servletName;
        return old;
    }

    /**
     * Returns the name of the currently active servlet. If this name is not <code>null</code> at the end of request
     * processing, more precisly in the case of an uncaught <code>Throwable</code> at the end of request processing,
     * this is the name of the servlet causing the uncaught <code>Throwable</code>.
     */
    public String getActiveServletName() {
        return activeServletName;
    }

    /**
     * @param servletManager
     */
    public void initServlet(ServletManager servletManager) {
        requestProgressTracker.log("Resource Path Info: {0}", servletRequest.getPathInfo());

        // finally resolve the servlet for the resource
        requestProgressTracker.startTimer("ServletResolution");
        Servlet servlet = servletManager.resolveServlet(slxRequest);
        requestProgressTracker.logTimer("ServletResolution", "URI={0} handled by Servlet={1}", getServletRequest().getRequestURI(),
            (servlet == null ? "-none-" : RequestUtil.getServletName(servlet)));
        this.setCurrentServlet(servlet);

    }

    public static void service(SlxHttpServletRequest request, SlxHttpServletResponse response) throws IOException, ServletException {
        RequestData requestData = RequestData.getRequestData(request);
        Servlet servlet = requestData.getCurrentServlet();
        if (servlet == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Servlet to handle request");
        } else {

            String name = RequestUtil.getServletName(servlet);
            // TODO add max call controls.
            // replace the current servlet name in the request
            Object oldValue = request.getAttribute(CURRENT_SERVLET_NAME);
            request.setAttribute(CURRENT_SERVLET_NAME, name);

            // setup the tracker for this service call
            String timerName = name + "#" + requestData.servletCallCounter;
            requestData.servletCallCounter++;
            requestData.getRequestProgressTracker().startTimer(timerName);
            try {

                String callerServlet = requestData.setActiveServletName(name);

                servlet.service(request, response);

                requestData.setActiveServletName(callerServlet);

            } finally {

                request.setAttribute(CURRENT_SERVLET_NAME, oldValue);

                requestData.getRequestProgressTracker().logTimer(timerName);

            }
        }
    }

    /**
     * @param request
     * @return
     */
    public static RequestData getRequestData(ServletRequest request) {
        return unwrap(unwrap(request)).getRequestData();
    }

    /**
     * @param request
     * @return
     */
    private static SlxHttpServletRequestImpl unwrap(SlxHttpServletRequest request) {

        if (request instanceof SlxHttpServletRequestImpl) {
            return (SlxHttpServletRequestImpl) request;
        }

        throw new IllegalArgumentException("SlxHttpServletRequest not of correct type");
    }

    private static SlxHttpServletResponse unwrap(ServletResponse response) {
        // early check for most cases
        if (response instanceof SlxHttpServletResponse) {
            return (SlxHttpServletResponse) response;
        }

        // unwrap wrappers
        while (response instanceof ServletResponseWrapper) {
            response = ((ServletResponseWrapper) response).getResponse();
            ;

            // immediate termination if we found one
            if (response instanceof SlxHttpServletRequest) {
                return (SlxHttpServletResponse) response;
            }
        }

        // if we unwrapped everything and did not find a
        // SlingHttpServletRequest, we lost
        throw new IllegalArgumentException("ServletRequest not wrapping SlxHttpServletRequest");

    }

    private static SlxHttpServletRequest unwrap(ServletRequest request) {

        // early check for most cases
        if (request instanceof SlxHttpServletRequest) {
            return (SlxHttpServletRequest) request;
        }

        // unwrap wrappers
        while (request instanceof ServletRequestWrapper) {
            request = ((ServletRequestWrapper) request).getRequest();

            // immediate termination if we found one
            if (request instanceof SlxHttpServletRequest) {
                return (SlxHttpServletRequest) request;
            }
        }

        // if we unwrapped everything and did not find a
        // SlingHttpServletRequest, we lost
        throw new IllegalArgumentException("ServletRequest not wrapping SlxHttpServletRequest");
    }

    /**
     * @return the servletRequest
     */
    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    /**
     * @param servletRequest the servletRequest to set
     */
    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    /**
     * @return the servletResponse
     */
    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    /**
     * @param servletResponse the servletResponse to set
     */
    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    /**
     * @return the currentServlet
     */
    public Servlet getCurrentServlet() {
        return currentServlet;
    }

    /**
     * @param currentServlet the currentServlet to set
     */
    public void setCurrentServlet(Servlet currentServlet) {
        this.currentServlet = currentServlet;
    }

    /**
     * @param request
     * @return
     */
    public static SlxHttpServletRequest toSlxHttpServletRequest(ServletRequest request) {
        return unwrap(request);
    }

    public static SlxHttpServletResponse toSlxHttpServletResponse(ServletResponse response) {
        return unwrap(response);
    }

}
