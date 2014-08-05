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

package org.solmix.runtime.extension;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.Extension;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.resource.ObjectTypeResolver;
import org.solmix.runtime.resource.PropertiesResolver;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.ResourceManagerImpl;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.SinglePropertyResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-3
 */

public class ExtensionContainer implements Container
{
   private static final Logger log = LoggerFactory.getLogger(ExtensionContainer.class);
  
   private final List<ContainerListener> containerListeners = new ArrayList<ContainerListener>(4);
   
   private  final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();

 /**
 * Container status cycle CREATING->INITIALIZING->CREATED->CLOSING->CLOSED
 */
    public static enum ContainerStatus
    {
        CREATING , INITIALIZING , CREATED , CLOSING , CLOSED;

    }

    protected final Map<Class<?>, Object> extensions;

    protected final Set<Class<?>> missingBeans;
    private final ExtensionManagerImpl extensionManager;
    protected String id;

    private ContainerStatus status;
    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>(16, 0.75f, 4);

    public ExtensionContainer()
    {
        this(null, null, Thread.currentThread().getContextClassLoader());
    }

    public ExtensionContainer(Map<Class<?>, Object> beans,
        Map<String, Object> properties)
    {
        this(beans, properties, Thread.currentThread().getContextClassLoader());
    }

    public ExtensionContainer(Map<Class<?>, Object> beans)
    {
        this(beans, null, Thread.currentThread().getContextClassLoader());
    }

    public ExtensionContainer(Map<Class<?>, Object> beans,
        Map<String, Object> properties, ClassLoader extensionClassLoader)
    {
        if (beans == null) {
            extensions = new ConcurrentHashMap<Class<?>, Object>(16, 0.75f, 4);
        } else {
            extensions = new ConcurrentHashMap<Class<?>, Object>(beans);
        }
        missingBeans = new CopyOnWriteArraySet<Class<?>>();

        setStatus( ContainerStatus.CREATING);
        ContainerFactory.possiblySetDefaultContainer(this);
        if (null == properties) {
            properties = new HashMap<String, Object>();
        }
        ResourceManager rm=new  ResourceManagerImpl();
        properties.put(CONTAINER_PROPERTY_NAME, DEFAULT_CONTAINER_ID);
        properties.put(DEFAULT_CONTAINER_ID, this);
        ResourceResolver propertiesResolver = new PropertiesResolver(properties);
        rm.addResourceResolver(propertiesResolver);
        
        ResourceResolver defaultContainer = new SinglePropertyResolver(DEFAULT_CONTAINER_ID, this);
        rm.addResourceResolver(defaultContainer);
        rm.addResourceResolver(new ObjectTypeResolver(this));
        rm.addResourceResolver(new ResourceResolver() {

            @Override
            public <T> T resolve(String resourceName, Class<T> resourceType) {
                if (extensionManager != null) {
                    T t = extensionManager.getExtension(resourceName,
                        resourceType);
                    if (t == null) {
                        t = getExtension(resourceType);
                    }
                    return t;
                }

                return null;
            }

            @Override
            public InputStream getAsStream(String name) {
                return null;
            }
        });
        extensions.put(ResourceManager.class,rm);
        
        extensionManager= new ExtensionManagerImpl(new String[0],
            extensionClassLoader,
            extensions,
            rm, 
            this);
        setStatus(ContainerStatus.INITIALIZING);
        extensionManager.load(new String[]{ExtensionManager.EXTENSION_LOCATION});
        extensionManager.activateAllByType(ResourceResolver.class);
        extensions.put(ExtensionManager.class, extensionManager);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * @return the status
     */
    public ContainerStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getExtension(java.lang.Class)
     */
    @Override
    public <T> T getExtension(Class<T> beanType) {
        Object obj = extensions.get(beanType);
        if (obj == null) {
            if (missingBeans.contains(beanType)) {
                // missing extensions,return null
                return null;
            }
            ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
            if (provider == null) {
                provider = createBeanProvider();
            }
            if (provider != null) {
                Collection<?> objs = provider.getBeansOfType(beanType);
                if (objs != null) {
                    for (Object o : objs) {
                        extensions.put(beanType, o);
                    }
                }
                obj = extensions.get(beanType);
            }
        }
        if (obj != null) {
            return beanType.cast(obj);
        } else {
            missingBeans.add(beanType);
        }
        return null;
    }

    /**
     * @return
     */
    protected synchronized ConfiguredBeanProvider createBeanProvider() {
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
        if (provider == null) {
            provider=extensionManager;
            this.setExtension(provider, ConfiguredBeanProvider.class);
        }
        return provider;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#setExtension(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> void setExtension(T bean, Class<T> beanType) {
        extensions.put(beanType, bean);
        missingBeans.remove(beanType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#hasExtensionByName(java.lang.String)
     */
    @Override
    public boolean hasExtensionByName(String name) {
        for (Class<?> c : extensions.keySet()) {
            if (name.equals(c.getName())) {
                return true;
            }
        }
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
        if (provider == null) {
            provider = createBeanProvider();
        }
        if (provider != null) {
            return provider.hasBeanOfName(name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getId()
     */
    @Override
    public String getId() {
        return id == null ? DEFAULT_CONTAINER_ID + Integer.toString(Math.abs(this.hashCode())) : id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#open()
     */
    @Override
    public void open() {
        synchronized (this) {
            setStatus(ContainerStatus.CREATED);
            while (status == ContainerStatus.CREATED) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored.
                }
            }
        }

    }

    

    public void initialize() {
        setStatus(ContainerStatus.INITIALIZING);
        doInitializeInternal();
        setStatus(ContainerStatus.CREATED);
        if(log.isDebugEnabled())
            log.debug("Container Created success for ID:"+getId());
    }
   
    /**
     * 
     */
    protected void doInitializeInternal() {
        extensionManager.initialize();
//        init features
        
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ContainerStatus status) {
        this.status = status;
        int type=ContainerEvent.CREATED;
        switch (status) {
            case CLOSED:
                type=ContainerEvent.POSTCLOSE;
                break;
            case CLOSING:
                type=ContainerEvent.PRECLOSE;
                break;
            case CREATED:
                type=ContainerEvent.CREATED;
                break;
            case CREATING:
               return;
            case INITIALIZING:
               return;
        }
        ContainerEvent event=new ContainerEvent(type,this,this);
        fireContainerEvent(event);
    }

    /**
     * 
     */
    protected void destroyBeans() {
        
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> map) {
        properties.clear();
        properties.putAll(map);
    }

    @Override
    public Object getProperty(String s) {
        return properties.get(s);
    }

    @Override
    public void setProperty(String s, Object o) {
        if (o == null) {
            properties.remove(s);
        } else {
            properties.put(s, o);
        }
    }
    @Override
    public void addListener(ContainerListener l) {
        synchronized (containerListeners) {
              containerListeners.add(l);
        }
  }
    @Override
    public void removeListener(ContainerListener l) {
        synchronized (containerListeners) {
              containerListeners.remove(l);
        }
        
       
  }
    private  boolean firstFireContainerListener=true;

    protected void fireContainerEvent(ContainerEvent event) {
        List<ContainerListener> toNotify = null;

        // Copy array
        synchronized (containerListeners) {
            if (firstFireContainerListener) {
                firstFireContainerListener=false;
                ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
                if (provider == null) {
                    provider = createBeanProvider();
                }
                if (provider != null) {
                    Collection<? extends ContainerListener> listeners = provider.getBeansOfType(ContainerListener.class);
                    if (listeners != null)
                        containerListeners.addAll(listeners);
                }
            }
            toNotify = new ArrayList<ContainerListener>(containerListeners);
        }
        // Notify all in toNotify
        for (Iterator<ContainerListener> i = toNotify.iterator(); i.hasNext();) {
            ContainerListener l = i.next();
            l.handleEvent(event);
        }
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#close()
     */
    @Override
    public void close() {
       close(true);
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#close(boolean)
     */
    @Override
    public void close(boolean wait) {
        if(status==ContainerStatus.CLOSING){
            return ;
        }
        synchronized(this){
            setStatus(ContainerStatus.CLOSING);
        }
        destroyBeans();
        synchronized(this){
            setStatus(ContainerStatus.CLOSED);
            notifyAll();
        }
        if(log.isDebugEnabled())
            log.debug("Container Closed for ID:"+getId());
        if(ContainerFactory.getDefaultContainer(false)==this){
            ContainerFactory.setDefaultContainer(null);
        }
        ContainerFactory.clearDefaultContainerForAnyThread(this);
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getExtensionLoader(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null)
            throw new IllegalArgumentException("Extension Type is null!");
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension Type:["
                + type.getName() + "] is not a interface!");
        }
        if (type.isAnnotationPresent(Extension.class)) {
            throw new IllegalArgumentException("Extension Type:["
                + type.getName() + "] ,without @"
                + Extension.class.getSimpleName() + " Annotation!");
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new DefaultExtensionLoader<T>(type,extensionManager));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

}
