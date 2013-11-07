/*
 * SOLMIX PROJECT
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

package org.solmix.api.context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-2
 */

public abstract class SystemContextFactory
{

    public static final String DEFAULT_FACTORY = "org.solmix.fmk.context.SystemContextFactoryImpl";

    public static final String FACTORY_PROPERTY_NAME = "org.solmix.context.system.factory";

    protected static SystemContext defaultContext;

    protected static final Logger log = LoggerFactory.getLogger(SystemContextFactory.class);

    protected static ThreadLocal<ContextHolder> threadContext = new ThreadLocal<ContextHolder>();

    protected static Map<Thread, ContextHolder> threadContexts = new WeakHashMap<Thread, ContextHolder>();

    /**
     * Returns the default system context, creating it if necessary.
     * 
     * @return the default system context.
     */
    public static synchronized SystemContext getDefaultSystemContext() {
        return getDefaultSystemContext(true);
    }

    /**
     * Return the default system context.
     * 
     * @param b
     * @return
     */
    public static synchronized SystemContext getDefaultSystemContext(boolean createIfNeeded) {
        if (defaultContext == null && createIfNeeded) {
            defaultContext = newInstance().createContext();
        }
        if (defaultContext == null) {
            // never set up.
            return null;
        } else {
            return defaultContext;
        }
    }
    /**
     * Sets the default bus if a default context is not already set.
     *
     * @param context the default context.
     * @return true if the SystemContext was not set and is now set
     */
    public static synchronized boolean possiblySetDefaultSystemContext(SystemContext context) {
        ContextHolder h = getThreadContextHolder(false);
        if (h.context == null) {
           h.context=context;
        }
        if (defaultContext == null) {
            defaultContext = context;
            return true;
        }
        return false;
    }
    /**
     * Create a new SystemContextFactory the class of {@link SystemContextFactory} is determined by looking for the
     * system property:org.solmix.context.system.fatory or by searching the classpath for:
     * META-INF/services/org.solmix.context.system.fatory
     * 
     * @return
     */
    public static SystemContextFactory newInstance() {
        return newInstance(null);
    }

    public static SystemContextFactory newInstance(String className) {
        SystemContextFactory instance = null;
        if (className == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            className = getFactoryClass(loader);
            if (className == null && loader != SystemContextFactory.class.getClassLoader()) {
                className = getFactoryClass(SystemContextFactory.class.getClassLoader());
            }
        }
        if (className == null) {
            className = SystemContextFactory.DEFAULT_FACTORY;
        }

        Class<? extends SystemContextFactory> factoryClass;
        try {
            factoryClass = ClassLoaderUtil.loadClass(className, SystemContextFactory.class).asSubclass(SystemContextFactory.class);

            instance = factoryClass.newInstance();
        } catch (Exception ex) {
            log.error("SystemContext instance exception", ex);
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
            String serviceId = "META-INF/services/" + SystemContextFactory.FACTORY_PROPERTY_NAME;
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

    public static synchronized void setDefaultSystemContext(SystemContext context) {
        defaultContext = context;
        ContextHolder h = getThreadContextHolder(false);
        h.context = context;
        if (context == null) {
            h.stale = true;
            threadContext.remove();
        }

    }

    public static void setThreadDefaultSystemContext(SystemContext context) {
        if (context == null) {
            ContextHolder h = threadContext.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContexts) {
                    h = threadContexts.get(cur);
                }
            }
            if (h != null) {
                h.context = null;
                h.stale = true;
                threadContext.remove();
            }
        } else {
            ContextHolder h = getThreadContextHolder(true);
            h.context = context;
        }
    }

    public static SystemContext getThreadDefaultSystemContext() {
        return getThreadDefaultSystemContext(true);
    }

    public static SystemContext getThreadDefaultSystemContext(boolean createIfNeeded) {
        if (createIfNeeded) {
            ContextHolder h = getThreadContextHolder(false);
            if (h.context == null) {
                h.context = createThreadContext();
            }
            return h.context;
        }
        ContextHolder h = threadContext.get();
        if (h == null || h.stale) {
            Thread cur = Thread.currentThread();
            synchronized (threadContexts) {
                h = threadContexts.get(cur);
            }
        }
        return h == null || h.stale ? null : h.context;

    }

    private static synchronized SystemContext createThreadContext() {
        ContextHolder h = getThreadContextHolder(false);
        if (h.context == null) {
            h.context = getDefaultSystemContext(true);
        }
        return h.context;
    }

    private static ContextHolder getThreadContextHolder(boolean set) {
        ContextHolder h = threadContext.get();
        if (h == null || h.stale) {
            Thread cur = Thread.currentThread();
            synchronized (threadContexts) {
                h = threadContexts.get(cur);
            }
            if (h == null || h.stale) {
                h = new ContextHolder();

                synchronized (threadContexts) {
                    threadContexts.put(cur, h);
                }
            }
            if (set) {
                threadContext.set(h);
            }
        }
        return h;
    }

    /**
     * Sets the default SystemContext for the thread.
     * 
     * @param context the new thread default context
     * @return the old thread default system context or null
     */
    public static SystemContext getAndSetThreadDefaultSystemContext(SystemContext context) {
        if (context == null) {
            ContextHolder h = threadContext.get();
            if (h == null) {
                Thread cur = Thread.currentThread();
                synchronized (threadContexts) {
                    h = threadContexts.get(cur);
                }
            }
            if (h != null) {
                SystemContext orig = h.context;
                h.context = null;
                h.stale = true;
                threadContext.remove();
                return orig;
            }
            return null;
        }
        ContextHolder b = getThreadContextHolder(true);
        SystemContext old = b.context;
        b.context = context;
        return old;
    }

    /**
     * Removes a SystemContext from being a thread default context for any thread.
     * <p>
     * This is typically done when a system context has ended its lifecycle (i.e.: a call to
     * {@link SystemContext#shutdown(boolean)} was invoked) and it wants to remove any reference to itself for any
     * thread.
     * 
     * @param bus the bus to remove
     */
    public static void clearDefaultBusForAnyThread(final SystemContext context) {
        synchronized (threadContexts) {
            for (final Iterator<ContextHolder> iterator = threadContexts.values().iterator(); iterator.hasNext();) {
                ContextHolder item = iterator.next();
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

    protected void initializeContext(SystemContext context) {

    }

    public abstract SystemContext createContext();

    static class ContextHolder
    {

        volatile boolean stale;

        SystemContext context;
    }
}
