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
import java.io.InputStream;
import java.util.concurrent.Executor;

import org.apache.commons.io.IOUtils;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.MessageUtils;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;
import org.solmix.exchange.support.DefaultMessage;

/**
 * 无返回拦截处理
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class OneWayInterceptor extends PhaseInterceptorSupport<Message> {

    public OneWayInterceptor() {
        super(Phase.PRE_LOGICAL);
    }

    public OneWayInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleFault(Message msg) {
        if (msg.getExchange().isOneWay() && !isRequest(msg)) {
            InputStream in = msg.getContent(InputStream.class);
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
        }
    }

    @Override
    public void handleMessage(Message msg) throws Fault {
        Exchange ex = msg.getExchange();
        if (ex.isOneWay() 
            && !MessageUtils.isRequest(msg) 
            && msg.get(OneWayInterceptor.class)==null
            && msg.getExchange().get(Executor.class) == null) {
            msg.put(OneWayInterceptor.class, this);
            
            Message partial =new DefaultMessage();
            Endpoint edp = ex.getEndpoint();
            if(edp!=null){
                partial= edp.getProtocol().createMessage(partial);
            }
            partial.setExchange(ex);
            
            try {
                Pipeline pl = ex.get(Transporter.class).getBackPipeline(msg);
                if(pl!=null){
                    msg.getExchange().setIn(null);
                    pl.prepare(partial);
                    pl.close(partial);
                    msg.getExchange().setIn(msg);
                }
            } catch (IOException e) {
            }
        }
    }

}
