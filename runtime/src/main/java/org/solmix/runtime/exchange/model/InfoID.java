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

import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.BaseID;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月27日
 */

public class InfoID extends BaseID {

    private static final long serialVersionUID = 4677663481983737102L;

    private final String name;

    private final String serviceSpace;

    private int hash;

    protected InfoID(AbstractNamespace n, String space, String name) {
        super(n);
        this.name = name;
        this.serviceSpace = space;
        this.hash = 7;
        this.hash = 31 * hash + space.hashCode();
        int nh = name.hashCode();
        this.hash = 31 * hash + (nh ^ (nh >>> 32));
    }

    @Override
    protected int namespaceCompareTo(BaseID o) {
        if (o == null || !(o instanceof InfoID)) {
            return Integer.MIN_VALUE;
        }
        InfoID other = (InfoID) o;
        int compare = this.serviceSpace.compareTo(other.getServiceSpace());
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
        if (o == null || !(o instanceof InfoID)) {
            return false;
        }
        InfoID other = (InfoID) o;
        if (serviceSpace.equals(other.getServiceSpace()))
            return name.equals(other.getName());
        return false;
    }

    
    @Override
    protected String namespaceGetName() {
        if (serviceSpace.equals("")) {
            return name;
        } else {
            return new StringBuilder().append("{").append(serviceSpace).append(
                "}").append(name).toString();
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
    public String getServiceSpace() {
        return serviceSpace;
    }

}
