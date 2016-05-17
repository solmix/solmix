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

import java.net.URI;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.identity.AbstractNamespace;
import org.solmix.runtime.identity.BaseID;
import org.solmix.runtime.identity.ResourceID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class URIID extends BaseID implements ResourceID {

    private static final long serialVersionUID = 7482429487272020648L;

    private final URI uri;

    public URIID(AbstractNamespace namespace, URI uri) {
        super(namespace);
        Assert.isNotNull(uri, "namespace not be null");
        this.uri = uri;
    }

    @Override
    protected int namespaceCompareTo(BaseID o) {
        if (this == o)
            return 0;
        if (!this.getClass().equals(o.getClass()))
            return Integer.MIN_VALUE;
        return this.uri.compareTo(((URIID) o).uri);
    }

    @Override
    protected boolean namespaceEquals(BaseID o) {
        if (this == o)
            return true;
        if (!this.getClass().equals(o.getClass()))
            return false;
        return this.uri.equals(((URIID) o).uri);
    }

    @Override
    protected String namespaceGetName() {
        return uri.toString();
    }

    @Override
    protected int namespaceHashCode() {
        return uri.hashCode();
    }

    @Override
    public URI toURI() {
        return uri;
    }

    @Override
    public String toString() {
        return "URIID [uri=" + uri + "]";
    }

}
