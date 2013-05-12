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

package com.solmix.fmk.engine.internel.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;

import com.solmix.fmk.engine.internel.request.SlxHttpServletRequestImpl;
import com.solmix.fmk.engine.internel.request.SlxHttpServletResponseImpl;

/**
 * Wrapper around a ServletContext for an external servlet context, i.e. one returned by
 * servletContext.getContext(String)
 */
public class ExternalServletContextWrapper implements ServletContext
{

    private final ServletContext delegate;

    public ExternalServletContextWrapper(ServletContext sc)
    {
        this.delegate = sc;
    }

    public ServletContext getContext(String s) {
        return delegate.getContext(s);
    }

    public int getMajorVersion() {
        return delegate.getMajorVersion();
    }

    public int getMinorVersion() {
        return delegate.getMinorVersion();
    }

    public String getMimeType(String s) {
        return delegate.getMimeType(s);
    }

    public Set getResourcePaths(String s) {
        return delegate.getResourcePaths(s);
    }

    public URL getResource(String s) throws MalformedURLException {
        return delegate.getResource(s);
    }

    public InputStream getResourceAsStream(String s) {
        return delegate.getResourceAsStream(s);
    }

    public RequestDispatcher getRequestDispatcher(String s) {
        return new RequestDispatcherWrapper(delegate.getRequestDispatcher(s));
    }

    public RequestDispatcher getNamedDispatcher(String s) {
        return new RequestDispatcherWrapper(delegate.getNamedDispatcher(s));
    }

    public Servlet getServlet(String s) throws ServletException {
        return delegate.getServlet(s);
    }

    public Enumeration getServlets() {
        return delegate.getServlets();
    }

    public Enumeration getServletNames() {
        return delegate.getServletNames();
    }

    public void log(String s) {
        delegate.log(s);
    }

    public void log(Exception exception, String s) {
        delegate.log(exception, s);
    }

    public void log(String s, Throwable throwable) {
        delegate.log(s, throwable);
    }

    public String getRealPath(String s) {
        return delegate.getRealPath(s);
    }

    public String getServerInfo() {
        return delegate.getServerInfo();
    }

    public String getInitParameter(String s) {
        return delegate.getInitParameter(s);
    }

    public Enumeration getInitParameterNames() {
        return delegate.getInitParameterNames();
    }

    public Object getAttribute(String s) {
        return delegate.getAttribute(s);
    }

    public Enumeration getAttributeNames() {
        return delegate.getAttributeNames();
    }

    public void setAttribute(String s, Object obj) {
        delegate.setAttribute(s, obj);
    }

    public void removeAttribute(String s) {
        delegate.removeAttribute(s);
    }

    public String getServletContextName() {
        return delegate.getServletContextName();
    }

    public String getContextPath() {
        return delegate.getContextPath();
    }

    static class RequestDispatcherWrapper implements RequestDispatcher
    {

        private final RequestDispatcher delegate;

        public RequestDispatcherWrapper(final RequestDispatcher rd)
        {
            this.delegate = rd;
        }

        public void forward(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
            delegate.forward(unwrapServletRequest(request), unwrapServletResponse(response));
        }

        public void include(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
            delegate.include(unwrapServletRequest(request), unwrapServletResponse(response));
        }

        RequestDispatcher getDelegate() {
            return delegate;
        }

        static ServletRequest unwrapServletRequest(ServletRequest request) {
            ServletRequest lastRequest = request;
            while (request != null) {
                if (request instanceof SlxHttpServletRequestImpl) {
                    return ((SlxHttpServletRequestImpl) request).getRequest();
                } else if (request instanceof ServletRequestWrapper) {
                    lastRequest = request;
                    request = ((ServletRequestWrapper) request).getRequest();
                } else {
                    return request;
                }
            }
            return lastRequest;
        }

        static ServletResponse unwrapServletResponse(ServletResponse response) {
            ServletResponse lastResponse = response;
            while (response != null) {
                if (response instanceof SlxHttpServletResponseImpl) {
                    return ((SlxHttpServletResponseImpl) response).getResponse();
                } else if (response instanceof ServletResponseWrapper) {
                    lastResponse = response;
                    response = ((ServletResponseWrapper) response).getResponse();
                } else {
                    return response;
                }
            }
            return lastResponse;
        }

    }

}
