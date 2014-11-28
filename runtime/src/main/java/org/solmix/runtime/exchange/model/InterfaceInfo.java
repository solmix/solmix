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

package org.solmix.runtime.exchange.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.identity.ID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月17日
 */

public class InterfaceInfo extends InfoPropertiesSupport {

    private InfoID iD;

    private final ServiceInfo service;

    private final Map<ID, OperationInfo> operations = new ConcurrentHashMap<ID, OperationInfo>(4, 0.75f, 2);

    public InterfaceInfo(ServiceInfo info, InfoID iD) {
        this.iD = iD;
        service = info;
        info.setInterface(this);
    }

    /**   */
    public InfoID getID() {
        return iD;
    }

    /**   */
    public void setID(InfoID iD) {
        this.iD = iD;
    }

    /**   */
    public ServiceInfo getService() {
        return service;
    }

    public OperationInfo addOperation(InfoID operationId) {
        Assert.isNotNull(operationId);
        if (operations.containsKey(operationId)) {
            throw new IllegalArgumentException("Duplicated operationId: "
                + operationId);
        }
        OperationInfo operation = new OperationInfo(this, operationId);
        addOperation(operation);
        return operation;
    }

    void addOperation(OperationInfo operation) {
        operations.put(operation.getID(), operation);
    }

    public void removeOperation(OperationInfo operation) {
        operations.remove(operation.getID());
    }
    
    public OperationInfo getOperation(InfoID operationId) {
        return operations.get(operationId);
    }
    
    public Collection<OperationInfo> getOperations() {
        return Collections.unmodifiableCollection(operations.values());
    }

}
