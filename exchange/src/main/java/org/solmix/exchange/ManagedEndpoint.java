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
package org.solmix.exchange;

import javax.management.JMException;
import javax.management.ObjectName;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.management.ManagedComponent;
import org.solmix.runtime.management.ManagementConstants;
import org.solmix.runtime.management.annotation.ManagedAttribute;
import org.solmix.runtime.management.annotation.ManagedOperation;
import org.solmix.runtime.management.annotation.ManagedResource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月20日
 */
@ManagedResource(componentName = "Endpoint", description = "Responsible for managing server instances.")
public class ManagedEndpoint implements ManagedComponent,
    ServerLifeCycleListener {

    public static final String ENDPOINT_NAME = "managed.endpoint.name";
    public static final String SERVICE_NAME = "managed.service.name";
    public static final String INSTANCE_ID = "managed.instance.id";

    protected final Container container;
    protected final Endpoint endpoint;
    protected final Server server;
    private enum State { CREATED, STARTED, STOPPED };
    private State state = State.CREATED;
    
    public ManagedEndpoint(Container container, Endpoint ep, Server s) {
        this.container = container;
        endpoint = ep;
        server = s;
    }
    
    @ManagedOperation        
    public void start() {
        if (state == State.STARTED) {
            return;
        }
        ServerLifeCycleManager mgr = container.getExtension(ServerLifeCycleManager.class);
        if (mgr != null) {
            mgr.registerListener(this);
        }
        server.start();
    }
    
    @ManagedOperation
    public void stop() {
        server.stop();
    }
    
    @ManagedOperation
    public void destroy() {
        server.destroy();
    }

    @ManagedAttribute(description = "Address Attribute", currencyTimeLimit = 60)
    public String getAddress() {
        return endpoint.getEndpointInfo().getAddress();
    }
    
    @ManagedAttribute(description = "TransportId Attribute", currencyTimeLimit = 60)
    public String getTransportId() {
        return endpoint.getEndpointInfo().getTransporter();
    }
    
    @ManagedAttribute(description = "Server State")
    public String getState() {
        return state.toString();
    }
    
    @Override
    public void startServer(Server s) {
        if (server.equals(s)) {
            state = State.STARTED;            
        }
    }

    
    @Override
    public void stopServer(Server s) {
        if (server.equals(s)) {
            state = State.STOPPED;
            ServerLifeCycleManager mgr = container.getExtension(ServerLifeCycleManager.class);
            if (mgr != null) {
                mgr.unRegisterListener(this);                
            }
        }
    }

    @Override
    public ObjectName getObjectName() throws JMException {
        String id = container.getId();
        StringBuilder buffer = new StringBuilder();
        buffer.append(ManagementConstants.DEFAULT_DOMAIN_NAME).append(':');
        buffer.append(ManagementConstants.CONTAINER_ID_PROP).append('=').append(id).append(',');
        buffer.append(ManagementConstants.TYPE_PROP).append('=').append("Exchange.Service.Endpoint,");
       

        String serviceName = (String)endpoint.get(SERVICE_NAME);
        if (StringUtils.isEmpty(serviceName)) {
            serviceName = endpoint.getService().getServiceName().toIdentityString();
        }
        serviceName = ObjectName.quote(serviceName);
        buffer.append(ManagementConstants.SERVICE_NAME_PROP).append('=').append(serviceName).append(',');
        
        
        String endpointName = (String)endpoint.get(ENDPOINT_NAME);
        if (StringUtils.isEmpty(endpointName)) {
            endpointName = endpoint.getEndpointInfo().getName().toIdentityString();
        }
        endpointName = ObjectName.quote(endpointName);
        buffer.append(ManagementConstants.PORT_NAME_PROP).append('=').append(endpointName).append(',');
        String instanceId = (String)endpoint.get(INSTANCE_ID);
        if (StringUtils.isEmpty(instanceId)) {
            instanceId = new StringBuffer().append(endpoint.hashCode()).toString();
        }
        // Added the instance id to make the ObjectName unique
        buffer.append(ManagementConstants.INSTANCE_ID_PROP).append('=').append(instanceId);
        
        //Use default domain name of server
        return new ObjectName(buffer.toString());
    }
}
