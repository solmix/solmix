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

import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class MessageSenderInterceptor extends PhaseInterceptorSupport<Message> {

    private final MessageSenderEndingInterceptor ending = new MessageSenderEndingInterceptor();

    public MessageSenderInterceptor() {
        super(Phase.PREPARE_SEND);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        try {
            getPipeline(message).prepare(message);
        } catch (IOException ex) {
            throw new Fault("send message failed", ex);
        }

        // 添加关闭pipeline
        message.getInterceptorChain().add(ending);

    }

    public static Pipeline getPipeline(Message message) {
        Exchange ex = message.getExchange();
        Pipeline pl = ex.getPipeline(message);
        if (pl == null && (ex.getOut() != null || ex.getOutFault() != null)) {
            pl = OutgoingChainInterceptor.getBackPipeline(message);
        }
        return pl;
    }

    /** 为了关闭pipeline */
    public static class MessageSenderEndingInterceptor extends
        PhaseInterceptorSupport<Message> {

        public MessageSenderEndingInterceptor() {
            super(Phase.PREPARE_SEND_ENDING);
        }

        @Override
        public void handleMessage(Message message) throws Fault {
            try {
                getPipeline(message).close(message);
            } catch (IOException e) {
                throw new Fault("send message failed", e);
            }
        }
    }
}
