/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.servlet;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.fileupload.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.context.WebContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-21 solmix-utils
 */
public class ServletTools
{

    private static Logger log = LoggerFactory.getLogger(ServletTools.class);

    /**
     * <code>&lt;HTML&gt;&lt;BODY&gt;</code>
     */
    private static String htmlStart = "<HTML><BODY>";

    /**
     * &lt;/BODY&gt;&lt;/HTML&gt;
     */
    private static String htmlEnd = "</BODY></HTML>";

    /**
     * Put the QueryString into Map. just like "name = value"into a map(name,value)
     * 
     * @param queryString
     * @return
     * @throws Exception
     */
    public static Map<String, String> parseQueryString(String queryString) throws SlxException {
        Map<String, String> params = new HashMap<String, String>();
        if (queryString != null) {
            if (queryString.startsWith("?"))
                queryString = queryString.substring(1);
            URLCodec urlCodec = new URLCodec("UTF-8");
            List<String> paramPairs = DataUtil.simpleSplit(queryString, "&");
            for (String str : paramPairs) {
                List<String> keyVale = DataUtil.simpleSplit(str, "=");

                try {
                    params.put(urlCodec.decode(keyVale.get(0)), urlCodec.decode(keyVale.get(1)));
                } catch (DecoderException e) {
                    throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_QUERYPARM_DECODE, e);
                }
            }
        }
        return params;
    }

    /**
     * According to the request path for MIME type
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public static String mimeTypeForContext(WebContext context) throws Exception {
        return DataUtil.mimeTypeForFileName(context.getRequestPath());
    }

    public static Map<String, Object> paramsToMap(ServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Enumeration paramNames = request.getParameterNames(); paramNames.hasMoreElements();) {
            String name = (String) paramNames.nextElement();
            String values[] = request.getParameterValues(name);
            if (values.length == 0)
                map.put(name, "");
            else if (values.length == 1)
                map.put(name, values[0]);
            else
                map.put(name, Arrays.asList(values));
        }

        return map;
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
        else
            return "Unsupported";
    }

    public static String getBrowserSummary(WebContext context) {
        return (new StringBuilder()).append(browserShortName(context.getRequest())).append(
            browserClaimsGZSupport(context.getRequest()) ? " with" : " WITHOUT").append(" Accept-Encoding header").append(
            !browserIsMSIE(context.getRequest()) || !IEReadyForCompressedJS(context) ? "" : ", ready for compressed JS").toString();
    }

    public static boolean IEReadyForCompressedJS(WebContext context) {
        if (!browserIsMSIE(context.getRequest()))
            return true;
        if (!jscriptContentEncodingCompressionEnabled())
            return false;
        if (!browserClaimsGZSupport(context.getRequest()))
            return false;
        if (IEIsOlderThanIE6SP2(context.getRequest()))
            return compressionReadyCookieIsSet(context);
        else
            return true;
    }

    public static boolean compressionReadyCookieIsSet(WebContext context) {
        String cookieValue = getCookieValue("isc_cState", context.getRequest());
        return cookieValue != null && !cookieValue.equals("__null__");
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

    public static boolean IEIsOlderThanIE6SP2(HttpServletRequest request) {
        String UA = request.getHeader("User-Agent");
        if (UA == null)
            return true;
        Float browserVersion;
        Float platformVersion;
        Float sixZero;
        Float fiveOne;
        try {
            browserVersion = IEBrowserVersion(UA);
            platformVersion = IEPlatformVersion(UA);
            sixZero = new Float("6.0");
            fiveOne = new Float("5.1");
            if (browserVersion.compareTo(sixZero) == -1 || platformVersion.compareTo(fiveOne) == -1)
                return true;
        } catch (Exception e) {
            log.warn((new StringBuilder()).append("Couldn't parser browser or platform version in UA: ").append(UA).toString());
            return true;
        }
        if (browserVersion.compareTo(sixZero) == 0 && platformVersion.compareTo(fiveOne) == 0)
            return UA.indexOf("SV1") == -1;
        return false;
    }

    public static Float IEPlatformVersion(String UA) {
        int start = UA.indexOf("Windows NT");
        if (start == -1)
            return new Float(3F);
        UA = UA.substring(start + 11);
        int end = UA.indexOf(";");
        if (end == -1)
            end = UA.indexOf(")");
        UA = UA.substring(0, end);
        String platformVersion = UA;
        return new Float(platformVersion);
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

    public static boolean jscriptContentEncodingCompressionEnabled() {
        return compressionEnabled();
        // && config.getBoolean("servlet.compress.jscriptContentEncoding", false);
    }

    public static boolean compressionEnabled() {
        // TODO
        return true;

        // return OSGIHelper.getCM().getSubtree("servlet").getBoolean("compress", false);
    }

    public static boolean browserClaimsGZSupport(HttpServletRequest request) {
        return false;
        // String viaHeader = request.getHeader("via");
        // if (viaHeader != null) {
        // log.debug((new StringBuilder()).append("Client sent Via header: ").append(viaHeader).toString());
        // int indexHttp10 = viaHeader.indexOf("1.0");
        // TODO
        // if (indexHttp10 != -1 &&
        // OSGIHelper.getCM().getSubtree("servlet").getBoolean("disableCompressionIfProxyChainContainsHTTP10", true)) {
        // String encodingHeader = request.getHeader("Accept-Encoding");
        // log.info((new StringBuilder()).append(
        // "Disallowing compression for request whose proxy chain contains an HTTP 1.0 proxy. Via header: ").append(viaHeader).append(
        // " Accept-Encoding header: ").append(encodingHeader != null ? encodingHeader : "not set").toString());
        // return false;
    }

    // }
    // TODO
    // if (OSGIHelper.getCM().getSubtree("servlet").getBoolean("useCompressionThroughProxies", false)
    // && (browserIsMSIE(request) || browserIsNav4(request) || browserIsMoz(request))) {
    // return true;
    // } else {
    // String encodingHeader = request.getHeader("Accept-Encoding");
    // return encodingHeader != null && encodingHeader.indexOf("gzip") != -1;
    // }
    // }

    public static boolean browserIsNav4(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtil.contains(userAgent, "Mozilla/4") && !DataUtil.contains(userAgent, "MSIE")
            && !DataUtil.contains(userAgent, "Opera");
    }

    public static boolean browserIsMSIE(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtil.contains(userAgent, "MSIE");
    }

    public static boolean browserIsMoz(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtil.contains(userAgent, "Gecko/");
    }

    public static boolean browserIsSafari(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && DataUtil.contains(userAgent, "Safari");
    }

    /**
     * send HTML to client
     * 
     * @param out always set by {@link javax.servlet.http.HttpServletResponse#getWriter getWriter()}
     * @throws SlxException
     * @throws Exception
     */
    public static void sendHTMLStart(Writer out) throws SlxException {
        synchronized (out) {
            try {
                out.write(htmlStart);
                out.flush();
            } catch (IOException e) {
                throw new SlxException(Tmodule.SERVLET, Texception.IO_EXCEPTION, e);
            }
        }
    }

    public static void sendHTMLEnd(Writer out) throws SlxException

    {
        synchronized (out) {
            try {
                out.write(htmlEnd);
                out.flush();
            } catch (IOException e) {
                throw new SlxException(Tmodule.SERVLET, Texception.IO_EXCEPTION, e);
            }
        }
    }

    public static String getContainerPath(ServletContext servletContext) throws SlxException {
        URL url = null;
        try {
            url = servletContext.getResource("/");
        } catch (MalformedURLException e) {
            throw new SlxException(Tmodule.SERVLET, Texception.IO_EXCEPTION, e);
        }
        if (url != null)
            return url.getPath();
        else
            return null;

    }

    /**
     * @param response
     * @param errorMessage
     * @param t
     */
    public static void handleServletError(HttpServletResponse response, String errorMessage, Throwable t) {
        // TODO Auto-generated method stub

    }

    public static boolean compressionEnabledForMimeType(String mimeType) {
        return compressionEnabledForMimeType(mimeType, null);
    }

    /**
     * @param mimeType
     * @param compressableMimeTypes
     * @return
     */
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
        // TODO
        // compressableMimeTypes = (List<String>)
        // OSGIHelper.getCM().getSubtree("compression").getList("compressableMimeTypes");
        // if (compressableMimeTypes == null) {
        // log.debug("compression.compressableMimeTypes is null, assuming compression enabled for all mime types.");
        // return true;
        // }
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
        // TODO
        List alwaysCompressMimeTypes = new ArrayList();
        // List alwaysCompressMimeTypes =
        // OSGIHelper.getCM().getSubtree("compression").getList("alwaysCompressMimeTypes");
        for (Iterator i = alwaysCompressMimeTypes.iterator(); i.hasNext();) {
            String compressableMimeType = (String) i.next();
            if (mimeTypeLC.indexOf(compressableMimeType.toLowerCase()) != -1)
                return true;
        }

        return false;
    }

    public static Float IEBrowserVersion(HttpServletRequest request) {
        return IEBrowserVersion(request.getHeader("User-Agent"));
    }

    /**
     * @param context
     * @param mimeType
     * @return
     */
    public static boolean compressionWorksForMimeType(WebContext context, String mimeType) {
        if (mimeType == null)
            return false;
        if (DataUtil.contains(mimeType, "zip"))
            return false;
        if (DataUtil.contains(mimeType, "css")
            && (browserIsNav4(context.getRequest()) || browserIsMSIE(context.getRequest())
                && IEBrowserVersion(context.getRequest()).floatValue() <= 5.5D))
            return false;
        if (!DataUtil.contains(mimeType, "javascript"))
            return true;
        if (browserIsNav4(context.getRequest()))
            return false;
        return IEReadyForCompressedJS(context);
    }

    /**
     * @param context
     * @return
     */
    public static boolean compressionWorksForContext(RequestContext context) {
        return true;
    }

    /**
     * @param context
     * @return
     */
    public static boolean contextIsIncluded(RequestContext context) {
        // TODO Auto-generated method stub
        return false;
    }

    public static boolean IENeedsToSeeACompressedPage(WebContext context) {
        return browserIsMSIE(context.getRequest()) && browserClaimsGZSupport(context.getRequest()) && jscriptContentEncodingCompressionEnabled()
            && !IEReadyForCompressedJS(context);
    }

    /**
     * @param context
     */
    public static void setCompressionReadyCookie(WebContext context) {
        setCookie(context, "isc_cState", "ready", "/");

    }

    public static Cookie setCookie(WebContext context, String name, String value, String path) {
        return setCookie(context, name, value, path, null, -1);
    }

    public static Cookie setCookie(WebContext context, String name, String value, String path, String domain, int maxAge) {
        log.debug((new StringBuilder()).append("setting cookie '").append(name).append("' to: '").append(value).append("'").toString());
        Cookie cookie = new Cookie(name, value);
        if (path == null)
            path = "/";
        cookie.setPath(path);
        if (maxAge != -1)
            cookie.setMaxAge(maxAge);
        if (domain != null) {
            log.debug((new StringBuilder()).append("setting domain to: ").append(domain).append(" fo cookie: ").append(name).toString());
            cookie.setDomain(domain);
        }
        context.getResponse().addCookie(cookie);
        return cookie;
    }
}
