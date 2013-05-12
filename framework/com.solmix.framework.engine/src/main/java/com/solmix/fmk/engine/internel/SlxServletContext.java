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

package com.solmix.fmk.engine.internel;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.fmk.engine.MainServlet;
import com.solmix.fmk.engine.internel.helper.ExternalServletContextWrapper;
import com.solmix.fmk.engine.internel.request.SlxRequestDispatcher;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */
@SuppressWarnings("rawtypes")
public class SlxServletContext implements ServletContext
{

    /** default log */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The service registration of this service as ServletContext
     * 
     * @see #dispose()
     */

    private final ServiceRegistration registration;

    private final MainServlet mainServlet;

    public SlxServletContext(final BundleContext bundleContext, MainServlet servlet)
    {
        this.mainServlet = servlet;
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(Constants.SERVICE_PID, getClass().getName());
        props.put(Constants.SERVICE_DESCRIPTION, "Solmix ServletContext");
        registration = bundleContext.registerService(ServletContext.class.getName(), this, props);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getContext(java.lang.String)
     */
    @Override
    public ServletContext getContext(String uripath) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            ServletContext otherContext = delegatee.getContext(uripath);
            if (otherContext != null && otherContext != delegatee) {
                return new ExternalServletContextWrapper(otherContext);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getContextPath()
     */
    @Override
    public String getContextPath() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            try {
                return (String) delegatee.getClass().getMethod("getContextPath", (Class<?>[]) null).invoke(getServletContext(), (Object[]) null);
            } catch (Throwable ignore) {
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getMajorVersion()
     */
    @Override
    public int getMajorVersion() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getMajorVersion();
        }

        return 2; // hard coded major version as fall back
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getMinorVersion()
     */
    @Override
    public int getMinorVersion() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getMinorVersion();
        }

        return 4; // hard coded minor version as fall back
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
     */
    @Override
    public String getMimeType(String file) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getMimeType(file);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
     */
    @Override
    public Set getResourcePaths(String path) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getResourcePaths(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getResource(java.lang.String)
     */
    @Override
    public URL getResource(String path) throws MalformedURLException {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getResource(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String path) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getResourceAsStream(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        if (path == null) {
            log.error("getRequestDispatcher: No path, cannot create request dispatcher");
            return null;
        }

        return new SlxRequestDispatcher(path);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
     */
    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getNamedDispatcher(name);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getServlet(java.lang.String)
     */
    @Deprecated
    @Override
    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getServlets()
     */
    @Deprecated
    @Override
    public Enumeration getServlets() {
        return Collections.enumeration(Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getServletNames()
     */
    @Deprecated
    @Override
    public Enumeration getServletNames() {
        return Collections.enumeration(Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#log(java.lang.String)
     */

    @Override
    public void log(String msg) {
        log.info(msg);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
     */
    @Deprecated
    @Override
    public void log(Exception exception, String msg) {
        log(msg, exception);

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void log(String message, Throwable throwable) {
        log.error(message, throwable);

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
     */
    @Override
    public String getRealPath(String path) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getRealPath(path);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getServerInfo()
     */
    @Override
    public String getServerInfo() {
        return mainServlet.getServerInfo();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
     */
    @Override
    public String getInitParameter(String name) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getInitParameter(name);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getInitParameterNames()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getInitParameterNames() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getInitParameterNames();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getAttribute(name);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getAttributeNames() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getAttributeNames();
        }

        return Collections.enumeration(Collections.<String> emptyList());
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object object) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            delegatee.setAttribute(name, object);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name) {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            delegatee.removeAttribute(name);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.ServletContext#getServletContextName()
     */
    @Override
    public String getServletContextName() {
        ServletContext delegatee = getServletContext();
        if (delegatee != null) {
            return delegatee.getServletContextName();
        }

        return null;
    }

    /**
     * unregister registed service.
     */
    public void dispose() {
        if (registration != null) {
            registration.unregister();
        }
    }

    // ---------- internal -----------------------------------------------------

    /**
     * Returns the real servlet context of the servlet container in which the Sling Servlet is running.
     */
    private ServletContext getServletContext() {
        return mainServlet.getServletContext();
    }
}
