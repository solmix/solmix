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

package org.solmix.runtime.exchange.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.identity.ID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public class MessageInfo extends AbstractMessageInfo {

    public static enum Type {
        INPUT , OUTPUT;
    }

    private Type type;

    public MessageInfo(OperationInfo op, Type type, NamedID name) {
        super(op, name);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Map<NamedID, ArgumentInfo> getArgumentsMap() {
        Map<NamedID, ArgumentInfo> args = new HashMap<NamedID, ArgumentInfo>();
        for (ArgumentInfo part : getArguments()) {
            args.put(part.getName(), part);
        }
        return args;
    }

    public List<ArgumentInfo> getOrderedParts(List<String> order) {
        if (StringUtils.isEmpty(order)) {
            return getMessageParts();
        }

        List<ArgumentInfo> orderedParts = new ArrayList<ArgumentInfo>();
        Map<NamedID, ArgumentInfo> partsMap = getArgumentsMap();
        for (String part : order) {
            ID qname = getArgumentId(part);
            orderedParts.add(partsMap.get(qname));
        }
        return orderedParts;
    }

    @Override
    public String toString() {
        return "[MessageInfo " + type + ": " + name.toQueryString() + "]";
    }
}
