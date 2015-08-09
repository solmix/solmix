/**
 * Copyright (container) 2014 The Solmix Project
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
package org.solmix.exchange.invoker;

import org.solmix.exchange.Exchange;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月4日
 */

public class SingletonFactory implements Factory {

    Object bean;
    Factory factory;
    public SingletonFactory(final Object bean) {
        this.bean = bean;
    }
    public SingletonFactory(final Class<?> beanClass) {
        this.factory = new PerRequestFactory(beanClass);
    }
    public SingletonFactory(final Factory f) {
        this.factory = f;
    }

    /** {@inheritDoc}*/
    @Override
    public Object create(Exchange ex) throws Throwable {
        if (bean == null && factory != null) {
            createBean(ex);
        }
        return bean;
    }

    private synchronized void createBean(Exchange e) throws Throwable {
        if (bean == null) {
            bean = factory.create(e);
        }
    }
    
    /** {@inheritDoc}*/
    @Override
    public void release(Exchange ex, Object o) {
        //nothing to do
    }

}
