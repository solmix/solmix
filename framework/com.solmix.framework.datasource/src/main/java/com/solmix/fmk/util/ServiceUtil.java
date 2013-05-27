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

package com.solmix.fmk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.SlxConstants;
import com.solmix.commons.util.DataUtil;

/**
 * 
 * @author solomon
 * @version 110035 2011-5-28
 */

public class ServiceUtil
{

    private static final Logger log = LoggerFactory.getLogger(ServiceUtil.class.getName());

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
        ServiceUtil.context = context;
    }

    /**
     * Lookup and return an osgi service
     * 
     * @return Object
     * @exception javax.io.IOException If an exception occurs during the service lookup
     * 
     */
    public static final Object getOsgiJndiService(String serviceName) {
        if (log.isTraceEnabled())
            log.trace("ServiceUtilities:getOSGIService()", serviceName);
        return getOsgiJndiService(serviceName, null);
    }

    /**
     * Lookup and return an osgi service
     * 
     * @return Object
     * 
     */
    public static final Object getOsgiJndiService(String serviceName, String filter) {
        if (log.isTraceEnabled())
            log.trace("ServiceUtilities:getOSGIService(): service[" + serviceName + "] filter[" + filter + "]");
        String name = SlxConstants.OSGI_SERVICE_PREFIX + serviceName;
        if (filter != null) {
            name = name + "/" + filter;
        }

        try {
            InitialContext ic = new InitialContext();
            return ic.lookup(name);
        } catch (NamingException e) {
            log.error("ServiceUtilities:getOSGIService() -- NamingException on OSGI service lookup" + name, e);
            e.printStackTrace();
            return null;
        }
    }

    public static final Object getJNDIService(String jndi) {
        try {
            InitialContext ic = new InitialContext();
            return ic.lookup(jndi);
        } catch (NamingException e) {
            log.error("ServiceUtilities:getJNDIService() -- NamingException on JNDI service lookup" + jndi, e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param interfaceName
     * @param filter
     * @return
     */
    public static <AdapterType> List<AdapterType> getOSGIServices(Class<AdapterType> clazz, String filter) {
        if (context != null) {
            List<AdapterType> res;
            try {
                if (DataUtil.isNotNullAndEmpty(filter)) {
                    Collection<ServiceReference<AdapterType>> ref = context.getServiceReferences(clazz, filter);
                    if (ref != null) {
                        res = new ArrayList<AdapterType>();
                        Iterator<ServiceReference<AdapterType>> it = ref.iterator();
                        do {
                            if (!it.hasNext())
                                break;
                            ServiceReference<AdapterType> sf = it.next();
                            res.add(context.getService(sf));

                        } while (true);
                        return res;
                    }
                } else {
                    ServiceReference<AdapterType> ref = context.getServiceReference(clazz);
                    if (ref != null) {
                        res = new ArrayList<AdapterType>();
                        res.add(context.getService(ref));
                        return res;
                    }
                }
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <AdapterType> AdapterType getOSGIService(Class<AdapterType> infClazz) {
        if (log.isTraceEnabled())
            log.trace("ServiceUtilities:getOSGIService(): service[" + infClazz.getName() + "]");
        ServiceReference<AdapterType> ref = context.getServiceReference(infClazz);
        return ref == null ? null : context.getService(ref);
    }

    /**
     * @param interfaceName
     * @param filter
     * @return
     */
    public static Object[] getOSGIServices(String interfaceName, String filter) {
        if (context != null) {
            Object[] res;
            try {
                if (DataUtil.isNotNullAndEmpty(filter)) {
                    ServiceReference[] ref = context.getServiceReferences(interfaceName, filter);
                    if (ref != null) {
                        res = new Object[ref.length];
                        for (int i = 0; i < ref.length; i++) {
                            res[i] = context.getService(ref[i]);
                        }
                        return res;
                    }
                } else {

                    ServiceReference ref = context.getServiceReference(interfaceName);
                    if (ref != null) {
                        res = new Object[1];
                        res[0] = context.getService(ref);
                        return res;
                    }
                }
            } catch (InvalidSyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object getOSGIService(String serviceName) {
        if (log.isTraceEnabled())
            log.trace("ServiceUtilities:getOSGIService(): service[" + serviceName + "]");
        ServiceReference ref = context.getServiceReference(serviceName);
        return ref == null ? null : context.getService(ref);
    }
}
