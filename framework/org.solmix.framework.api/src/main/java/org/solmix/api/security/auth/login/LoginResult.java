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

package org.solmix.api.security.auth.login;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

/**
 * 
 * Login result contain subject.
 * 
 * @author solmix
 * @version 110035 2012-10-11
 */

public class LoginResult
{

    public static final int STATUS_NO_LOGIN = 0;

    /**
     * Returned if a login handler is not able to perform the operation.
     */
    public static LoginResult NOT_HANDLED = new LoginResult(LoginResult.STATUS_NOT_HANDLED);

    /**
     * Returned if a login handler was able to perform the operation but the authentication failed.
     */
    public static LoginResult NO_LOGIN = new LoginResult(STATUS_NO_LOGIN);

    private final int status;

    private LoginException loginException;

    private Subject subject;

    public static final int STATUS_IN_PROCESS = 4;

    public static final int STATUS_NOT_HANDLED = 3;

    public static final int STATUS_FAILED = 2;

    public static final int STATUS_SUCCEEDED = 1;

    public LoginResult(int status)
    {
        this.status = status;
    }

    public LoginResult(int status, LoginException loginException)
    {
        this.status = status;
        this.loginException = loginException;
    }

    public LoginResult(int status, Subject subject)
    {
        this.status = status;
        this.subject = subject;
    }

    public int getStatus() {
        return this.status;
    }

    public Subject getSubject() {
        return this.subject;
    }

    /**
     * @return an instance of {@link LoginException}. Warning: it can be null.
     */
    public LoginException getLoginException() {
        return this.loginException;
    }

    /*
     * public static void setCurrentLoginResult(LoginResult loginResult) {
     * ContextTools.setAttribute(ATTRIBUTE_LOGINERROR, loginResult); }
     * 
     * public static LoginResult getCurrentLoginResult() { LoginResult loginResult = (LoginResult)
     * ContextTools.getAttribute(LoginResult.ATTRIBUTE_LOGINERROR); if (loginResult == null) { loginResult = NO_LOGIN; }
     * return loginResult; }
     */
}
