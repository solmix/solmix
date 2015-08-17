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
package org.solmix.exchange.interceptor.phase;


/**
 * 定义拦截链中不同的步骤.
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月12日
 */

public class Phase implements Comparable<Object>
{
    /**接收*/
    public static final String RECEIVE = "receive";
    
    /**流预处理*/
    public static final String PRE_STREAM = "pre-stream";
    /**流预处理结束*/
    public static final String PRE_STREAM_ENDING = "pre-stream-ending";
    /**流读取*/
    public static final String READ = "read";
    
    /**协议预处理*/
    public static final String PRE_PROTOCOL = "pre-protocol";
    public static final String PRE_PROTOCOL_FRONTEND = "pre-protocol-frontend";
    public static final String PRE_PROTOCOL_ENDING = "pre-protocol-ending";
    public static final String USER_PROTOCOL = "user-protocol";
    public static final String USER_PROTOCOL_ENDING = "user-protocol-ending";
    public static final String POST_PROTOCOL = "post-protocol";
    public static final String POST_PROTOCOL_ENDING = "post-protocol-ending";
    /**编码*/
    public static final String PRE_ENCODE = "pre-encode";
    public static final String ENCODE = "encode";
    public static final String POST_ENCODE = "post-encode";
    public static final String ENCODE_ENDING = "encode-ending";
    /**业务逻辑*/
    public static final String PRE_LOGICAL = "pre-logical";
    public static final String PRE_LOGICAL_ENDING = "pre-logical-ending";
    public static final String USER_LOGICAL = "user-logical";
    public static final String USER_LOGICAL_ENDING = "user-logical-ending";
    public static final String POST_LOGICAL = "post-logical";
    public static final String POST_LOGICAL_ENDING = "post-logical-ending";
    
    /**服务调用*/
    public static final String PRE_INVOKE = "pre-invoke";
    public static final String INVOKE = "invoke";
    public static final String POST_INVOKE = "post-invoke";
    
    public static final String SETUP = "setup";
    public static final String SETUP_ENDING = "setup-ending";
    
    
   
    public static final String PREPARE_SEND = "prepare-send";
    public static final String PREPARE_SEND_ENDING = "prepare-send-ending";
  
    public static final String USER_STREAM = "user-stream";
    public static final String USER_STREAM_ENDING = "user-stream-ending";
    public static final String POST_STREAM = "post-stream";
    public static final String POST_STREAM_ENDING = "post-stream-ending";
    public static final String WRITE = "write";
    public static final String WRITE_ENDING = "write-ending";
    public static final String SEND = "send";
    public static final String SEND_ENDING = "send-ending";
   
    
   
    public static final String PROTOCOL = "protocol";
    public static final String PRE_DECODE = "pre-decode";
    public static final String DECODE = "encode";
    public static final String POST_DECODE = "post-decode";
  
    private String name;
    private int priority;
    
    public Phase() {
    }
    
    public Phase(String n, int p) {
        this.name = n;
        this.priority = p;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String n) {
        this.name = n;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int p) {
        this.priority = p;
    }
    
    @Override
    public int hashCode() {
        return priority;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Phase)) {
            return false;
        }
        Phase p = (Phase)o;
        
        return p.priority == priority
            && p.name.equals(name);
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Phase) {
            Phase p = (Phase)o;
            
            if (priority == p.priority) {
                return name.compareTo(p.name); 
            }
            return priority - p.priority;
        }
        return 1;
    }
    
    @Override
    public String toString() {
        return "Phase(" + getName() + ")";
    }

}
