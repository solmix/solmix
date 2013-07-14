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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.solmix.api.exception.SlxException;
import org.solmix.api.security.User;
import org.solmix.api.security.UserManager;
import org.solmix.api.security.auth.ACL;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.context.SlxContext.Op;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-10
 */

public class UserManagerImpl extends UserManagerBase implements UserManager
{

    private static UserManagerImpl instance;

    UserManagerImpl()
    {

    }

    public static synchronized UserManagerImpl getInstance() {
        if (instance == null) {
            instance = new UserManagerImpl();
        }
        return instance;
    }

    private int maxFailedLoginAttempts;

    private int lockTimePeriod;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getUser(java.lang.String)
     */
    @Override
    public User getUser(String name) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getUserById(java.lang.String)
     */
    @Override
    public User getUserById(String id) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getSystemUser()
     */
    @Override
    public User getSystemUser() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getAnonymousUser()
     */
    @Override
    public User getAnonymousUser() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getAllUsers()
     */
    @Override
    public Collection<User> getAllUsers() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

 
    @Override
    public User createUser(String name, String pw) throws SlxException {
       validateUserName(name);
        return SlxContext.doInSystemContext(new Op<User,SlxException>(){

            @Override
            public User exe() throws SlxException {
                // TODO Auto-generated method stub
                return null;
            }
            
        });
    }

    

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#changePassword(org.solmix.api.security.User, java.lang.String)
     */
    @Override
    public User changePassword(User user, String newPassword) throws UnsupportedOperationException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#setProperty(org.solmix.api.security.User, java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public User setProperty(User user, String propertyName, Object propertyValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#setLockTimePeriod(int)
     */
    @Override
    public void setLockTimePeriod(int lockTimePeriod) throws UnsupportedOperationException {
        this.lockTimePeriod = lockTimePeriod;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getLockTimePeriod()
     */
    @Override
    public int getLockTimePeriod() throws UnsupportedOperationException {
        return lockTimePeriod;
    }

    @Override
    public void setMaxFailedLoginAttempts(int maxFailedLoginAttempts) throws UnsupportedOperationException {
        this.maxFailedLoginAttempts = maxFailedLoginAttempts;

    }

    @Override
    public int getMaxFailedLoginAttempts() throws UnsupportedOperationException {
        return this.maxFailedLoginAttempts;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#addRole(org.solmix.api.security.User, java.lang.String)
     */
    @Override
    public User addRole(User user, String roleName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#addGroup(org.solmix.api.security.User, java.lang.String)
     */
    @Override
    public User addGroup(User user, String groupName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#updateLastAccessTimestamp(org.solmix.api.security.User)
     */
    @Override
    public void updateLastAccessTimestamp(User user) throws UnsupportedOperationException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#hasAny(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasAny(String principal, String resourceName, String resourceType) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#getACLs(org.solmix.api.security.User)
     */
    @Override
    public Map<String, ACL> getACLs(User user) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#removeGroup(org.solmix.api.security.User, java.lang.String)
     */
    @Override
    public User removeGroup(User user, String groupName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.UserManager#removeRole(org.solmix.api.security.User, java.lang.String)
     */
    @Override
    public User removeRole(User user, String roleName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param realmName
     * @return
     */
    @Override
    public UserManager get(String realmName) {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * @param name 
     * 
     */
    protected void validateUserName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException(name + " is not a valid username.");
        }
        
    }

}
