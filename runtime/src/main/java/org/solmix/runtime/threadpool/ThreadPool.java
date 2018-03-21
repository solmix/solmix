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

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;


/**
 * 线程池
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月18日
 */

public interface ThreadPool extends Executor {

    
    String getName();
    
    /**
     * 立即执行,如果被拒绝,那么等待timeout毫秒
     * @param work
     * @param timeout
     */
    void execute(Runnable runnable, long timeout);
    /**
     * 
     * @param processRemainingWorkItems 为true,等待所有活动的任务执行完成后返回,否则直接返回.
     */
    void shutdown(boolean processRemainingWorkItems);
    
    boolean isShutdown();

   <T> Future<T> submit(Callable<T> call);
}
