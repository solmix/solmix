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
package org.solmix.runtime.exchange;

import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.solmix.runtime.interceptor.InterceptorChain;


/**
 * 消息
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月4日
 */

public interface Message extends StringTypeMap {
    String  ONEWAY            = Message.class.getName() + ".ONEWAY";;

    /** 附件key,值类型:Map<String, DataHandler>   */
    String ATTACHMENTS        = Message.class.getName() + ".ATTACHMENTS";

    /**
     * 在消息中存放拦截器的key,用于对每个不同的消息做添加自定义拦截器.
     */
    /**输入拦截器key,值类型: Collection<Interceptor<? extends Message>>  */
    String IN_INTERCEPTORS = Message.class.getName() + ".IN_INTERCEPTORS";
    
    /**输出拦截器key,值类型: Collection<Interceptor<? extends Message>> */
    String OUT_INTERCEPTORS = Message.class.getName() + ".OUT_INTERCEPTORS";
    
    /**输入错误拦截器key,值类型: Collection<Interceptor<? extends Message>> */
    String FAULT_IN_INTERCEPTORS = Message.class.getName() + ".FAULT_IN_INTERCEPTORS";
    
    /**输出错误拦截器key,值类型: Collection<Interceptor<? extends Message>> */
    String FAULT_OUT_INTERCEPTORS = Message.class.getName() + ".FAULT_OUT_INTERCEPTORS";
    
    /**
     *拦截器提供者,值类型:Collection<InterceptorProvider> 
     */
    String INTERCEPTOR_PROVIDERS = Message.class.getName() + ".INTERCEPTOR_PROVIDER";

    /**存储invocation参数的key,值类型Map<String,Object>    */
    String INVOCATION_CONTEXT =  Message.class.getName() + ".INVOCATION_CONTEXT";
    
    /**是否为请求消息,值类型Boolean    */
    String REQUEST_MESSAGE = Message.class.getName() + ".REQUEST_MESSAGE"; 

    /** 类型   */
    String CONTENT_TYPE = "Content-Type";
    
    /**信息中指定的地址,值类型String    */
    String ENDPOINT_ADDRESS = Message.class.getName() + ".ENDPOINT_ADDRESS";
    


    /**
     * 返回消息ID
     * @return
     */
    String getId();
    /**
     * 设置消息ID
     * @param id
     */
    void setId(String id);
    
    /**
     * 返回该消息所处拦截链的快照,便于动态添加修改.
     * @return
     */
    InterceptorChain getInterceptorChain();
    
    /**
     * 设置拦截链
     * @param chain
     */
    void setInterceptorChain(InterceptorChain chain);
    
    
    Exchange getExchange();
    
    void setExchange(Exchange e);
    /**
     * 返回指定类型的消息体
     * @param format
     * @return
     */
    <T> T getContent(Class<T> type);
    
    /**
     * 设置消息体.对于请求消息对应请求数据,对于响应消息对应返回数据.
     * 
     * @param type
     * @param content
     */
    <T> void setContent(Class<T> type, Object content);
    
    /**
     * 返回消息体类型集合.
     * @return
     */
    Set<Class<?>> getContentType();
    
    /**
     * 删除指定类型的数据
     * @param type
     */
    <T> void removeContent(Class<T> type);
    /**
     * 根据附件标示返回附件处理Handler.
     * 
     * @param id
     * @return
     */
    DataHandler getAttachment(String id);

    Set<String> getAttachmentNames();
    
    void removeAttachment(String id);

    /**
     * 往消息中添加附件
     * @param id        存储附件的主键
     * @param content   处理附件的Handler
     */
    void addAttachment(String id, DataHandler content);

    /**
     * 返回所有的附件
     *
     * @return the attachments in a map or <tt>null</tt>
     */
    Map<String, DataHandler> getAttachments();

    /**
     * 设置消息中附件
     */
    void setAttachments(Map<String, DataHandler> attachments);

    /**
     *返回该消息中是否包含附件
     *
     * @return <tt>true</tt> if this message has any attachments.
     */
    boolean hasAttachments();
}
