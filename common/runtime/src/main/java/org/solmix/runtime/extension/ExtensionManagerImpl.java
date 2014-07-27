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

package org.solmix.runtime.extension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.resource.ObjectTypeResolver;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.SinglePropertyResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class ExtensionManagerImpl implements ExtensionManager,
    ConfiguredBeanProvider
{

    private static final Logger log = LoggerFactory.getLogger(ExtensionManagerImpl.class);

    public static final String PROP_EXTENSION_MANAGER = "extensionManager";

    private final ClassLoader loader;

    private final Container container;

    private final ResourceManager resourceManager;

    private final ExtensionObjectCache activated;

    private final Set<ExtensionInfo> all = new ConcurrentSkipListSet<ExtensionInfo>(
        new ExtensionComparator());

    public ExtensionManagerImpl(String resources[], ClassLoader cl,
        ExtensionObjectCache cache, ResourceManager resourceManager,
        Container container)
    {
        this.loader = cl;
        this.resourceManager = resourceManager;
        this.activated = cache;
        this.container = container;
        ResourceResolver extensionManagerResolver = new SinglePropertyResolver(
            PROP_EXTENSION_MANAGER, this);
        resourceManager.addResourceResolver(extensionManagerResolver);
        resourceManager.addResourceResolver(new ObjectTypeResolver(this));
        load(resources);
        for (ExtensionInfo ext : ExtensionRegistry.getRegisteredExtensions()) {
            if (!all.contains(ext)) {
                all.add(ext);
            }
        }
    }

    public ExtensionManagerImpl(String resource, ClassLoader cl,
        ExtensionObjectCache initialExtensions,
        ResourceManager resourceManager, Container container)
    {
        this(new String[] { resource }, cl, initialExtensions, resourceManager,
            container);
    }

    final void load(String resources[]) {
        if (resources == null) {
            return;
        }
        try {
            for (String resource : resources) {
                load(resource);
            }
        } catch (IOException ex) {
            throw new ExtensionException(ex);
        }
    }

    final void load(String resource) throws IOException {
        if (loader != getClass().getClassLoader()) {
            load(resource, getClass().getClassLoader());
        }
        load(resource, loader);
    }

    final synchronized void load(String resource, ClassLoader l)
        throws IOException {

        Enumeration<URL> urls = l.getResources(resource);

        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            InputStream is;
            try {
                is = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {

                    @Override
                    public InputStream run() throws Exception {
                        return url.openStream();
                    }
                });
            } catch (PrivilegedActionException pae) {
                throw (IOException) pae.getException();
            }
            try {
                List<ExtensionInfo> exts = new InternalExtensionParser(loader).getExtensions(is);
                for (ExtensionInfo e : exts) {
                    if (loader != l) {
                        e.classloader = l;
                    }
                    if (!all.contains(e)) {
                        all.add(e);
                    }
                }
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#getBeanNamesOfType(java.lang.Class)
     */
    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#getBeanOfType(java.lang.String,
     *      java.lang.Class)
     */
    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        if (name == null) {
            return null;
        }
        ExtensionInfo info = findOne(name);
        if (info != null) {
            if (info.getLoadedObject() != null) {
                loadAndRegister(info);
            }
            return type.cast(info.getLoadedObject());
        }
        return null;
    }

    /**
     * @param info
     */
    final void loadAndRegister(ExtensionInfo e) {
        synchronized (e) {
            Class<?> cls = null;
            if (null != e.getInterfaceName()
                && !"".equals(e.getInterfaceName())) {
                cls = e.loadInterface(loader);
            } else {
                cls = e.getClassObject(loader);
            }

            if (null != activated && null != cls
                && null != activated.getObject(cls)) {
                return;
            }

            Object obj = e.load(loader, container);
            if (obj == null) {
                return;
            }

            if (null != activated) {
                BeanConfigurer configurer = (BeanConfigurer) (activated.getObject(BeanConfigurer.class));
                if (null != configurer) {
                    configurer.configureBean(obj);
                }
            }
            ResourceInjector injector = new ResourceInjector(resourceManager);

            injector.inject(obj);
            injector.construct(obj);

            if (null != activated) {
                if (cls == null) {
                    cls = obj.getClass();
                }
                activated.putObject(cls, obj);
            }
        }

    }

    private ExtensionInfo findOne(String name) {
        List<ExtensionInfo> more = null;
        for (ExtensionInfo info : all) {
            if (info.getName().equals(name)) {
                if (info.getExtensionType() == null) {
                    return info;
                } else {
                    if (more == null)
                        more = new ArrayList<ExtensionInfo>();
                    more.add(info);
                }
            }
        }
        if (more != null) {
            // TODO used annotation default value as default
            return more.get(0);
        }
        return null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#getBeansOfType(java.lang.Class)
     */
    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#loadBeansOfType(java.lang.Class,
     *      org.solmix.runtime.bean.ConfiguredBeanProvider.BeanLoaderListener)
     */
    @Override
    public <T> boolean loadBeansOfType(Class<T> type,
        BeanLoaderListener<T> listener) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#hasBeanOfName(java.lang.String)
     */
    @Override
    public boolean hasBeanOfName(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#activateAll()
     */
    @Override
    public void activateAll() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#activateAllByType(java.lang.Class)
     */
    @Override
    public <T> void activateAllByType(Class<T> type) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#getExtension(java.lang.String,
     *      java.lang.Class)
     */
    @Override
    public <T> T getExtension(String ns, Class<T> type) {
        // TODO Auto-generated method stub
        return null;
    }
}
