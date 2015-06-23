/**
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

public class ProtocolInfo extends InfoPropertiesSupport {

    private NamedID name;

    private ServiceInfo service;

    private final String protocolId;

    public ProtocolInfo(ServiceInfo service, String protocolId) {
        this.service = service;
        this.protocolId = protocolId;
    }

    /**   */
    public NamedID getName() {
        return name;
    }

    /**   */
    public void setName(NamedID name) {
        this.name = name;
    }

    /**   */
    public ServiceInfo getService() {
        return service;
    }

    /**   */
    public void setService(ServiceInfo service) {
        this.service = service;
    }

    /**   */
    public String getProtocolId() {
        return protocolId;
    }

}
