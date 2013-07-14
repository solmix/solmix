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
import java.util.List;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-28
 */

public interface AccessManager extends Serializable
{

    /**
     * Determines whether the specified permissions are granted to the given path.
     * 
     * @param path path for which permissions are checked
     * @param permissions permission mask
     * @return true if this access manager has permissions to the specified path
     */
    boolean isGranted(String path, long permissions);

    /**
     * Sets the list of permissions this manager will use to determine access, implementation is free to define the
     * structure of this list.
     * 
     * @param permissions
     */
    void setPermissionList(List<Permission> permissions);

    /**
     * Get permission list assigned to this access manager.
     */
    List<Permission> getPermissionList();

    /**
     * Get permissions assigned to the given path.
     * 
     * @see Permission all possible permissions
     * @param path for which permissions are requested
     * @return permission mask
     */
    long getPermissions(String path);
}
