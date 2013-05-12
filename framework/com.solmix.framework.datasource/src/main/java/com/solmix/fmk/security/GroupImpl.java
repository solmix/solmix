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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.solmix.api.exception.SlxException;
import com.solmix.api.security.Group;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-14
 */

public class GroupImpl implements Group, Serializable
{

    private static final long serialVersionUID = 341317934696836505L;

    private final String name;

    private final Map<String, String> properties;

    private final Collection<String> groups;

    private final Collection<String> roles;

    private final String id;

    public GroupImpl(String id, String name, Collection<String> subgroupNames, Collection<String> roleNames)
    {
        this.id = id;
        this.name = name;
        this.groups = Collections.unmodifiableCollection(subgroupNames);
        this.roles = Collections.unmodifiableCollection(roleNames);
        this.properties = new HashMap<String, String>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#hasRole(java.lang.String)
     */
    @Override
    public boolean hasRole(String roleName) throws SlxException {
        return this.roles.contains(roleName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String propertyName, String value) {
        this.properties.put(propertyName, value);

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getGroups()
     */
    @Override
    public Collection<String> getGroups() {
        return this.groups;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getAllGroups()
     */
    @Override
    public Collection<String> getAllGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getRoles()
     */
    @Override
    public Collection<String> getRoles() {
        return Collections.unmodifiableCollection(this.roles);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.Group#getId()
     */
    @Override
    public String getId() {
        return id;
    }

}
