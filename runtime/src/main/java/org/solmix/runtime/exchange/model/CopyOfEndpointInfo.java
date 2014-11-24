/*
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
package org.solmix.runtime.exchange.model;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月15日
 */

public class CopyOfEndpointInfo
{
    private ProtocolInfo protocolInfo;
    
    private ServiceInfo serviceInfo;
    
    private String phasePolicy;
    
    /**   */
    public ProtocolInfo getBinding() {
        return protocolInfo;
    }

    
    /**   */
    public void setBinding(ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    
    /**   */
    public ServiceInfo getService() {
        return serviceInfo;
    }

    
    /**   */
    public void setService(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }


    /**
     * @return
     */
    public String getPhasePolicy() {
        return phasePolicy;
    }


    
    /**   */
    public void setPhasePolicy(String phasePolicy) {
        this.phasePolicy = phasePolicy;
    }
    
    

}
