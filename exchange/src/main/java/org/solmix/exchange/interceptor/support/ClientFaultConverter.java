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
package org.solmix.exchange.interceptor.support;

import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;


/**
 * 转化为本地Exception.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月12日
 */

public class ClientFaultConverter extends PhaseInterceptorSupport<Message> {

    public ClientFaultConverter() {
        this(Phase.DECODE);
    }

    public ClientFaultConverter(String phase) {
        super(phase);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.interceptor.Interceptor#handleMessage(org.solmix.exchange.Message)
     */
    @Override
    public void handleMessage(Message message) throws Fault {
        // TODO Auto-generated method stub
        
    }

}
