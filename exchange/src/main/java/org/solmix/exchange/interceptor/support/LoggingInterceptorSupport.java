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

import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class LoggingInterceptorSupport extends PhaseInterceptorSupport<Message> {

    /** @param phase */
    public LoggingInterceptorSupport(String phase) {
        super(phase);
    }

    public LoggingInterceptorSupport(String id, String phase) {
        super(id, phase);
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
