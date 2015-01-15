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
package org.solmix.runtime.exchange.support;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.EndpointException;
import org.solmix.runtime.exchange.PipelineSelector;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.TransporterFactory;
import org.solmix.runtime.exchange.model.NamedID;
import org.solmix.runtime.interceptor.support.InterceptorProviderSupport;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月13日
 */

public abstract class AbstractEndpointFactory extends InterceptorProviderSupport
{

    private static final long serialVersionUID = -2130719449925733112L;
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEndpointFactory.class);

    protected Container container;
    
    protected ProtocolFactory protocolFactory;
    
    protected String transporter;
    
    protected String protocol;
    
    protected TransporterFactory transporterFactory;
    
    protected PipelineSelector pipelineSelector;
    
    protected String address;
    
    protected Map<String, Object> properties;
    
    protected NamedID serviceName;
    
    protected NamedID endpointName;
    
    private Object configObject;
    
    protected abstract Endpoint createEndpoint() throws  EndpointException;

    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Container getContainer() {
        return container;
    }
    
    public Container getContainer(boolean created) {
        if (container == null && created) {
            container = ContainerFactory.getThreadDefaultContainer(created);
        }
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
    
    /**   */
    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    /**   */
    public void setProtocolFactory(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }
    
    /**   */
    public TransporterFactory getTransporterFactory() {
        return transporterFactory;
    }
    
    /**   */
    public void setTransporterFactory(TransporterFactory transporterFactory) {
        this.transporterFactory = transporterFactory;
    }

    /**   */
    public PipelineSelector getPipelineSelector() {
        return pipelineSelector;
    }

    /**   */
    public void setPipelineSelector(PipelineSelector pipelineSelector) {
        this.pipelineSelector = pipelineSelector;
    }

    /**   */
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**   */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    protected void initializeAnnotationInterceptors(Endpoint ep, Class<?> cls) {
        initializeAnnotationInterceptors(ep, new Class<?>[] {cls});
    }

    protected void initializeAnnotationInterceptors(Endpoint ep, Class<?> ... cls) {
//        AnnotationInterceptors provider = new AnnotationInterceptors(cls);
//        if (initializeAnnotationInterceptors(provider, ep)) {
//            LOG.trace("Added annotation based interceptors and features");
//        }
    }
    
    /**   */
    public NamedID getServiceName() {
        return serviceName;
    }
    
    /**   */
    public void setServiceName(NamedID serviceName) {
        this.serviceName = serviceName;
    }
    
    /**   */
    public NamedID getEndpointName() {
        return endpointName;
    }

    /**   */
    public void setEndpointName(NamedID endpointName) {
        this.endpointName = endpointName;
    }

    /**   */
    public String getTransporter() {
        return transporter;
    }
    
    /**   */
    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }
    
    /**   */
    public String getProtocol() {
        return protocol;
    }
    
    /**   */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**   */
    public Object getConfigObject() {
        return configObject;
    }


    /**   */
    public void setConfigObject(Object configObject) {
        this.configObject = configObject;
    } 
    
}