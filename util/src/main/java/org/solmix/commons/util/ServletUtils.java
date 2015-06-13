/**
 * Copyright (c) 2015 The Solmix Project
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
package org.solmix.commons.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月21日
 */

public class ServletUtils {

    private ServletUtils(){
        
    }
    public static String getResourcePath(HttpServletRequest request) {
        String pathInfo = Files.normalizeAbsolutePath(request.getPathInfo(), false);
        String servletPath = Files.normalizeAbsolutePath(request.getServletPath(), pathInfo.length() != 0);

        return servletPath + pathInfo;
    }
    public static String getBaseURL(HttpServletRequest request) {
        String fullURL = request.getRequestURL().toString();
        String fullPath;

        try {
            fullPath = new URL(fullURL).getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + fullURL, e);
        }

        // 基本URL
        StringBuilder buf = new StringBuilder(fullURL);
        buf.setLength(fullURL.length() - fullPath.length());

        // 加上contextPath
        buf.append(Files.normalizeAbsolutePath(request.getContextPath(), true));

        return buf.toString();
    }
    public static String normalizeURI(String uri) {
        return URI.create(StringUtils.trimToEmpty(uri)).normalize().toString();
    }
}
