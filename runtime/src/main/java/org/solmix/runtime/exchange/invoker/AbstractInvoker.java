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

package org.solmix.runtime.exchange.invoker;

import org.solmix.runtime.exchange.Exchange;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月25日
 */

public abstract class AbstractInvoker implements Invoker {

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.invoker.Invoker#invoke(org.solmix.runtime.exchange.Exchange,
     *      java.lang.Object)
     */
    @Override
    public Object invoke(Exchange exchange, Object o) {
        final Object serviceObject = getServiceObject(exchange);
        try {
            return null;
        } finally {
            releaseServiceObject(exchange, serviceObject);
        }
    }

    /**
     * Called when the invoker is done with the object. Default implementation
     * does nothing.
     * 
     * @param context
     * @param obj
     */
    public void releaseServiceObject(final Exchange context, Object obj) {
    }

    /**
     * Creates and returns a service object depending on the scope.
     */
    public abstract Object getServiceObject(final Exchange context);
}
