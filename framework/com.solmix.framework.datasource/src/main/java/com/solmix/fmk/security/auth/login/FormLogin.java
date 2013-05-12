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

package com.solmix.fmk.security.auth.login;

import static com.solmix.commons.util.DataUtil.isNotNullAndEmpty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.SecurityAdmin;
import com.solmix.api.security.auth.callback.CredentialsCallbackHandler;
import com.solmix.api.security.auth.login.LoginResult;
import com.solmix.fmk.security.auth.callback.PlainTextCallbackHandler;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-11
 */

public class FormLogin implements LoginHandler
{

    private static final Logger log = LoggerFactory.getLogger(FormLogin.class);

    public static final String PARAMETER_USER_ID = "UserId";

    public static final String PARAMETER_PSWD = "UserPSWD";

    public static final String PARAMETER_REALM = "Realm";

    private final SecurityAdmin securityAdmin;

    private String jaasModuleName = SecurityAdmin.DEFAULT_JAAS_LOGIN_CHAIN;

    /**
     * @param securityAdmin
     */
    public FormLogin(SecurityAdmin securityAdmin, String jaasModuleName)
    {
        this.securityAdmin = securityAdmin;
        this.jaasModuleName = jaasModuleName;
    }

    /**
     * @return the jaasModuleName
     */
    public String getJaasModuleName() {
        return jaasModuleName;
    }

    /**
     * @param jaasModuleName the jaasModuleName to set
     */
    public void setJaasModuleName(String jaasModuleName) {
        this.jaasModuleName = jaasModuleName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.security.auth.login.LoginHandler#handle(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public LoginResult handle(HttpServletRequest request, HttpServletResponse response) {
        String userid = request.getParameter(PARAMETER_USER_ID);
        log.debug("handle login for {}", userid);
        if (isNotNullAndEmpty(userid)) {
            String pswd = request.getParameter(PARAMETER_PSWD);
            String realm = request.getParameter(PARAMETER_REALM);
            CredentialsCallbackHandler callbackHandler = new PlainTextCallbackHandler(userid, pswd.toCharArray(), realm);
            return securityAdmin.authenticate(callbackHandler, jaasModuleName);

        }
        return null;
    }

}
