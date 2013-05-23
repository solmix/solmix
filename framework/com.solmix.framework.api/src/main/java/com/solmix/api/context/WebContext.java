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

package com.solmix.api.context;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import com.solmix.api.exception.SlxException;

/**
 * This is a web context extends {@link com.solmix.api.context.Context} , {@link com.solmix.api.datasource.DSRequest
 * DataSource Request} from web servlet, must used this interface to access the framework.
 * 
 * @version 110035 2012-9-28
 */

public interface WebContext extends Context
{

    /**
     * Get a parameter value as string.
     * 
     * @return parameter value
     */
    public String getParameter(String name);

    /**
     * Get all parameter values as a Map&lt;String, String&gt;.
     * 
     * @return parameter values
     */
    public Map<String, String> getParameters();

    /**
     * Get the current context path.
     */
    public String getContextPath();

    /**
     * Get the solmix wrapped http-request.
     * 
     * @return the request
     */
    public HttpServletRequest getRequest();

    /**
     * Get the solmix wrapped http-response.
     * 
     * @return
     */
    public HttpServletResponse getResponse();

    /**
     * Get the current Servlet Context.
     * 
     * @return
     */
    ServletContext getServletContext();

    /**
     * Parser Cookies as a String pair .Like:
     * 
     * <pre>
     * Name:'name1',Value='vlaue1', domain:'..' , path:'..' , maxAge:'..' , isSecure:'..'
     * </pre>
     * 
     * @return
     */
    String getCookiesAsString();

    /**
     * Parser Headers as a String pair .Like:
     * 
     * <pre>
     * headerName = value
     * </pre>
     * 
     * @return
     */
    public String getHeadersAsString();

    public String getParamsAsString() throws SlxException;

    public void setNoCacheHeaders() throws SlxException;

    /**
     * Return the ServletResponse writer.if the content type is null set "text/html"
     * 
     * @return the out
     * @throws SlxException
     */
    public Writer getOut() throws SlxException;

     public void setOut(Writer out);

    /**
     * @return the requestPath
     */
    public String getRequestPath();

    /**
     * @return the servletPath
     */
    public String getServletPath();

    /**
     * @return the session
     */
    public HttpSession getSession();

    public boolean isCachingEnabled();

    public void setCachingEnabled(boolean cachingEnabled);

    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request);

    /**
     * @param response the response to set
     */
    public void setResponse(HttpServletResponse response);

    /**
     * @return
     */
    public boolean isMultipart();

    /**
     * @param fieldName
     * @param errors
     * @return
     */
    public FileItem getUploadedFile(String fieldName, List<Object> errors) throws SlxException;

    /**
     * @param mimeType
     */
    public void setContentType(String mimeType);

    public void init(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws SlxException;
}
