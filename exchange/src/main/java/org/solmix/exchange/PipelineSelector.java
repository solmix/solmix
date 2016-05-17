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

package org.solmix.exchange;

/**
 * 传输管道选择器，提供获取pipeline的策略。
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月17日
 */

public interface PipelineSelector {

    /**
     * 在消息通过切面之前准备pipeline<br>
     * 可用于为消息提前准备pipeline.
     * @param message
     */
    void prepare(Message message);

    /**
     * 需要的时候选择pipeline<br>
     * 懒加载在select时才创建pipeline.
     * @param message
     * @return
     */
    Pipeline select(Message message);

    /**
     * 在消息交换结束时调用<br>
     * 可以在complete提供销毁pipeline策略.
     * @param exchange
     */
    void complete(Exchange exchange);

    Endpoint getEndpoint();

    /**
     * pipelineSelector 对应的{@link org.solmix.exchange.Endpoint Endpoint}
     * @param endpoint
     */
    void setEndpoint(Endpoint endpoint);

}
