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

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.model.FaultInfo;
import org.solmix.runtime.exchange.model.OperationInfo;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhaseInterceptorSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class FaultOutInterceptor extends PhaseInterceptorSupport<Message> {

    /** @param phase */
    public FaultOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Fault f = (Fault) message.getContent(Exception.class);
        if (f == null) {
            return;
        }
        Throwable cause = f.getCause();
        if (cause == null) {
            return;
        }
        OperationInfo oi = message.getExchange().get(OperationInfo.class);

        if (oi == null) {
            return;
        }
        FaultInfo fi = getFaultForClass(oi, cause.getClass());
        if (fi != null && cause instanceof Exception) {
            //TODO
        }
    }

    protected FaultInfo getFaultForClass(OperationInfo oi,
        Class<? extends Throwable> class1) {
        FaultInfo selectedFaultInfo = null;
        Class<?> selectedFaultInfoClass = null;
        for (FaultInfo fi : oi.getFaults()) {
            Class<?> c = (Class<?>) fi.getProperty(Class.class.getName());
            if (c != null
                && c.isAssignableFrom(class1)
                && (selectedFaultInfo == null || selectedFaultInfoClass.isAssignableFrom(c))) {
                selectedFaultInfo = fi;
                selectedFaultInfoClass = c;

            }
        }
        return selectedFaultInfo;
    }

}
