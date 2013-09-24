/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.api.application.Application;
import org.solmix.api.application.ApplicationManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-11-12
 */

public class ApplicationManagerImpl implements ApplicationManager
{

    private static final Map<String, Application> providers;
    static {
        providers = new ConcurrentHashMap<String, Application>();
        // and the default datasource implementations.
        // providers.put(key, value)( new AppBase() );

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.ApplicationManager#findByID(java.lang.String)
     */
    @Override
    public Application findByID(String appID) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * find the application by id,if the id is <code>null</code>,used as default "builtinApplication".
     * @param appID
     * @return
     */
    public synchronized static Application findAppByID(String appID) {
        if (appID == null)
            appID=BUILT_IN_APPLICATION;
        Application theApp = null;
        if (appID.equalsIgnoreCase(BUILT_IN_APPLICATION) || appID.equalsIgnoreCase(DEFAULT_APPLICATION)) {
            
                theApp = new AppBase();
                return theApp;
            
        } else {
            //XXX extension point
        }
        return theApp;

    }

    @Override
    public void register(Application app) {
        String serverType = app.getServerID();
        if (serverType == null)
            return;
        providers.put(serverType, app);
    }

    @Override
    public void unregister(Application app) {
            providers.remove(app.getServerID());
    }
}
