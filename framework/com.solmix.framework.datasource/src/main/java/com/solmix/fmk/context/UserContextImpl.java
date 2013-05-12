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

package com.solmix.fmk.context;

import javax.security.auth.Subject;

import com.solmix.api.context.UserContext;
import com.solmix.api.security.User;
import com.solmix.api.security.UserManager;
import com.solmix.fmk.security.PrincipalUtil;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-8
 */

public class UserContextImpl extends AbstractContext implements UserContext
{

    private User user;

    private Subject subject;

    private String SESSION_SUBJECT = Subject.class.getName();

    public User getUser() {
        if (user != null) {
            return user;
        }

        user = PrincipalUtil.findPrincipal(getSubject(), User.class);
        if (user == null) {
            throw new IllegalStateException("Subject must have a com.solmix.api.security.User principal.");
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.context.AbstractContext#getSubject()
     */
    @Override
    public Subject getSubject() {
        if (subject != null) {
            return this.subject;
        }

        // were we logged in by a previous request?
        subject = (Subject) getAttribute(SESSION_SUBJECT, Scope.SESSION);
        if (subject != null) {
            return this.subject;
        }

        // default to anonymous user
        login(getAnonymousSubject());
        return subject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.UserContext#login(javax.security.auth.Subject)
     */
    @Override
    public void login(Subject subject) {
        User user = PrincipalUtil.findPrincipal(subject, User.class);
        if (user == null) {
            throw new IllegalArgumentException("When logging in the Subject must have a com.solmix.api.security.User principal.");
        }
        this.subject = subject;
        this.user = user;
        if (!user.getName().equals(UserManager.ANONYMOUS_USER)) {
            setAttribute(SESSION_SUBJECT, subject, Scope.SESSION);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.context.UserContext#logout()
     */
    @Override
    public void logout() {
        subject = null;
        user = null;
        removeAttribute(SESSION_SUBJECT, Scope.SESSION);
    }

}
