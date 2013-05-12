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

package com.solmix.fmk.servlets.internel.handler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.osgi.framework.ServiceReference;

import com.solmix.fmk.servlets.internel.ServletManagerImpl;
import com.solmix.fmk.servlets.internel.helper.SlxServletConfig;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-17
 */

public class ServletHandler
{

    private final String pattern;

    private final Servlet servlet;

    private final ServletContext context;

    private final ServiceReference<Servlet> reference;

    public ServletHandler(ServletContext context, Servlet servlet, ServiceReference<Servlet> reference, String pattern)
    {
        this.pattern = pattern;
        this.servlet = servlet;
        this.context = context;
        this.reference = reference;

    }

    public void init() throws ServletException {
        String name = ServletManagerImpl.getName(reference);
        ServletConfig config = new SlxServletConfig(this.context, reference, name);
        this.servlet.init(config);
    }

    public void destroy() {
        this.servlet.destroy();
    }

    public boolean equals(ServletHandler hander) {
        if (hander.getPattern() == null || getPattern() == null)
            return false;
        if (hander.getServlet() == null || getServlet() == null)
            return false;
        String targetClassName = hander.getServlet().getClass().getName();
        if (hander.getPattern().equals(getPattern()) && targetClassName.equals(servlet.getClass().getName()))
            return true;
        return false;
    }

    public boolean matches(String uri) {
        if (uri == null) {
            return this.pattern.equals("/");
        } else if (this.pattern.equals("/")) {
            return uri.startsWith(this.pattern);
        } else {
            return uri.equals(this.pattern) || uri.startsWith(this.pattern + "/");
        }
    }

    public static ServletHandler matche(String path, List<ServletHandler> handlers) {

        if (handlers == null || handlers.size() == 0) {
            return null;
        }
        // 1. Path info equal pattern.
        for (ServletHandler handler : handlers) {
            if (handler.getPattern().equals(path)) {
                return handler;
            }
        }
        // 2.Match with directory
        for (ServletHandler handler : handlers) {
            if (path.startsWith(handler.getPattern())) {
                return handler;
            }
        }
        for (ServletHandler handler : handlers) {
            String pattern = handler.getPattern();
            if (pattern.endsWith("*")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }
            if (path.startsWith(pattern)) {
                return handler;
            }
        }
        Pattern p;
        Matcher m;
        if (path.indexOf('*') != -1) {
            for (ServletHandler handler : handlers) {
                p = Pattern.compile(handler.getPattern());
                m = p.matcher(path);
                if (m.find())
                    return handler;
            }
        }
        // can't find it,return null.
        return null;

    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return the servlet
     */
    public Servlet getServlet() {
        return servlet;
    }

    /**
     * @param handler
     * @return
     */
    public boolean subMatches(ServletHandler handler) {
        return matches(handler.getPattern());
    }

}
