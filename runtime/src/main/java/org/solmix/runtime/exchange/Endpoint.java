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

import java.io.Closeable;
import java.util.List;
import java.util.Map;

import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.interceptor.InterceptorProvider;


/**
 * 负责接收消息的端点
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月11日
 */

public interface Endpoint extends InterceptorProvider,Map<String,Object>
{
    
    EndpointInfo getEndpointInfo();
    
    Protocol getBinding();
    
    Service getService();
    
    void addCleanupHook(Closeable c);
    List<Closeable> getCleanupHooks();

    /**
     * @return
     */
    Processor getOutFaultProcessor();
    
    void setOutFaultProcessor(Processor p);
    
    Processor getInFaultProcessor();
    
    void setInFaultProcessor(Processor p);

}
