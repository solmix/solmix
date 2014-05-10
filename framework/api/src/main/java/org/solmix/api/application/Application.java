/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.api.application;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.runtime.Context;

/**
 * the instance of this interface may not thread safety.
 * @author administrator
 * @version 0.0.3 2011-11-11
 * @since 0.0.3
 */

public interface Application
{

    enum UserType
    {
        /**
         * Defined As a ADMINISTRATOR User.
         */
        ADMIN_USER(2) ,
        /**
         * Defined As a AUTHENTICATION User.
         */
        AUTH_USER(1) ,
        /**
         * Defined As a ANONYMONUS User.
         */
        ANONY_USER(0);

        int value;

        UserType(int i)
        {
            value = i;
        }

        public int value() {
            return this.value;
        }

        public static UserType fromValue(int v) {
            for (UserType c : UserType.values()) {
                if (c.value == v) {
                    return c;
                }
            }
            throw new IllegalArgumentException("" + v);
        }
    }

    public static final String BUILT_IN_APPLICATION = ApplicationManager.BUILT_IN_APPLICATION;

    public static final String DEFAULT_APPLICATION = ApplicationManager.DEFAULT_APPLICATION;

     DataSource getDataSource(String dsName) throws SlxException;

    /**
     * @param request
     * @param context {@link org.solmix.runtime.Context context}
     * @return
     * @throws SlxException
     */
     DSResponse execute(DSRequest request, Context context) throws SlxException;

    /**
     * Return the unique ID of this Application implementation.
     * 
     * @return
     */
     String getServerID();
    
    void  setApplicationSecurity(ApplicationSecurity security);

    public boolean isPermitted(DSRequest request, Context context) throws SlxException;

}
