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

package com.solmix.api.security;

import java.security.Principal;

/**
 * 
 * @version 110035 2012-9-26
 */

public interface Realm extends Principal
{

    /**
     * The realm for the admin interface.
     */
    public static final Realm REALM_ADMIN = new RealmImpl("admin");

    /**
     * No realm --> all users.
     */
    public static final Realm REALM_ALL = new RealmImpl("all");

    /**
     * Contains not removable system users: anonymous, superuser.
     */
    public static final Realm REALM_SYSTEM = new RealmImpl("system");

    /**
     * The default realm is {@link Realm#REALM_ALL}.
     */
    public static final Realm DEFAULT_REALM = REALM_ALL;

    /**
     * Implementation of the realm. Enum would be easier to read, but would not be backward compatible.
     * 
     * @author had
     * @version $Id: Realm.java 50229 2011-10-20 16:10:16Z tmattsson $
     */
    class RealmImpl implements Realm
    {

        private final String name;

        public RealmImpl(String name)
        {
            if (name == null) {
                throw new NullPointerException("Realm name can't be null!");
            }
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Realm)) {
                return false;
            }
            return this.name.equals(((Realm) o).getName());
        }
    }

    /**
     * Factory for providing realms.
     */
    public class Factory
    {

        public static Realm newRealm(String realmName) {
            if (REALM_ADMIN.getName().equals(realmName))
                return REALM_ADMIN;
            if (REALM_ALL.getName().equals(realmName))
                return REALM_ALL;
            if (REALM_SYSTEM.getName().equals(realmName))
                return REALM_SYSTEM;
            return new RealmImpl(realmName);
        }
    }
}
