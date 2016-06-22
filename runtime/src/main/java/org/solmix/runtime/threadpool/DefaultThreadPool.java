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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.commons.util.Reflection;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月19日
 */

public class DefaultThreadPool implements ThreadPool {
    
    public static final String PROPERTY_NAME = "name";
    
    public static final int DEFAULT_MAX_SIZE = 256;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultThreadPool.class);

    private String name;

    private int maxQueueSize;

    private int initialThreads;

    private int maxThreads;

    private int minThreads;

    private long dequeueTimeout;

    private volatile int approxThreadCount;

    private ThreadPoolFactory threadFactory;

    private ThreadPoolExecutor executor;

    private WatchDog watchDog;

    private Method addWorkerMethod;

    private Object addWorkerArgs[];

    private ReentrantLock mainLock;

    final ReentrantLock addThreadLock = new ReentrantLock();

    private boolean shared;

    int sharedCount;
    
    private List<PropertyChangeListener> changeListenerList;
    
    public DefaultThreadPool() {
        this("solmix-threadpool");
    }

    public DefaultThreadPool(String name) {
        this(DEFAULT_MAX_SIZE, name);
    }
    public DefaultThreadPool(int maxSize) {
        this(maxSize, ThreadPoolManager.DEFAULT);
    }

    public DefaultThreadPool(int maxSize, String name) {
        this(maxSize,
            0,
            25,
            5,
            2 * 60 * 1000L,
            name);
    }
    
    public DefaultThreadPool(int coreThread,int queueSize, String name) {
        this(queueSize,
        		coreThread,
        		coreThread*2,
        		coreThread,
	            2 * 60 * 1000L,
	            name);
    }
    
    public DefaultThreadPool(int maxQueueSize,
        int initialThreads,
        int maxThreads,
        int minThreads,
        long dequeueTimeout) {
        this(maxQueueSize,
            initialThreads,
            maxThreads,
            minThreads,
            dequeueTimeout,
            ThreadPoolManager.DEFAULT);
    }
    
    public DefaultThreadPool(int maxQueueSize,
                             int initialThreads,
                             int maxThreads,
                             int minThreads,
                             long dequeueTimeout,
                             String name) {
        this.maxQueueSize = maxQueueSize == -1 ? DEFAULT_MAX_SIZE : maxQueueSize;
        this.initialThreads = initialThreads;
        this.maxThreads = maxThreads == -1 ? Integer.MAX_VALUE : maxThreads;
        this.minThreads = minThreads == -1 ? Integer.MAX_VALUE : minThreads;
        this.dequeueTimeout = dequeueTimeout;
        this.name = name;
        this.changeListenerList = new ArrayList<PropertyChangeListener>();
    }

    @Override
    public void execute(final Runnable command) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                ClassLoaderHolder orig = ClassLoaderUtils.setThreadContextClassloader(loader);
                try {
                    command.run();
                } finally {
                    if (orig != null) {
                        orig.reset();
                    }
                }
            }
        };
        ThreadPoolExecutor ex = getExecutor();
        ex.execute(r);
        /**
         * 在JDK中当线程队列满了后,线程池才增加线程,这里当线程不够时立即增加线程,增加线程到最大值后加入队列.
         */
        if (addWorkerMethod != null 
            && !ex.getQueue().isEmpty() 
            && this.approxThreadCount < maxThreads
            && addThreadLock.tryLock()) {
            try {
                mainLock.lock();
                try {
                    int ps = this.getPoolSize();
                    int sz = executor.getQueue().size();
                    int sz2 = this.getActiveCount();
                    
                    if ((sz + sz2) > ps) {
                        Reflection.setAccessible(addWorkerMethod).invoke(executor, addWorkerArgs);
                    }
                } catch (Exception exc) {
                    //ignore
                } finally {
                    mainLock.unlock();
                }
            } finally {
                addThreadLock.unlock();
            }
        }
    }
    
    /**
     * 返回曾经同时位于池中的最大线程数.
     * @return
     */
    public int getLargestPoolSize() {
        if (executor == null) {
            return 0;
        }
        return executor.getLargestPoolSize();
    }
    
    /**
     * 最大允许等待队列数.
     * 
     * @return
     */
    public long getMaxQueueSize() {
        return maxQueueSize;
    }
    
    /**
     * 当前队列中数量.
     * 
     * @return
     */
    public long getQueueSize() {
        return executor == null ? 0 : executor.getQueue().size();
    }
    
    /**
     * 队列是否为空.
     * 
     * @return
     */
    public boolean isQueueEmpty() {
        return executor == null ? true : executor.getQueue().size() == 0;
    }

    /**
     * 队列是否已满.
     * 
     * @return
     */
    public boolean isQueueFull() {
        return executor == null ? false : executor.getQueue().remainingCapacity() == 0;
    }
    
    /**
     * 返回线程池中线程数.
     * 
     * @return
     */
    public int getPoolSize() {
        if (executor == null) {
            return 0;
        }
        return executor.getPoolSize();
    }
    
    /**
     * 最大允许线程数
     * @return
     */
    public int getMaxThreads() {
        int hwm = executor == null ? maxThreads : executor.getMaximumPoolSize();
        return hwm == Integer.MAX_VALUE ? -1 : hwm;
    }

    /**
     * 最小线程数
     * @return
     */
    public int getMinThreads() {
        int lwm = executor == null ? minThreads : executor.getCorePoolSize();
        return lwm == Integer.MAX_VALUE ? -1 : lwm;
    }
    
    /**
     * 初始线程数
     * @return
     */
    public int getInitialSize() {
        return this.initialThreads;
    }
    
    /**
     * 当前活动线程数.
     * 
     * @return
     */
    public int getActiveCount() {
        if (executor == null) {
            return 0;
        }
        return executor.getActiveCount();
    }
    
    public void addChangeListener(PropertyChangeListener listener) {
        this.changeListenerList.add(listener);
    }
    
    public void removeChangeListener(PropertyChangeListener listener) {
        this.changeListenerList.remove(listener);
    }
    
    public void notifyChangeListeners(PropertyChangeEvent event) {
        for (PropertyChangeListener listener : changeListenerList) {
            listener.propertyChange(event);
        }
    }
    
    public void setMaxThreads(int max) {
        maxThreads = max < 0 ? Integer.MAX_VALUE : max;
        if (executor != null) {
            notifyChangeListeners(new PropertyChangeEvent(this, "maxThreads",
                this.executor.getMaximumPoolSize(), max));
            executor.setMaximumPoolSize(maxThreads);
        }
    }
    
    public void setMinThreads(int min) {
        minThreads = min < 0 ? Integer.MAX_VALUE : min;
        if (executor != null) {
            notifyChangeListeners(new PropertyChangeEvent(this, "minThreads",
                this.executor.getMaximumPoolSize(), min));
            executor.setCorePoolSize(minThreads);
        }
    }
    
    public void setInitialSize(int initialSize) {
        notifyChangeListeners(new PropertyChangeEvent(this, "initialSize", this.initialThreads, initialSize));
        this.initialThreads = initialSize;
    }
    
    public void setQueueSize(int size) {
        notifyChangeListeners(new PropertyChangeEvent(this, "queueSize", this.maxQueueSize, size));
        this.maxQueueSize = size;
    }
    
    public void setDequeueTimeout(long l) {
        notifyChangeListeners(new PropertyChangeEvent(this, "dequeueTimeout", this.dequeueTimeout, l));
        this.dequeueTimeout = l;
    }
    
    
    protected synchronized ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            threadFactory = createThreadFactory(name);
            executor = new ThreadPoolExecutor(minThreads, maxThreads,
                TimeUnit.MILLISECONDS.toMillis(dequeueTimeout),
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                    maxQueueSize), threadFactory) {

                @Override
                protected void terminated() {
                    ThreadFactory f = executor.getThreadFactory();
                    if (f instanceof ThreadPoolFactory) {
                        ((ThreadPoolFactory) f).shutdown();
                    }
                    if (watchDog != null) {
                        watchDog.shutdown();
                    }
                }
            };
            if (LOG.isTraceEnabled()) {
                StringBuilder buf = new StringBuilder();
                buf.append("Constructing ThreadPool with:\n");
                buf.append("max queue size: " + maxQueueSize + "\n");
                buf.append("initialThreads: " + initialThreads + "\n");
                buf.append("minThreads: " + minThreads + "\n");
                buf.append("maxThreads: " + maxThreads + "\n");
                LOG.trace(buf.toString());
            }
            if (initialThreads > maxThreads) {
                initialThreads = maxThreads;
            }
            if (initialThreads < Integer.MAX_VALUE && initialThreads > 0) {
                executor.setCorePoolSize(initialThreads);
                int started = executor.prestartAllCoreThreads();
                if (started < initialThreads) {
                    LOG.warn("THREAD_START_FAILURE_MSG");
                }
                executor.setCorePoolSize(minThreads);
            }

            ReentrantLock l = null;
            try {
                Field f = ThreadPoolExecutor.class.getDeclaredField("mainLock");
                Reflection.setAccessible(f);
                l = (ReentrantLock)f.get(executor);
            } catch (Throwable t) {
                l = new ReentrantLock();
            }
            mainLock = l;

            try {
                //java 5/6
                addWorkerMethod = ThreadPoolExecutor.class.getDeclaredMethod("addIfUnderMaximumPoolSize",
                                                                             Runnable.class);
                addWorkerArgs = new Object[] {null};
            } catch (Throwable t) {
                try {
                    //java 7
                    addWorkerMethod = ThreadPoolExecutor.class.getDeclaredMethod("addWorker",
                                                                                 Runnable.class, Boolean.TYPE);
                    addWorkerArgs = new Object[] {null, Boolean.FALSE};
                } catch (Throwable t2) {
                    //nothing we cando
                }
            }
        }
        
        return executor;
    }
    
    private ThreadPoolFactory createThreadFactory(final String tname) {
        ThreadGroup group;
        try {
            group = AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {

                @Override
                public ThreadGroup run() {
                    ThreadGroup group = Thread.currentThread().getThreadGroup();
                    ThreadGroup parent = group;
                    try {
                        while (parent != null) {
                            group = parent;
                            parent = parent.getParent();
                        }
                    } catch (SecurityException se) {
                        // ignore
                    }
                    return new ThreadGroup(group, tname + "-threadpool");
                }
            });
        } catch (SecurityException e) {
            group = new ThreadGroup(tname + "-threadpool");
        }
        return new ThreadPoolFactory(group, tname);
    }

    @Override
    public String getName() {
        return name;
    }
    public void setName(String s) {
        name = s;
        if (threadFactory != null) {
            threadFactory.setName(s);
        }
    }
    @Override
    public void shutdown(boolean processRemainingWorkItems) {
        if (executor != null) {
            if (!processRemainingWorkItems) {
                executor.getQueue().clear();
            }
            executor.shutdown();
        }
    }

    @Override
    public boolean isShutdown() {
        if (executor == null) {
            return false;
        }
        return executor.isShutdown();
    }
  
    
    
    public void update(Dictionary<String, String> config) {
        String s = config.get("maxThreads");
        if (s != null) {
            this.maxThreads = Integer.parseInt(s);
        }
        s = config.get("minThreads");
        if (s != null) {
            this.minThreads = Integer.parseInt(s);
        }
        s = config.get("initialSize");
        if (s != null) {
            this.initialThreads = Integer.parseInt(s);
        }
        s = config.get("dequeueTimeout");
        if (s != null) {
            this.dequeueTimeout = Long.parseLong(s);
        }
        s = config.get("queueSize");
        if (s != null) {
            this.maxQueueSize = Integer.parseInt(s);
        } 
    }
    
    class ThreadPoolFactory implements ThreadFactory {

        final AtomicInteger threadNumber = new AtomicInteger(1);
        ThreadGroup group;
        String name;
        ClassLoader loader;

        ThreadPoolFactory(ThreadGroup group, String name) {
            this.group = group;
            this.name = name;
            loader = DefaultThreadPool.class.getClassLoader();
        }
        @Override
        public Thread newThread(final Runnable r) {
            if (group.isDestroyed()) {
                group = new ThreadGroup(group.getParent(), name + "-threadpool");
            }
            Runnable wrapped = new Runnable() {
                @Override
                public void run() {
                    ++approxThreadCount;
                    try {
                        r.run();
                    } finally {
                        --approxThreadCount;
                    }
                }
            };
            final Thread t = new Thread(group, 
                                  wrapped, 
                                  name + "-thread-" + threadNumber.getAndIncrement(),
                                  0);
            AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    t.setContextClassLoader(loader);
                    return true;
                }
            });
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
        public void setName(String s) {
            name = s;
        }
        public void shutdown() {
            if (!group.isDestroyed()) {
                try {
                    group.destroy();
                    group.setDaemon(true);
                } catch (Throwable t) {
                    //ignore
                }
            }            
        }
    }
    
    static class DelayedTaskWrapper implements Delayed, Runnable {
        long trigger;
        Runnable work;
        
        DelayedTaskWrapper(Runnable work, long delay) {
            this.work = work;
            trigger = System.currentTimeMillis() + delay;
        }
        
        @Override
        public long getDelay(TimeUnit unit) {
            long n = trigger - System.currentTimeMillis();
            return unit.convert(n, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed delayed) {
            long other = ((DelayedTaskWrapper)delayed).trigger;
            int returnValue;
            if (this.trigger < other) {
                returnValue = -1;
            } else if (this.trigger > other) {
                returnValue = 1;
            } else {
                returnValue = 0;
            }
            return returnValue;
        }

        @Override
        public void run() {
            work.run();
        }
        
    }
    class WatchDog extends Thread {
        DelayQueue<DelayedTaskWrapper> delayQueue;
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        WatchDog(DelayQueue<DelayedTaskWrapper> queue) {
            delayQueue = queue;
        }
        
        public void shutdown() {
            shutdown.set(true);
            // to exit the waiting thread
            interrupt();
        }
        
        @Override
        public void run() {
            DelayedTaskWrapper task;
            try {
                while (!shutdown.get()) {
                    task = delayQueue.take();
                    if (task != null) {
                        try {
                            execute(task);
                        } catch (Exception ex) {
                            LOG.warn("Executing the task from DelayQueue with exception: " + ex);
                        }
                    }
                }
            } catch (InterruptedException e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("The DelayQueue watchdog Task is stopping");
                }
            }

        }
        
    }
    public void setShared(boolean shared) {
        this.shared = shared;
    }
    public boolean isShared() {
        return shared;
    }
    public void addSharedUser() {
        sharedCount++;
    }
    public void removeSharedUser() {
        sharedCount--;
    }
    public int getShareCount() {
        return sharedCount;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.threadpool.ThreadPool#execute(java.lang.Runnable, long)
     */
    @Override
    public void execute(Runnable runnable, long timeout) {
        try {
            execute(runnable);
        } catch (RejectedExecutionException ree) {
            try {
                if (!getExecutor().getQueue().offer(runnable, timeout, TimeUnit.MILLISECONDS)) {
                    throw ree;
                }
            } catch (InterruptedException ie) {
                throw ree;
            }
        }    
    }
    public Dictionary<String, String> getProperties() {
        Dictionary<String, String> properties = new Hashtable<String, String>();
        NumberFormat nf = NumberFormat.getIntegerInstance();
        properties.put("name", nf.format(getName()));
        properties.put("maxThreads", nf.format(getMaxThreads()));
        properties.put("minThreads", nf.format(getMinThreads()));
        properties.put("initialSize", nf.format(getInitialSize()));
        properties.put("maxQueueSize", nf.format(getMaxQueueSize()));
        properties.put("queueSize", nf.format(getQueueSize()));
        return properties;
    }

}
