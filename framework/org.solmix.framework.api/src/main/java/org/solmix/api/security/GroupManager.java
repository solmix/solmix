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

import java.util.Collection;
import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.security.auth.ACL;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-26
 */

public interface GroupManager
{

    /**
     * @throws UnsupportedOperationException if the implementation does not support writing
     */
    public Group createGroup(String name) throws SlxException;

    /**
     * @throws UnsupportedOperationException if the implementation does not support writing
     */
    public Group getGroup(String name) throws SlxException;

    /**
     * Get all groups defined in the system.
     */
    public Collection<Group> getAllGroups() throws UnsupportedOperationException;

    /**
     * Get all groups related to one concrete group.
     */
    public Collection<String> getAllGroups(String groupName) throws SlxException;

    public Map<String, ACL> getACLs(String group);

    /**
     * Grants to the group a role.
     * 
     * @return Group object with the role already granted.
     */
    public Group addRole(Group group, String roleName) throws SlxException;

    /**
     * Adds to the group to a group.
     * 
     * @return group object with the group already assigned.
     */
    public Group addGroup(Group group, String groupName) throws SlxException;
}
