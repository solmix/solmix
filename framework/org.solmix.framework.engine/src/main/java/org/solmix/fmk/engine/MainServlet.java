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

package org.solmix.fmk.engine;

import static org.osgi.framework.Constants.BUNDLE_VERSION;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.request.RequestProcessor;
import org.solmix.api.servlet.FilterManager;
import org.solmix.api.servlet.ServletManager;
import org.solmix.fmk.engine.internal.SlxHttpContext;
import org.solmix.fmk.engine.internal.SlxServletContext;
import org.solmix.fmk.engine.internal.request.SlxRequestProcessor;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-12
 */

public class MainServlet extends GenericServlet
{

    /**
     * Gerneration Serial verison UID.
     */
    private static final long serialVersionUID = -3935191639532112181L;

    public static String ROOT = "/";

    public static String PRODUCT_NAME = "solmix";

    private volatile BundleContext managedContext;

    private HttpService httpService;

    private String productInfo = PRODUCT_NAME;

    private String serverInfo;

    private final SlxRequestProcessor requestProcessor = new SlxRequestProcessor();

    private final SlxHttpContext httpContext = new SlxHttpContext();

    private String paramaterEncoding;

    /** default log */
    private final Logger log = LoggerFactory.getLogger(MainServlet.class);

    private ServiceRegistration<?> requestProcessorRegistration;

    private SlxServletContext slxServletContext;

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        res.setCharacterEncoding(paramaterEncoding);
        if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest) req;

            // set the thread name according to the request
            String threadName = setThreadName(request);
            // TODO 发送事项

            requestProcessor.processRequest(request, (HttpServletResponse) res);
            Thread.currentThread().setName(threadName);
        } else {
            throw new ServletException("Application Engine must be run in an HTTP servlet environment.");
        }
    }

    /**
     * Blueprint init-Method.
     */
    public void activate() {
        // setup server info
        setProductInfo(managedContext);
        final Dictionary<String, String> configuration = new Hashtable<String, String>();

        // put parameter configuration with services.
        configuration.put(EngineConstants.PROP_DEFAULT_PARAMETER_ENCODING, this.paramaterEncoding);
        try {
            this.httpService.registerServlet(ROOT, this, configuration, httpContext);
            log.info("{} ready to serve requests", this.getServerInfo());
        } catch (Exception e) {
            log.error("Cannot register " + this.getServerInfo(), e);
        }

        slxServletContext = new SlxServletContext(this.managedContext, this);
        // provide the RequestProcessor service
        Hashtable<String, String> srpProps = new Hashtable<String, String>();
        srpProps.put(Constants.SERVICE_DESCRIPTION, "Solmix Request Processor");
        requestProcessorRegistration = managedContext.registerService(RequestProcessor.NAME, requestProcessor, srpProps);
    }

    /**
     * Blueprint destroy-Method.
     */
    public void deactivate() {
        if (requestProcessorRegistration != null) {
            requestProcessorRegistration.unregister();
            this.requestProcessorRegistration = null;
        }
        //
        if (slxServletContext != null) {
            slxServletContext.dispose();
            slxServletContext = null;
        }
        //
        httpService.unregister(ROOT);
        log.info(this.getServerInfo() + " shut down");
    }

    @Override
    public void init() {
        setServerInfo();
    }

    /**
     * @return the productInfo
     */
    public String getProductInfo() {
        return productInfo;
    }

    /**
     * @return the serverInfo
     */
    public String getServerInfo() {
        return serverInfo;
    }

    /**
     * @param productInfo the productInfo to set
     */
    private void setProductInfo(final BundleContext bundleContext) {
        final Dictionary<?, ?> props = bundleContext.getBundle().getHeaders();
        final Version bundleVersion = Version.parseVersion((String) props.get(BUNDLE_VERSION));
        final String productVersion = bundleVersion.getMajor() + "." + bundleVersion.getMinor();
        this.productInfo = PRODUCT_NAME + "/" + productVersion;

        // update the server info
        this.setServerInfo();
    }

    private void setServerInfo() {
        final String containerProductInfo;
        if (getServletConfig() == null || getServletContext() == null) {
            containerProductInfo = "unregistered";
        } else {
            final String containerInfo = getServletContext().getServerInfo();
            if (containerInfo != null && containerInfo.length() > 0) {
                int lbrace = containerInfo.indexOf('(');
                if (lbrace < 0) {
                    lbrace = containerInfo.length();
                }
                containerProductInfo = containerInfo.substring(0, lbrace).trim();
            } else {
                containerProductInfo = "unknown";
            }
        }

        this.serverInfo = String.format("%s (%s, %s %s, %s %s %s)", this.productInfo, containerProductInfo, System.getProperty("java.vm.name"),
            System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));

        if (this.requestProcessor != null) {
            this.requestProcessor.setServerInfo(serverInfo);
        }
    }

    public <Type> Type adaptTo(Object object, Class<Type> type) {
        // AdapterManager adapterManager = this.adapterManager;
        // if (adapterManager != null) {
        // return adapterManager.getAdapter(object, type);
        // }

        // no adapter manager, nothing to adapt to
        return null;
    }

    /**
     * @return the managedContext
     */
    public BundleContext getManagedContext() {
        return managedContext;
    }

    /**
     * @param managedContext the managedContext to set
     */
    public void setManagedContext(BundleContext managedContext) {
        this.managedContext = managedContext;
    }

    /**
     * @return the httpService
     */
    public HttpService getHttpService() {
        return httpService;
    }

    /**
     * @param httpService the httpService to set
     */
    public void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * @return the servletManager
     */
    public void unsetServletManager(ServletManager servletManager) {
        requestProcessor.unsetServletManager(servletManager);
    }

    /**
     * Inject servletManager to {@link org.solmix.fmk.engine.internal.request.SlxRequestProcessor requestProcessor}
     * 
     * @param servletManager the servletManager to set
     */
    public void setServletManager(ServletManager servletManager) {
        requestProcessor.setServletManager(servletManager);
    }

    /**
     * @return the paramaterEncoding
     */
    public String getParamaterEncoding() {
        return paramaterEncoding;
    }

    /**
     * @param paramaterEncoding the paramaterEncoding to set
     */
    public void setParamaterEncoding(String paramaterEncoding) {
        this.paramaterEncoding = paramaterEncoding;
    }

    /**
     * Sets the name of the current thread to the IP address of the remote client with the current system time and the
     * first request line consisting of the method, path and protocol.
     * 
     * @param request The request to extract the remote IP address, method, request URL and protocol from.
     * @return The name of the current thread before setting the new name.
     */
    private String setThreadName(HttpServletRequest request) {

        // get the name of the current thread (to be returned)
        Thread thread = Thread.currentThread();
        String oldThreadName = thread.getName();

        // construct and set the new thread name of the form:
        // 127.0.0.1 [1224156108055] GET /system/console/config HTTP/1.1
        StringBuffer buf = new StringBuffer();
        buf.append(request.getRemoteAddr());
        buf.append(" [").append(System.currentTimeMillis()).append("] ");
        buf.append(request.getMethod()).append(' ');
        buf.append(request.getRequestURI()).append(' ');
        buf.append(request.getProtocol());
        thread.setName(buf.toString());

        // return the previous thread name
        return oldThreadName;
    }

    /**
     * @param filterManager the filterManager to set
     */
    public void setFilterManager(FilterManager filterManager) {
        requestProcessor.setFilterManager(filterManager);
    }

}
