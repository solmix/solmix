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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月21日
 */

public class ServletUtils {

    private ServletUtils(){
        
    }
    public static String getResourcePath(HttpServletRequest request) {
        String pathInfo = FileUtils.normalizeAbsolutePath(request.getPathInfo(), false);
        String servletPath = FileUtils.normalizeAbsolutePath(request.getServletPath(), pathInfo.length() != 0);

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
        buf.append(FileUtils.normalizeAbsolutePath(request.getContextPath(), true));

        return buf.toString();
    }
    
    public static String normalizeURI(String uri) {
        return URI.create(StringUtils.trimToEmpty(uri)).normalize().toString();
    }
    
    /**
     * Put the QueryString into Map. just like "name = value"into a map(name,value)
     * 
     * @param queryString
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<String, String>();
        if (queryString != null) {
            if (queryString.startsWith("?"))
                queryString = queryString.substring(1);
            URLCodec urlCodec = new URLCodec("UTF-8");
            List<String> paramPairs = DataUtils.simpleSplit(queryString, "&");
            for (String str : paramPairs) {
                List<String> keyVale = DataUtils.simpleSplit(str, "=");

                try {
                    params.put(urlCodec.decode(keyVale.get(0)), urlCodec.decode(keyVale.get(1)));
                } catch (DecoderException e) {
                    throw new RuntimeException("parseQueryString", e);
                }
            }
        }
        return params;
    }
    
    public static String mimeTypeForPath(String path) throws Exception {
        return DataUtils.mimeTypeForFileName(path);
    }
    public static String browserOS(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        if(agent==null){
            agent="";
        }
        String[] pl=agent.split(";");
        if(pl.length>=2){
            return pl[1];
        }
        return "";
    }
    public static String browserIpAddress(HttpServletRequest request) {
       String ip=request.getHeader("x-forwarded-for");
       if(StringUtils.isEmpty(ip)||"unknown".equals(ip)){
           ip=request.getHeader("Proxy-Client-IP");
       }
       if(StringUtils.isEmpty(ip)||"unknown".equals(ip)){
           ip=request.getHeader("WL-Proxy-Client-IP");
       }
       if(StringUtils.isEmpty(ip)||"unknown".equals(ip)){
           ip=request.getRemoteAddr();
       }
       if(StringUtils.isEmpty(ip)||"unknown".equals(ip)){
           ip=request.getRemoteHost();
       }
        return ip;
    }
    public static String browserShortName(HttpServletRequest request) {
        if (browserIsMSIE(request))
            return "MSIE";
        if (browserIsNav4(request))
            return "Nav4";
        if (browserIsMoz(request))
            return "Moz (Gecko)";
        if (browserIsSafari(request))
            return "Safari";
        else{
            String agent = request.getHeader("User-Agent");
            if(agent==null){
                agent="";
            }
            return agent;
        }
    }
    

    public static boolean browserIsMSIE(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtils.contains(userAgent, "MSIE");
    }

    public static boolean browserIsMoz(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtils.contains(userAgent, "Gecko/");
    }

    public static boolean browserIsSafari(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtils.contains(userAgent, "Safari");
    }
    
    public static boolean browserIsNav4(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtils.contains(userAgent, "Mozilla/4") && !DataUtils.contains(userAgent, "MSIE")
            && !DataUtils.contains(userAgent, "Opera");
    }
    
    public static boolean compressionEnabledForMimeType(String mimeType) {
        return compressionEnabledForMimeType(mimeType, null);
    }
    
    public static boolean compressionEnabledForMimeType(String mimeType, List<String> compressableMimeTypes) {
        if (mimeType == null)
            return false;
        String mimeTypeLC = mimeType.toLowerCase();
        if (compressableMimeTypes != null) {
            for (String compressableMimeType : compressableMimeTypes) {
                if (mimeTypeLC.indexOf(compressableMimeType.toLowerCase()) != -1)
                    return true;
            }
            return false;
        }
        if (alwaysCompressMimeType(mimeType))
            return true;
        
        for (String compressableMimeType : compressableMimeTypes) {
            if (mimeTypeLC.indexOf(compressableMimeType.toLowerCase()) != -1)
                return true;
        }
        return false;
    }
    
    public static boolean alwaysCompressMimeType(String mimeType) {
        if (mimeType == null)
            return false;
        String mimeTypeLC = mimeType.toLowerCase();
        List alwaysCompressMimeTypes = new ArrayList();
        for (Iterator i = alwaysCompressMimeTypes.iterator(); i.hasNext();) {
            String compressableMimeType = (String) i.next();
            if (mimeTypeLC.indexOf(compressableMimeType.toLowerCase()) != -1)
                return true;
        }

        return false;
    }
    
    public static boolean compressionWorksForMimeType(HttpServletRequest request, String mimeType) {
        if (mimeType == null)
            return false;
        if (DataUtils.contains(mimeType, "zip"))
            return false;
        if (DataUtils.contains(mimeType, "css")
            && (browserIsNav4(request) || browserIsMSIE(request)
                && IEBrowserVersion(request).floatValue() <= 5.5D))
            return false;
        if (!DataUtils.contains(mimeType, "javascript"))
            return true;
        if (browserIsNav4(request))
            return false;
        return false;
    }
    
    public static Float IEBrowserVersion(HttpServletRequest request) {
        return IEBrowserVersion(request.getHeader("User-Agent"));
    }
    
    public static Float IEBrowserVersion(String UA) {
        int start = UA.indexOf("MSIE");
        UA = UA.substring(start + 5);
        int end = UA.indexOf(";");
        UA = UA.substring(0, end);
        for (String browserVersion = UA; browserVersion.length() > 0;)
            try {
                return new Float(browserVersion);
            } catch (NumberFormatException nfe) {
                browserVersion = browserVersion.substring(0, browserVersion.length() - 1);
            }

        return new Float(4F);
    }
    public static String encodeParameter(String name, String value) {
        Pattern tspecials = Pattern.compile("[<()@,;:/?={} >\"\\[\\]\\t\\\\]");
        Matcher matcher = tspecials.matcher(value);
        if (value.length() <= 78)
            if (!matcher.find())
                return (new StringBuilder()).append(name).append("=").append(value).toString();
            else
                return (new StringBuilder()).append(name).append("=").append("\"").append(value).append("\"").toString();
        int counter = 0;
        String returnVal = "";
        for (; value.length() > 78; value = value.substring(78)) {
            String work = value.substring(0, 78);
            matcher.reset(work);
            if (matcher.find())
                work = (new StringBuilder()).append("\"").append(work).append("\"").toString();
            if (counter > 0)
                returnVal = (new StringBuilder()).append(returnVal).append("; ").toString();
            returnVal = (new StringBuilder()).append(returnVal).append(name).append("*").append(counter).append("=").append(work).toString();
            counter++;
        }

        matcher.reset(value);
        if (matcher.find())
            value = (new StringBuilder()).append("\"").append(value).append("\"").toString();
        if (counter > 0)
            returnVal = (new StringBuilder()).append(returnVal).append("; ").toString();
        returnVal = (new StringBuilder()).append(returnVal).append(name).append("*").append(counter).append("=").append(value).toString();
        return returnVal;
    }
    
    public static Cookie getCookie(String targetCookieName, HttpServletRequest request) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null) {
            for (int ii = 0; ii < cookies.length; ii++) {
                String currentCookieName = cookies[ii].getName();
                if (cookies[ii].getName().equals(targetCookieName))
                    if (cookies[ii].getValue().equals("__null__"))
                        return null;
                    else
                        return cookies[ii];
            }

        }
        return null;
    }

    public static String getCookieValue(String targetCookieName, HttpServletRequest request) {
        Cookie theCookie = getCookie(targetCookieName, request);
        if (theCookie == null)
            return null;
        else
            return theCookie.getValue();
    }
}
