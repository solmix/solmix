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

package org.solmix.exchange.processor;

import java.util.Collection;
import java.util.SortedSet;

import org.solmix.exchange.Client;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Interceptor;
import org.solmix.exchange.interceptor.InterceptorProvider;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorChain;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.runtime.Container;

/**
 *
 * 输出异常消息处理器.
 * 处理器会收集Client/Endpoint/Service/Protocol/Message中的interceptor配置,完成切面链调用.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月19日
 */
public class OutFaultChainProcessor extends AbstractFaultChainInitProcessor {

    public OutFaultChainProcessor(Container c, PhasePolicy phasePolicy) {
        super(c, phasePolicy);
    }

    @Override
    protected void initializeInterceptors(Exchange ex,
        PhaseInterceptorChain chain) {
        Endpoint e = ex.get(Endpoint.class);
        Client c = ex.get(Client.class);
        if (c != null) {
            chain.add(c.getOutFaultInterceptors());
        }
        chain.add(e.getService().getOutFaultInterceptors());
        chain.add(e.getOutFaultInterceptors());
        chain.add(e.getProtocol().getOutFaultInterceptors());
        if (e.getService().getDataFormat() instanceof InterceptorProvider) {
            chain.add(((InterceptorProvider) e.getService().getDataFormat()).getOutFaultInterceptors());
        }
        addToChain(chain, ex.getIn());
        addToChain(chain, ex.getOutFault());
    }

    @SuppressWarnings("unchecked")
    private void addToChain(PhaseInterceptorChain chain, Message m) {

        final Collection<InterceptorProvider> providers = (Collection<InterceptorProvider>) m.get(Message.INTERCEPTOR_PROVIDERS);
        if (providers != null) {
            for (InterceptorProvider p : providers) {
                chain.add(p.getOutFaultInterceptors());
            }
        }
        Interceptor<Message> is = (Interceptor<Message>) m.get(Message.FAULT_OUT_INTERCEPTORS);
        if (is != null) {
            chain.add(is);
        }
    }

    @Override
    protected SortedSet<Phase> getPhases() {
        return  phasePolicy.getOutPhases();
    }

    @Override
    protected boolean isOutMessage() {
        return true;
    }

}
