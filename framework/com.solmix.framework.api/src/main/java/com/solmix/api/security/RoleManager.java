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

package com.solmix.api.security;

import java.util.Map;

import com.solmix.api.security.auth.ACL;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-26
 */

public interface RoleManager
{

    /**
     * Create a role without any security restrictions.
     * 
     * @throws UnsupportedOperationException in case the role manager does not support this operation
     */
    public Role createRole(String name) throws UnsupportedOperationException, Exception;

    /**
     * Get the specific role without any security restrictions.
     * 
     * @throws UnsupportedOperationException in case the role manager does not support this operation
     */
    public Role getRole(String name) throws UnsupportedOperationException;

    /**
     * Obtain list of ACLs defined for specified role.
     * 
     * @throws UnsupportedOperationException in case the role manager does not support this operation
     */
    public Map<String, ACL> getACLs(String role) throws UnsupportedOperationException;

    /**
     * Add permission to the specified role, assuming current user has enough rights to perform such operation.
     */
    public void addPermission(Role role, String workspaceName, String path, long permission);

    /**
     * Remove permission from the specified role.
     */
    public void removePermission(Role role, String workspace, String path, long permission);

    /**
     * Retrieve role name by its identifier.
     */
    public String getRoleNameById(String string);
}
