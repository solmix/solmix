/*
 * ========THE SOLMIX PROJECT=====================================
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

package com.solmix.eventservice.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.log.LogService;

import com.solmix.eventservice.Activator;

/**
 * 
 * @author solomon
 * @version 110035 2011-10-1
 */

public class EventThreadPool extends ThreadPoolExecutor
{

    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     */
    public EventThreadPool(int corePoolSize, final boolean isSynThread)
    {
        super(corePoolSize, corePoolSize + 10, 6, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        if (isSynThread) {
            this.setThreadFactory(new ThreadFactory() {

                ThreadGroup tgroup = new ThreadGroup("SYN-Event");

                @Override
                public Thread newThread(Runnable command) {
                    final Thread thread = new SyncEventThread(tgroup, command, "SYN-Event-Thread-" + nextThreadNum());
                    thread.setPriority(Thread.NORM_PRIORITY);
                    thread.setDaemon(true);
                    return thread;
                }

            });
        } else {
            this.setThreadFactory(new ThreadFactory() {

                ThreadGroup tgroup = new ThreadGroup("ASY-Event");

                @Override
                public Thread newThread(final Runnable command) {
                    final Thread thread = new Thread(tgroup, command, "ASY-Event-Thread-" + nextThreadNum());
                    thread.setPriority(Thread.NORM_PRIORITY);
                    thread.setDaemon(true);

                    return thread;
                }

            });
        }
        this.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void updatePoolSize(int poolSize) {
        setCorePoolSize(poolSize);
        setMaximumPoolSize(poolSize + 10);
    }

    /* For autonumbering anonymous threads. */
    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /**
     * @param runnable
     */
    public void executeTask(Runnable runnable) {
        try {
            super.execute(runnable);
        } catch (Throwable t) {
            Activator.getLogService().log(LogService.LOG_WARNING, "Exception: " + t, t);
        }
    }
}
