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

package org.solmix.cms.server.security;

import java.security.Principal;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.security.authentication.Authentication;

/**
 * 
 * @author Administrator
 * @version 0.1.1 2012-8-20
 */

public abstract class TrustedCredentials implements Credentials
{

    /**
     *
     */
    private static final long serialVersionUID = 5153578149776402602L;

    private Principal principal;

    private Authentication authentication;

    /**
     * @param userId
     */
    public TrustedCredentials(final String userId)
    {
        principal = getPrincipal(userId);
        authentication = new Authentication() {

            public boolean canHandle(Credentials credentials) {
                return (credentials instanceof AdminCredentials) || (credentials instanceof AnonCredentials);
            }

            public boolean authenticate(Credentials credentials) throws RepositoryException {
                System.out.println("1232312");
                return (credentials instanceof AdminCredentials) || (credentials instanceof AnonCredentials);
            }
        };
    }

    /**
     * @param userId
     * @return
     */
    protected abstract Principal getPrincipal(String userId);

    public Principal getPrincipal() {
        return principal;
    }

    /**
     * @return
     */
    protected Authentication getTrustedAuthentication() {
        return authentication;
    }

    /**
     * @return null
     */
    public Object getImpersonator() {
        return null;
    }
}
