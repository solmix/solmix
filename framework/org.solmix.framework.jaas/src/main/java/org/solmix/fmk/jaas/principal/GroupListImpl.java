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

package org.solmix.fmk.jaas.principal;

import org.solmix.api.security.auth.GroupList;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-12
 */

public class GroupListImpl extends AbstractPrincipalList implements GroupList
{

    private static final long serialVersionUID = -6336738727958870737L;

    @Override
    String getDefaultName() {
        return DEFAULT_NAME;
    }

}
