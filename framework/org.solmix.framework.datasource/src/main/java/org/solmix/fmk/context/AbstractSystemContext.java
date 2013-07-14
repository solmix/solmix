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

package org.solmix.fmk.context;

import java.util.Locale;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.event.EventManager;
import org.solmix.api.rpc.RPCManagerFactory;
import org.solmix.api.security.SecurityAdmin;

/**
 * 
 * @author Administrator
 * @version 110035 2012-10-5
 */

public abstract class AbstractSystemContext extends AbstractContext implements SystemContext
{

    private static final Logger log = LoggerFactory.getLogger(AbstractSystemContext.class);

    private DataSourceManager dataSourceManager;

    private SecurityAdmin securityAdmin;

    private BundleContext bundleContext;

    private EventManager eventManager;

    private RPCManagerFactory rpcManagerFactory;

    public AbstractSystemContext()
    {
        this.setAttributeProvider(new MapAttributeProvider());
    }

    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        if (scope == Scope.SESSION || scope == Scope.LOCAL) {
            log.warn("you should not set an attribute in the system context in request or session scope. You are setting {}={}", name, value);
        }
        super.setAttribute(name, value, scope);

    }

    @Override
    public void removeAttribute(String name, Scope scope) {
        if (scope == Scope.SESSION || scope == Scope.LOCAL) {
            log.warn("you should not manipulate an attribute in the system context in request or session scope. You are setting name {}", name);
        }
        super.removeAttribute(name, scope);
        this.getAttributeProvider().removeAttribute(name, scope);

    }

    @Deprecated
    @Override
    public void setLocale(Locale locale) {
        throw new UnsupportedOperationException(
            "setLocale() should not be called on SystemContext - system default locale is handled by MessagesManager");
    }

    /**
     * Return the default locale ,used resourceManager's default locale.
     */
    public Locale getLocale() {
        return this.getResourceBundleManager().getDefaultLocale();
    }

    /**
     * @return the systemContext
     */
    protected SystemContext getSystemContext() {
        return this;
    }

    /**
     * @return the securityAdmin
     */
    public SecurityAdmin getSecurityAdmin() {
        return securityAdmin;
    }

    /**
     * @return the bundleContext
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * @param bundleContext the bundleContext to set
     */
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * @param securityAdmin the securityAdmin to set
     */
    public void setSecurityAdmin(SecurityAdmin securityAdmin) {
        this.securityAdmin = securityAdmin;
    }

    /**
     * @return the dataSourceManagerProvider
     */
    public DataSourceManager getDataSourceManagerProvider() {
        return dataSourceManager;
    }

    /**
     * @param dataSourceManagerProvider the dataSourceManagerProvider to set
     */
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * @return the eventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @param eventManager the eventManager to set
     */
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * @return the dataSourceManager
     */
    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    /**
     * /**
     * 
     * @return the rpcManagerFactory
     */
    public RPCManagerFactory getRpcManagerFactory() {
        return rpcManagerFactory;
    }

    /**
     * @param rpcManagerFactory the rpcManagerFactory to set
     */
    public void setRpcManagerFactory(RPCManagerFactory rpcManagerFactory) {
        this.rpcManagerFactory = rpcManagerFactory;
    }

}
