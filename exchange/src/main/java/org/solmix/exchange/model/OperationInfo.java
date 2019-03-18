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

package org.solmix.exchange.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.identity.ID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public class OperationInfo extends InfoPropertiesSupport {

	InterfaceInfo interfaceInfo;

	NamedID operationId;

	String inName;

	MessageInfo inputMessage;

	String outName;

	MessageInfo outputMessage;

	Map<ID, FaultInfo> faults;

	List<String> parameterOrdering;

	OperationInfo unwrappedOperation;

	public OperationInfo() {
	}

	public OperationInfo(InterfaceInfo interfaceInfo, NamedID operationId) {
		this.interfaceInfo = interfaceInfo;
		this.operationId = operationId;
	}

	/**
	 * @return
	 */
	public NamedID getName() {
		return operationId;
	}

	public void setName(NamedID iD) {
		this.operationId = iD;
	}

	public MessageInfo createMessage(NamedID messageId, MessageInfo.Type type) {
		return new MessageInfo(this, type, messageId);
	}

	public InterfaceInfo getInterface() {
		return interfaceInfo;
	}

	public MessageInfo getOutput() {
		return outputMessage;
	}

	public String getOutputName() {
		return outName;
	}

	public void setOutput(String nm, MessageInfo out) {
		outName = nm;
		outputMessage = out;
		if (unwrappedOperation != null && unwrappedOperation.getOutput() != null) {
			unwrappedOperation.getOutput().setDelegate(out, false);
		}
	}

	public boolean hasOutput() {
		return outputMessage != null;
	}

	public MessageInfo getInput() {
		return inputMessage;
	}

	public String getInputName() {
		return inName;
	}

	public void setInput(String nm, MessageInfo in) {
		inName = nm;
		inputMessage = in;
		if (unwrappedOperation != null && unwrappedOperation.getInput() != null) {
			unwrappedOperation.getInput().setDelegate(in, false);
		}
	}

	public boolean hasInput() {
		return inputMessage != null;
	}

	public boolean isOneWay() {
		return inputMessage != null && outputMessage == null;
	}

	public boolean isUnwrappedCapable() {
		return unwrappedOperation != null;
	}

	public OperationInfo getUnwrappedOperation() {
		return unwrappedOperation;
	}

	public void setUnwrappedOperation(OperationInfo op) {
		unwrappedOperation = op;
	}

	public boolean isUnwrapped() {
		return false;
	}

	/**
	 * Adds an fault to this operation.
	 * 
	 * @param name the fault name.
	 */
	public FaultInfo addFault(NamedID faultId, NamedID messageid) {
		Assert.isNotNull(faultId);
		if (faults != null && faults.containsKey(faultId)) {
			throw new IllegalArgumentException("duplicated Fault id:" + faultId);
		}
		FaultInfo fault = new FaultInfo(this, messageid, faultId);
		addFault(fault);
		return fault;
	}

	/**
	 * Adds a fault to this operation.
	 * 
	 * @param fault the fault.
	 */
	public synchronized void addFault(FaultInfo fault) {
		if (faults == null) {
			faults = new ConcurrentHashMap<ID, FaultInfo>(4, 0.75f, 2);
		}
		faults.put(fault.getFaultID(), fault);
	}

	/**
	 * Returns the fault with the given name, if found.
	 * 
	 * @param name the name.
	 * @return the fault; or <code>null</code> if not found.
	 */
	public FaultInfo getFault(NamedID name) {
		if (faults != null) {
			return faults.get(name);
		}
		return null;
	}

	public boolean hasFaults() {
		return faults != null && faults.size() > 0;
	}

	/**
	 * Returns all faults for this operation.
	 * 
	 * @return all faults.
	 */
	public Collection<FaultInfo> getFaults() {
		if (faults == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(faults.values());
	}

	public void setParameterOrdering(List<String> o) {
		this.parameterOrdering = o;
	}

	public List<String> getParameterOrdering() {
		return parameterOrdering;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("[OperationInfo: ").append(operationId).append("]").toString();
	}

	@Override
	public int hashCode() {
		return operationId == null ? -1 : operationId.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof OperationInfo)) {
			return false;
		}
		OperationInfo oi = (OperationInfo) o;
		return equals(operationId, oi.operationId) && equals(inputMessage, oi.inputMessage)
				&& equals(outputMessage, oi.outputMessage) && equals(faults, oi.faults)
				&& equals(interfaceInfo.getName(), oi.interfaceInfo.getName());
	}

}
