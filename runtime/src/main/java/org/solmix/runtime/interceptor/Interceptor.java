/**
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

import org.solmix.runtime.exchange.Message;

/**
 * 消息拦截器
 * 
 * <pre>
 *       +----------------------------------------------+----------+   
 *       |          |        Interceptor     1          |          |    
 *       +----------+-----------------------------------+----------+ 
 *                  |                                  /|\
 *             handleMessae()                     handleFault()
 *                 \|/                                  |
 *       +----------------------------------------------+----------+   
 *       |          |        Interceptor     2                     |    
 *       +----------+-----------------------------------+----------+ 
 *                             .       |                 |
 *                  |          .       |               发生异常              
 *                  |          .       |---------------------------+       .                                             
 *                 \|/         .       |    
 *       +----------------------------------------------+----------+   
 *       |          |        Interceptor     N                     |    
 *       +----------+-----------------------------------+----------+
 * 
 * </pre>
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月4日
 */

public interface Interceptor<T extends Message> {

    /**
     * 处理拦截的消息
     * 
     * @param message
     * @throws InterceptorException
     */
    void handleMessage(T message) throws Fault;

    /**
     * 回退处理异常
     * 
     * @param message
     */
    void handleFault(T message);
}
