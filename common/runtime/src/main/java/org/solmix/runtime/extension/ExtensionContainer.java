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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.ContainerListener;
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
   public static enum ContainerStatus
    {
        CREATING , INITIALIZING , CREATED , CLOSING , CLOSED;

    }

    protected final ExtensionObjectCache extensions;

    protected final Set<Class<?>> missingBeans;
    private final ExtensionManager extensionManager;
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
            extensions = new ExtensionObjectCache(16, 0.75f, 4);
        } else {
            extensions = new ExtensionObjectCache(beans);
        }
        missingBeans = new CopyOnWriteArraySet<Class<?>>();

        status = ContainerStatus.CREATING;
        ContainerFactory.possiblySetDefaultContainer(this);
        if (null == properties) {
            properties = new HashMap<String, Object>();
        }
        ResourceManager rm=new  ResourceManagerImpl();
        properties.put(CONTAINER_PROPERTY_NAME, DEFAULT_CONTAINER_ID);
        properties.put(DEFAULT_CONTAINER_ID, this);
        ResourceResolver propertiesResolver = new PropertiesResolver(properties);
        rm.addResourceResolver(propertiesResolver);
        
        ResourceResolver busResolver = new SinglePropertyResolver(DEFAULT_CONTAINER_ID, this);
        rm.addResourceResolver(busResolver);
        rm.addResourceResolver(new ObjectTypeResolver(this));
        
        
        extensionManager= new ExtensionManagerImpl(new String[0],
            extensionClassLoader,
            extensions,
            rm, 
            this);
    }

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
     * @see org.solmix.runtime.Container#getBean(java.lang.Class)
     */
    @Override
    public <T> T getBean(Class<T> beanType) {
        Object obj = extensions.getObject(beanType);
        if (obj == null) {
            if (missingBeans.contains(beanType)) {
                // missing extensions,return null
                return null;
            }
            ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.getObject(ConfiguredBeanProvider.class);
            if (provider == null) {
                provider = createBeanProvider();
            }
            if (provider != null) {
                Collection<?> objs = provider.getBeansOfType(beanType);
                if (objs != null) {
                    for (Object o : objs) {
                        extensions.putObject(beanType, o);
                    }
                }
                obj = extensions.getObject(beanType);
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
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.getObject(ConfiguredBeanProvider.class);
        if (provider == null) {
            provider = new ConfiguredBeanProvider() {

                @Override
                public List<String> getBeanNamesOfType(Class<?> type) {
                    return null;
                }

                @Override
                public <T> T getBeanOfType(String name, Class<T> type) {
                    return null;
                }

                @Override
                public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
                    return null;
                }

                @Override
                public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
                    return false;
                }

                @Override
                public boolean hasBeanOfName(String name) {
                    return false;
                }

            };
            this.setBean(provider, ConfiguredBeanProvider.class);
        }
        return provider;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#setBean(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> void setBean(T bean, Class<T> beanType) {
        extensions.putObject(beanType, bean);
        missingBeans.remove(beanType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#hasBeanByName(java.lang.String)
     */
    @Override
    public boolean hasBeanByName(String name) {
        for (Class<?> c : extensions.keySet()) {
            if (name.equals(c.getName())) {
                return true;
            }
        }
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.getObject(ConfiguredBeanProvider.class);
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
            status = ContainerStatus.CREATED;
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
        //XXX
       /* Collection<? extends ContainerCreationListener> ls = getExtension(ConfiguredBeanProvider.class)
            .getBeansOfType(ContainerCreationListener.class);
        for (ContainerCreationListener l : ls) {
            l.busCreated(this);
        }*/
        
        doInitializeInternal();
        
       /* BusLifeCycleManager lifeCycleManager = this.getExtension(BusLifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.initComplete();
        } */   
        setStatus(ContainerStatus.CREATED);
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
    protected void fireContainerEvent(ContainerEvent event) {
        List<ContainerListener> toNotify = null;
        // Copy array
        synchronized (containerListeners) {
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
            status=ContainerStatus.CLOSING;
        }
        //XXX preClose()
        destroyBeans();
        synchronized(this){
            status=ContainerStatus.CLOSED;
            notifyAll();
        }
        //XXX postClose()
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
    @Override
    public <T> ExtensionLoader<T> getExtensionLoader(Class<T> beanType) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
