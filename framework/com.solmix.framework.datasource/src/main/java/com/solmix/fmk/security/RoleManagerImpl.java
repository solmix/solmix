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

package com.solmix.fmk.security;

import java.util.Map;

import com.solmix.api.security.Role;
import com.solmix.api.security.RoleManager;
import com.solmix.api.security.auth.ACL;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-14
 */

public class RoleManagerImpl implements RoleManager
{

    private static RoleManager instance;

    RoleManagerImpl()
    {

    }

    public static synchronized RoleManager getInstance() {
        if (instance == null) {
            instance = new RoleManagerImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#createRole(java.lang.String)
     */
    @Override
    public Role createRole(String name) throws UnsupportedOperationException, Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#getRole(java.lang.String)
     */
    @Override
    public Role getRole(String name) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#getACLs(java.lang.String)
     */
    @Override
    public Map<String, ACL> getACLs(String role) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#addPermission(com.solmix.api.security.Role, java.lang.String,
     *      java.lang.String, long)
     */
    @Override
    public void addPermission(Role role, String workspaceName, String path, long permission) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#removePermission(com.solmix.api.security.Role, java.lang.String,
     *      java.lang.String, long)
     */
    @Override
    public void removePermission(Role role, String workspace, String path, long permission) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.RoleManager#getRoleNameById(java.lang.String)
     */
    @Override
    public String getRoleNameById(String string) {
        // TODO Auto-generated method stub
        return null;
    }

}
