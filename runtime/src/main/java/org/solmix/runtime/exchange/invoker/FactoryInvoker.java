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
import org.solmix.runtime.interceptor.Fault;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月4日
 */

public class FactoryInvoker extends AbstractInvoker {

    private Factory factory;

    /**
     * Create a FactoryInvoker object.
     * 
     * @param factory the factory used to create service object.
     */
    public FactoryInvoker(Factory factory) {
        this.factory = factory;
    }
    public FactoryInvoker() {
    }
    public void setFactory(Factory f) {
        this.factory = f;
    }

    @Override
    public Object getServiceObject(Exchange ex) {
        try {
            return factory.create(ex);
        } catch (Fault e) {
            throw e;
        } catch (Throwable e) {
            throw new Fault("CREATE_SERVICE_OBJECT_EXC", e);
        }
    }
    
    @Override
    public void releaseServiceObject(final Exchange ex, Object obj) {
        factory.release(ex, obj);
    }
    
    public boolean isSingletonFactory() {
        return factory instanceof SingletonFactory; 
    }

}
