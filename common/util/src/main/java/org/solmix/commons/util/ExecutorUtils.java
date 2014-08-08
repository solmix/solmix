/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.commons.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月15日
 */

public class ExecutorUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ExecutorUtils.class);
    private static final ThreadPoolExecutor shutdownExecutor = new ThreadPoolExecutor(0, 1,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(100),
        new NamedThreadFactory("Close-ExecutorService-Timer", true)); 
    /**
     * @param executor
     * @param timeout
     */
    public static void gracefulShutdown(ExecutorService executor, int timeout) {
        if (!(executor instanceof ExecutorService) || isShutdown(executor)) {
            return;
        }
        final ExecutorService es = executor;
        try {
            es.shutdown(); // Disable new tasks from being submitted
        } catch (SecurityException ex2) {
            return ;
        } catch (NullPointerException ex2) {
            return ;
        }
        try {
            if(! es.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException ex) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
        if (!isShutdown(es)){
            newThreadToCloseExecutor(es);
        }
        
    }
    public static boolean isShutdown(Executor executor) {
        if (executor instanceof ExecutorService) {
            if (((ExecutorService) executor).isShutdown()) {
                return true;
            }
        }
        return false;
    }
    private static void newThreadToCloseExecutor(final ExecutorService es) {
        if (!isShutdown(es)) {
            shutdownExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i=0;i<1000;i++){
                            es.shutdownNow();
                            if (es.awaitTermination(10, TimeUnit.MILLISECONDS)){
                                break;
                            }
                        }
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    } 
                }
            });
        }
    }

    /**
     * @param executor
     * @param i
     */
    public static void shutdownNow(ExecutorService executor, int timeout) {
        if (!(executor instanceof ExecutorService) || isShutdown(executor)) {
            return;
        }
        final ExecutorService es = executor;
        try {
            es.shutdownNow();
        } catch (SecurityException ex2) {
            return ;
        } catch (NullPointerException ex2) {
            return ;
        }
        try {
            es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        if (!isShutdown(es)){
            newThreadToCloseExecutor(es);
        }
        
    }

}
