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

import java.util.List;

import org.solmix.api.security.AccessManager;
import org.solmix.api.security.Permission;
import org.solmix.commons.util.DataUtil;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-29
 */

public class AccessManagerImpl implements AccessManager
{

    private static final long serialVersionUID = -5752602071966575220L;

    private List<Permission> permissions;

    public AccessManagerImpl(List<Permission> permissions)
    {
        this.permissions = permissions;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.AccessManager#isGranted(java.lang.String, long)
     */
    @Override
    public boolean isGranted(String path, long permissions) {
        if (DataUtil.isNullOrEmpty(path))
            path = "/";//$NON-NLS-1$
        long currentPermission = getPermissions(path);
        boolean granted = (currentPermission & permissions) == permissions;

        return granted;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.AccessManager#setPermissionList(java.util.List)
     */
    @Override
    public void setPermissionList(List<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.AccessManager#getPermissionList()
     */
    @Override
    public List<Permission> getPermissionList() {
        return permissions;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.AccessManager#getPermissions(java.lang.String)
     */
    @Override
    public long getPermissions(String path) {
        if (permissions == null) {
            return Permission.NONE;
        }
        long permission = 0;
        int patternLength = 0;
        for (Permission p : permissions) {
            if (p.match(path)) {
                int l = p.getPattern().getLength();
                if (patternLength == l && (permission < p.getPermissions())) {
                    permission = p.getPermissions();
                } else if (patternLength < l) {
                    patternLength = l;
                    permission = p.getPermissions();
                }
            }
        }
        return permission;
    }

}
