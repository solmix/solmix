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
package org.solmix.runtime.threadpool;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.management.ComponentManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月19日
 */

public class DefaultThreadPoolManager implements ThreadPoolManager {

    private static final Logger LOG  = LoggerFactory.getLogger(DefaultThreadPoolManager.class);
    Map<String, ThreadPool> namedPools = new ConcurrentHashMap<String, ThreadPool>(4, 0.75f, 2);

    private Container container;

    private boolean inShutdown;

    private ComponentManager componentManager;
    
    public DefaultThreadPoolManager() {

    }

    public DefaultThreadPoolManager(Container c) {
        setContainer(c);
    }
    
    public Container getContainer() {
        return container;
    }
    
    public void setContainer(Container container) {
        this.container = container;
        if (container != null) {
            container.setExtension(this, ThreadPoolManager.class);
            componentManager = container.getExtension(ComponentManager.class);
            if (componentManager != null) {
                 try {
                    componentManager.register(new ThreadPoolManagerMBean(this));
                } catch (JMException e) {
                    LOG.warn(e.getMessage(),e);
                }
            }
            ConfiguredBeanProvider cbp = container.getExtension(ConfiguredBeanProvider.class);
            if (cbp != null) {
                Collection<? extends ThreadPool> pools = cbp.getBeansOfType(ThreadPool.class);
                if (pools != null) {
                    for (ThreadPool pool : pools) {
                        addThreadPool(pool.getName(), pool);
                    }
                }
                if (!namedPools.containsKey(DEFAULT)) {
                    ThreadPool dfPool = cbp.getBeanOfType("threadpool.default",
                        ThreadPool.class);
                    if (dfPool != null) {
                        namedPools.put(DEFAULT, dfPool);
                    }
                }
            }
            container.addListener(new ThreadPoolListener());
        }
    }
    
    @Override
    public ThreadPool getDefaultThreadPool() {
        ThreadPool tp = getThreadPool(DEFAULT);
        if (tp == null) {
            tp = createDefaultThreadPool();
        }
        return tp;
    }

    private ThreadPool createDefaultThreadPool() {
        DefaultThreadPool dtp = new DefaultThreadPool(DEFAULT);
        namedPools.put(DEFAULT, dtp);
        return dtp;
    }
    @Override
    public ThreadPool getThreadPool(String name) {
        return namedPools.get(name);
    }

    public void run() {
        synchronized (this) {
            while (!inShutdown) {
                try {            
                    wait();
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
            for (ThreadPool q : namedPools.values()) {
                while (!q.isShutdown()) {
                    try {            
                        wait(100);
                    } catch (InterruptedException ex) {
                        // ignore
                    }
                }
            }
        }
    }

    @Override
    public void addThreadPool(String name, ThreadPool executor) {
        namedPools.put(name, executor);
        if (executor instanceof DefaultThreadPool) {
            DefaultThreadPool dtp = (DefaultThreadPool) executor;
            if (dtp.isShared()) {
                synchronized (dtp) {
                    if (dtp.getShareCount() == 0 && componentManager != null
                        && componentManager.getMBeanServer() != null) {
                        try {
                            componentManager.register(new ThreadPoolMBean((DefaultThreadPool)executor, this));
                        } catch (JMException e) {
                            LOG.warn(e.getMessage(),e);
                        }
                    }
                }

            } else if (componentManager != null) {
                try {
                    componentManager.register(new ThreadPoolMBean((DefaultThreadPool)executor, this));
                } catch (JMException e) {
                    LOG.warn(e.getMessage(),e);
                }
            }
        }
    }

   
    @Override
    public void shutdown(final boolean processRemainingWorkItems) {
        inShutdown = true;
        for (ThreadPool q : namedPools.values()) {
            if (q instanceof DefaultThreadPool) {
                DefaultThreadPool impl = (DefaultThreadPool) q;
                if (impl.isShared()) {
                    synchronized (impl) {
                        impl.removeSharedUser();

                        if (impl.getShareCount() == 0
                            && componentManager != null
                            && componentManager.getMBeanServer() != null) {
                            
                            try {
                                componentManager.unregister(new ThreadPoolMBean(impl, this));
                            } catch (JMException jmex) {
                                LOG.warn(jmex.getMessage(),jmex);
                            }
                             
                        }
                    }
                } else {
                    q.shutdown(processRemainingWorkItems);
                }
            } else {
                q.shutdown(processRemainingWorkItems);
            }
        }

        synchronized (this) {
            notifyAll();
        }
    }

    class ThreadPoolListener implements ContainerListener {

        /**
         * {@inheritDoc}
         * 
         * @see org.solmix.runtime.ContainerListener#handleEvent(org.solmix.runtime.ContainerEvent)
         */
        @Override
        public void handleEvent(ContainerEvent event) {
            int type = event.getType();
            switch (type) {
            case ContainerEvent.PRECLOSE:
                shutdown(true);
            default:
                return;
            }
            
        }
        
    }

    @Override
    public ThreadPool createPool(String name) {
        ThreadPool pool  = new DefaultThreadPool(name);
        addThreadPool(name, pool);
        return getThreadPool(name);
    }

    @Override
    public ThreadPool createPool(String name, int initialThreads, int maxThreads, int minThreads, int maxQueueSize, long dequeueTimeout) {
        ThreadPool pool  = new DefaultThreadPool(maxQueueSize,initialThreads,maxThreads,minThreads,dequeueTimeout,name);
        addThreadPool(name, pool);
        return getThreadPool(name);
    }

    @Override
    public ThreadPool createPool(String name, int coreThreads, int maxQueueSize) {
        ThreadPool pool  = new DefaultThreadPool(coreThreads,maxQueueSize,name);
        addThreadPool(name, pool);
        return getThreadPool(name);
    }
}
