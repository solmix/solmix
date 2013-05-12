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

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.GroupManager;
import com.solmix.api.security.Realm;
import com.solmix.api.security.RoleManager;
import com.solmix.api.security.SecurityAdmin;
import com.solmix.api.security.UserManager;
import com.solmix.api.security.auth.callback.CredentialsCallbackHandler;
import com.solmix.api.security.auth.login.LoginResult;

/**
 * 
 * @version 110035 2012-9-29
 * @since 0.1
 */

public class SecurityAdminImpl implements SecurityAdmin
{

    private static final Logger log = LoggerFactory.getLogger(SecurityAdminImpl.class);

    private UserManagerImpl userManager;

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.SecurityAdmin#getUserManager()
     */
    @Override
    public UserManager getUserManager() {
        if (userManager == null) {
            userManager = UserManagerImpl.getInstance();
        }
        return userManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.SecurityAdmin#getRoleManager()
     */
    @Override
    public RoleManager getRoleManager() {
        return RoleManagerImpl.getInstance();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.SecurityAdmin#getGroupManager()
     */
    @Override
    public GroupManager getGroupManager() {
        return GroupManagerImpl.getInstance();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.security.SecurityAdmin#getUserManager(java.lang.String)
     */
    @Override
    public UserManager getUserManager(String realmName) {
        if (Realm.REALM_ALL.getName().equals(realmName)) {
            return getUserManager();
        }
        return userManager.get(realmName);
    }

    /**
     * login using {@link com.solmix.fmk.context.ContextTools#login login}
     * 
     * @see com.solmix.api.security.SecurityAdmin#authenticate(com.solmix.api.security.auth.callback.CredentialsCallbackHandler,
     *      java.lang.String)
     */
    @Override
    public LoginResult authenticate(CredentialsCallbackHandler callbackHandler, String jaasModuleName) {
        Subject subject;
        try {
            LoginContext loginContext = createLoginContext(callbackHandler, jaasModuleName);
            loginContext.login();
            subject = loginContext.getSubject();

            return new LoginResult(LoginResult.STATUS_SUCCEEDED, subject);
        } catch (LoginException e) {
            logLoginException(e);
            return new LoginResult(LoginResult.STATUS_FAILED, e);
        }
    }

    /**
     * Logs plain LoginException in error level, but subclasses in debug, since they are specifically thrown when a
     * known error occurs (wrong password, blocked account, etc.).
     */
    private void logLoginException(LoginException e) {
        if (e.getClass().equals(LoginException.class)) {
            log.error("Can't login due to: ", e);
        } else {
            // specific subclasses were added in Java5 to identify what the login failure was
            log.debug("Can't login due to: ", e);
        }
    }

    /**
     * @param callbackHandler
     * @param customLoginModule Used <code>customLoginModule</code> login module from JAAS configuration file.If not set
     *        used default.
     * @return
     * @throws LoginException
     */
    protected static LoginContext createLoginContext(CredentialsCallbackHandler callbackHandler, String customLoginModule) throws LoginException {
        final String loginContextName = StringUtils.defaultString(customLoginModule, DEFAULT_JAAS_LOGIN_CHAIN);
        return new LoginContext(loginContextName, callbackHandler);
    }
}
