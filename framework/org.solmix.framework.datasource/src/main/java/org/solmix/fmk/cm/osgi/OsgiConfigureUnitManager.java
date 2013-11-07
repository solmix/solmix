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

package org.solmix.fmk.cm.osgi;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.commons.util.OsgiUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class OsgiConfigureUnitManager implements ConfigureUnitManager
{

    private final BundleContext context;

    private ConfigurationAdmin admin;

    public OsgiConfigureUnitManager(BundleContext context)
    {
        this.context = context;

    }

    protected ConfigurationAdmin getConfigurationAdmin() {
        if (admin == null) {
            admin = OsgiUtil.getService(context, ConfigurationAdmin.class);
        }
        if (admin == null)
            throw new java.lang.RuntimeException("Can't find osgi configurationadmin service");
        return admin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnitManager#createFactoryConfigureUnit(java.lang.String)
     */
    @Override
    public ConfigureUnit createFactoryConfigureUnit(String factoryPid) throws IOException {
        Configuration c = getConfigurationAdmin().createFactoryConfiguration(factoryPid);

        if (c != null) {
            return new OsigConfigureUnitDelegating(c);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnitManager#getConfigureUnit(java.lang.String)
     */
    @Override
    public ConfigureUnit getConfigureUnit(String pid) throws IOException {
        return new OsigConfigureUnitDelegating(getConfigurationAdmin().getConfiguration(pid));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnitManager#listConfigureUnits(java.lang.String)
     */
    @Override
    public ConfigureUnit[] listConfigureUnits(String filter) throws IOException {
     
        try {
            Configuration[] configs= getConfigurationAdmin().listConfigurations(filter);
            ConfigureUnit[] _return=new ConfigureUnit[configs.length];
            for(int i=0;i<configs.length;i++){
                _return[i]=new OsigConfigureUnitDelegating(configs[i]);
            }
            return _return;
        } catch (InvalidSyntaxException e) {
         //ignore.
        }
        return null;
    }

}
