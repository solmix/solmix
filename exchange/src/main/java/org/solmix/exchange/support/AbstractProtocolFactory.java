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
package org.solmix.exchange.support;

import java.util.Dictionary;

import javax.annotation.Resource;

import org.solmix.exchange.ProtocolFactory;
import org.solmix.exchange.Service;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ProtocolInfo;
import org.solmix.exchange.model.ServiceInfo;
import org.solmix.runtime.Container;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月10日
 */

public abstract class AbstractProtocolFactory implements ProtocolFactory {

    protected Container container;
    public AbstractProtocolFactory(){
    	
    }
    public AbstractProtocolFactory(Container container) {
        this.container = container;
    }

    @Override
    public ProtocolInfo createProtocolInfo(Service service, String protocol,
        Dictionary<String, ?> configObject) {
        ProtocolInfo pi = createProtocolInfo(service.getServiceInfo(),
            protocol, configObject);
        if (pi.getName() == null) {
            pi.setName(new NamedID(
                service.getServiceName().getServiceNamespace(),
                service.getServiceName().getName() + "Protocol"));
        }
        return pi;
    }
    
    public ProtocolInfo createProtocolInfo(ServiceInfo info, String protocol,
        Dictionary<String, ?> configObject) {
        return new ProtocolInfo(info, protocol);
    }
    
    /**   */
    public Container getContainer() {
        return container;
    }
    
    @Resource
    public void setContainer(Container container) {
        this.container = container;
    }
    
}
