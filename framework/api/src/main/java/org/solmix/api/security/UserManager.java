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

package org.solmix.api.security;

import java.util.Collection;
import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.security.auth.ACL;

/**
 * Managed users.Implementor should register this interface as a osgi μ-service.
 * 
 * @version 0.1 2012-9-26
 * @since 0.1
 */

public interface UserManager
{

    public static final String SYSTEM_USER = "admin";

    public static final String SYSTEM_PSWD = "admin123";

    /**
     * Anonymous user name.
     */
    public static final String ANONYMOUS_USER = "anonymous";

    /**
     * Find a specific user. Not all implementations will support this method.
     * 
     * @param name the name of the user
     * @return the user object
     */
    public User getUser(String name) throws SlxException;

    /**
     * Find a specific user. Not all implementations will support this method.
     * 
     * @param id user identifier
     * @return the user object
     */
    public User getUserById(String id) throws SlxException;

    /**
     * Get system user, this user must always exist in magnolia repository.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public User getSystemUser() throws SlxException;

    /**
     * Get Anonymous user, this user must always exist in magnolia repository.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public User getAnonymousUser() throws SlxException;

    /**
     * Get all users.
     * 
     * @return collection of User objects
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public Collection<User> getAllUsers() throws SlxException;

    /**
     * Creates a user without security restrictions.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public User createUser(String name, String pw) throws SlxException;

    /**
     * Sets a new password.
     * 
     * @return user object with updated password.
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public User changePassword(User user, String newPassword) throws SlxException;

    /**
     * Sets given property for the user and returns updated user object with new value of the property.
     * 
     * @param user User to be updated. If property doesn't exist yet, it will be created. If the value is null, property
     *        will be removed if existing.
     * @param propertyName Name of the property.
     * @param propertyValue Value of the property.getString(), getBinary(), getDate(), getDecimal(), getLong(),
     *        getDouble() and getBoolean(). getStream().
     * 
     * @return
     */
    public User setProperty(User user, String propertyName, Object propertyValue);

    /* ---------- User Manager configuration ----------- */

    /**
     * Sets a time period for account lock.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public void setLockTimePeriod(int lockTimePeriod) throws SlxException;

    /**
     * Gets a time period for account lock.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public int getLockTimePeriod() throws SlxException;

    /**
     * Sets a number of failed attempts before locking account.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public void setMaxFailedLoginAttempts(int maxFailedLoginAttempts) throws SlxException;

    /**
     * Gets a number of failed attempts before locking account.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public int getMaxFailedLoginAttempts() throws SlxException;

    /**
     * Grants user role.
     * 
     * @return user object with the role already granted.
     */
    public User addRole(User user, String roleName);

    /**
     * Adds user to a group.
     * 
     * @return user object with the group already assigned.
     */
    public User addGroup(User user, String groupName);

    /**
     * Updates last access timestamp for the user.
     * 
     * @throws UnsupportedOperationException if the current implementation doesn't support this operation
     */
    public void updateLastAccessTimestamp(User user) throws SlxException;

    /**
     * Checks whether principal belongs to the named resource.
     * 
     * @param name principal name
     * @param resourceName either group or role name
     * @param resourceType either group or role see
     * @return
     */
    public boolean hasAny(String principal, String resourceName, String resourceType);

    /**
     * Returns all ACLs assigned to the given user.
     * 
     * @return
     */
    public Map<String, ACL> getACLs(User user);

    /**
     * Removes user from a group.
     * 
     * @return user object with the group assignment removed.
     */
    public User removeGroup(User user, String groupName);

    /**
     * Removes role from a user.
     * 
     * @return user object without removed role.
     */
    public User removeRole(User user, String roleName);
    public UserManager get(String realmName);
}
