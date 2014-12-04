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

package org.solmix.runtime.interceptor.support;

import java.io.IOException;
import java.io.InputStream;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhaseInterceptorSupport;

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
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public void handleMessage(Message msg) throws Fault {
//TODO
    }

}
