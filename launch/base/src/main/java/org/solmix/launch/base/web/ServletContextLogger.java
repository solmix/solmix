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

import javax.servlet.ServletContext;

import org.apache.felix.framework.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-18
 */

public class ServletContextLogger extends Logger
{

    private ServletContext servletContext;

    /**
     * @param servletContext
     */
    protected ServletContextLogger(ServletContext servletContext)
    {
        this.servletContext = servletContext;

    }

    @Override
    protected void doLog(Bundle bundle, ServiceReference sr, int level, String msg, Throwable throwable) {

        // unwind throwable if it is a BundleException
        if ((throwable instanceof BundleException) && (((BundleException) throwable).getNestedException() != null)) {
            throwable = ((BundleException) throwable).getNestedException();
        }

        String s = (sr == null) ? null : "SvcRef " + sr;
        s = (s == null) ? null : s + " Bundle '" + bundle.getBundleId() + "'";
        s = (s == null) ? msg : s + " " + msg;
        s = (throwable == null) ? s : s + " (" + throwable + ")";

        switch (level) {
            case LOG_DEBUG:
                servletContext.log("DEBUG: " + s);
                break;
            case LOG_ERROR:
                if (throwable == null) {
                    servletContext.log("ERROR: " + s);
                } else {
                    servletContext.log("ERROR: " + s, throwable);
                }
                break;
            case LOG_INFO:
                servletContext.log("INFO: " + s);
                break;
            case LOG_WARNING:
                servletContext.log("WARNING: " + s);
                break;
            default:
                servletContext.log("UNKNOWN[" + level + "]: " + s);
        }
    }
}
