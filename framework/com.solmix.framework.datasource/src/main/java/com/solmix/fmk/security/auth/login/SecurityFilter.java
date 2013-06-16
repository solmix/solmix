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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.security.SecurityAdmin;
import com.solmix.api.security.auth.login.LoginResult;
import com.solmix.fmk.context.SlxContext;
import com.solmix.fmk.filter.ContextFilter;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-10
 */

public class SecurityFilter extends ContextFilter implements Filter
{

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private SecurityAdmin securityAdmin;

    public static final String ATTRIBUTE_LOGIN_RESULT = "LoginResult";

    private Collection<LoginHandler> loginHandlers = new ArrayList<LoginHandler>();

    private String jaasModule;

    /**
     * @return the securityAdmin
     */
    public SecurityAdmin getSecurityAdmin() {
        return securityAdmin;
    }

    /**
     * @param securityAdmin the securityAdmin to set
     */
    public void setSecurityAdmin(SecurityAdmin securityAdmin) {
        this.securityAdmin = securityAdmin;
    }

    /**
     * @return the jaasModule
     */
    public String getJaasModule() {
        return jaasModule;
    }

    /**
     * @param jaasModule the jaasModule to set
     */
    public void setJaasModule(String jaasModule) {
        this.jaasModule = jaasModule;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        loginHandlers.add(new FormLogin(securityAdmin, jaasModule));
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.filter.AbstractFilter#doFilter(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
        for (LoginHandler handler : this.getLoginHandlers()) {
            LoginResult loginResult = handler.handle(request, response);
            setCurrentLoginResult(loginResult);
            if(loginResult==null){
                continue;
            } else  if (loginResult.getStatus() == LoginResult.STATUS_IN_PROCESS) {
                // special handling to support multi step login mechanisms like ntlm
                // do not continue with the filter chain
                return;
            } else if (loginResult.getStatus() == LoginResult.STATUS_SUCCEEDED) {
                Subject subject = loginResult.getSubject();
                if (subject == null) {
                    log.error("Invalid login result from handler [" + handler.getClass().getName() + "] returned STATUS_SUCCEEDED but no subject");
                    throw new ServletException("Invalid login result");
                }
                if (request.getSession(false) != null) {
                    request.getSession().invalidate();
                }
                SlxContext.login(subject);
                // AuditLoggingUtil.log(loginResult, request);
                // do not continue the login handler chain after a successful login ... otherwise previous success will
                // be invalidated by above session wipeout
                break;
            } else {
                log.debug("Login request::", request);
            }
        }
        // continue even if all login handlers failed
        chain.doFilter(request, response);
    }

    /**
     * @param loginResult
     */
    public static void setCurrentLoginResult(LoginResult loginResult) {
        SlxContext.setAttribute(ATTRIBUTE_LOGIN_RESULT, loginResult);

    }

    /**
     * @return the loginHandlers
     */
    public Collection<LoginHandler> getLoginHandlers() {
        return loginHandlers;
    }

    /**
     * @param loginHandlers the loginHandlers to set
     */
    public void setLoginHandlers(Collection<LoginHandler> loginHandlers) {
        this.loginHandlers = loginHandlers;
    }

}
