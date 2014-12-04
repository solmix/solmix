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

import java.lang.reflect.Modifier;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月4日
 */

public class PerRequestFactory implements Factory {

    private final Class<?> svcClass;

    public PerRequestFactory(final Class<?> svcClass) {
        super();
        this.svcClass = svcClass;
    }

    @Override
    public Object create(Exchange ex) throws Throwable {
        try {
            if (svcClass.isInterface()) {
                throw new Fault("SVC_CLASS_IS_INTERFACE");
            }

            if (Modifier.isAbstract(svcClass.getModifiers())) {
                throw new Fault("SVC_CLASS_IS_ABSTRACT");
            }
            Object o = svcClass.newInstance();
            Container b = ex.get(Container.class);
            ResourceManager resourceManager = b.getExtension(ResourceManager.class);
            if (resourceManager != null) {
                ResourceInjector injector = new ResourceInjector(resourceManager);
                injector.inject(o);
                injector.construct(o);
            }
            return o;
        } catch (InstantiationException e) {
            throw new Fault("COULD_NOT_INSTANTIATE",e);
        } catch (IllegalAccessException e) {
            throw new Fault("ILLEGAL_ACCESS", e);
        }
    }

    @Override
    public void release(Exchange ex, Object o) {
        //nothing to do
    }

}
