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

package org.solmix.fmk.security;

import java.security.Principal;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.solmix.api.security.User;
import org.solmix.api.security.auth.ACL;
import org.solmix.api.security.auth.PrincipalCollection;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-8
 */

public class PrincipalUtil
{

    public static Subject createSubject(User user) {
        Subject subject = new Subject();
        subject.getPrincipals().add(user);
        return subject;
    }

    public static <T extends Principal> T findPrincipal(Subject subject, Class<T> clazz) {
        return findPrincipal(subject.getPrincipals(), clazz, null);
    }

    public static ACL findAccessControlList(Iterable<Principal> principals, String name) {
        return findPrincipal(principals, ACL.class, name);
    }

    public static ACL findAccessControlList(Subject subject, String name) {
        return findPrincipal(subject.getPrincipals(), ACL.class, name);
    }

    public static <T extends Principal> T removePrincipal(Iterable<Principal> principals, Class<T> clazz) {
        return removePrincipal(principals, clazz, null);
    }

    private static <T extends Principal> T findPrincipal(Iterable<Principal> principals, Class<T> clazz, String name) {
        for (Principal principal : principals) {
            if (matches(principal, clazz, name)) {
                return (T) principal;
            }
            if (principal instanceof PrincipalCollection) {
                T t = findPrincipal((PrincipalCollection) principal, clazz, name);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    private static <T extends Principal> T removePrincipal(Iterable<Principal> principals, Class<T> clazz, String name) {
        for (Iterator<Principal> iterator = principals.iterator(); iterator.hasNext();) {
            Principal principal = iterator.next();
            if (matches(principal, clazz, name)) {
                iterator.remove();
                return (T) principal;
            }
            if (principal instanceof PrincipalCollection) {
                T t = removePrincipal((PrincipalCollection) principal, clazz, name);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    private static boolean matches(Principal principal, Class<? extends Principal> clazz, String name) {
        return (clazz == null || clazz.isAssignableFrom(principal.getClass())) && (name == null || name.equals(principal.getName()));
    }
}
