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

package com.solmix.fmk.jaas;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.solmix.api.security.SecurityAdmin;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-9
 */

public class SecurityAdminSupport
{

    private boolean debug;

    private BundleContext bundleContext;

    private Map<String, ?> options;

    public SecurityAdminSupport(Map<String, ?> options)
    {
        this.debug = Boolean.parseBoolean((String) options.get("debug"));
        // the bundle context is set in the Config JaasRealm by default
        this.bundleContext = (BundleContext) options.get(BundleContext.class.getName());
        this.options = options;
        if (this.bundleContext == null) {
            throw new IllegalStateException("No bundleContext,Please make sure this module is running in a OSGI Container.");
        }
    }

    public SecurityAdmin getSecurityAdmin() {
        // lookup the encryption service reference
        ServiceReference<SecurityAdmin> securityAdminReference;
        securityAdminReference = bundleContext.getServiceReference(SecurityAdmin.class);
        if (securityAdminReference == null) {

            throw new IllegalStateException("No SecurityAdmin service found. Please install the solmix  datasource module.");
        }
        SecurityAdmin securityAdmin = bundleContext.getService(securityAdminReference);

        if (securityAdmin == null) {
            throw new IllegalStateException("No SecurityAdmin supporting the required options could be found.");
        }
        return securityAdmin;
    }
}
