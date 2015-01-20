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

package org.solmix.runtime.interceptor.phase;

import java.util.Collection;
import java.util.Set;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.Interceptor;

/**
 * 分阶拦截器.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月12日
 */

public interface PhaseInterceptor<T extends Message> extends Interceptor<T> {

    /**
     * 该节点在这些节点的后面
     * 
     * @return
     */
    Set<String> getAfter();

    /**
     * 该节点在这些节点的前面
     * 
     * @return
     */
    Set<String> getBefore();

    /**
     * 拦截器Id,在一个拦截链中唯一
     * 
     * @return
     */
    String getId();

    /**
     * 该拦截器所处阶段
     * 
     * @return
     */
    String getPhase();

    /**
     * 当该拦截器被添加时,还需额外添加的拦截器.
     * 
     * @return
     */
    Collection<PhaseInterceptor<? extends Message>> extInterceptors();
}
