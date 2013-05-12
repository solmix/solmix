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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.User;
import com.solmix.api.security.UserManager;

/**
 * 
 * @version 110035 2012-10-15
 */

public class AuthenticationModule extends AbstractLoginModule
{

    private static final Logger log = LoggerFactory.getLogger(AuthenticationModule.class);

    private UserManager userManager;

    private User user;

    /**
     * @throws LoginException
     * 
     */
    @Override
    public void validateUser() throws LoginException {
        user = userManager.getUser(name);
        if (this.getMaxAttempts() > 0 && !UserManager.ANONYMOUS_USER.equals(user.getName()) && getTimeLock() > 0) {
            Calendar currentTime = new GregorianCalendar(TimeZone.getDefault());
            Calendar lockTime = new GregorianCalendar(TimeZone.getDefault());
            if (user.getReleaseTime() != 0) {
                lockTime.clear();
                lockTime.setTime(new Date(user.getReleaseTime()));
                if (lockTime.after(currentTime)) {
                    throw new LoginException("User account " + this.name + " is locked until " + new Date(user.getReleaseTime()) + ".");
                }
            }

        }
        String serverPassword = user.getPassword();
        if (this.user == null) {
            throw new AccountNotFoundException("User account " + this.name + " not found.");
        }
        if (!this.user.isEnabled()) {
            throw new AccountLockedException("User account " + this.name + " is locked.");
        }
        if (serverPassword == null || serverPassword.isEmpty()) {
            throw new FailedLoginException("Does not allow login to users with no password.");
        }
        String encrypedPsd = getEncryptedPassword(new String(this.pswd));
        boolean match = checkPassword(serverPassword, encrypedPsd);
        if (!match) {
            if (this.getMaxAttempts() > 0 && !UserManager.ANONYMOUS_USER.equals(user.getName())) {
                userManager.setProperty(user, User.RPOP_FAILED_LOGIN_ATTEMPTS, user.getFailedLoginAttempts() + 1);

                // hard lock
                if (user.getFailedLoginAttempts() > this.getMaxAttempts() && this.getTimeLock() <= 0) {
                    userManager.setProperty(user, User.PROP_ENABLE, Boolean.FALSE);
                    userManager.setProperty(user, User.RPOP_FAILED_LOGIN_ATTEMPTS, new Integer(0));

                }
                // time period lock.
                else if (user.getFailedLoginAttempts() > this.getMaxAttempts() && this.getTimeLock() > 0) {
                    userManager.setProperty(user, User.RPOP_FAILED_LOGIN_ATTEMPTS, new Integer(0));
                    Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
                    calendar.add(Calendar.MINUTE, (int) getTimeLock());
                    userManager.setProperty(user, User.RPOP_RELEASE_TIME, new Long(calendar.getTime().getTime()));
                }

            }
            throw new FailedLoginException("Passwords do not match");

        }

    }

    /**
     * Get number of failed login attempts before locking account.
     */
    public int getMaxAttempts() {

        return userManager.getMaxFailedLoginAttempts();
    }

    /**
     * Get time period for time lockout.
     */
    public long getTimeLock() {
        return userManager.getLockTimePeriod();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.jaas.AbstractLoginModule#setEntity()
     */
    @Override
    public void setEntity() {
        this.subject.getPrincipals().add(user);
        for (String group : user.getAllGroups()) {
            this.addGroupName(group);
        }
        for (String role : user.getAllRoles()) {
            this.addRoleName(role);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.jaas.AbstractLoginModule#setACL()
     */
    @Override
    public void setACL() {
        // NULL

    }

}
