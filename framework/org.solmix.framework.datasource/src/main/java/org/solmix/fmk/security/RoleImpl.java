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

package org.solmix.fmk.security;

import java.io.Serializable;
import java.util.Collection;

import org.solmix.api.security.Role;
import org.solmix.api.security.auth.ACL;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-14
 */

public class RoleImpl implements Role, Serializable
{

    private static final long serialVersionUID = 1249700771815418888L;

    private final String roleName;

    private final String roleId;

    private final Collection<ACL> acls;

    public RoleImpl(String name, String roleId, Collection<ACL> acls)
    {
        this.roleName = name;
        this.roleId = roleId;
        this.acls = acls;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.Role#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.Role#getId()
     */
    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

}
