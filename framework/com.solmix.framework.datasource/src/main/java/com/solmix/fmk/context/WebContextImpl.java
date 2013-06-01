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

package com.solmix.fmk.context;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.oro.text.perl.Perl5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.context.WebContext;
import com.solmix.api.exception.SlxException;
import com.solmix.api.serialize.JSParser;
import com.solmix.api.serialize.JSParserFactory;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.collections.DataTypeMap;
import com.solmix.fmk.internel.DSConfigManager;
import com.solmix.fmk.serialize.JSParserFactoryImpl;
import com.solmix.fmk.servlet.ServletTools;
import com.solmix.fmk.servlet.WrappedHttpServletRequest;

/**
 * The method of this type ,can't use local cache to provider result,because every time of new request will be
 * {@link #init(HttpServletRequest, HttpServletResponse, ServletContext)} this type
 * 
 * @version 110035 2012-10-8
 * @since 0.1
 */

public class WebContextImpl extends UserContextImpl implements WebContext
{

    private static Logger log = LoggerFactory.getLogger(WebContextImpl.class.getName());

    public ServletContext servletContext;

    public WrappedHttpServletRequest wrappedRequest;

    private JSParser jsParser;

    protected Writer out;

    private static DataTypeMap globalConfig;

    public HttpSession session;

    public String requestPath;

    public String servletPath;

    public String pathInfo;

    private HttpServletResponse response;

    public boolean cachingEnabled;

    public String contentType;

    /**
     * used with WebContextFactory.
     */
    WebContextImpl()
    {
    }

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws SlxException {
        this.setAttributeProvider(new WebAttributeProvider(this));
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        this.jsParser = jsFactory.get();
        this.servletContext = servletContext;
        globalConfig = DSConfigManager.getConfig().getSubtree("request");
        requestPath = getRequestPath(request);
        if (globalConfig == null)
            globalConfig = new DataTypeMap();
        this.session = request.getSession();
        servletPath = request.getServletPath();
        pathInfo = request.getPathInfo();
        this.wrappedRequest = new WrappedHttpServletRequest(request);
        this.response = response;
        if (globalConfig.getBoolean("reportParams", false)) {
            if (log.isDebugEnabled())
                log.debug((new StringBuilder()).append("Request parameters: ").append(getParamsAsString()).toString());
        }
        if (log.isInfoEnabled()) {
            String cgetHeader = request.getHeader("If-Modified-Since");
            boolean isCGET = cgetHeader != null && !"".equals(cgetHeader);
            boolean alreadyLoggedURL = request.getAttribute("isc_alreadyLoggedURL") != null;
            if ((!isCGET || log.isDebugEnabled()) && !alreadyLoggedURL) {
                request.setAttribute("isc_alreadyLoggedURL", new Object());
                log.debug((new StringBuilder()).append(Thread.currentThread().getId()).append('-').append(isCGET ? "CGET " : "").append("URL: '").append(requestPath).append("'").append(
                    ", User-Agent: '").append(request.getHeader("User-Agent")).append("'").append(": ").append(ServletTools.getBrowserSummary(this)).toString());
            }
        }
        if (globalConfig.getBoolean("logCookies", false))
            logCookies();
        if (globalConfig.getBoolean("logHeaders", false))
            logHeaders();
    }

    public void logCookies() {
        if (log.isInfoEnabled())
            log.info(getCookiesAsString());
    }

    public void logHeaders() {
        if (log.isInfoEnabled())
            log.info(getHeadersAsString());
    }

    /**
     * @param request
     * @return
     */
    public static String getRequestPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        if (requestPath.indexOf("//") != -1) {
            Perl5Util regex = new Perl5Util();
            requestPath = regex.substitute("s#//#/#g", requestPath);
        }
        return requestPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(String name) {

        return this.wrappedRequest.getParameter(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getParameters()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getParameters() {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> paramEnum = this.wrappedRequest.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            final String name = paramEnum.nextElement();
            map.put(name, this.wrappedRequest.getParameter(name));
        }
        return map;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getContextPath()
     */
    @Override
    public String getContextPath() {
        return this.wrappedRequest.getContextPath();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getRequest()
     */
    @Override
    public HttpServletRequest getRequest() {
        return this.wrappedRequest;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getResponse()
     */
    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getServletContext()
     */
    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getCookiesAsString()
     */
    @Override
    public String getCookiesAsString() {
        String output = "Cookies:";
        Cookie cookies[] = wrappedRequest.getCookies();
        if (cookies == null) {
            output = (new StringBuilder()).append(output).append(" NONE").toString();
            return output;
        }
        for (int ii = 0; ii < cookies.length; ii++)
            output = (new StringBuilder()).append(output).append("\nName: '").append(cookies[ii].getName()).append("', value: '").append(
                cookies[ii].getValue()).append("', domain: '").append(cookies[ii].getDomain()).append("', path: '").append(cookies[ii].getPath()).append(
                "', maxAge: '").append(cookies[ii].getMaxAge()).append("', isSecure: '").append(cookies[ii].getSecure()).append("'").toString();

        return output;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getHeadersAsString()
     */
    @Override
    public String getHeadersAsString() {
        String output = "Client HTTP Headers:";
        Enumeration<?> headers = this.wrappedRequest.getHeaderNames();
        if (headers == null)
            output = (new StringBuilder()).append(output).append(" request.getHeaderNames() returned null").toString();
        else
            while (headers.hasMoreElements()) {
                String headerName = (String) headers.nextElement();
                output = (new StringBuilder()).append(output).append("\n").append(headerName).append(": ").append(
                    wrappedRequest.getHeader(headerName)).toString();
            }
        return output;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getParamsAsString()
     */
    @Override
    public String getParamsAsString() throws SlxException {
        return jsParser.toJavaScript(wrappedRequest.getParams());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setNoCacheHeaders()
     */
    @Override
    public void setNoCacheHeaders() throws SlxException {
        if (response.isCommitted())
            throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_REQ_ALREADY_COMMITED,
                "Response has already been committed, unable to setNoCacheHeaders()");
        if (cachingEnabled) {
            setNoCacheHeaders(response);
            cachingEnabled = false;
        }

    }

    public static void setNoCacheHeaders(HttpServletResponse response) {
        if (log.isDebugEnabled())
            log.debug("Setting headers to disable caching");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", System.currentTimeMillis());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getOut()
     */
    @Override
    public Writer getOut() throws SlxException {
        Writer out ;
            if (log.isDebugEnabled())
                log.debug("Getting output stream via servletResponse.getWriter()");
            if (contentType == null)
                setContentType(globalConfig.getString("defaultMimeType", "text/html"));
            try {
                out = response.getWriter();
            } catch (IOException e) {
                throw new SlxException(Tmodule.SERVLET, Texception.IO_EXCEPTION, "ioexception with response.getWriter()", e);
            }
        return out;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setOut(java.io.Writer)
     */
    @Override
    public void setOut(Writer out) {
        this.out = out;

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getRequestPath()
     */
    @Override
    public String getRequestPath() {
        return this.requestPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getServletPath()
     */
    @Override
    public String getServletPath() {
        return this.servletPath;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#getSession()
     */
    @Override
    public HttpSession getSession() {
        return this.session;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#isCachingEnabled()
     */
    @Override
    public boolean isCachingEnabled() {
        return this.cachingEnabled;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setCachingEnabled(boolean)
     */
    @Override
    public void setCachingEnabled(boolean cachingEnabled) {
        this.cachingEnabled = cachingEnabled;

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setRequest(com.solmix.api.request.SlxHttpServletRequest)
     */
    @Override
    public void setRequest(HttpServletRequest request) {
        this.wrappedRequest = null;
        this.wrappedRequest = new WrappedHttpServletRequest(request);

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#isMultipart()
     */
    @Override
    public boolean isMultipart() {

        return wrappedRequest.isMultipart();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setResponse(javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void setResponse(HttpServletResponse response) {
        this.response = response;

    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see com.solmix.api.context.WebContext#getUploadedFile(java.lang.String, java.util.List)
     */
    @Override
    public FileItem getUploadedFile(String fieldName, List<Object> errors) throws SlxException {
        return wrappedRequest.getUploadedFile(fieldName, errors);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.WebContext#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String mimeType) {
        this.contentType = mimeType;
        response.setContentType(mimeType);

    }

    @Override
    public void release() {
        this.wrappedRequest = null;
        this.response = null;

    }

}
