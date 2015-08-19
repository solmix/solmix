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

package org.solmix.exchange.interceptor.support;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.PipelineSelector;
import org.solmix.exchange.Protocol;
import org.solmix.exchange.Service;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.Interceptor;
import org.solmix.exchange.interceptor.InterceptorChain;
import org.solmix.exchange.interceptor.InterceptorProvider;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseChainCache;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorChain;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.exchange.support.PreexistingPipelineSelector;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class OutgoingChainInterceptor extends PhaseInterceptorSupport<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(OutgoingChainInterceptor.class);
    private PhaseChainCache chainCache = new PhaseChainCache();
    public OutgoingChainInterceptor() {
        // in phase 最后一步
        super(Phase.POST_INVOKE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Exchange ex = message.getExchange();
        Message out = ex.getOut();
        if (out != null) {
            getBackPipeline(out);
            InterceptorChain chian = out.getInterceptorChain();
            if (chian == null) {
                chian = getChain(ex, chainCache);
                out.setInterceptorChain(chian);
            }
            chian.doIntercept(out);
        }
    }

    protected static Pipeline getBackPipeline(Message message) {
        Pipeline pipeline = null;
        Exchange ex = message.getExchange();
        if(ex.getPipeline(message)==null
            && ex.get(Transporter.class)!=null){
            try {
                pipeline = ex.get(Transporter.class).getBackPipeline(ex.getIn());
                ex.put(PipelineSelector.class, new PreexistingPipelineSelector(pipeline,ex.getEndpoint()));
            } catch (IOException e) {
            }
        }
        return pipeline;
    }
    
    public static InterceptorChain getOutInterceptorChain(Exchange ex) {
        Endpoint edp= ex.getEndpoint();
        PhasePolicy policy=edp.getPhasePolicy();
        Protocol protocol= edp.getProtocol();
        Service service = edp.getService();
        PhaseInterceptorChain chain = new PhaseInterceptorChain(policy.getOutPhases());
        
        List<Interceptor<? extends Message>> l1=  edp.getOutInterceptors();
        if(LOG.isTraceEnabled()){
            LOG.trace("Interceptors contributed by endpoint: " + l1);
        }
        chain.add(l1);
        if(protocol!=null){
            l1=  protocol.getOutInterceptors();
            if(LOG.isTraceEnabled()){
                LOG.trace("Interceptors contributed by protocol: " + l1);
            }
            chain.add(l1);
        }
        if(service!=null){
            l1=service.getOutInterceptors();
            if(LOG.isTraceEnabled()){
                LOG.trace("Interceptors contributed by service: " + l1);
            }
            chain.add(l1);
        }
        addToChain(chain,ex.getIn());
        addToChain(chain,ex.getOut());
        chain.setFaultProcessor(edp.getOutFaultProcessor());
        return chain;
        
    }
    
    @SuppressWarnings("unchecked")
    private static void addToChain(InterceptorChain chain, Message m) {
        Collection<InterceptorProvider> providers = (Collection<InterceptorProvider>) m.get(Message.INTERCEPTOR_PROVIDERS);
        if (providers != null) {
            for (InterceptorProvider p : providers) {
                chain.add(p.getOutInterceptors());
            }
        }
        Interceptor<Message> is = (Interceptor<Message>) m.get(Message.OUT_INTERCEPTORS);
        if (is != null) {
            chain.add(is);
        }
    }
    private static PhaseInterceptorChain getChain(Exchange ex, PhaseChainCache chainCache) {
        Endpoint edp = ex.getEndpoint();
        Protocol ptl = edp.getProtocol();
        Service service = edp.getService();
        PhasePolicy policy = edp.getPhasePolicy();
        List<Interceptor<? extends Message>> i2 = service.getOutInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors contributed by service: " + i2);
        }
        List<Interceptor<? extends Message>> i3 = edp.getOutInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors contributed by endpoint: " + i3);
        }
        List<Interceptor<? extends Message>> i4 = null;
        if (ptl != null) {
            i4 = ptl.getOutInterceptors();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Interceptors contributed by protocol: " + i4);
            }
        }
        List<Interceptor<? extends Message>> i5 = null;
        if (service.getDataProcessor() instanceof InterceptorProvider) {
            i5 = ((InterceptorProvider)service.getDataProcessor()).getOutInterceptors();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Interceptors contributed by dataprocessor: " + i5);
            }
            if (i4 == null) {
                i4 = i5;
                i5 = null;
            }
        }
        PhaseInterceptorChain chain = chainCache.get(policy.getOutPhases(), i2, i3, i4, i5);
        
        addToChain(chain,ex.getIn());
        addToChain(chain,ex.getOut());
        chain.setFaultProcessor(edp.getOutFaultProcessor());
        return chain;
    }
}
