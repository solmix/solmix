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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.solmix.api.application.Application;
import org.solmix.api.application.ApplicationManager;
import org.solmix.api.application.ApplicationSecurity;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.SystemContext;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-11-12
 */

public class ApplicationManagerImpl implements ApplicationManager
{

    private SystemContext sc;
    public static final String SERVICE_PID="org.solmix.framework.app";
    public static final String P_DEFAULT_PREFIX="default";

    public ApplicationManagerImpl(){
        this(null);
    }
    public ApplicationManagerImpl(SystemContext sc){
        setApplicationManager(sc);
    }
    /**
     * @param sc
     */
    @Resource
    private void setApplicationManager(SystemContext sc) {
       this.sc=sc;
       if(sc!=null){
           sc.setBean(this, ApplicationManager.class);
       }
        
    }
    private static final Map<String, Application> providers;
    static {
        providers = new ConcurrentHashMap<String, Application>();
        // and the default datasource implementations.

    }

    /**
     * find the application by id,if the id is <code>null</code>,used as default "builtinApplication".
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.ApplicationManager#findByID(java.lang.String)
     */
    @Override
    public Application findByID(String appID) {
        if (appID == null)
            appID = BUILT_IN_APPLICATION;
        Application application = providers.get(BUILT_IN_APPLICATION);
        if (application == null && (appID.equalsIgnoreCase(BUILT_IN_APPLICATION) || appID.equalsIgnoreCase(DEFAULT_APPLICATION))) {
            application = new BuiltInApplication(getConfig().getSubtree(P_DEFAULT_PREFIX));
            return application;

        }
        application.setApplicationSecurity(findApplicationSecurity());
        return application;
    }
    protected ApplicationSecurity findApplicationSecurity(){
        if(sc!=null){
           return sc.getBean(ApplicationSecurity.class);
        }
        return null;
    }

    protected DataTypeMap getConfig() {
        DataTypeMap appConfig;
        ConfigureUnitManager cum = sc.getBean(org.solmix.api.cm.ConfigureUnitManager.class);
        ConfigureUnit cu = null;
        try {
            cu = cum.getConfigureUnit(SERVICE_PID);
        } catch (IOException e) {
        }
        if (cu != null)
            appConfig = cu.getProperties();
        else
            appConfig = new DataTypeMap();

        return appConfig;

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
