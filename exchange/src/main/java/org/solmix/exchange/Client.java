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

import java.util.Map;
import java.util.concurrent.Executor;

import org.solmix.exchange.interceptor.InterceptorProvider;
import org.solmix.exchange.model.OperationInfo;
import org.solmix.runtime.Container;

/**
 * C/S消息交互模式
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月11日
 */

public interface Client extends InterceptorProvider, Processor {

    String REQUEST_CONTEXT = "RequestContext";

    String RESPONSE_CONTEXT = "ResponseContext";

    String KEEP_PIPELINE_ALIVE = "KeepPipelineAlive";

    void destroy();

    Container getContainer();

    Endpoint getEndpoint();
    
    Pipeline getPipeline();
    
    PipelineSelector getPipelineSelector();
    
    void setPipelineSelector(PipelineSelector selector);

    void setExecutor(Executor executor);

    Map<String, Object> getRequestContext();
   
    Map<String, Object> getResponseContext();
    
    Object[] invoke(OperationInfo oi,
                    Object... params) throws Exception;
    
    Object[] invoke(OperationInfo oi,
                    Object[] params,
                    Map<String, Object> context) throws Exception;
    
    Object[] invoke(OperationInfo oi,
                    Object[] params,
                    Exchange exchange) throws Exception;
    
    Object[] invoke(OperationInfo oi,
                    Object[] params,
                    Map<String, Object> context,
                    Exchange exchange) throws Exception;

    void invoke(ClientCallback callback, 
                OperationInfo oi, 
                Object... params) throws Exception;
    
    void invoke(ClientCallback callback, 
                OperationInfo oi, 
                Object[] params,
                Map<String, Object> context) throws Exception;
    
    void invoke(ClientCallback callback, 
                OperationInfo oi, 
                Object[] params,
                Map<String, Object> context,
                Exchange exchange) throws Exception;
    
    void invoke(ClientCallback callback, 
                OperationInfo oi, 
                Object[] params,
                Exchange exchange) throws Exception;

    
}
