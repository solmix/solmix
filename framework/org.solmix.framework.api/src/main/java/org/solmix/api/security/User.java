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
import java.security.Principal;
import java.util.Collection;

/**
 * Represents a User.
 * 
 * @version 0.1 2012-9-26
 * @since 0.1
 */

public interface User extends Principal, Serializable
{

    public static final String RPOP_FAILED_LOGIN_ATTEMPTS = "failedLoginAttempts";

    public static final String PROP_ENABLE = "enable";

    public static final String RPOP_RELEASE_TIME = "releaseTime";

    public static final String RPOP_LANGUAGE = "language";

    public static final String PROP_PASSWORD = "pswd";

    /**
     * Is this user in a specified role?
     * 
     * @param roleName the name of the role
     * @return true if in role
     */
    boolean hasRole(String roleName);

    /**
     * Is this user in a specified group?
     * 
     * @return true if in group
     */
    boolean inGroup(String groupName);

    /**
     * Returns false if the user was explicitly disabled. Implementations should return true by default if the status is
     * unknown.
     */
    boolean isEnabled();

    String getLanguage();

    @Override
    String getName();

    String getPassword();

    /**
     * Gets an arbitrary property from this user.
     */
    String getProperty(String propertyName);

    /**
     * Gets user identifier.
     */
    String getIdentifier();

    /**
     * Get groups that are directly assigned to the user.
     */
    Collection<String> getGroups();

    /**
     * Get all groups to which this user belongs to, collected recursively including.
     */
    Collection<String> getAllGroups();

    /**
     * Get roles that are directly assigned to the user.
     */
    Collection<String> getRoles();

    /**
     * Get all roles assigned to this user, collected recursively including groups/subgroups.
     */
    Collection<String> getAllRoles();

    int getFailedLoginAttempts();

    long getReleaseTime();
}
