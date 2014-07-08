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

package org.solmix.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.solmix.SlxConstants;
import org.solmix.commons.util.IOUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-4-17
 */

@SuppressWarnings("serial")
public class StaticResourceServlet extends HttpServlet
{

    private final String name;
    private final String alies;
    private final BundleContext context;

    public StaticResourceServlet( String name,BundleContext context)
    {
        this.alies=null;
        this.name=name;
        this.context=context;
    }
    /**
     * @param alies
     * @param name
     */
    public StaticResourceServlet(String alies, String name,BundleContext context)
    {
        this.alies=alies;
        this.name=name;
        this.context=context;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
     
        String target = req.getPathInfo();
        if(this.alies!=null){
            target = target.replace(alies, name);
        }
        if (target == null) {
            target = "";
        }

        if (!target.startsWith("/")) {
            target += "/" + target;
        }

       final String resName = target;
        URL url = this.context.getBundle().getResource(resName);
        if (url == null) {
            String webRoot = System.getProperty(SlxConstants.SOLMIX_WEB_ROOT);
            assert webRoot != null;
            if (webRoot.endsWith("/") || webRoot.endsWith("\\"))
                webRoot = webRoot.substring(0, webRoot.length() - 1);
            try {
                File f = new File(webRoot + resName);
                if (f.exists())
                    url = f.toURL();
            } catch (Exception e) {
                url = null;
            }
        }
        if (url == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            handle(req, res, url, resName);
        }
    }

    private void handle(HttpServletRequest req, HttpServletResponse res, URL url, String resName) throws IOException {
        String contentType = getServletContext().getMimeType(resName);
        if (contentType != null) {
            res.setContentType(contentType);
        }

        long lastModified = getLastModified(url);
        if (lastModified != 0) {
            res.setDateHeader("Last-Modified", lastModified);
        }
        copyResource(url, res);
        if (!resourceModified(lastModified, req.getDateHeader("If-Modified-Since"))) {
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        } else {
            copyResource(url, res);
        }
    }

    private long getLastModified(URL url) {
        long lastModified = 0;

        try {
            URLConnection conn = url.openConnection();
            lastModified = conn.getLastModified();
        } catch (Exception e) {
            // Do nothing
        }

        if (lastModified == 0) {
            String filepath = url.getPath();
            if (filepath != null) {
                File f = new File(filepath);
                if (f.exists()) {
                    lastModified = f.lastModified();
                }
            }
        }

        return lastModified;
    }

    private boolean resourceModified(long resTimestamp, long modSince) {
        modSince /= 1000;
        resTimestamp /= 1000;

        return resTimestamp == 0 || modSince == -1 || resTimestamp > modSince;
    }

    private void copyResource(URL url, HttpServletResponse res) throws IOException {
        OutputStream os = null;
        InputStream is = null;
     // Write the resource to the client.
        URLConnection connection = url.openConnection();
        try {
            int length = connection.getContentLength();
            if (length >= 0) {
                res.setContentLength(length);
            }
        } catch (Throwable e) {
            // This can be ignored, content length header is not required.
            // Need to close the input stream because of
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4257700 to
            // prevent it from hanging, but that is done below.
        }
        try {
            os = res.getOutputStream();
            is = url.openStream();
            IOUtils.copyStreams(is, os);
        } finally {
            if (is != null) {
                is.close();
            }

            if (os != null) {
                os.close();
            }
        }
    }
}
