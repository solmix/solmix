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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.solmix.runtime.adapter.AdapterManager;
import org.solmix.runtime.adapter.support.AdapterManagerImpl;
import org.solmix.runtime.extension.ExtensionInfo;
import org.solmix.runtime.extension.ExtensionRegistry;
import org.solmix.runtime.identity.IDFactory;
import org.solmix.runtime.identity.IIDFactory;
import org.solmix.runtime.identity.Namespace;
import org.solmix.runtime.support.blueprint.BPNamespaceFactory;
import org.solmix.runtime.support.blueprint.BPNamespaceRegisterer;
import org.solmix.runtime.support.blueprint.RuntimeNamespaceHandler;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class RuntimeActivator implements BundleActivator {

    private ExtensionBundleListener bundleListener;
    private IdentityBundleListener identityListener;
    private List<ExtensionInfo> extensions;
    private ServiceRegistration<IIDFactory> idFactoryServiceRegistration;
    private ServiceRegistration<AdapterManager> adapterServiceRegistration;
    private ServiceTracker<Namespace,Namespace> namespacesTracker;
    
    @Override
    public void start(final BundleContext context) throws Exception {
        idFactoryServiceRegistration = context.registerService(IIDFactory.class, IDFactory.getDefault(), null);
        namespacesTracker = new ServiceTracker<Namespace, Namespace>(context,
            Namespace.class,
            new ServiceTrackerCustomizer<Namespace, Namespace>() {

                @Override
                public Namespace addingService(
                    ServiceReference<Namespace> reference) {
                    Namespace ns = context.getService(reference);
                    if (ns != null && ns.getName() != null) {
                        IDFactory.addNamespace0(ns);
                    }
                    return ns;
                }

                @Override
                public void modifiedService(
                    ServiceReference<Namespace> reference, Namespace service) {
                }

                @Override
                public void removedService(
                    ServiceReference<Namespace> reference, Namespace service) {
                    IDFactory.removeNamespace0(service);
                }
            });
        namespacesTracker.open();
        bundleListener = new ExtensionBundleListener(context.getBundle().getBundleId());
        identityListener=new IdentityBundleListener(context.getBundle().getBundleId());
        // 监听新添加的bundle
        context.addBundleListener(bundleListener);
        context.addBundleListener(identityListener);
        // 注册已经存在的
        bundleListener.registerExistingBundles(context);
        identityListener.regiterExistingNamespce(context);

        extensions = new ArrayList<ExtensionInfo>();
        extensions.add(createBunleListenerExtensionInfo(context));

        ExtensionRegistry.addExtensions(extensions);
        
        adapterServiceRegistration= context.registerService(AdapterManager.class, new AdapterManagerImpl(), null);

        BPNamespaceFactory factory = new BPNamespaceFactory() {
            
            @Override
            public Object createHandler() {
                return new RuntimeNamespaceHandler();
            }
        };
        BPNamespaceRegisterer.register(context, factory, "http://www.solmix.org/schema/rt/v1.0.0");
    }

    /**
     * @param context
     * @return
     */
    private ExtensionInfo createBunleListenerExtensionInfo(BundleContext context) {
        ExtensionInfo containerListener = new ExtensionInfo(BundleContainerListener.class);
        containerListener.setArgs(new Object[]{context});
        return containerListener;
    }

    @Override
    public void stop(BundleContext context) throws Exception {

        if (namespacesTracker != null) {
            namespacesTracker.close();
            namespacesTracker = null;
        }

        if (idFactoryServiceRegistration != null) {
            idFactoryServiceRegistration.unregister();
            idFactoryServiceRegistration = null;
        }
        
        if(adapterServiceRegistration!=null){
            adapterServiceRegistration.unregister();
            adapterServiceRegistration=null;
        }
        
        context.removeBundleListener(bundleListener);
        bundleListener.close();
        context.removeBundleListener(identityListener);
        identityListener.close();
        ExtensionRegistry.removeExtensions(extensions);
    }

}
