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
import java.util.Map;
import java.util.WeakHashMap;

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

    protected static Map<Thread, ContainerHolder> threadContainers = new WeakHashMap<Thread, ContainerHolder>();

    /**
     * Returns the default system context, creating it if necessary.
     * 
     * @return the default system context.
     */
    public static synchronized Container getDefaultContainer() {
        return getDefaultContainer(true);
    }

    /**
     * Return the default system context.
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
     * Sets the default bus if a default context is not already set.
     *
     * @param context the default context.
     * @return true if the Container was not set and is now set
     */
    public static synchronized boolean possiblySetDefaultContainer(Container context) {
        ContainerHolder h = getThreadContainerHolder(false);
        if (h.context == null) {
           h.context=context;
        }
        if (defaultContainer == null) {
            defaultContainer = context;
            return true;
        }
        return false;
    }
    /**
     * Create a new ContainerFactory the class of {@link ContainerFactory} is determined by looking for the
     * system property:org.solmix.context.system.fatory or by searching the classpath for:
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
                    // busFactoryCondition = rd.readLine();
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
            log.error("Failed found system context factory class", ex);
        }
        return factoryClass;
    }

    public static synchronized void setDefaultContainer(Container context) {
        defaultContainer = context;
        ContainerHolder h = getThreadContainerHolder(false);
        h.context = context;
        if (context == null) {
            h.stale = true;
            threadContainer.remove();
        }

    }

    public static void setThreadDefaultContainer(Container context) {
        if (context == null) {
            ContainerHolder h = threadContainer.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContainers) {
                    h = threadContainers.get(cur);
                }
            }
            if (h != null) {
                h.context = null;
                h.stale = true;
                threadContainer.remove();
            }
        } else {
            ContainerHolder h = getThreadContainerHolder(true);
            h.context = context;
        }
    }

    public static Container getThreadDefaultContainer() {
        return getThreadDefaultContainer(true);
    }

    public static Container getThreadDefaultContainer(boolean createIfNeeded) {
        if (createIfNeeded) {
            ContainerHolder h = getThreadContainerHolder(false);
            if (h.context == null) {
                h.context = createThreadContainer();
            }
            return h.context;
        }
        ContainerHolder h = threadContainer.get();
        if (h == null || h.stale) {
            Thread cur = Thread.currentThread();
            synchronized (threadContainers) {
                h = threadContainers.get(cur);
            }
        }
        return h == null || h.stale ? null : h.context;

    }

    private static synchronized Container createThreadContainer() {
        ContainerHolder h = getThreadContainerHolder(false);
        if (h.context == null) {
            h.context = getDefaultContainer(true);
        }
        return h.context;
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
     * @param context the new thread default context
     * @return the old thread default system context or null
     */
    public static Container getAndSetThreadDefaultContainer(Container context) {
        if (context == null) {
            ContainerHolder h = threadContainer.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContainers) {
                    h = threadContainers.get(cur);
                }
            }
            if (h != null) {
                Container orig = h.context;
                h.context = null;
                h.stale = true;
                threadContainer.remove();
                return orig;
            }
            return null;
        }
        ContainerHolder b = getThreadContainerHolder(true);
        Container old = b.context;
        b.context = context;
        return old;
    }

    /**
     * Removes a Container from being a thread default context for any thread.
     * <p>
     * This is typically done when a system context has ended its lifecycle (i.e.: a call to
     * {@link Container#shutdown(boolean)} was invoked) and it wants to remove any reference to itself for any
     * thread.
     * 
     * @param bus the bus to remove
     */
    public static void clearDefaultContainerForAnyThread(final Container context) {
        synchronized (threadContainers) {
            for (final Iterator<ContainerHolder> iterator = threadContainers.values().iterator(); iterator.hasNext();) {
                ContainerHolder item = iterator.next();
                if (context == null || item == null || item.context == null || item.stale || context.equals(item.context)) {
                    if (item != null) {
                        item.context = null;
                        // mark as stale so if a thread asks again, it will create a new one
                        item.stale = true;
                    }
                    // This will remove the BusHolder from the only place that should
                    // strongly reference it
                    iterator.remove();
                }
            }
        }
    }

    protected void initializeContainer(Container context) {

    }

    public abstract Container createContainer();

    static class ContainerHolder
    {

        volatile boolean stale;

        Container context;
    }
}
