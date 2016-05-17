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

package org.solmix.runtime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-2
 */

public abstract class ContainerFactory
{

    public static final String DEFAULT_FACTORY = "org.solmix.runtime.support.ContainerFactoryImpl";

    public static final String FACTORY_PROPERTY_NAME = "org.solmix.runtime.ContainerFactory";

    protected static Container defaultContainer;

    protected static final Logger log = LoggerFactory.getLogger(ContainerFactory.class);

    protected static ThreadLocal<ContainerHolder> threadContainer = new ThreadLocal<ContainerHolder>();

    protected static Map<Thread, ContainerHolder> threadContainers = new WeakHashMap<Thread, ContainerHolder>(8);

    protected static List<Container> containersRef= new CopyOnWriteArrayList<Container>();
    /**
     * Returns the default system container, creating it if necessary.
     * 
     * @return the default system container.
     */
    public static synchronized Container getDefaultContainer() {
        return getDefaultContainer(true);
    }
    
    public static Container[] getContainers(){
    	
    	return containersRef.toArray(new Container[]{});
    }

   
    /**
     * Return the default system container.
     * 
     * @param b
     * @return
     */
    public static synchronized Container getDefaultContainer(boolean createIfNeeded) {
        if (defaultContainer == null && createIfNeeded) {
            defaultContainer = newInstance().createContainer();
        }
        if (defaultContainer == null) {
            // never set up.
            return null;
        } else {
            return defaultContainer;
        }
    }
    /**
     * Sets the default bus if a default container is not already set.
     *
     * @param container the default container.
     * @return true if the Container was not set and is now set
     */
    public static synchronized boolean possiblySetDefaultContainer(Container container) {
        if(!containersRef.contains(container)){
            containersRef.add(container);
        }
        ContainerHolder h = getThreadContainerHolder(false);
        if (h.container == null) {
           h.container=container;
        }
        if (defaultContainer == null) {
            defaultContainer = container;
            return true;
        }
        return false;
    }
    /**
     * Create a new ContainerFactory the class of {@link ContainerFactory} is determined by looking for the
     * system property:org.solmix.container.system.fatory or by searching the classpath for:
     * META-INF/services/org.solmix.runtime.ContainerFactory
     * 
     * @return
     */
    public static ContainerFactory newInstance() {
        return newInstance(null);
    }

    public static ContainerFactory newInstance(String className) {
        ContainerFactory instance = null;
        if (className == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            className = getFactoryClass(loader);
            if (className == null && loader != ContainerFactory.class.getClassLoader()) {
                className = getFactoryClass(ContainerFactory.class.getClassLoader());
            }
        }
        if (className == null) {
            className = ContainerFactory.DEFAULT_FACTORY;
        }

        Class<? extends ContainerFactory> factoryClass;
        try {
            factoryClass = ClassLoaderUtils.loadClass(className, ContainerFactory.class).asSubclass(ContainerFactory.class);

            instance = factoryClass.newInstance();
        } catch (Exception ex) {
            log.error("Container instance exception", ex);
            throw new RuntimeException(ex);
        }
        return instance;
    }

    /**
     * @param loader
     * @return
     */
    public static String getFactoryClass(ClassLoader classLoader) {
        String factoryClass = null;
        // next check system properties
        factoryClass = System.getProperty(FACTORY_PROPERTY_NAME);
        if (factoryClass != null && "".equals(factoryClass.trim())) {
            return factoryClass;
        }

        try {
            // next, check for the services stuff in the jar file
            String serviceId = "META-INF/services/" + ContainerFactory.FACTORY_PROPERTY_NAME;
            InputStream is = null;

            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }

            if (classLoader == null) {
                is = ClassLoader.getSystemResourceAsStream(serviceId);
            } else {
                is = classLoader.getResourceAsStream(serviceId);
            }

            if (is != null) {
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    factoryClass = rd.readLine();
                } finally {
                    if (rd != null) {
                        rd.close();
                    }
                }
            }
            if (factoryClass != null &&! "".equals(factoryClass.trim())) {
                // XXX In OSGI maybe need import another class,and those class can configured in
                // META-INF/services/xxx,this version we just used dynamic-import:*
                return factoryClass;

            } else {
                factoryClass = DEFAULT_FACTORY;
            }

        } catch (Exception ex) {
            log.error("Failed found system container factory class", ex);
        }
        return factoryClass;
    }

    public static synchronized void setDefaultContainer(Container container) {
        defaultContainer = container;
        ContainerHolder h = getThreadContainerHolder(false);
        h.container = container;
        if (container == null) {
            h.stale = true;
            threadContainer.remove();
        }
    }

    public static void setThreadDefaultContainer(Container container) {
        if (container == null) {
            ContainerHolder h = threadContainer.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContainers) {
                    h = threadContainers.get(cur);
                }
            }
            if (h != null) {
                h.container = null;
                h.stale = true;
                threadContainer.remove();
            }
        } else {
            ContainerHolder h = getThreadContainerHolder(true);
            h.container = container;
        }
    }

    public static Container getThreadDefaultContainer() {
        return getThreadDefaultContainer(true);
    }

    public static Container getThreadDefaultContainer(boolean createIfNeeded) {
        if (createIfNeeded) {
            ContainerHolder h = getThreadContainerHolder(false);
            if (h.container == null) {
                h.container = createThreadContainer();
            }
            return h.container;
        }
        ContainerHolder h = threadContainer.get();
        if (h == null || h.stale) {
            Thread cur = Thread.currentThread();
            synchronized (threadContainers) {
                h = threadContainers.get(cur);
            }
        }
        return h == null || h.stale ? null : h.container;

    }

    private static synchronized Container createThreadContainer() {
        ContainerHolder h = getThreadContainerHolder(false);
        if (h.container == null) {
            h.container = getDefaultContainer(true);
        }
        return h.container;
    }

    private static ContainerHolder getThreadContainerHolder(boolean set) {
        ContainerHolder h = threadContainer.get();
        if (h == null || h.stale) {
            Thread cur = Thread.currentThread();
            synchronized (threadContainers) {
                h = threadContainers.get(cur);
            }
            if (h == null || h.stale) {
                h = new ContainerHolder();

                synchronized (threadContainers) {
                    threadContainers.put(cur, h);
                }
            }
            if (set) {
                threadContainer.set(h);
            }
        }
        return h;
    }

    /**
     * Sets the default Container for the thread.
     * 
     * @param container the new thread default container
     * @return the old thread default system container or null
     */
    public static Container getAndSetThreadDefaultContainer(Container container) {
        if (container == null) {
            ContainerHolder h = threadContainer.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContainers) {
                    h = threadContainers.get(cur);
                }
            }
            if (h != null) {
                Container orig = h.container;
                h.container = null;
                h.stale = true;
                threadContainer.remove();
                return orig;
            }
            return null;
        }
        ContainerHolder b = getThreadContainerHolder(true);
        Container old = b.container;
        b.container = container;
        return old;
    }

    /**
     * Removes a Container from being a thread default container for any thread.
     * <p>
     * This is typically done when a system container has ended its lifecycle (i.e.: a call to
     * {@link Container#shutdown(boolean)} was invoked) and it wants to remove any reference to itself for any
     * thread.
     * 
     */
    public static void clearDefaultContainerForAnyThread(final Container container) {
        synchronized (threadContainers) {
            for (final Iterator<ContainerHolder> iterator = threadContainers.values().iterator(); iterator.hasNext();) {
                ContainerHolder item = iterator.next();
                if (container == null || item == null || item.container == null || item.stale || container.equals(item.container)) {
                    if (item != null) {
                        item.container = null;
                        // mark as stale so if a thread asks again, it will create a new one
                        item.stale = true;
                    }
                    // This will remove the ContainerHolder from the only place that should
                    // strongly reference it
                    iterator.remove();
                }
            }
        }
        containersRef.remove(container);
    }

    protected void initializeContainer(Container container) {

    }

    public abstract Container createContainer();

    static class ContainerHolder
    {

        volatile boolean stale;

        Container container;
    }
}
