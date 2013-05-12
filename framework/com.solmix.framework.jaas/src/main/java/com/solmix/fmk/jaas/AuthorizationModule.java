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

package com.solmix.fmk.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.GroupManager;
import com.solmix.api.security.Permission;
import com.solmix.api.security.RoleManager;
import com.solmix.api.security.User;
import com.solmix.api.security.UserManager;
import com.solmix.api.security.auth.ACL;
import com.solmix.api.security.auth.GroupList;
import com.solmix.api.security.auth.PrincipalCollection;
import com.solmix.api.security.auth.RoleList;
import com.solmix.fmk.jaas.principal.GroupListImpl;
import com.solmix.fmk.jaas.principal.RoleListImpl;
import com.solmix.fmk.security.PrincipalUtil;
import com.solmix.fmk.security.auth.ACLImpl;
import com.solmix.fmk.security.auth.PrincipalCollectionImpl;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-7
 */

public class AuthorizationModule extends AbstractLoginModule
{

    private UserManager userManager;

    private User user;

    private static final Logger log = LoggerFactory.getLogger(AuthorizationModule.class);

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {

        super.initialize(subject, callbackHandler, sharedState, options);
        userManager = this.securitySupport.getSecurityAdmin().getUserManager();
    }

    // do nothing here, we are only responsible for authorization, not authentication!
    @Override
    public boolean login() throws LoginException {
        this.success = true;
        this.setSharedStatus(STATUS_SUCCEEDED);
        return this.success;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.jaas.AbstractLoginModule#setEntity()
     */
    @Override
    public void setEntity() {
        // TODO Auto-generated method stub

    }

    /**
     * Authentication Module not set Access Control Lists.
     */
    @Override
    public void setACL() {
        String[] roles = getRoleNames().toArray(new String[getRoleNames().size()]);
        String[] groups = getGroupNames().toArray(new String[getGroupNames().size()]);

        if (log.isDebugEnabled()) {
            log.debug("Roles: {}", ArrayUtils.toString(roles));
            log.debug("Groups: {}", ArrayUtils.toString(groups));
        }
        addRoles(roles);
        addGroups(groups);
        PrincipalCollection principalList = new PrincipalCollectionImpl();
        setACLForRoles(roles, principalList);
        setACLForGroups(groups, principalList);
        user = PrincipalUtil.findPrincipal(subject, User.class);
        if (user == null && this.name != null) {
            user = this.userManager.getUser(name);
        }
        if (user != null) {
            setACLForUser(user, principalList);
        }
        if (log.isDebugEnabled()) {
            for (Iterator<Principal> iterator = principalList.iterator(); iterator.hasNext();) {
                Principal principal = iterator.next();
                log.debug("ACL: {}", principal);
            }
        }
        // set principal list, a set of info.magnolia.jaas.principal.ACL
        this.subject.getPrincipals().add(principalList);
    }

    /**
     * @param groups
     * @param principalList
     */
    private void setACLForGroups(String[] groups, PrincipalCollection principalList) {
        for (String group : groups) {
            GroupManager groupManager = securitySupport.getSecurityAdmin().getGroupManager();
            mergePrincipals(principalList, groupManager.getACLs(group).values());

        }
    }

    private void setACLForUser(User user, PrincipalCollection principalList) {
        mergePrincipals(principalList, this.userManager.getACLs(user).values());

    }

    private void mergePrincipals(PrincipalCollection principalList, Collection<ACL> principals) {
        for (ACL princ : principals) {
            if (principalList.contains(princ.getName())) {
                ACL oldACL = (ACL) principalList.get(princ.getName());
                Collection<Permission> permissions = new HashSet<Permission>(oldACL.getList());
                permissions.addAll(princ.getList());
                principalList.remove(oldACL);
                princ = new ACLImpl(princ.getName(), new ArrayList<Permission>(permissions));
            }
            principalList.add(princ);
        }
    }

    /**
     * @param roles
     * @param principalList
     */
    private void setACLForRoles(String[] roles, PrincipalCollection principalList) {
        for (String role : roles) {
            RoleManager roleManager = securitySupport.getSecurityAdmin().getRoleManager();
            mergePrincipals(principalList, roleManager.getACLs(role).values());

        }

    }

    /**
     * @param groups
     */
    private void addGroups(String[] groups) {
        GroupList groupList = new GroupListImpl();
        for (Iterator<String> iterator = getGroupNames().iterator(); iterator.hasNext();) {
            String group = iterator.next();
            groupList.add(group);
        }
        this.subject.getPrincipals().add(groupList);

    }

    /**
     * @param roles
     */
    private void addRoles(String[] roles) {
        RoleList roleList = new RoleListImpl();
        for (Iterator<String> iterator = getRoleNames().iterator(); iterator.hasNext();) {
            String role = iterator.next();
            roleList.add(role);
        }
        this.subject.getPrincipals().add(roleList);

    }

    /**
     * Authorization Module not validate user identity.
     */
    @Override
    public void validateUser() throws LoginException {
    }

}
