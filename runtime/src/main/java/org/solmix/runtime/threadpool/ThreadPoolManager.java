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

/**
 * Manage the thread pool in container
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月18日
 */

public interface ThreadPoolManager {

    public static final String DEFAULT = "default";
    
    /**
     * Get the default Thread Pool ,is not exist create it.
     * 
     * @return Thread Pool
     */
    ThreadPool getDefaultThreadPool();

    /**
     * Get Thread pool by name,is not exist return null.
     * 
     * @param name thread name
     * @return
     */
    ThreadPool getThreadPool(String name);
    
    /**
     * Create a new thread pool  and managed by this manager
     * @param name thread pool name
     * @return
     */
    ThreadPool createPool(String name);
    
    /**
     * Create a new thread pool  and managed by this manager
     * 
     * @param name thread pool name
     * @param initialThreads initial threads in pool
     * @param maxThreads max threads
     * @param minThreads min threads
     * @param maxQueueSize max queue size
     * @param dequeueTimeout 
     * @return
     */
    ThreadPool createPool(String name,int initialThreads,int maxThreads,int minThreads,int maxQueueSize,long dequeueTimeout);
 
    /**
     * Create a new thread pool  and managed by this manager
     * @param name thread pool name
     * @param coreThreads main threads,equals initialThreads and minThreads,50% of maxThreads
     * @param maxQueueSize
     * @return
     */
    ThreadPool createPool(String name,int coreThreads,int maxQueueSize);

    void addThreadPool(String name, ThreadPool executor);

    void shutdown(boolean processRemainingWorkItems);
}
