/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.runtime.interceptor;

import java.util.Collection;
import java.util.ListIterator;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Processor;


/**
 * 拦截链,支持拦截器的遍历执行和异常回退.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月11日
 */

public interface InterceptorChain extends Iterable<Interceptor<? extends Message>>
{
    enum State {
        PAUSED,
        SUSPENDED,
        EXECUTING,
        COMPLETE,
        ABORTED,
    };
    /**
     * 添加拦截器
     * @param i
     */
    void add(Interceptor<? extends Message> i);
    
    /**
     *  批量添加拦截器
     * @param i
     */
    void add(Collection<Interceptor<? extends Message>> i);
    
    /**
     * 从拦截链中移除拦截器
     * @param i
     */
    void remove(Interceptor<? extends Message> i);
    
    /**
     * 拦截处理消息,正常完成返回true,异常返回false.
     * @param message
     * @return
     */
    boolean doIntercept(Message message);
    
    /**
     * 从某拦截器后开始执行消息拦截.
     * @param message
     * @param inteceptorId  拦截器ID
     * @return
     */
    boolean doInterceptAfter(Message message,String inteceptorId);
    
    /**
     * 在某拦截器开始执行消息拦截.
     * @param message
     * @param inteceptorId  拦截器ID
     * @return
     */
    boolean doInterceptAt(Message message,String inteceptorId);
    
    /**
     * 支持逆向遍历
     * @return
     */
    ListIterator<Interceptor<? extends Message>> listIterator();
    
    /**
     * 拦截链状态
     * @return
     */
    State getState();
    
    /**
     * 重置状态,包括State和迭代器状态
     */
    void reset();
    
    Processor getFaultProcessor();
    
    void setFaultProcessor(Processor processor);
    /**
     * Pauses the current chain.   When the stack unwinds, the chain will just
     * return from the doIntercept method normally.
     */
    void pause();

    /**
     * Suspends the current chain.  When the stack unwinds, the chain back up
     * the iterator by one (so on resume, the interceptor that called pause will
     * be re-entered) and then throw a SuspendedInvocationException to the caller
     */
    void suspend();
    
    /**
     * Resumes the chain.  The chain will use the current thread to continue processing
     * the last message that was passed into doIntercept
     */
    void resume();

    /**
     * If the chain is marked as paused, this will JUST mark the chain as
     * in the EXECUTING phase.   This is useful if an interceptor pauses the chain,
     * but then immediately decides it should not have done that.   It can unpause
     * the chain and return normally and the normal processing will continue.
     */
    void unpause();
}
