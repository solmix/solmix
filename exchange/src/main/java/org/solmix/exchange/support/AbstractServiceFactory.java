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

package org.solmix.exchange.support;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.exchange.Service;
import org.solmix.exchange.ServiceFactoryListener;
import org.solmix.exchange.dataformat.DataFormat;
import org.solmix.exchange.event.ServiceFactoryEvent;
import org.solmix.exchange.interceptor.support.OneWayInterceptor;
import org.solmix.exchange.interceptor.support.OutgoingChainInterceptor;
import org.solmix.exchange.interceptor.support.ServiceInvokerInterceptor;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月21日
 */

public abstract class AbstractServiceFactory {

    protected boolean dataFormatSetted;
    
    protected Service service;

    private DataFormat dataFormat;

    private final CopyOnWriteArrayList<ServiceFactoryListener> listeners = new CopyOnWriteArrayList<ServiceFactoryListener>();
    
    private Container container;
    public abstract Service create();

    /**   */
    public Container getContainer() {
        return container;
    }

    public void pulishEvent(ServiceFactoryEvent event) {
        for (ServiceFactoryListener listener : listeners) {
            listener.onHandle(event);
        }
    }

    public void pulishEvent(int type, Object... args) {
        ServiceFactoryEvent event = new ServiceFactoryEvent(type, this, args);
        for (ServiceFactoryListener listener : listeners) {
            listener.onHandle(event);
        }
    }

    /**   */
    public void setContainer(Container container) {
        this.container = container;
        if (container == null) {
            return;
        }
        ConfiguredBeanProvider cp = container.getExtension(ConfiguredBeanProvider.class);
        if (cp != null) {
            Collection<? extends ServiceFactoryListener> ls = cp.getBeansOfType(ServiceFactoryListener.class);
            for (ServiceFactoryListener l : ls) {
                listeners.addIfAbsent(l);
            }
        }
    }

    /**   */
    public DataFormat getDataFormat() {
        return getDataFormat(true);

    }

    /**   */
    public DataFormat getDataFormat(boolean create) {
        if (dataFormat == null && create) {
            dataFormat = defaultDataFormat();
        }
        return dataFormat;
    }

    /**
     * @return
     */
    protected DataFormat defaultDataFormat() {
        return null;
    }

    /**   */
    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
        dataFormatSetted = dataFormat != null;
    }

    public Service getService() {
        return service;
    }

    protected void setService(Service service) {
        this.service = service;
    }

    /**
     * 
     */
    protected void initDefaultInterceptor() {
        service.getInInterceptors().add(new ServiceInvokerInterceptor());
        service.getInInterceptors().add(new OutgoingChainInterceptor());
        service.getInInterceptors().add(new OneWayInterceptor());
    }

    /**
     * @return
     */
    public abstract Invoker getInvoker() ;

    /**
     * @return
     */
    public abstract Invoker createInvoker() ;

}
