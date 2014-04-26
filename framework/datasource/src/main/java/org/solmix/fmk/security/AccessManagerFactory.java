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

package org.solmix.fmk.security;

import java.util.List;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.security.AccessManager;
import org.solmix.api.security.Permission;
import org.solmix.api.security.User;

/**
 * 
 * @author Administrator
 * @version 110035 2012-9-29
 */

public class AccessManagerFactory
{

    private static final Logger log = LoggerFactory.getLogger(AccessManagerFactory.class);

    AccessManagerFactory()
    {

    }

    private static AccessManagerFactory instance;

    public synchronized static AccessManagerFactory getInstance() {
        if (instance == null) {
            instance = new AccessManagerFactory();
        }
        return instance;
    }

    public AccessManager get(Subject subject) {

        List<Permission> availablePermissions = getPermissions(subject);
        if (availablePermissions == null) {
            log.warn("no permissions found for " + subject.getPrincipals(User.class));
        }
        return new AccessManagerImpl(availablePermissions);

    }

    /**
     * @param subject
     * @return
     */
    private List<Permission> getPermissions(Subject subject) {
        // TODO Auto-generated method stub
        return null;
    }

}
