/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.runtime.exchange.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.solmix.runtime.bean.Configurable;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.dataformat.DataFormat;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.model.NamedID;
import org.solmix.runtime.exchange.model.ServiceInfo;
import org.solmix.runtime.interceptor.support.InterceptorProviderSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月25日
 */

public class DefaultService extends InterceptorProviderSupport implements
    Service, Configurable {

    private static final long serialVersionUID = -6341561816353503936L;

    private final List<ServiceInfo> serviceInfos;

    private DataFormat dataFormat;

    private Executor executor;

    private Invoker invoker;

    private final Map<NamedID, Endpoint> endpoints = new HashMap<NamedID, Endpoint>();

    public DefaultService() {
        this((ServiceInfo) null);
    }

    /**
     * @param info
     */
    public DefaultService(ServiceInfo info) {
        serviceInfos = new ArrayList<ServiceInfo>();
        if (info != null) {
            serviceInfos.add(info);
        }
    }

    @Override
    public String getConfigureName() {
        return getServiceName().getName();
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public Map<NamedID, Endpoint> getEndpoints() {
        return endpoints;
    }

    @Override
    public EndpointInfo getEndpointInfo(NamedID eid) {
        EndpointInfo ef = getServiceInfo().getEndpoint(eid);
        if (ef != null) {
            return ef;
        }
        return null;
    }

    @Override
    public DataFormat getDataFormat() {
        return dataFormat;
    }

    @Override
    public void setDataFormat(DataFormat s) {
        this.dataFormat = s;
    }

    /**   */
    @Override
    public ServiceInfo getServiceInfo() {
        return serviceInfos.get(0);
    }
    
    @Override
    public List<ServiceInfo> getServiceInfos() {
        return serviceInfos;
    }

    /**   */
    public void setServiceInfo(ServiceInfo serviceInfo) {
        if (serviceInfo != null) {
            serviceInfos.add(serviceInfo);
        }
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Service#getServiceName()
     */
    @Override
    public NamedID getServiceName() {
        return getServiceInfo().getName();
    }
    @Override
    public String toString() {
        return "[DefaultService " + getServiceName() + "]";
    }
}
