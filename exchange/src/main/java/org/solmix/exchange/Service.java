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

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.solmix.exchange.dataformat.DataFormat;
import org.solmix.exchange.interceptor.InterceptorProvider;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ServiceInfo;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月12日
 */

public interface Service extends Map<String, Object>, InterceptorProvider {

    Invoker getInvoker();
    
    NamedID getServiceName();

    void setInvoker(Invoker invoker);

    Map<NamedID, Endpoint> getEndpoints();

    EndpointInfo getEndpointInfo(NamedID eid);
    
    DataFormat getDataFormat();
    
    void setDataFormat(DataFormat df);
    
    Executor getExecutor();
    
    List<ServiceInfo> getServiceInfos();

    void setExecutor(Executor executor);

    /**
     * 
     */
    ServiceInfo getServiceInfo();

}
