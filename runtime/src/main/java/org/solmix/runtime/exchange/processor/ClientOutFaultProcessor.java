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
package org.solmix.runtime.exchange.processor;

import java.util.Map;
import java.util.SortedSet;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Client;
import org.solmix.runtime.exchange.ClientCallback;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhasePolicy;


/**
 * 客服端的输出为请求数据,出错直接回调返回错误
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月20日
 */

public class ClientOutFaultProcessor extends FaultChainInitProcessorSupport
{

    /** @param c */
    public ClientOutFaultProcessor(Container c, String phasePolicy)
    {
        super(c,phasePolicy);
    }

   
    @Override
    protected SortedSet<Phase> getPhases() {
        return getContainer()
            .getExtensionLoader(PhasePolicy.class)
            .getExtension(phasePolicy)
            .getOutPhases();
    }
    @Override
    @SuppressWarnings("unchecked")
    public void process(Message m) {
        
        Exception ex = m.getContent(Exception.class);
        ClientCallback callback = m.getExchange().get(ClientCallback.class);

        if (callback != null) {
            Map<String, Object> resCtx = (Map<String, Object>) m.getExchange().getOut().get(Message.INVOCATION_CONTEXT);
            resCtx = (Map<String, Object>) resCtx.get(Client.RESPONSE_CONTEXT);
            callback.handleException(resCtx, ex);
        }
    }
    
    @Override
    protected boolean isOutMessage() {
        return true;
    }

}
