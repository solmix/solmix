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

package org.solmix.exchange.event;

import java.io.Serializable;

import org.solmix.exchange.support.AbstractServiceFactory;
import org.solmix.runtime.event.Event;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月24日
 */

public class ServiceFactoryEvent implements Event, Serializable {

    private static final long serialVersionUID = 1L;

    public final static int DATAPROCESSOR_INITIALIZED = 0x00000001;

    /**
     * EndpointInfo, Endpoint, Class
     */
    public final static int ENDPOINT_CREATED = 0x00000002;

    /**
     * Server, targetObject, Class
     */
    public final static int PRE_SERVER_CREATE = 0x00000003;

    /**
     * Server, targetObject, Class
     */
    public final static int SERVER_CREATED = 0x00000004;
    
    /**
     * InterfaceInfo
     */
    public final static int INTERFACE_CREATED = 0x00000005;

    /**
     * ProtocolInfo
     */
    public final static int PROTOCOL_CREATED = 0x00000006;

    /**
     * Endpoint
     */
    public final static int PRE_CLIENT_CREATE = 0x00000007;

    /**
     * Endpoint, Client
     */
    public final static int CLIENT_CREATED = 0x00000008;

    /**
     * EndpointInfo, Endpoint, SEI Class, Class
     */
    public final static int ENDPOINT_SELECTED = 0x00000009;

    /**
     * EndpointInfo
     */
    public final static int ENDPOINTINFO_CREATED = 0x0000000A;

    public final static int START_CREATE = 0x0000000C;
    
    public final static int END_CREATE = 0x0000000D;

    public final static int CREATE_FROM_CLASS = 0x0000000E;

    public final static int OPERATIONINFO_IN_MESSAGE_SET = 0x0000000F;

    public final static int OPERATIONINFO_OUT_MESSAGE_SET = 0x00000010;
    
    public static final int OPERATIONINFO_BOUND = 0x00000011;
    
    public final static int OPERATIONINFO_FAULT = 0x00000012;

    public final static int SERVER_CREATED_END = 0x00000013;

    

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
    
    public Object[] getArgs(){
        return args;
    }

    /**   */
    public AbstractServiceFactory getFactory() {
        return factory;
    }

}
