/*
 * SOLMIX PROJECT
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

package com.solmix.fmk.security.auth;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.solmix.api.security.auth.PrincipalCollection;
import com.solmix.commons.util.DataUtil;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-29
 */

public class PrincipalCollectionImpl implements PrincipalCollection
{

    private static final long serialVersionUID = 1L;

    private static final String NAME = "PrincipalCollection";

    private String name;

    /**
     * Collection of principal objects.
     */
    private final Collection<Principal> collection = new ArrayList<Principal>();

    @Override
    public String getName() {
        if (DataUtil.isNullOrEmpty(name))
            return NAME;
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

    @Override
    public void add(Principal principal) {
        collection.add(principal);
    }

    @Override
    public void addAll(Collection<Principal> principal) {
        collection.addAll(principal);

    }

    @Override
    public void remove(Principal principal) {
        collection.remove(principal);
    }

    @Override
    public void clearAll() {
        collection.clear();
    }

    @Override
    public boolean contains(Principal principal) {
        return collection.contains(principal);
    }

    @Override
    public Iterator<Principal> iterator() {
        return collection.iterator();
    }

    @Override
    public boolean contains(String name) {
        return this.get(name) != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.auth.PrincipalCollection#get(java.lang.String)
     */
    @Override
    public Principal get(String name) {
        Iterator<Principal> principalIterator = collection.iterator();
        while (principalIterator.hasNext()) {
            Principal principal = principalIterator.next();
            if (principal.getName().equalsIgnoreCase(name)) {
                return principal;
            }
        }
        return null;
    }

}
