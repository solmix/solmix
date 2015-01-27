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

import org.solmix.runtime.identity.BaseID;
import org.solmix.runtime.identity.IDFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class NamedID extends BaseID {

    private static final long serialVersionUID = 4677663481983737102L;

    private final String name;

    private final String serviceNamespace;

    private int hash;

    public NamedID(NamedID other) {
        this(other.getServiceNamespace(), other.getName());
    }
    
    public NamedID(String space, String name) {
        this((NamedIDNamespace) IDFactory.getDefault().getNamespaceByName(
            NamedIDNamespace.NAME), space, name);
    }

    protected NamedID(NamedIDNamespace n, String space, String name) {
        super(n);
        this.name = name;
        this.serviceNamespace = space;
        this.hash = 7;
        this.hash = 31 * hash + space.hashCode();
        int nh = name.hashCode();
        this.hash = 31 * hash + (nh ^ (nh >>> 32));
    }

    @Override
    protected int namespaceCompareTo(BaseID o) {
        if (o == null || !(o instanceof NamedID)) {
            return Integer.MIN_VALUE;
        }
        NamedID other = (NamedID) o;
        int compare = this.serviceNamespace.compareTo(other.getServiceNamespace());
        if (compare == 0) {
            return this.getName().compareTo(other.getName());
        }
        return compare;
    }

    @Override
    protected boolean namespaceEquals(BaseID o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof NamedID)) {
            return false;
        }
        NamedID other = (NamedID) o;
        if (serviceNamespace.equals(other.getServiceNamespace())) {
            return name.equals(other.getName());
        }
        return false;
    }

    @Override
    protected String namespaceGetName() {
        if (serviceNamespace.equals("")) {
            return name;
        } else {
            if (serviceNamespace.endsWith("/")) {
                return new StringBuilder().append(serviceNamespace).append(name).toString();
            } else {
                return new StringBuilder().append(serviceNamespace).append("/").append(
                    name).toString();
            }
        }
    }

    @Override
    protected int namespaceHashCode() {
        return hash;
    }

    /**   */
    @Override
    public String getName() {
        return name;
    }

    /**   */
    public String getServiceNamespace() {
        return serviceNamespace;
    }

    @Override
    public String toString() {
        return namespaceGetName();
    }
}
