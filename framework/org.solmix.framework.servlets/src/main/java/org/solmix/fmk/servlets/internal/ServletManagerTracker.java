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

package org.solmix.fmk.servlets.internal;

import static org.solmix.api.servlets.ServletManager.SERVLET_NAME;
import static org.solmix.api.servlets.ServletManager.SERVLET_PATTERN;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import org.solmix.fmk.servlets.internal.handler.ServletHandler;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-17
 */

public class ServletManagerTracker extends ServiceTracker<Servlet, Servlet>
{

    // TODO: use servlet (&(objectclass=javax.servlet.Servlet)(pattern=*))
    private static final String SERVLET_SERVICE_NAME = Servlet.class.getName();

    private final List<ServletHandler> handlers = new ArrayList<ServletHandler>();

    private final BundleContext context;

    private final ServletContext servletContext;

    /**
     * @param context
     * @param clazz
     * @param customizer
     */
    public ServletManagerTracker(BundleContext context, ServletContext servletContext)
    {
        super(context, SERVLET_SERVICE_NAME, null);
        this.context = context;
        this.servletContext = servletContext;
    }

    @Override
    public Servlet addingService(ServiceReference<Servlet> reference) {
        super.addingService(reference);
        return createServlet(reference);
    }

    public Servlet getDefaultServlet() {
      /*  Servlet servlet = new StaticResourceServlet("");
        try {
            servlet.init(new SlxServletConfig(this.servletContext, null, ""));
        } catch (ServletException e) {
            return null;
        }*/
        return null;
    }

    /**
     * @param reference
     */
    private synchronized Servlet createServlet(ServiceReference<Servlet> reference) {
        Servlet servlet = context.getService(reference);
        String name = (String) reference.getProperty(SERVLET_NAME);
        String pattern = (String) reference.getProperty(SERVLET_PATTERN);
        if (servlet == null)
            return null;
        if (name == null && pattern == null) {
            return null;
        }
        if (name != null && pattern == null)
            throw new IllegalArgumentException("A registed-Servlet must register with a property named <" + SERVLET_PATTERN + ">");
        try {
            ServletHandler handler = new ServletHandler(this.servletContext, servlet, reference, pattern);
            checkInsert(handler);
            handler.init();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return servlet;
    }

    private void checkInsert(ServletHandler handler) {
        for (int i = 0; i < handlers.size(); i++) {
            ServletHandler h = handlers.get(i);
            if (h.subMatches(handler)) {
                handlers.add(i, handler);
                break;
            }
        }
        handlers.add(handler);
    }

    public Servlet getServlet(String url) {
        ServletHandler target = ServletHandler.matche(url, handlers);
        if (target != null) {
            return target.getServlet();
        }
        return null;
    }

    @Override
    public void removedService(ServiceReference<Servlet> reference, Servlet service) {
        // fixed bug.
        super.removedService(reference, service);
        String pattern = (String) reference.getProperty(SERVLET_PATTERN);
        if (pattern == null)
            return;
        synchronized (handlers) {
            ServletHandler waitRemove = null;
            for (ServletHandler handler : handlers) {
                if (pattern.equals(handler.getPattern())) {
                    waitRemove = handler;
                    break;
                }
            }
            if (waitRemove != null)
                handlers.remove(waitRemove);
        }
    }
}
