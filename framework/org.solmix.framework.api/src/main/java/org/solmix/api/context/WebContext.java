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

package org.solmix.api.context;

import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.solmix.api.exception.SlxException;

/**
 * This is a web context extends {@link org.solmix.api.context.Context} , {@link org.solmix.api.datasource.DSRequest
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
    String getParameter(String name);

    /**
     * Get all parameter values as a Map&lt;String, String&gt;.
     * 
     * @return parameter values
     */
    Map<String, String> getParameters();

    /**
     * Get the current context path.
     */
    String getContextPath();

    /**
     * Get the solmix wrapped http-request.
     * 
     * @return the request
     */
    HttpServletRequest getRequest();

    /**
     * Get the solmix wrapped http-response.
     * 
     * @return
     */
    HttpServletResponse getResponse();

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
    String getHeadersAsString();

    String getParamsAsString() throws SlxException;

    void setNoCacheHeaders() throws SlxException;

    /**
     * Return the ServletResponse writer.if the content type is null set "text/html"
     * 
     * @return the out
     * @throws SlxException
     */
    Writer getOut() throws SlxException;

    void setOut(Writer out);

    /**
     * @return the requestPath
     */
    String getRequestPath();

    /**
     * @return the servletPath
     */
    String getServletPath();

    /**
     * @return the session
     */
    HttpSession getSession();

    boolean isCachingEnabled();

    void setCachingEnabled(boolean cachingEnabled);

    /**
     * @param request the request to set
     */
    void setRequest(HttpServletRequest request);

    /**
     * @param response the response to set
     */
    void setResponse(HttpServletResponse response);

    /**
     * @return
     */
    boolean isMultipart();

    /**
     * @param fieldName
     * @param errors
     * @return
     */
    FileItem getUploadedFile(String fieldName, List<Object> errors) throws SlxException;

    /**
     * @param mimeType
     */
    void setContentType(String mimeType);

    void init(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws SlxException;

    SystemContext getSystemContext();
}
