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

package org.solmix.commons.tasks;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-24
 */

public class TaskThreadPool extends ThreadPoolExecutor
{

    private static final Logger log = LoggerFactory.getLogger(TaskThreadPool.class);

    public static AtomicLong totalTask = new AtomicLong();

    public TaskThreadPool(int corePoolSize, final String groupTheadName)
    {
        super(corePoolSize, corePoolSize + 50, 6, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.setThreadFactory(new ThreadFactory() {

            ThreadGroup tgroup = new ThreadGroup(groupTheadName + totalTask.getAndIncrement());

            @Override
            public Thread newThread(Runnable command) {
                final Thread thread = new SyncEventThread(tgroup, command);
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.setDaemon(true);

                return thread;
            }

        });

        this.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void executeTask(Runnable runnable) {
        try {
            super.execute(runnable);
        } catch (Throwable t) {
            log.error("Exception: " + t.getMessage(), t);
        }
    }

    public class SyncEventThread extends Thread
    {

        public SyncEventThread(Runnable command)
        {
            super(command);
        }

        public SyncEventThread(ThreadGroup group, Runnable command)
        {
            super(group, command);
        }
    }

}
