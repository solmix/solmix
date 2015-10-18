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

import java.util.Collection;
import java.util.Set;

import org.solmix.commons.collections.StringTypeMap;
import org.solmix.exchange.interceptor.InterceptorChain;


/**
 * 代表一个消息载体
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
    
    
    String EVENT_MESSAGE = Message.class.getName() + ".EVENT_MESSAGE"; 

    /** 内容类型定义,值类型String,比如:text/xml   */
    String CONTENT_TYPE = "Content-Type";
    
    String CONTENT_LENGTH = "Content-Length";

    /**信息中指定的地址,值类型String    */
    String ENDPOINT_ADDRESS = Message.class.getName() + ".ENDPOINT_ADDRESS";
    
    String PATH_INFO = Message.class.getName() + ".PATH_INFO";

    String QUERY_STRING = Message.class.getName() + ".QUERY_STRING";

    
    /**消息响应code,值类型int   */
    String RESPONSE_CODE = Message.class.getName() + ".RESPONSE_CODE";

    String PROTOCOL_HEADERS = Message.class.getName() + ".PROTOCOL_HEADERS";

    /**表示为空返回消息,值类型Boolean    */
    String EMPTY_PARTIAL_RESPONSE_MESSAGE = Message.class.getName() + ".EMPTY_PARTIAL_RESPONSE_MESSAGE";;
    
    /**字符集,值类型String    */
    String ENCODING = Message.class.getName() + ".ENCODING";

    /** 客户端连接超时,值类型 long string */
    String CONNECTION_TIMEOUT = Message.class.getName() + ".CONNECTION_TIMEOUT";

    /** 客户端接收超时,值类型 long string */
    String RECEIVE_TIMEOUT = Message.class.getName() + ".RECEIVE_TIMEOUT";

    String REQUEST_URI = Message.class.getName() + ".REQUEST_URI";
    String REQUEST_URL = Message.class.getName() + ".REQUEST_URL";

    String ACCEPT_CONTENT_TYPE =  "Accept";

    String BASE_PATH = Message.class.getName() + ".BASE_PATH";;

    String ERROR_MESSAGE = Message.class.getName() + ".ERROR_MESSAGE";
    
    String PARTIAL_RESPONSE_MESSAGE = Message.class.getName() + ".PARTIAL_RESPONSE_MESSAGE";

    String OPERATION = Message.class.getName() + ".OPERATION";
    
    /**
     * 判断是否为请求消息<br>
     *<li>true:请求
     * <li>false:响应
     * @return
     */
    boolean isRequest();
    
    /**
     *  设置是否为请求消息<br>
     * <li>true:请求
     * <li>false:响应
     * @param isRequest
     */
    void setRequest(boolean isRequest);
    
    /**
     * 判断是否为消息交换模型中的输入消息.<br>
     * <li>true:输入
     * <li>false:输出
     * @return
     */
    boolean isInbound();
    /**
     * 设置是否为消息交换模型中的输入消息.<br>
     * <li>true:输入
     * <li>false:输出
     *  @param isRequest
     */
    void setInbound(boolean isRequest);
    /**
     * 返回消息ID
     * @return
     */
    long getId();
    /**
     * 设置消息ID
     * @param id
     */
    void setId(long id);
    
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
    Set<Class<?>> getContentTypes();
    
    /**
     * 删除指定类型的数据
     * @param type
     */
    <T> void removeContent(Class<T> type);


    /**
     * 返回所有的附件
     *
     * @return the attachments in a map or <tt>null</tt>
     */
    Collection<Attachment> getAttachments();

    /**
     * 设置消息中附件
     */
    void setAttachments(Collection<Attachment> attachments);


}
