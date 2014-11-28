/*
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

package org.solmix.runtime.identity;

import org.solmix.commons.util.Assert;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public abstract class BaseID implements ID {

    private static final long serialVersionUID = -6025003816232785375L;

    protected AbstractNamespace namespace;

    protected BaseID() {
        //
    }

    protected BaseID(AbstractNamespace namespace) {
        Assert.isNotNull(namespace, "namespace cannot be null");
        this.namespace = namespace;
    }

    @Override
    public int compareTo(Object o) {
        Assert.isTrue(o != null && o instanceof BaseID,
            "incompatible types for compare");
        return namespace.getCompareToForObject(this, (BaseID) o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof BaseID)) {
            return false;
        }
        return namespace.testIDEquals(this, (BaseID) o);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.ID#getName()
     */
    @Override
    public String getName() {
        return namespace.getNameForID(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.identity.ID#getNamespace()
     */
    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return namespace.getHashCodeForID(this);
    }

    @Override
    public String toQueryString() {
        return namespace.toQueryString(this);
    }

    /**
     * Called by {@link Namespace#getCompareToForObject(BaseID, BaseID)}.
     * 
     * @param o the other ID to compare to. Will not be <code>null</code>.
     * @return the appropriate value as per {@link Comparable} contract.
     */
    protected abstract int namespaceCompareTo(BaseID o);

    /**
     * Called by {@link Namespace#testIDEquals(BaseID, BaseID)}.
     * 
     * @param o the other ID to test against. May be <code>null</code>.
     * @return <code>true</code> if this ID is equal to the given ID.
     *         <code>false</code> otherwise.
     */
    protected abstract boolean namespaceEquals(BaseID o);

    /**
     * Called by {@link Namespace#getNameForID(BaseID)}.
     * 
     * @return String name for this ID. Must not be <code>null</code>. Value
     *         returned should be unique within this Namespace.
     */
    protected abstract String namespaceGetName();

    /**
     * Called by {@link Namespace#getHashCodeForID(BaseID)}.
     * 
     * @return int hashCode for this ID. Returned value must be unique within
     *         this process.
     */
    protected abstract int namespaceHashCode();

    /**
     * Called by {@link Namespace#toQueryString(BaseID)}.
     * 
     * @return String that represents this ID. Default implementation is to
     *         return
     * 
     *         <pre>
     * namespace.getScheme() + Namespace.SCHEME_SEPARATOR + namespaceGetName();
     * </pre>
     */
    protected String namespaceToQueryString() {
        return namespace.getScheme() + AbstractNamespace.SCHEME_SEPARATOR
            + namespaceGetName();
    }
}
