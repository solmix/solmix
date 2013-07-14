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

package org.solmix.api.security.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-29
 */

public interface PrincipalCollection extends Principal, Serializable, Iterable<Principal>
{

    public static final String DEFAULT_NAME = "PrincipalCollection";

    @Override
    public String getName();

    public void setName(String name);

    public void add(Principal principal);

    public void addAll(Collection<Principal> principal);

    public void remove(Principal principal);

    public void clearAll();

    public boolean contains(Principal principal);

    @Override
    public Iterator<Principal> iterator();

    /**
     * Checks if this collection contains object with the specified name.
     */
    public boolean contains(String name);

    /**
     * Get principal associated to the specified name from the collection.
     */
    public Principal get(String name);

}
