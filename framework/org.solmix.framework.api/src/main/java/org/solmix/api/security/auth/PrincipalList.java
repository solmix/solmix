/*
 *  Copyright 2012 The Solmix Project
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

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-12
 */

public interface PrincipalList extends Principal, Serializable
{

    @Override
    String getName();

    void setName(String name);

    /**
     * Add a name to the list.
     */
    void add(String name);

    /**
     * Gets list of groups/roles as strings.
     */
    Collection<String> getList();

    /**
     * Checks if the name exist in this list.
     */
    boolean has(String name);
}
