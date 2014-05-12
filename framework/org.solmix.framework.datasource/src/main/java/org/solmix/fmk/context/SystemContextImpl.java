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

package org.solmix.fmk.context;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.SystemContextFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-3
 */

public class SystemContextImpl extends AbstractContext implements SystemContext
{
    private static final Logger log = LoggerFactory.getLogger(SystemContextImpl.class);

   public static enum ContextStatus
    {
        INITIAL , INITIALIZING , OPENING , CLOSING , CLOSED;

    }

    protected final Map<Class<?>, Object> beans;

    protected final Set<Class<?>> missingBeans;

    protected String id;

    private ContextStatus status;

    public SystemContextImpl()
    {
        this(null);
    }

    public SystemContextImpl(Map<Class<?>, Object> beans)
    {
        if (beans == null) {
            beans = new ConcurrentHashMap<Class<?>, Object>(16, 0.75f, 4);
        } else {
            beans = new ConcurrentHashMap<Class<?>, Object>(beans);
        }
        this.beans = beans;
        missingBeans = new CopyOnWriteArraySet<Class<?>>();

        status = ContextStatus.INITIAL;
        SystemContextFactory.possiblySetDefaultSystemContext(this);
    }

    public void setId(String id) {
        this.id = id;
    }

    
    /**
     * @return the status
     */
    public ContextStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.SystemContext#getBean(java.lang.Class)
     */
    @Override
    public <T> T getBean(Class<T> beanType) {
        Object obj = beans.get(beanType);
        if (obj == null) {
            if (missingBeans.contains(beanType)) {
                // missing beans,return null
                return null;
            }
            ConfiguredBeanProvider provider = (ConfiguredBeanProvider) beans.get(ConfiguredBeanProvider.class);
            if (provider == null) {
                provider = createBeanProvider();
            }
            if (provider != null) {
                Collection<?> objs = provider.getBeansOfType(beanType);
                if (objs != null) {
                    for (Object o : objs) {
                        beans.put(beanType, o);
                    }
                }
                obj = beans.get(beanType);
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
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) beans.get(ConfiguredBeanProvider.class);
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
     * @see org.solmix.runtime.SystemContext#setBean(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> void setBean(T bean, Class<T> beanType) {
        beans.put(beanType, bean);
        missingBeans.remove(beanType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.SystemContext#hasBeanByName(java.lang.String)
     */
    @Override
    public boolean hasBeanByName(String name) {
        for (Class<?> c : beans.keySet()) {
            if (name.equals(c.getName())) {
                return true;
            }
        }
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) beans.get(ConfiguredBeanProvider.class);
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
     * @see org.solmix.runtime.SystemContext#getId()
     */
    @Override
    public String getId() {
        return id == null ? DEFAULT_CONTEXT_ID + Integer.toString(Math.abs(this.hashCode())) : id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.SystemContext#open()
     */
    @Override
    public void open() {
        synchronized (this) {
            status = ContextStatus.OPENING;
            while (status == ContextStatus.OPENING) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored.
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.SystemContext#close(boolean)
     */
    @Override
    public void close(boolean wait) {
        if(status==ContextStatus.CLOSING){
            return ;
        }
        synchronized(this){
            status=ContextStatus.CLOSING;
        }
        //XXX preClose()
        destroyBeans();
        synchronized(this){
            status=ContextStatus.CLOSED;
            notifyAll();
        }
        //XXX postClose()
        if(SystemContextFactory.getDefaultSystemContext(false)==this){
            SystemContextFactory.setDefaultSystemContext(null);
        }
        SystemContextFactory.clearDefaultBusForAnyThread(this);
        
    }

    public void initialize() {
        setStatus(ContextStatus.INITIALIZING);
        //XXX
       /* Collection<? extends ContextCreationListener> ls = getExtension(ConfiguredBeanProvider.class)
            .getBeansOfType(ContextCreationListener.class);
        for (ContextCreationListener l : ls) {
            l.busCreated(this);
        }*/
        
        doInitializeInternal();
        
       /* BusLifeCycleManager lifeCycleManager = this.getExtension(BusLifeCycleManager.class);
        if (null != lifeCycleManager) {
            lifeCycleManager.initComplete();
        } */   
        setStatus(ContextStatus.OPENING);
    }
    @Deprecated
    @Override
    public void setLocale(Locale locale) {
        throw new UnsupportedOperationException(
            "setLocale() should not be called on SystemContext - system default locale is handled by MessagesManager");
    }
    /**
     * Return the default locale ,used resourceManager's default locale.
     */
    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException(
            "getLocale() should not be called on SystemContext - system default locale is handled by MessagesManager");
   
    }
    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        if (scope == Scope.SESSION || scope == Scope.LOCAL) {
            log.warn("you should not set an attribute in the system context in request or session scope. You are setting {}={}", name, value);
        }
        super.setAttribute(name, value, scope);

    }

    @Override
    public void removeAttribute(String name, Scope scope) {
        if (scope == Scope.SESSION || scope == Scope.LOCAL) {
            log.warn("you should not manipulate an attribute in the system context in request or session scope. You are setting name {}", name);
        }
        super.removeAttribute(name, scope);

    }
    /**
     * 
     */
    protected void doInitializeInternal() {
        
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ContextStatus status) {
        this.status = status;
    }

    /**
     * 
     */
    protected void destroyBeans() {
        
    }

}
