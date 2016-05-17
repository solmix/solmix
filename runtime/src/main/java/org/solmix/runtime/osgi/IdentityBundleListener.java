/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.runtime.osgi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.identity.IDFactory;
import org.solmix.runtime.identity.IdentityExtensionParser;
import org.solmix.runtime.identity.Namespace;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class IdentityBundleListener implements SynchronousBundleListener
{

    private static final Logger LOG = LoggerFactory.getLogger(IdentityBundleListener.class);

    private final long id;

    private final ConcurrentMap<Long, List<Namespace>> extensions = new ConcurrentHashMap<Long, List<Namespace>>(16, 0.75f, 4);

    public IdentityBundleListener(long bundleId)
    {
        this.id = bundleId;
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.RESOLVED && id != event.getBundle().getBundleId()) {
            register(event.getBundle());
        } else if (event.getType() == BundleEvent.UNRESOLVED || event.getType() == BundleEvent.UNINSTALLED) {
            unregister(event.getBundle().getBundleId());
        }
    }

    /**
     * @param bundleId
     */
    private void unregister(long bundleId) {
        List<Namespace> list = extensions.remove(bundleId);
        if (list != null) {
            LOG.info("Removing the extensions for bundle " + bundleId);
            for (Namespace ns : list) {
                IDFactory.getDefault().removeNamespace(ns);
            }
        }
    }

    /**
     * @param context
     */
    public void regiterExistingNamespce(BundleContext context) {
        for (Bundle bundle : context.getBundles()) {
            if ((bundle.getState() == Bundle.RESOLVED || bundle.getState() == Bundle.STARTING || bundle.getState() == Bundle.ACTIVE || bundle.getState() == Bundle.STOPPING)
                && bundle.getBundleId() != context.getBundle().getBundleId()) {
                register(bundle);
            }
        }

    }

    /**
     * @param bundle
     */
    private void register(Bundle bundle) {
        Enumeration<?> e = bundle.findEntries("META-INF/solmix/", "identity", false);
        while (e != null && e.hasMoreElements()) {
            List<Namespace> orig = new OsgiIdentityParaser(bundle).getNamespace((URL) e.nextElement());
            List<Namespace> list = extensions.get(bundle.getBundleId());
            if (list == null) {
                list = new CopyOnWriteArrayList<Namespace>();
                List<Namespace> preList = extensions.putIfAbsent(bundle.getBundleId(), list);
                if (preList != null) {
                    list = preList;
                }
            }
            List<String> names = new ArrayList<String>(orig.size());
            for (Namespace ns : orig) {
                names.add(ns.getName());
                IDFactory.getDefault().addNamespace(ns);
            }
            LOG.info("Adding the Namespace from bundle " + bundle.getSymbolicName() 
                + " (" + bundle.getBundleId() + ") " + orig); 
            
        }
    }

    public void close() {
        while (!extensions.isEmpty()) {
            unregister(extensions.keySet().iterator().next());
        }
    }

    static class OsgiIdentityParaser extends IdentityExtensionParser{

        private Bundle bundle;
        /**
         * @param loader
         */
        public OsgiIdentityParaser(Bundle bundle)
        {
            super(null);
            this.bundle=bundle;
        }
        @Override
        protected Class<?> loadClass(String classname) throws ClassNotFoundException{
            return bundle.loadClass(classname);
        }
    }
}
