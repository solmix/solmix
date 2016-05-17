/**
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.identity;

import java.security.BasicPermission;
import java.security.Permission;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年4月4日
 */

public class NamespacePermission extends BasicPermission {

    private static final long serialVersionUID = 662669153166711545L;

    public static final String ADD_NAMESPACE = "add";

    public static final String ALL_NAMESPACE = "all";

    public static final String CONTAINS_NAMESPACE = "contains";

    public static final String GET_NAMESPACE = "get";

    public static final String REMOVE_NAMESPACE = "remove";

    protected String actions;

    public NamespacePermission(String s) {
        super(s);
    }

    public NamespacePermission(String s, String s1) {
        super(s, s1);
        actions = s1;
    }

    @Override
    public String getActions() {
        return actions;
    }

    @Override
    public boolean implies(Permission p) {
        return false;
    }
}
