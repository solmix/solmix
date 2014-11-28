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

package org.solmix.runtime.exchange.event;

import java.io.Serializable;

import org.solmix.runtime.event.Event;
import org.solmix.runtime.exchange.support.AbstractServiceFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月24日
 */

public class ServiceFactoryEvent implements Event, Serializable {

    private static final long serialVersionUID = 1L;

    public final static int DATABINDING_INITIALIZED = 0x00000001;

    /**
     * EndpointInfo, Endpoint, Class
     */
    public final static int ENDPOINT_CREATED = 0x00000001;

    /**
     * Server, targetObject, Class
     */
    public final static int PRE_SERVER_CREATE = 0x00000001;

    /**
     * Server, targetObject, Class
     */
    public final static int SERVER_CREATED = 0x00000001;

    /**
     * BindingInfo, BindingOperationInfo, Implementation Method
     */
    public final static int BINDING_OPERATION_CREATED = 0x00000001;

    /**
     * BindingInfo
     */
    public final static int PROTOCOL_CREATED = 0x00000001;

    /**
     * Endpoint
     */
    public final static int PRE_CLIENT_CREATE = 0x00000001;

    /**
     * Endpoint, Client
     */
    public final static int CLIENT_CREATED = 0x00000001;

    /**
     * EndpointInfo, Endpoint, SEI Class, Class
     */
    public final static int ENDPOINT_SELECTED = 0x00000001;

    /**
     * EndpointInfo
     */
    public final static int ENDPOINTINFO_CREATED = 0x00000001;

    /**
     * Class[], InvokationHandler, Proxy
     */
    public final static int PROXY_CREATED = 0x00000001;

    public static int START_CREATE;

    public static int CREATE_FROM_CLASS;

    private final int type;

    private final AbstractServiceFactory factory;

    private Object[] args;

    /**
     * @param source
     */
    public ServiceFactoryEvent(int type, AbstractServiceFactory factory) {
        this.type = type;
        this.factory = factory;
    }

    public ServiceFactoryEvent(int type, AbstractServiceFactory factory,
        Object... args) {
        this.type = type;
        this.factory = factory;
        this.args = args;
    }

    /**   */
    public int getType() {
        return type;
    }

    /**   */
    public AbstractServiceFactory getFactory() {
        return factory;
    }

}
