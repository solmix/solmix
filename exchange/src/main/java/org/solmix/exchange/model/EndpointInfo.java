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

package org.solmix.exchange.model;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月15日
 */

public class EndpointInfo extends InfoPropertiesSupport {

    private ServiceInfo service;

    private ProtocolInfo protocol;

    private String address;

    private NamedID name;

    private String transporter;

    public EndpointInfo() {

    }

    public EndpointInfo(ServiceInfo service, String transporter) {
        this.service = service;
        this.transporter = transporter;
    }

    /**   */
    public ServiceInfo getService() {
        return service;
    }

    public ProtocolInfo getProtocol() {
        return protocol;
    }

    /**   */
    public void setService(ServiceInfo serviceInfo) {
        this.service = serviceInfo;
    }

    /**   */
    public String getTransporter() {
        return transporter;
    }

    /**   */
    public void setTransportor(String transporter) {
        this.transporter = transporter;
    }

    public InterfaceInfo getInterface() {
        if (service == null) {
            return null;
        }
        return service.getInterface();
    }

    /**
     * 
     */
    public String getAddress() {
        return address;
    }

    
    /**   */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return
     */
    public NamedID getName() {
        return name;
    }

    public void setName(NamedID iD) {
        this.name = iD;
    }

    @Override
    public String toString() {
        return "ProtocolID="
            + (protocol == null ? ""
                : (protocol.getName() + ", ServiceID=" + (protocol.getService() == null ? ""
                    : protocol.getService().getName()))) + ", ID=" + name;
    }

    /**
     * @param ptl
     */
    public void setProtocol(ProtocolInfo ptl) {
        this.protocol=ptl;
    }

}
