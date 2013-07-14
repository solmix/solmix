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

package org.solmix.api.security;

import java.io.Serializable;
import java.util.Collection;

import org.solmix.api.exception.SlxException;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-26
 */

public interface Group extends Serializable
{

    public String getName();

    public boolean hasRole(String roleName) throws SlxException;

    /**
     * Gets an arbitrary property from this group.
     */
    String getProperty(String propertyName);

    /**
     * Sets an arbitrary property for this group.
     */
    void setProperty(String propertyName, String value);

    /**
     * Get groups that are directly assigned to group.
     */
    public Collection<String> getGroups();

    /**
     * Get all groups assigned to this group, collected recursively from subgroups.
     * */
    public Collection<String> getAllGroups();

    /**
     * Get roles that are directly assigned to group.
     */
    public Collection<String> getRoles();

    /**
     * Gets identifier of the group.
     */
    public String getId();
}
