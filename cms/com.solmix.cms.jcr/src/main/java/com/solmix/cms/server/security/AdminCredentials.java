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

package com.solmix.cms.server.security;

import java.security.Principal;

import org.apache.jackrabbit.core.security.principal.AdminPrincipal;

/**
 * 
 * @author Administrator
 * @version 0.1.1 2012-8-20
 */

public final class AdminCredentials extends TrustedCredentials
{

    /**
     * 
     */
    private static final long serialVersionUID = 8149839664059444606L;

    /**
     * @param adminUser
     */
    public AdminCredentials(String adminUser)
    {
        super(adminUser);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.sling.jcr.jackrabbit.server.impl.security.TrustedCredentials#getPrincipal(java.lang.String)
     */
    @Override
    protected Principal getPrincipal(String userId) {
        return new AdminPrincipal(userId);
    }

}
