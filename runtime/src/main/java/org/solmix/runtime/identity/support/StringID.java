/**
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.identity.support;

import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.BaseID;
import org.solmix.runtime.identity.Namespace;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class StringID extends BaseID {

    private static final long serialVersionUID = -115141851463598350L;

    protected String value;

    protected StringID(AbstractNamespace n, String s) {
        super(n);
        value = s;
        setEmptyNamespace();
    }

    @Override
    public int compareTo(Object o) {
        setEmptyNamespace();
        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {
        setEmptyNamespace();
        return super.equals(o);
    }

    @Override
    public String getName() {
        setEmptyNamespace();
        return super.getName();
    }

    @Override
    public int hashCode() {
        setEmptyNamespace();
        return super.hashCode();
    }

    @Override
    public Namespace getNamespace() {
        setEmptyNamespace();
        return namespace;
    }

    @Override
    public String toQueryString() {
        setEmptyNamespace();
        return super.toQueryString();
    }

    @Override
    public String toString() {
        setEmptyNamespace();
        int strlen = value.length();
        StringBuffer sb = new StringBuffer(strlen + 10);
        sb.insert(0, "StringID[").insert(9, value).insert(strlen + 9, ']');
        return sb.toString();
    }

    @Override
    protected int namespaceCompareTo(BaseID obj) {
        return getName().compareTo(obj.getName());
    }

    @Override
    protected boolean namespaceEquals(BaseID obj) {
        if (!(obj instanceof StringID))
            return false;
        StringID o = (StringID) obj;
        return value.equals(o.getName());
    }

    @Override
    protected String namespaceGetName() {
        return value;
    }

    @Override
    protected int namespaceHashCode() {
        return value.hashCode() ^ getClass().hashCode();
    }

    private synchronized void setEmptyNamespace() {
        if (namespace == null) {
            namespace = new StringNamespace();
        }
    }
}
