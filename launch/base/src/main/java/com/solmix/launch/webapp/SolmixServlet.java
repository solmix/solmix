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

package com.solmix.launch.webapp;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.solmix.launch.base.shared.LaunchConstants;
import com.solmix.launch.base.shared.Launcher;
import com.solmix.launch.base.shared.Loader;
import com.solmix.launch.base.shared.Notifiable;

/**
 * 
 * @author ffz
 * @version 0.0.1 2012-3-16
 * @since 0.0.4
 */

public class SolmixServlet extends GenericServlet implements Notifiable
{

    /**
     * Auto generate serial UID.
     */
    private static final long serialVersionUID = 1077520471637999526L;

    /**
     * The number times Sling will be tried to be started before giving up (value is 20). This number is chosen
     * deliberately as generally Sling should start up smoothly. Whether any bundles within Sling start or not is not
     * counted here.
     */
    private static final int MAX_START_FAILURES = 20;

    private Servlet internal;

    private Thread startingThread;

    private String home;

    private Loader loader;

    private int startFailureCounter;

    private Map<String, String> properties;

    private String SOLMIX_HOME_PREFIX = "solmix.home.prefix";

    private String SOLMIX_HOME_PREFIX_DEFAULT = "resource";

    public static final String DEFAULT_SOLMIX_HOME = "resource";

    /**
     * The starting delimiter of variable names (value is "${").
     */
    private static final String DELIM_START = "${";

    /**
     * The ending delimiter of variable names (value is "}").
     */
    private static final String DELIM_STOP = "}";

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Servlet delegatee = internal;
        if (delegatee != null) {
            final HttpServletRequest request = (HttpServletRequest) req;
            if (request.getPathInfo() == null && request.getServletPath() != null && request.getServletPath().endsWith(".jsp")) {
                req = new HttpServletRequestWrapper(request) {

                    @Override
                    public String getPathInfo() {
                        return request.getServletPath();
                    }

                    @Override
                    public String getServletPath() {
                        return "";
                    }

                };
            }
            delegatee.service(req, res);
        } else if (startFailureCounter > MAX_START_FAILURES) {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            startInternal(req);
        }
    }

    @Override
    public void init() {
        this.properties = collectInitParameters();
        putWebRootInSystem();
        this.home = getSolmixHome(null);
        if (this.home != null) {
            startInternal();
        } else {
            log("Solmix Servlet cannot be started yet, because solmix.home is not defined yet");
        }
        log("Servlet " + getServletName() + " initialized");
    }

    /**
     * 
     */
    private void putWebRootInSystem() {
        String realroot = this.getServletContext().getRealPath("/");
        System.setProperty(LaunchConstants.SOLMIX_WEB_ROOT, realroot);

    }

    /**
     * 
     */
    private void startInternal() {

        try {
            File solmixBase = getSolmixBase(home);

            this.loader = new Loader(new File(solmixBase, "lib")) {

                @Override
                protected void info(String msg) {
                    log(msg);
                }
            };
        } catch (IllegalArgumentException iae) {
            startupFailure(null, iae);
            return;
        }
        synchronized (this) {
            if (internal != null) {
                log("Solmix Internal Servlet already started,nothing to do");
                return;
            } else if (startingThread != null) {
                log("Solmix internal Servlet is being started by Thread " + startingThread);
                return;
            }

            startingThread = Thread.currentThread();
            Object object;
            try {
                object = loader.loadLaucher(LaunchConstants.DEFAULT_SOLMIX_SERVLET);
            } catch (IllegalArgumentException iae) {
                startupFailure("Cannot load Launcher Servlet " + LaunchConstants.DEFAULT_SOLMIX_SERVLET, iae);
                return;
            }

            if (object instanceof Servlet) {
                Servlet servletLauncher = (Servlet) object;
                if (object instanceof Launcher) {
                    Launcher launcher = (Launcher) object;
                    launcher.setNotifiable(this);
                    launcher.setCommandLine(properties);
                    launcher.setSolmixHome(home);
                }
                SolmixSessionListener.startDelegate(servletLauncher.getClass().getClassLoader());
                try {
                    log("starting framework...");
                    servletLauncher.init(getServletConfig());
                    this.internal = servletLauncher;
                    this.startFailureCounter = 0;
                    log("Startup completed...");
                } catch (ServletException e) {
                    startupFailure(null, e);
                }
            }
            synchronized (this) {
                startingThread = null;
            }
        }

    }

    /**
     * @param home
     * @return
     */
    private File getSolmixBase(String home) {
        String solmixBase = properties.get(LaunchConstants.SOLMIX_BASE);
        if (solmixBase == null || solmixBase.length() == 0) {
            properties.put(LaunchConstants.SOLMIX_BASE, home);
            return new File(home);
        }

        File baseDir = new File(solmixBase);
        if (!baseDir.isAbsolute()) {
            baseDir = new File(home, solmixBase);
        }

        properties.put(LaunchConstants.SOLMIX_BASE, baseDir.getAbsolutePath());
        return baseDir;
    }

    /**
     * 获取solmix.home值 <br>
     * 首先从servlet initial configuration中获取 <br>
     * 然后从servlet context configuration中获取<br>
     * 任然没有？自动生成
     * 
     * @param request
     * @return home
     */
    private String getSolmixHome(HttpServletRequest request) {
        String source = null;
        // access config and context to be able to log the solmix.home source

        // 1. servlet config parameter
        String home = getServletConfig().getInitParameter(LaunchConstants.SOLMIX_HOME);
        if (home != null) {

            source = "servlet parameter solmix.home";

        } else {

            // 2. servlet context parameter
            home = getServletContext().getInitParameter(LaunchConstants.SOLMIX_HOME);
            if (home != null) {

                source = "servlet context parameter solmix.home";

            } else {

                // 3. servlet context path (Servlet API 2.5 and later)
                try {

                    String contextPath = getServletContext().getContextPath();
                    home = toSolmixHome(contextPath);
                    source = "servlet context path";

                } catch (NoSuchMethodError nsme) {

                    // 4.servlet context path (Servlet API 2.4 and earlier)
                    if (request != null) {

                        String contextPath = request.getContextPath();
                        home = toSolmixHome(contextPath);
                        source = "servlet context path (from request)";
                    } else {
                        log("ServletContext path not available here, delaying startup until first request");
                        return null;
                    }
                }
            }
        }
        // substitute any ${...} references and make absolute
        home = substVars(home);

        log("Setting solmix.home=" + home + " (" + source + ")");
        System.out.println("Setting solmix.home=" + home + " (" + source + ")");
        return home;
    }

    /**
     * @param home
     * @return
     */
    private String substVars(String val) {
        if (val.contains(DELIM_START)) {
            return substVars(val, null, null, properties);
        }

        return val;
    }

    private static String substVars(String val, String currentKey, Map<String, String> cycleMap, Map<String, String> configProps)
        throws IllegalArgumentException {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null) {
            cycleMap = new HashMap<String, String>();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = val.indexOf(DELIM_STOP);

        // Find the matching starting "${" variable delimiter
        // by looping until we find a start delimiter that is
        // greater than the stop delimiter we have found.
        int startDelim = val.indexOf(DELIM_START);
        while (stopDelim >= 0) {
            int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
            if ((idx < 0) || (idx > stopDelim)) {
                break;
            } else if (idx < stopDelim) {
                startDelim = idx;
            }
        }

        // If we do not have a start or stop delimiter, then just
        // return the existing value.
        if ((startDelim < 0) && (stopDelim < 0)) {
            return val;
        }
        // At this point, we found a stop delimiter without a start,
        // so throw an exception.
        else if (((startDelim < 0) || (startDelim > stopDelim)) && (stopDelim >= 0)) {
            throw new IllegalArgumentException("stop delimiter with no start delimiter: " + val);
        }

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable = val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null) {
            throw new IllegalArgumentException("recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = (configProps != null) ? configProps.get(variable) : null;
        if (substValue == null) {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim) + substValue + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }

    /**
     * 返回根目录，如果没有设置，那么就再WEB-INF下加载。
     * 
     * @param contextPath
     * @return
     */
    private String toSolmixHome(String contextPath) {
        String prefix = System.getProperty(SOLMIX_HOME_PREFIX, SOLMIX_HOME_PREFIX_DEFAULT);
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        prefix = "WEB-INF/" + prefix;

        String scAbsolutePath = getServletContext().getRealPath("/");
        return scAbsolutePath + prefix;
    }

    @Override
    public String getServletInfo() {
        if (internal != null) {
            return internal.getServletInfo();
        }

        return "solmix Launcher Proxy";
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> collectInitParameters() {
        HashMap<String, String> props = new HashMap<String, String>();
        for (Enumeration<String> keys = getServletContext().getInitParameterNames(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            props.put(key, getServletContext().getInitParameter(key));
        }
        for (Enumeration<String> keys = getServletConfig().getInitParameterNames(); keys.hasMoreElements();) {
            String key = keys.nextElement();
            props.put(key, getServletConfig().getInitParameter(key));
        }
        return props;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.loaders.shared.Notifiable#stopped()
     */
    @Override
    public void stopped() {
        log("Solmix framework has been stopped");
        internal = null;
        SolmixSessionListener.stopDelegatee();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.loaders.shared.Notifiable#updated(java.io.File)
     */
    @Override
    public void updated(File updateFile) {
        // drop reference to be able to restart.
        synchronized (this) {
            if (this.startingThread == null) {
                internal = null;
                SolmixSessionListener.stopDelegatee();
            }
        }
        // ensure we have a VM as clean as possible
        loader.cleanupVM();

    }

    private void startInternal(final ServletRequest request) {
        if (startingThread == null) {
            home = getSolmixHome((HttpServletRequest) request);
            Thread starter = new Thread(new Runnable() {

                public void run() {
                    startInternal();
                }
            }, "SolmixStarter_" + System.currentTimeMillis());

            starter.setDaemon(true);
            starter.start();
        }
    }

    /**
     * @param string
     * @param ioe
     */
    private void startupFailure(String message, Throwable cause) {
        if (message == null) {
            message = "Failed to start Solmix Servlet in" + home;
        }
        // unwrap to get the real cause
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        // log it now and increase the failure counter
        log(message, cause);
        startFailureCounter++;

        // ensure the startingSling fields is not set
        synchronized (this) {
            startingThread = null;
        }
    }

    @Override
    public void destroy() {

        SolmixSessionListener.stopDelegatee();

        if (internal != null) {
            internal.destroy();
        }

        internal = null;
        home = null;
        loader = null;
    }

}
