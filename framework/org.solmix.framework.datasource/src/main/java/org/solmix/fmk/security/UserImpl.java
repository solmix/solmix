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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.security.GroupManager;
import org.solmix.api.security.SecurityAdmin;
import org.solmix.api.security.User;

/**
 * 
 * @version 1.0-SNAPSHOT.
 */

public class UserImpl extends AbstractUser implements User, Serializable
{

    private static final long serialVersionUID = -1822500810059553419L;

    private static final Logger log = LoggerFactory.getLogger(UserImpl.class);

    private final Map<String, String> properties;

    private final Collection<String> groups;

    private final Collection<String> roles;

    private final String realm;

    private final String name;

    private final String language;

    private boolean enabled = true;

    private final String encodedPassword;

    private final SecurityAdmin securityAdmin;

    public UserImpl(String name, String realm, Collection<String> groups, Collection<String> roles, Map<String, String> properties,
        SecurityAdmin securityAdmin)
    {
        this.name = name;
        this.securityAdmin = securityAdmin;
        this.roles = Collections.unmodifiableCollection(roles);
        this.groups = Collections.unmodifiableCollection(groups);
        this.properties = Collections.unmodifiableMap(properties);
        this.realm = realm;
        // shortcut some often accessed props so we don't have to search hashmap for them.
        this.language = properties.get(User.RPOP_LANGUAGE);
        String enbld = properties.get(User.PROP_ENABLE);
        enabled = enbld == null ? true : Boolean.parseBoolean(enbld);
        encodedPassword = properties.get(User.PROP_PASSWORD);

    }

    @Override
    public boolean hasRole(String roleName) {
        log.debug("hasRole({})", roleName);
        return false;
    }

    @Override
    public boolean inGroup(String groupName) {
        log.debug("inGroup({})", groupName);
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        log.debug("isEnabled()");
        return enabled;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getLanguage()
     */
    @Override
    public String getLanguage() {
        log.debug("getLanguage()=>{}", language);
        return this.language;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getName()
     */
    @Override
    public String getName() {
        log.debug("getName()=>{}", name);
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getPassword()
     */
    @Override
    public String getPassword() {
        log.debug("getPassword()");
        return encodedPassword;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(String propertyName) {
        log.debug("getProperty({})", propertyName);
        return properties.get(propertyName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getGroups()
     */
    @Override
    public Collection<String> getGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getAllGroups()
     */
    @Override
    public Collection<String> getAllGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getRoles()
     */
    @Override
    public Collection<String> getRoles() {
        log.debug("getRoles()");
        return roles;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getAllRoles()
     */
    @Override
    public Collection<String> getAllRoles() {
        log.debug("get roles for {}", getName());
        final Set<String> allRoles = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        final Collection<String> roles = getRoles();

        // add all direct user groups
        allRoles.addAll(roles);

        Collection<String> allGroups = getAllGroups();
        GroupManager groupManager = this.securityAdmin.getGroupManager();
        for (String group : allGroups) {

            try {
                allRoles.addAll(groupManager.getGroup(group).getRoles());
            } catch (SlxException e) {
                log.debug("Skipping denied group " + group + " for user " + getName(), e);
            } catch (UnsupportedOperationException e) {
                log.debug("Skipping unsupported  getGroup() for group " + group + " and user " + getName(), e);
            }
        }
        return allGroups;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getFailedLoginAttempts()
     */
    @Override
    public int getFailedLoginAttempts() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.security.User#getReleaseTime()
     */
    @Override
    public long getReleaseTime() {
        // TODO Auto-generated method stub
        return 0;
    }

}
