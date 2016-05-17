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

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.extension.ExtensionManagerImpl;
import org.solmix.runtime.threadpool.ThreadPoolManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月7日
 */

public class BundleContainerListener implements ContainerListener
{

    public static final String CONTEXT_SYMBOLIC_NAME_PROPERTY = "solmix.container.symbolicname";

    public static final String CONTEXT_VERSION_PROPERTY = "solmix.container.version";

    public static final String CONTAINER_ID_PROPERTY = "solmix.container.id";
    BundleContext defaultContext;
    private ServiceRegistration<?> service;
    private final Container container;

    public BundleContainerListener(Container container)
    {
        this(container, null);
    }

    public BundleContainerListener(Container container, Object args[])
    {
        this.container=container; 
        if (args != null && args.length > 0 
            && args[0] instanceof BundleContext) {
            defaultContext = (BundleContext)args[0];
        }
        registedConfiguredBeanProvider();
    }
    
    private void registedConfiguredBeanProvider(){
        final ConfiguredBeanProvider provider = container.getExtension(ConfiguredBeanProvider.class);
        if(provider instanceof ExtensionManagerImpl){
            container.setExtension(new OSGIBeanProvider(provider,defaultContext), ConfiguredBeanProvider.class);
        }
    }

  
    @Override
    public void handleEvent(ContainerEvent event) {
        int type = event.getType();
        if (ContainerEvent.CREATED == type) {
            registerAsOsgiService();
        } else if (ContainerEvent.POSTCLOSE == type) {
            unregister();
        }

    }

    /**
     * 
     */
    private void unregister() {
        if (service != null) {
            service.unregister();
            service = null;
        }
    }

    /**
     * @param container
     */
    private void registerAsOsgiService() {
        BundleContext bctx = container.getExtension(BundleContext.class);
        //把所有container中的thead 加入统一管理
        ManagedThreadPoolList wqList = container.getExtension(ManagedThreadPoolList.class);
        if (wqList != null) {
            ThreadPoolManager manager = container.getExtension(ThreadPoolManager.class);
            wqList.addAllToWorkQueueManager(manager);
        }
        if (bctx != null) {
            Hashtable<String, Object> props = new Hashtable<String, Object>();
            props.put(CONTEXT_SYMBOLIC_NAME_PROPERTY,
                bctx.getBundle().getSymbolicName());
            props.put(CONTEXT_VERSION_PROPERTY,
                getBundleVersion(bctx.getBundle()));
            props.put(CONTAINER_ID_PROPERTY, container.getId());

            service = bctx.registerService(Container.class.getName(),
                container, props);
        }

    }

    /**
     * @param bundle
     * @return
     */
    private Object getBundleVersion(Bundle bundle) {
        Dictionary<?, ?> headers = bundle.getHeaders();
        String version = (String) headers.get(Constants.BUNDLE_VERSION);
        return (version != null) ? Version.parseVersion(version)
            : Version.emptyVersion;
    }

}
