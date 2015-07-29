/**
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.support.ObjectTypeResolver;
import org.solmix.runtime.resource.support.SinglePropertyResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class ExtensionManagerImpl implements ExtensionManager,
    ConfiguredBeanProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionManagerImpl.class);

    public static final String PROP_EXTENSION_MANAGER = "extensionManager";

    private final ClassLoader loader;

    private final Container container;

    private final ResourceManager resourceManager;

    private final Map<Class<?>, Object> activated;

    private final Map<String, ExtensionInfo> all = new ConcurrentHashMap<String, ExtensionInfo>();

    public ExtensionManagerImpl(String resources[], ClassLoader cl,
        Map<Class<?>, Object> cache, ResourceManager resourceManager,
        Container container) {
        this.loader = cl;
        this.resourceManager = resourceManager;
        this.activated = cache;
        this.container = container;
        ResourceResolver extensionManagerResolver = new SinglePropertyResolver(
            PROP_EXTENSION_MANAGER, this);
        resourceManager.addResourceResolver(extensionManagerResolver);
        resourceManager.addResourceResolver(new ObjectTypeResolver(this));
        load(resources);
        for (Map.Entry<String, ExtensionInfo> ext : ExtensionRegistry.getRegisteredExtensions().entrySet()) {
            if (!all.containsKey(ext.getKey())) {
                all.put(ext.getKey(), ext.getValue());
            }
        }
    }
    
   public ExtensionInfo getExtensionInfo(String name){
       return  all.get(name);
    }

    public ExtensionManagerImpl(String resource, ClassLoader cl,
        Map<Class<?>, Object> initialExtensions,
        ResourceManager resourceManager, Container container) {
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
            if (LOG.isTraceEnabled()) {
                LOG.trace("Load ExtensionInfo from :" + url.getPath());
            }
            InputStream is;
            String inf = url.getFile();
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
                List<ExtensionInfo> exts = new InternalExtensionParser(loader)
                                            .getExtensions(is, inf);
                for (ExtensionInfo e : exts) {
                    if (loader != l) {
                        e.classloader = l;
                    }
                    if (!all.containsKey(e.getName())) {
                        all.put(e.getName(), e);
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

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        List<String> ret = new LinkedList<String>();
        for (ExtensionInfo ex : all.values()) {
            synchronized (ex) {
                Class<?> cls = ex.getClassObject(loader);
                if (cls != null && type.isAssignableFrom(cls)) {
                    ret.add(ex.getName());
                }
            }            
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#getBeanOfType(java.lang.String,
     *      java.lang.Class)
     */
    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        ExtensionInfo info =all.get(name);
        if (info != null) {
            if (info.getLoadedObject() == null) {
                loadAndRegister(info);
            }
            return type.cast(info.getLoadedObject());
        }
        return null;
    }
    @Override
    public <T> T getBeanOfType(Class<T> type) {
        for(ExtensionInfo info : all.values()){
            synchronized (info) {
                Class<?> cls = info.getClassObject(loader);
                if (cls != null && type.isAssignableFrom(cls)) {
                    if (info.getLoadedObject() == null) {
                        loadAndRegister(info);
                    }
                    return type.cast(info.getLoadedObject());
                }
            }
        }
        return null;
    }
    /**
     * @param info
     */
    final void loadAndRegister(ExtensionInfo info) {
        if(LOG.isTraceEnabled()){
            LOG.trace("Loading and initial Extension: "+info.getName());
        }
        synchronized (info) {
            Class<?> cls = null;
            cls = info.getClassObject(loader);
            if (null != activated && null != cls
                && null != activated.get(cls)) {
                return;
            }

            Object obj = info.load(loader, container);
            if (obj == null) {
                return;
            }

            if (null != activated) {
                BeanConfigurer configurer = (BeanConfigurer) (activated.get(BeanConfigurer.class));
                if (null != configurer) {
                    configurer.configureBean(obj);
                }
            }
            ResourceInjector injector = new ResourceInjector(resourceManager);

            injector.inject(obj);
            injector.injectAware(obj);
            injector.construct(obj);
           
            if (null != activated) {
                if (cls == null) {
                    cls = obj.getClass();
                }
                activated.put(cls, obj);
            }
        }
    }


    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#getBeansOfType(java.lang.Class)
     */
    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        List<T> ret = new LinkedList<T>();
        for(ExtensionInfo info : all.values()){
            synchronized (info) {
                Class<?> cls = info.getClassObject(loader);
                if (cls != null && type.isAssignableFrom(cls)) {
                    if (info.getLoadedObject() == null) {
                        loadAndRegister(info);
                    }
                    ret.add(type.cast(info.getLoadedObject()));
                }
            }
        }
        return ret;
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
        boolean loaded = false;
        for (ExtensionInfo ex : all.values()) {
            synchronized (ex) {
                Class<?> cls = ex.getClassObject(loader);
                if (cls != null 
                    && type.isAssignableFrom(cls)
                    && listener.loadBean(ex.getName(), cls.asSubclass(type))) {
                    if (ex.getLoadedObject() == null) {
                        loadAndRegister(ex);
                    }
                    if (listener.beanLoaded(ex.getName(), type.cast(ex.getLoadedObject()))) {
                        return true;
                    }
                    loaded = true;
                }
            }
        }
        return loaded;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.bean.ConfiguredBeanProvider#hasBeanOfName(java.lang.String)
     */
    @Override
    public boolean hasBeanOfName(String name) {
        if (name == null) {
            return false;
        }
        for (ExtensionInfo info : all.values()) {
            if (name.equals(info.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#activateAll()
     */
    @Override
    public void activateAll() {
        for (ExtensionInfo info : all.values()) {
            if (info.getLoadedObject() == null) {
                loadAndRegister(info);
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#activateAllByType(java.lang.Class)
     */
    @Override
    public <T> void activateAllByType(Class<T> type) {
        for (ExtensionInfo info : all.values()) {
            if (info.getLoadedObject() == null) {
                synchronized (info) {
                    Class<?> cls = info.getClassObject(loader);
                    if (cls != null && type.isAssignableFrom(cls)) {
                        loadAndRegister(info);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#getExtension(java.lang.String,
     *      java.lang.Class)
     */
    @Override
    public <T> T getExtension(String name, Class<T> type) {
        if (name == null) {
            return null;
        }
        ExtensionInfo info = all.get(name);
        if (info != null) {
            synchronized (info) {
                Class<?> cls = info.getClassObject(loader);

                if (cls != null
                    && type.isAssignableFrom(info.getClassObject(loader))) {
                    if (info.getLoadedObject() == null) {
                        loadAndRegister(info);
                    }
                    return type.cast(info.getLoadedObject());
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionManager#initialize()
     */
    @Override
    public void initialize() {
        for (ExtensionInfo info : all.values()) {
            if (!info.isDeferred() && info.getLoadedObject() == null) {
                loadAndRegister(info);
            }
        }
    }

    Set<ExtensionInfo> getExtensionInfos(Class<?> type) {
        Set<ExtensionInfo> set = new HashSet<ExtensionInfo>();
        for (ExtensionInfo info : all.values()) {
            if (type.isAssignableFrom(info.getClassObject(loader))) {
                set.add(info);
            }
        }
        return set;
    }

    public void removeBeansOfNames(List<String> names) {
        for (String s : names) {
            for (ExtensionInfo info : all.values()) {
                if (s.equals(info.getName())) {
                    all.remove(info);
                }
            }
        }
    }

    /**
     * @param info
     */
    void createExtension(ExtensionInfo info) {
        if (info.getLoadedObject() == null) {
            loadAndRegister(info);
        }

    }

    public void destroyBeans() {
        for (ExtensionInfo ex : all.values()) {
            if (ex.getLoadedObject() != null) {
                ResourceInjector inject = new ResourceInjector(resourceManager);
                inject.destroy(ex.getLoadedObject());
            }
        }

    }

   
}
