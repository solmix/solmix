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

package org.solmix.runtime.exchange.support;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.EndpointException;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.ProtocolFactoryManager;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.exchange.processor.InFaultChainProcessor;
import org.solmix.runtime.exchange.processor.OutFaultChainProcessor;
import org.solmix.runtime.extension.ExtensionException;
import org.solmix.runtime.interceptor.phase.PhasePolicy;
import org.solmix.runtime.interceptor.support.ClientFaultConverter;
import org.solmix.runtime.interceptor.support.InterceptorProviderAttrSupport;
import org.solmix.runtime.interceptor.support.MessageSenderInterceptor;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月17日
 */

public class DefaultEndpoint extends InterceptorProviderAttrSupport implements
    Endpoint {

    private static final long serialVersionUID = -7789429134686264716L;

    private final EndpointInfo endpointInfo;

    private Protocol protocol;

    private List<Closeable> cleanupHooks;

    private Container container;

    private final Service service;

    private Processor inFaultProcessor;

    private Processor outFaultProcessor;
    
    private  PhasePolicy phasePolicy;
    
    private Executor executor;

    public DefaultEndpoint(Container container, Service s, EndpointInfo ed,
        PhasePolicy phasePolicy) throws EndpointException {
        Assert.isNotNull(ed);
        this.phasePolicy = phasePolicy;
        if (container == null) {
            container = ContainerFactory.getThreadDefaultContainer();
        } else {
            this.container = container;
        }
        this.service = s;
        this.endpointInfo = ed;
        
        createProtocol(endpointInfo.getProtocol());

        inFaultProcessor = new InFaultChainProcessor(container, phasePolicy);
        outFaultProcessor = new OutFaultChainProcessor(container, phasePolicy);
        
        getInFaultInterceptors().add(new ClientFaultConverter());
        getOutInterceptors().add(new MessageSenderInterceptor());
        getOutFaultInterceptors().add(new MessageSenderInterceptor());

    }

    

    @Override
    public EndpointInfo getEndpointInfo() {
        return endpointInfo;
    }

    protected void createProtocol(ProtocolInfo pi) throws EndpointException {
        if (pi != null) {
            String pid = pi.getProtocolId();
            ProtocolFactory protocolFactory;
            try {
                protocolFactory = container.getExtension(
                    ProtocolFactoryManager.class).getProtocolFactory(pid);
                if (protocolFactory == null) {
                    throw new EndpointException("No found protocol for " + pid);
                }
                protocol = protocolFactory.createProtocol(pi);
            } catch (ExtensionException e) {
                throw new EndpointException(e);
            }
        }
    }
    
    /**   */
    @Override
    public Executor getExecutor() {
        return executor == null ? service.getExecutor() : executor;
    }
    
    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Endpoint#getProtocol()
     */
    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Endpoint#getService()
     */
    @Override
    public Service getService() {
        return service;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Endpoint#addCleanupHook(java.io.Closeable)
     */
    @Override
    public void addCleanupHook(Closeable c) {
        if (cleanupHooks == null) {
            cleanupHooks = new CopyOnWriteArrayList<Closeable>();
        }
        cleanupHooks.add(c);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Endpoint#getCleanupHooks()
     */
    @Override
    public List<Closeable> getCleanupHooks() {
        if (cleanupHooks == null) {
            return Collections.emptyList();
        }
        return cleanupHooks;
    }

    /**   */
    @Override
    public Processor getInFaultProcessor() {
        return inFaultProcessor;
    }

    /**   */
    @Override
    public void setInFaultProcessor(Processor inFaultProcessor) {
        this.inFaultProcessor = inFaultProcessor;
    }

    /**   */
    @Override
    public Processor getOutFaultProcessor() {
        return outFaultProcessor;
    }

    /**   */
    @Override
    public void setOutFaultProcessor(Processor outFaultProcessor) {
        this.outFaultProcessor = outFaultProcessor;
    }

    @Override
    public int hashCode() {
        return endpointInfo.hashCode();
    }

    
    /**   */
    @Override
    public PhasePolicy getPhasePolicy() {
        return phasePolicy;
    }
    
}
