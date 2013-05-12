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

package com.solmix.fmk.mock;

import org.easymock.EasyMock;

import com.solmix.api.datasource.DataSourceManager;
import com.solmix.api.event.EventManager;
import com.solmix.api.i18n.ResourceBundleManager;
import com.solmix.api.security.SecurityAdmin;
import com.solmix.fmk.context.SingleSystemContext;
import com.solmix.fmk.context.SlxContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */

public class ContextMocks
{

    public static void initContext() {
        SingleSystemContext sc = new SingleSystemContext();
        sc.setEventManager(ContextMocks.getEventManager());
        sc.setDataSourceManager(getDataSourceManager());
        sc.setResourceBundleManager(getResourceBundleManager());
        sc.setSecurityAdmin(getSecurityAdmin());
        SlxContext.setSystemContext(sc);
    }

    /**
     * @return
     */
    public static SecurityAdmin getSecurityAdmin() {
        return EasyMock.createMock(SecurityAdmin.class);
    }

    /**
     * @return
     */
    public static ResourceBundleManager getResourceBundleManager() {
        return EasyMock.createMock(ResourceBundleManager.class);
    }

    /**
     * @return
     */
    public static DataSourceManager getDataSourceManager() {
        return EasyMock.createMock(DataSourceManager.class);
    }

    public static EventManager getEventManager() {
        return EasyMock.createMock(EventManager.class);
    }

}
