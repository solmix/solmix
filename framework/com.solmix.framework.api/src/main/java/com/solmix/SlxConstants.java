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

package com.solmix;

import org.osgi.framework.BundleContext;

/**
 * 
 * @version 110035
 */
public class SlxConstants
{

    private static BundleContext context;

    /**
     * @return the context
     */
    public BundleContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(BundleContext context) {
        SlxConstants.context = context;
    }

    public static final String MODULE_DS_NAME = "datasource";

    public static final String MODULE_CONFIG_NAME = "config";

    public static final String MODULE_API_NAME = "api";

    public static final String MODULE_SQL_NAME = "sql";

    public static final String SERVICE_CM_NAME = "com.solmix.service.cm";

    public static final String GROUP_SEP = "/";

    public static final String SERVICE_DS_PROVIDER = "com.solmix.service.ds.provider";

    public static String OSGI_SERVICE_PREFIX = "osgi:service/";

    public static final String MONITOR_TOPIC_SUFFIX = "com/solmix/monitor/";

    public static final String VALIDATION_TOPIC_SUFFIX = "com/solmix/validation/";

    public static final String CURRENT_SERVLET_NAME = "solmix.current.servlet.name";

    public static String SOLMIX_WEB_ROOT = "solmix.web.root";

    /**
     * Indicate project's environment.if the runtime is under OSGI environment returns <code>true</code>,else return
     * <code>false</code>
     * 
     * @return
     */
    public static boolean isOSGI() {
        if (context == null)
            return false;
        else
            return true;
    }
}
