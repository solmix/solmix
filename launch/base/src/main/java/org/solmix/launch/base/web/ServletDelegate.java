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

package org.solmix.launch.base.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.http.proxy.ProxyServlet;
import org.osgi.framework.BundleException;
import org.solmix.launch.base.internal.Solmix;
import org.solmix.launch.base.shared.LaunchConstants;
import org.solmix.launch.base.shared.Launcher;
import org.solmix.launch.base.shared.Notifiable;

/**
 * <code>ServletDelegate</code>作为solmix框架servlet入口的基本类，它主要完成下列一些操作：
 * 
 * @author ffz
 * @version 110035 2012-3-16
 */

@SuppressWarnings("serial")
public class ServletDelegate extends GenericServlet implements Launcher
{

    private String solmixHome;

    private Servlet delegatee;

    private Notifiable notifiable;

    private boolean servletDestroyed = false;

    private Map<String, String> properties;

    private OsgiBridge bridge;

    @Override
    public final void init() throws ServletException {
        Servlet tmpDelegatee = null;
        OsgiBridge tmpBridge = null;

        try {
            // read the default parameters
            Map<String, String> props = loadConfigProperties(solmixHome);
            tmpBridge = new OsgiBridge(notifiable, props, getServletContext());
            // set up the OSGi HttpService proxy servlet
            tmpDelegatee = new ProxyServlet();
            tmpDelegatee.init(getServletConfig());
            if (servletDestroyed) {
                log("Servlet Delegatee destroyed while starting framework.");
            } else {
                this.bridge = tmpBridge;
                this.delegatee = tmpDelegatee;
                tmpBridge = null;
                tmpDelegatee = null;
                log("Started osgi framework sucess.");
            }
        } catch (BundleException e) {

            throw new ServletException("Failed to start Solmix internal OSGI framework in " + solmixHome, e);

        } catch (ServletException se) {

            throw new ServletException("Failed to start bridge servlet for solmix ", se);

        } catch (Throwable t) {

            throw new ServletException("Uncaught Failure starting solmix", t);

        } finally {

            // clean up temporary fields
            if (tmpDelegatee != null) {
                tmpDelegatee.destroy();
            }
            if (tmpBridge != null) {
                tmpBridge.destroy();
            }
        }
    }

    /**
     * @param solmixHome
     * @return configuration properties.
     */
    private Map<String, String> loadConfigProperties(String solmixHome) {
        // The config properties file is either specified by a system
        // property or it is in the etc config directory。.
        // Try to load it from one of these places.
        Map<String, String> props = new HashMap<String, String>();

        // The following property must start with a comma!
        final String servletVersion = getServletContext().getMajorVersion() + "." + getServletContext().getMinorVersion();
        props.put(Solmix.PROP_SYSTEM_PACKAGES, ",javax.servlet;javax.servlet.http;javax.servlet.resources; version=" + servletVersion);

        if (this.properties != null) {
            props.putAll(this.properties);
        } else {
            // copy context init parameters
            @SuppressWarnings("unchecked")
            Enumeration<String> cpe = getServletContext().getInitParameterNames();
            while (cpe.hasMoreElements()) {
                String name = cpe.nextElement();
                props.put(name, getServletContext().getInitParameter(name));
            }

            // copy servlet init parameters
            @SuppressWarnings("unchecked")
            Enumeration<String> pe = getInitParameterNames();
            while (pe.hasMoreElements()) {
                String name = pe.nextElement();
                props.put(name, getInitParameter(name));
            }
        }
        // TODO add bundles repo.
        // set solmix home
        props.put(LaunchConstants.SOLMIX_HOME, solmixHome);
        if (!props.containsKey(LaunchConstants.SOLMIX_BASE)) {
            props.put(LaunchConstants.SOLMIX_BASE, solmixHome);
        }
        return props;
    }

    /**
     * @return the delegatee
     */
    public Servlet getDelegatee() {
        return delegatee;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        // delegate the request to the registered delegatee servlet
        Servlet delegatee = getDelegatee();
        if (delegatee == null) {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            delegatee.service(req, res);
        }
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public void stop() {
        destroy();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.loaders.shared.Launcher#setSolmixHome(java.lang.String)
     */
    @Override
    public void setSolmixHome(String solmixHome) {
        this.solmixHome = solmixHome;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.loaders.shared.Launcher#setNotifiable(org.solmix.loaders.shared.Notifiable)
     */
    @Override
    public void setNotifiable(Notifiable notifiable) {
        this.notifiable = notifiable;

    }

    /**
     * Properties form servletcontext.
     */
    @Override
    public void setCommandLine(Map<String, String> args) {
        this.properties = args;

    }

    /**
     * Destroys this servlet by shutting down the OSGi framework and hence the delegatee servlet if one is set at all.
     */
    public final void destroy() {

        // set the destroyed flag to signal to the startSling method
        // that Sling should be terminated immediately
        servletDestroyed = true;

        // destroy the delegatee
        if (delegatee != null) {
            delegatee.destroy();
            delegatee = null;
        }

        // shutdown the Felix container
        if (bridge != null) {
            bridge.destroy();
            bridge = null;
        }

        // finally call the base class destroy method
        super.destroy();
    }
}
