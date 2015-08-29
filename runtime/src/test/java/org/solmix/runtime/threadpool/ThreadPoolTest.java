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

import java.util.concurrent.RejectedExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月20日
 */

public class ThreadPoolTest extends Assert {

    DefaultThreadPool threadpool;

    public static final long DEFAULT_DEQUEUE_TIMEOUT = 2 * 60 * 1000L;

    @After
    public void tearDown() {
        if (threadpool != null) {
            threadpool.shutdown(true);
            threadpool = null;
        }
    }

    @Test
    public void testDefault() {
        threadpool = new DefaultThreadPool(-1, 2, -1, -1,
            DEFAULT_DEQUEUE_TIMEOUT);
        assertNotNull(threadpool);
        assertEquals(DefaultThreadPool.DEFAULT_MAX_SIZE, threadpool.getMaxQueueSize());
        assertEquals(-1, threadpool.getMaxThreads());
        assertEquals(-1, threadpool.getMinThreads());
    }
    
    @Test
    public void testConstructor() {
        threadpool = new DefaultThreadPool(10, 2, 10, 1,
            DEFAULT_DEQUEUE_TIMEOUT);
        assertNotNull(threadpool);
        assertEquals(10, threadpool.getMaxQueueSize());
        assertEquals(10, threadpool.getMaxThreads());
        assertEquals(1, threadpool.getMinThreads());
    }
    
    @Test
    public void testExecute() {
        threadpool = new DefaultThreadPool(10, 2, 10, 1,
            DEFAULT_DEQUEUE_TIMEOUT);

        try {
            Thread.sleep(100);
        } catch (Exception e) {
            // ignore
        }
        assertEquals(0, threadpool.getQueueSize());
        assertEquals(0, threadpool.getPoolSize());

        assertEquals(0, threadpool.getActiveCount());
        threadpool.execute(new TestRunner(), 100);
        int i = 0;
        while (threadpool.getQueueSize() != 0 && i++ < 50) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                // ignore
            }
        }
        assertEquals(0, threadpool.getQueueSize());

    }
    
    @Test
    public void testBlockExecute() {
        threadpool = new DefaultThreadPool(10, 2, 10, 1,
            DEFAULT_DEQUEUE_TIMEOUT);
        BlockingRunner[] b = new BlockingRunner[10];
        BlockingRunner[] b2 = new BlockingRunner[10];
        try {
            // 先加入10个任务,并阻塞
            for (int i = 0; i < 10; i++) {
                b[i] = new BlockingRunner();
                try {
                    threadpool.execute(b[i]);
                } catch (RejectedExecutionException ex) {
                    fail("failed on item[" + i + "] with: " + ex);
                }
            }
            // 等所有线程执行.
            while (threadpool.getActiveCount() < 10) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    // ignore
                }
            }
            // 10线程达到最大值,加入队列
            for (int i = 0; i < 10; i++) {
                b2[i] = new BlockingRunner();
                try {
                    System.out.println(threadpool.getQueueSize());
                    threadpool.execute(b2[i]);
                } catch (RejectedExecutionException ex) {
                    fail("failed on item[" + i + "] with: " + ex);
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                // ignore
            }
            assertTrue(threadpool.isQueueFull());
            assertEquals(10, threadpool.getQueueSize());
            assertEquals(10, threadpool.getPoolSize());
            assertEquals(10, threadpool.getActiveCount());
            // 继续添加将被拒绝.
            try {
                threadpool.execute(new BlockingRunner());
                fail("the pool is full.");
            } catch (RejectedExecutionException ex) {
                // ignore
            }
            // 释放一个让后面的执行
            b[0].unblock();
            b[0] = new BlockingRunner();
            boolean accepted = false;
            for (int i = 0; i < 20 && !accepted; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    // ignore
                }
                try {
                    threadpool.execute(b[0]);
                    accepted = true;
                } catch (RejectedExecutionException ex) {
                    // ignore
                }
            }
            assertTrue(accepted);
        } finally {
            for (int i = 0; i < 10; i++) {
                if (b[i] != null) {
                    b[i].unblock();
                }
            }
            for (int i = 0; i < 10; i++) {
                if (b2[i] != null) {
                    b2[i].unblock();
                }
            }
        }
    }
    
    @Test
    public void testDeadLockEnqueueLoads() {
        threadpool = new DefaultThreadPool(100, 2, 1, 1,
            DEFAULT_DEQUEUE_TIMEOUT);
        //开启一个线程,每过10毫秒,执行一次,执行200次.执行完成一次计数器+1
        DeadLockThread dead = new DeadLockThread(threadpool, 200, 10L);

        checkDeadLock(dead);
    }
    private void checkDeadLock(DeadLockThread dead) {
        dead.start();
        checkCompleted(dead);
    }
    private void checkCompleted(DeadLockThread dead) {
        int oldCompleted = 0;
        int newCompleted = 0;
        int noProgressCount = 0;
        while (!dead.isFinished()) {
            newCompleted = dead.getWorkItemCompletedCount();
            //计数器在不断累加,线程运行正常,
            if (newCompleted > oldCompleted) {
                oldCompleted = newCompleted;
                noProgressCount = 0;
            } else {
                // 250毫秒内,计数器不变,说明部分线程出现死锁.
                if (oldCompleted != 0
                    && ++noProgressCount > 5) {
                    
                    fail("No reduction in threads in 1.25 secs: \n" 
                         + "oldCompleted: " + oldCompleted 
                         + "\nof " + dead.getWorkItemCount()); 
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ie) {
                // ignore
            }
        }
    }
    
    public interface Callback {
        void workItemCompleted(String name);
    }

    public class DeadLockThread extends Thread implements Callback {
        public static final long DEFAULT_WORK_TIME = 10L;
        public static final int DEFAULT_WORK_ITEMS = 200;

        DefaultThreadPool workqueue;
        int nWorkItems;
        int nWorkItemsCompleted;
        long worktime;
        long finishTime;
        long startTime;

        public DeadLockThread(DefaultThreadPool pool) {
            this(pool, DEFAULT_WORK_ITEMS, DEFAULT_WORK_TIME);
        }

        public DeadLockThread(DefaultThreadPool pool, int nwi) {
            this(pool, nwi, DEFAULT_WORK_TIME);
        }

        public DeadLockThread(DefaultThreadPool pool, int nwi, long wt) {
            workqueue = pool;
            nWorkItems = nwi;
            worktime = wt;
        }

        public synchronized boolean isFinished() {
            return nWorkItemsCompleted == nWorkItems;
        }

        @Override
        public synchronized void workItemCompleted(String name) {
            nWorkItemsCompleted++;
            if (isFinished()) {
                finishTime = System.currentTimeMillis();
            }
        }

        public int getWorkItemCount() {
            return nWorkItems;
        }

        public long worktime() {
            return worktime;
        }

        public synchronized int getWorkItemCompletedCount() {
            return nWorkItemsCompleted;
        }

        public long finishTime() {
            return finishTime;
        }

        public long duration() {
            return finishTime - startTime;
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();

            for (int i = 0; i < nWorkItems; i++) {
                try {
                    workqueue.execute(new TestRunner(String.valueOf(i), worktime, this), 100);
                } catch (RejectedExecutionException ex) {
                    // ignore
                }
            }
            while (!isFinished()) {
                try {
                    Thread.sleep(worktime);
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }
    }
    class BlockingRunner implements Runnable {

        private boolean unblocked;

        @Override
        public void run() {
            synchronized (this) {
                while (!unblocked) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {
                        // ignore
                    }
                }
            }
        }

        void unblock() {
            synchronized (this) {
                unblocked = true;
                notify();
            }
        }
    }
    
    class TestRunner implements Runnable{

       
        String name;
        long worktime;
        Callback callback;

        public TestRunner() {
            this("WI");
        }

        public TestRunner(String n) {
            this(n, DeadLockThread.DEFAULT_WORK_TIME);
        }

        public TestRunner(String n, long wt) {
            this(n, wt, null);
        }

        public TestRunner(String n, long wt, Callback c) {
            name = n;
            worktime = wt;
            callback = c;
        }

        @Override
        public void run() {
            try {
                try {
                    Thread.sleep(worktime);
                } catch (InterruptedException ie) {
                    // ignore
                    return;
                }
            } finally {
                if (callback != null) {
                    callback.workItemCompleted(name);
                }
            }
        }

        @Override
        public String toString() {
            return "[TestWorkItem:name=" + name + "]";
        }
        
    }

}
