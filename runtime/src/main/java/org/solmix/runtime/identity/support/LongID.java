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

package org.solmix.runtime.identity.support;

import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.BaseID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class LongID extends BaseID {

    private static final long serialVersionUID = -5989122263674756204L;

    private Long value = null;

    protected LongID(AbstractNamespace n, long v) {
        super(n);
        value = new Long(v);
    }

    @Override
    protected int namespaceCompareTo(BaseID o) {
        Long ovalue = ((LongID) o).value;
        return value.compareTo(ovalue);
    }

    @Override
    protected boolean namespaceEquals(BaseID o) {
        if (!(o instanceof LongID))
            return false;
        LongID obj = (LongID) o;
        return value.equals(obj.value);
    }

    @Override
    protected String namespaceGetName() {
        return value.toString();
    }

    @Override
    protected int namespaceHashCode() {
        return value.hashCode();
    }

    public long longValue() {
        return value.longValue();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("LongID[");
        sb.append(value).append("]");
        return sb.toString();

    }

}
