/*
 * Copyright 2013 The Solmix Project
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
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.extension.ExtensionException;
import org.solmix.runtime.extension.ExtensionInfo;
import org.solmix.runtime.extension.ExtensionRegistry;
import org.solmix.runtime.extension.InternalExtensionParser;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class ExtensionBundleListener implements SynchronousBundleListener
{
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionBundleListener.class);
    private final ConcurrentMap<Long, List<OSGiExtension>> extensions 
    = new ConcurrentHashMap<Long, List<OSGiExtension>>(16, 0.75f, 4);

    private final long id;
    public ExtensionBundleListener(long bundleId) {
        this.id = bundleId;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.RESOLVED && id != event.getBundle().getBundleId()) {
            register(event.getBundle());
        } else if (event.getType() == BundleEvent.UNRESOLVED || event.getType() == BundleEvent.UNINSTALLED) {
            unregister(event.getBundle().getBundleId());
        }

    }
    protected void register(final Bundle bundle) {
        Enumeration<URL> e = bundle.findEntries("META-INF/solmix/", "extensions", false);
        while (e != null && e.hasMoreElements()) {
            List<ExtensionInfo> orig = new InternalExtensionParser(null).getExtensions(e.nextElement());
            addExtensions(bundle, orig);
        }
    }
    protected void unregister(final long bundleId) {
        List<OSGiExtension> list = extensions.remove(bundleId);
        if (list != null) {
            LOG.debug("Removing the extensions for bundle {}" , bundleId);
            ExtensionRegistry.removeExtensions(list);
        }
    }
    private boolean addExtensions(final Bundle bundle, List<ExtensionInfo> orig) {
        if (orig.isEmpty()) {
            return false;
        }
        
        List<String> names = new ArrayList<String>(orig.size());
        for (ExtensionInfo ext : orig) {
            names.add(ext.getName());
        }
        LOG.debug("Adding the extensions from bundle {} ({}) {}" , bundle.getSymbolicName() , bundle.getBundleId() , names); 
        List<OSGiExtension> list = extensions.get(bundle.getBundleId());
        if (list == null) {
            list = new CopyOnWriteArrayList<OSGiExtension>();
            List<OSGiExtension> preList = extensions.putIfAbsent(bundle.getBundleId(), list);
            if (preList != null) {
                list = preList;
            }
        }
        for (ExtensionInfo ext : orig) {
            list.add(new OSGiExtension(ext, bundle));
        }
        ExtensionRegistry.addExtensions(list);
        return !list.isEmpty();
    }
    /**
     * @param context
     */
    public void registerExistingBundles(BundleContext context) {
        for (Bundle bundle : context.getBundles()) {
            if ((bundle.getState() == Bundle.RESOLVED 
                || bundle.getState() == Bundle.STARTING 
                || bundle.getState() == Bundle.ACTIVE 
                || bundle.getState() == Bundle.STOPPING)
                && bundle.getBundleId() != context.getBundle().getBundleId()) {
                register(bundle);
            }
        }
    }
    public class OSGiExtension extends ExtensionInfo {
        final Bundle bundle;
        Object serviceObject;
        public OSGiExtension(ExtensionInfo e, Bundle b) {
            super(e);
            bundle = b;
        }

        public void setServiceObject(Object o) {
            serviceObject = o;
            obj = o;
        }
        @Override
        public Object load(ClassLoader cl, Container b) {
            if (interfaceName == null && bundle.getBundleContext() != null) {
                ServiceReference<?> ref = bundle.getBundleContext().getServiceReference(className);
                if (ref != null && ref.getBundle().getBundleId() == bundle.getBundleId()) {
                    Object o = bundle.getBundleContext().getService(ref);
                    serviceObject = o;
                    obj = o;
                    return obj;
                }
            }
            return super.load(cl, b);
        }
        @Override
        protected Class<?> tryClass(String name, ClassLoader cl) {
            Class<?> c = null;
            Throwable origExc = null;
            try {
                c = bundle.loadClass(className);
            } catch (Throwable e) {
                origExc = e;
            }
            if (c == null) {
                try {
                    return super.tryClass(name, cl);
                } catch (ExtensionException ee) {
                    if (origExc != null) {
                        throw new ExtensionException("PROBLEM_LOADING_EXTENSION_CLASS:"+name,origExc);
                    } else {
                        throw ee;
                    }
                }
            }
            return c;
        }

        @Override
        public OSGiExtension cloneNoObject() {
            OSGiExtension ext = new OSGiExtension(this, bundle);
            ext.obj = serviceObject;
            return ext;
        }

    }
    /**
     * 
     */
    public void close() {
        while (!extensions.isEmpty()) {
            unregister(extensions.keySet().iterator().next());
        }
    }
}
