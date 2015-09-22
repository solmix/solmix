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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.runtime.identity.ID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月15日
 */

public class ServiceInfo extends InfoPropertiesSupport {

    private InterfaceInfo interfaceInfo;

    private final List<EndpointInfo> endpoints = new CopyOnWriteArrayList<EndpointInfo>();

    private final List<ProtocolInfo> protocols = new CopyOnWriteArrayList<ProtocolInfo>();

    private Map<NamedID, MessageInfo> messages;

    private NamedID name;

    public ServiceInfo() {

    }

    public InterfaceInfo getInterface() {
        return interfaceInfo;
    }

    public InterfaceInfo createInterface(NamedID infId) {
        interfaceInfo = new InterfaceInfo(this, infId);
        return interfaceInfo;
    }

    /**   */
    public NamedID getName() {
        return name;
    }

    /**   */
    public void setName(NamedID name) {
        this.name = name;
    }

    public Map<NamedID, MessageInfo> getMessages() {
        if (messages == null) {
            initMessagesMap();
        }
        return messages;
    }

    public ProtocolInfo getProtocol(ID qn) {
        for (ProtocolInfo bi : protocols) {
            if (qn.equals(bi.getName())) {
                return bi;
            }
        }
        return null;
    }

    public void addProtocol(ProtocolInfo binding) {
        ProtocolInfo bi = getProtocol(binding.getName());
        if (bi != null) {
            protocols.remove(bi);
        }
        protocols.add(binding);
    }

    public MessageInfo getMessage(ID qname) {
        return getMessages().get(qname);
    }

    /**
     * 
     */
    private void initMessagesMap() {
        messages = new ConcurrentHashMap<NamedID, MessageInfo>(16, 0.75f, 2);
        for (OperationInfo operation : getInterface().getOperations()) {
            if (operation.getInput() != null) {
                messages.put(operation.getInput().getName(), operation.getInput());
            }
            if (operation.getOutput() != null) {
                messages.put(operation.getOutput().getName(),
                    operation.getOutput());
            }
        }
    }

    public void refresh() {
        initMessagesMap();
    }

    public void setMessages(Map<NamedID, MessageInfo> msgs) {
        messages = msgs;
    }

    /**
     * @param eid
     * @return
     */
    public EndpointInfo getEndpoint(ID eid) {
        for (EndpointInfo ei : endpoints) {
            if (eid.equals(ei.getName())) {
                return ei;
            }
        }
        return null;
    }

    public void addEndpoint(EndpointInfo ep) {
        EndpointInfo ei = getEndpoint(ep.getName());
        if (ei != null) {
            endpoints.remove(ei);
        }
        endpoints.add(ep);
    }

    public Collection<EndpointInfo> getEndpoints() {
        return Collections.unmodifiableCollection(endpoints);
    }

    public Collection<ProtocolInfo> getProtocols() {
        return Collections.unmodifiableCollection(protocols);
    }

    /**
     * @param interfaceInfo
     */
    public void setInterface(InterfaceInfo inf) {
        this.interfaceInfo = inf;
    }

}
