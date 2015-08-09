/**
 * Copyright (container) 2014 The Solmix Project
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.identity.ID;
import org.solmix.runtime.identity.IDFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public class AbstractMessageInfo extends InfoPropertiesSupport {

    private final OperationInfo operation;

    NamedID name;

    private final Map<NamedID, ArgumentInfo> messageParts = new LinkedHashMap<NamedID, ArgumentInfo>(
        4);

    private List<ArgumentInfo> outOfBandArguments;

    AbstractMessageInfo(OperationInfo op, NamedID name) {
        operation = op;
        this.name = name;
    }

    public List<ArgumentInfo> getArguments() {
        if (outOfBandArguments == null) {
            return new ArrayList<ArgumentInfo>(messageParts.values());
        }
        List<ArgumentInfo> parts = new ArrayList<ArgumentInfo>(
            messageParts.values());
        parts.addAll(outOfBandArguments);
        return parts;
    }

    public ArgumentInfo addArgument(NamedID argumentId) {
        Assert.isNotNull(argumentId);

        ArgumentInfo part = new ArgumentInfo(argumentId, this);
        addArgument(part);
        return part;
    }

    public ArgumentInfo getArgument(NamedID name) {
        ArgumentInfo mpi = messageParts.get(name);
        if (mpi != null) {
            return mpi;
        }
        for (ArgumentInfo mpi2 : getOutOfBandArguments()) {
            if (name.equals(mpi2.getName())) {
                return mpi2;
            }
        }
        return mpi;
    }

    /**
     * @param part
     * @return
     */
    public ID getArgumentId(String part) {
        return IDFactory.getDefault().createID(
            operation.getName().getNamespace(), part);
    }

    public void removeArgument(NamedID name) {
        ArgumentInfo messagePart = getArgument(name);
        if (messagePart != null) {
            messageParts.remove(name);
        }
    }

    public void addArgument(ArgumentInfo part) {
        if (messageParts.containsKey(part.getName())) {
            part.setIndex(messageParts.get(part.getName()).getIndex());
        } else {
            part.setIndex(messageParts.size());
        }
        messageParts.put(part.getName(), part);
    }

    public int getArgumentIndex(ArgumentInfo part) {
        int i = 0;
        for (ArgumentInfo p : messageParts.values()) {
            if (part == p) {
                return i;
            }
            i++;
        }
        for (ArgumentInfo p : getOutOfBandArguments()) {
            if (part == p) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public ArgumentInfo addOutOfBandArgument(NamedID argumentId) {
        Assert.isNotNull(argumentId);

        ArgumentInfo part = new ArgumentInfo(argumentId, this);
        if (outOfBandArguments == null) {
            outOfBandArguments = new ArrayList<ArgumentInfo>(1);
        }
        part.setIndex(messageParts.size() + outOfBandArguments.size());
        outOfBandArguments.add(part);
        return part;
    }

    /**
     * Returns all message parts for this message.
     * 
     * @return all message parts.
     */
    public List<ArgumentInfo> getMessageParts() {
        if (outOfBandArguments == null) {
            return new ArrayList<ArgumentInfo>(messageParts.values());
        }
        List<ArgumentInfo> parts = new ArrayList<ArgumentInfo>(
            messageParts.values());
        parts.addAll(outOfBandArguments);
        return parts;
    }

    public List<ArgumentInfo> getOutOfBandArguments() {
        if (outOfBandArguments == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(outOfBandArguments);
    }

    public int size() {
        return messageParts.size() + getOutOfBandArguments().size();
    }

    @Override
    public int hashCode() {
        return name == null ? -1 : name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AbstractMessageInfo)) {
            return false;
        }
        AbstractMessageInfo oi = (AbstractMessageInfo) o;
        return equals(name, oi.name)
            && equals(messageParts, oi.messageParts)
            && equals(outOfBandArguments, oi.outOfBandArguments);
    }

    public NamedID getName() {
        return name;
    }

    public void setName(NamedID iD) {
        this.name = iD;
    }
}
