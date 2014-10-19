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
package org.solmix.runtime.exchange;

import org.solmix.runtime.Container;


/**
 * 信息交换上下文(ME)<p>
 * 消息从消费者发送后,在消息处理流转过程中Exchange作为消息的上下文容器.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月11日
 */

public interface Exchange extends StringTypeMap
{

    /**
     * 返回输入消息
     * 
     * @return
     */
    Message getIn();
    
    /**
     * 设置输入消息
     * 
     * @param in
     */
    void setIn(Message in);
    
    /**
     * 返回输出消息
     * @return
     */
    Message getOut();
    
    /**
     * 设置输入消息
     * @param out
     */
    void setOut(Message out);
    
    /**
     * 返回输入异常消息
     * 
     * @return
     */
    Message getInFault();
    
    /**
     * 设置输入异常
     * @param m
     */
    void setInFault(Message m);

    /**
     * 返回输出异常
     * @return
     */
    Message getOutFault();
    /**
     * 设置输出异常
     * @param m
     */
    void setOutFault(Message m);
    
    /**
     * 返回关联的传输管道
     * @return
     */
    Pipeline getPipeline(Message message);
    
    /**
     * 设置传输管道
     * @param pipeline
     */
    void setPipeline(Pipeline pipeline);
    
    /**
     * 为真表示为单向消息无须返回消息,为假表示为双向消息有返回消息
     * @return
     */
    boolean isOneWay();
    /**
     * 为真表示为单向消息无须返回消息,为假表示为双向消息有返回消息
     * @param b
     */
    void setOneWay(boolean b);
    
    /**
     * 为真表示为同步消息交换,为假为异步消息交换
     * @return
     */
    boolean isSync();
    
    /**
     * 为真表示为同步消息交换,为假为异步消息交换
     * @param b
     */
    void setSync(boolean b);
    
    
    Container getContainer();
    
    Endpoint getEndpoint();
}
