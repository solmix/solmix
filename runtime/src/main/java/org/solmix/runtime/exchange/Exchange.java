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
 * 信息交换上下文.
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
    Message getInException();
    
    /**
     * 设置输入异常
     * @param m
     */
    void setInException(Message m);

    /**
     * 返回输出异常
     * @return
     */
    Message getOutException();
    /**
     * 设置输出异常
     * @param m
     */
    void setOutException(Message m);
    
    /**
     * 返回关联的传输管道
     * @return
     */
    Pipeline getPipeline();
    
    /**
     * 设置传输管道
     * @param pipeline
     */
    void setPipeline(Pipeline pipeline);
    
    boolean isOneWay();
    void setOneWay(boolean b);
    
    boolean isSync();
    void setSync(boolean b);
    
    Container getContainer();
    
    Transceiver getTransceiver();
}
