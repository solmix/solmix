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

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.commons.lang.StringUtils;
import org.apache.karaf.jaas.modules.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.Realm;
import com.solmix.fmk.jaas.encryprtion.EncryptSupport;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-15
 */

public abstract class AbstractLoginModule implements LoginModule
{

    protected Logger log = LoggerFactory.getLogger(getClass());

    public static final String OPTION_REALM = "realm";

    protected Subject subject;

    protected CallbackHandler callbackHandler;

    protected Map<String, Object> sharedState;

    protected Map<String, Object> options;

    protected Object realm;

    protected boolean success;

    protected String name;

    protected char[] pswd;

    public static final String STATUS = "statusValue";

    public static final String GROUP_NAMES = "groupNames";

    public static final String ROLE_NAMES = "roleNames";

    public static final int STATUS_SUCCEEDED = 1;

    public static final int STATUS_FAILED = 2;

    public static final int STATUS_SKIPPED = 3;

    public static final int STATUS_UNAVAILABLE = 4;

    protected EncryptSupport encryptSupport;

    protected SecurityAdminSupport securitySupport;

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     *      javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        // don't overwrite group and roles set in the shared state
        if (this.sharedState.get(GROUP_NAMES) == null) {
            this.sharedState.put(GROUP_NAMES, new LinkedHashSet<String>());
        }
        if (this.sharedState.get(ROLE_NAMES) == null) {
            this.sharedState.put(ROLE_NAMES, new LinkedHashSet<String>());
        }
        String realmName = (String) options.get(OPTION_REALM);
        this.realm = StringUtils.isBlank(realmName) ? Realm.DEFAULT_REALM : Realm.Factory.newRealm(realmName);
        encryptSupport = new EncryptSupport(options);
        securitySupport = new SecurityAdminSupport(options);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        if (this.callbackHandler == null) {
            throw new LoginException("Error: no CallbackHandler available");
        }
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("name");
        callbacks[1] = new PasswordCallback("pswd", false);

        // if the realm is not defined in the jaas configuration
        // we ask use a callback to get the value
        // if (this.useRealmCallback) {
        // callbacks = (Callback[]) ArrayUtils.add(callbacks, new RealmCallback());
        // }

        this.success = false;
        try {
            this.callbackHandler.handle(callbacks);
            this.name = ((NameCallback) callbacks[0]).getName();
            this.pswd = ((PasswordCallback) callbacks[1]).getPassword();

            this.validateUser();
        } catch (IOException ioe) {
            log.debug("Exception caught", ioe);
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException ce) {
            log.debug(ce.getMessage(), ce);
            throw new LoginException(ce.getCallback().toString() + " not available");
        }

        // TODO: should not we set success BEFORE calling validateUser to give it chance to decide whether to throw an
        // exception or reset the value to false?
        this.success = true;
        this.setSharedStatus(STATUS_SUCCEEDED);
        return this.success;
    }

    /**
     * Sets shared status value to be used by subsequent LoginModule(s).
     * */
    public void setSharedStatus(int status) {
        this.sharedState.put(STATUS, new Integer(status));
    }

    public int getSharedStatus() {
        Integer status = (Integer) this.sharedState.get(STATUS);
        if (null != status) {
            return status.intValue();
        }
        return STATUS_UNAVAILABLE;
    }

    public void setGroupNames(Set<String> names) {
        this.getGroupNames().addAll(names);
    }

    public void addGroupName(String groupName) {
        getGroupNames().add(groupName);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getGroupNames() {
        return (Set<String>) this.sharedState.get(GROUP_NAMES);
    }

    public void setRoleNames(Set<String> names) {
        this.getRoleNames().addAll(names);
    }

    public void addRoleName(String roleName) {
        getRoleNames().add(roleName);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRoleNames() {
        return (Set<String>) this.sharedState.get(ROLE_NAMES);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit() throws LoginException {
        /**
         * If login module failed to authenticate then this method should simply return false instead of throwing an
         * exception - refer to specs for more details
         * */
        if (!this.success) {
            return false;
        }
        this.setEntity();
        this.setACL();
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort() throws LoginException {
        return release();
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout() throws LoginException {
        return release();
    }

    public String getEncryptedPassword(String password) {
        Encryption encryption = encryptSupport.getEncryption();
        String encryptionPrefix = encryptSupport.getEncryptionPrefix();
        String encryptionSuffix = encryptSupport.getEncryptionSuffix();

        if (encryption == null) {
            return password;
        } else {
            boolean prefix = encryptionPrefix == null || password.startsWith(encryptionPrefix);
            boolean suffix = encryptionSuffix == null || password.endsWith(encryptionSuffix);
            if (prefix && suffix) {
                return password;
            } else {
                String p = encryption.encryptPassword(password);
                if (encryptionPrefix != null) {
                    p = encryptionPrefix + p;
                }
                if (encryptionSuffix != null) {
                    p = p + encryptionSuffix;
                }
                return p;
            }
        }
    }

    public boolean checkPassword(String plain, String encrypted) {
        Encryption encryption = encryptSupport.getEncryption();
        String encryptionPrefix = encryptSupport.getEncryptionPrefix();
        String encryptionSuffix = encryptSupport.getEncryptionSuffix();

        if (encryption == null) {
            return plain.equals(encrypted);
        } else {
            boolean prefix = encryptionPrefix == null || encrypted.startsWith(encryptionPrefix);
            boolean suffix = encryptionSuffix == null || encrypted.endsWith(encryptionSuffix);
            if (prefix && suffix) {
                encrypted = encrypted.substring(encryptionPrefix != null ? encryptionPrefix.length() : 0, encrypted.length()
                    - (encryptionSuffix != null ? encryptionSuffix.length() : 0));
                return encryption.checkPassword(plain, encrypted);
            } else {
                return plain.equals(encrypted);
            }
        }
    }

    /**
     * Sets user details.
     */
    public abstract void setEntity();

    /**
     * Sets access control list from the user, roles and groups.
     */
    public abstract void setACL();

    /**
     * Checks if the credentials exist in the repository.
     * 
     * @throws LoginException or specific subclasses to report failures.
     */
    public abstract void validateUser() throws LoginException;

    /**
     * Releases all associated memory.subclass should override it.
     */
    public boolean release() {
        return true;
    }
}
