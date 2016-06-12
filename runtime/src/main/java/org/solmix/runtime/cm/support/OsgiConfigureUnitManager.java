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

package org.solmix.runtime.cm.support;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.solmix.commons.util.OsgiUtils;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;

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
            admin = OsgiUtils.getService(context, ConfigurationAdmin.class);
        }
        if (admin == null)
            throw new java.lang.RuntimeException("Can't find osgi configurationadmin service");
        return admin;
    }

 
    @Override
    public ConfigureUnit createFactoryConfigureUnit(String factoryPid) throws IOException {
        Configuration c = getConfigurationAdmin().createFactoryConfiguration(factoryPid);

        if (c != null) {
            return new OsigConfigureUnitDelegating(c);
        }
        return null;
    }

    @Override
    public ConfigureUnit getConfigureUnit(String pid) throws IOException {
        return new OsigConfigureUnitDelegating(getConfigurationAdmin().getConfiguration(pid));
    }

   
    @Override
    public ConfigureUnit[] listConfigureUnits(String filter) throws IOException {

        try {
            Configuration[] configs = getConfigurationAdmin().listConfigurations( filter);
            ConfigureUnit[] result = new ConfigureUnit[configs.length];
            for (int i = 0; i < configs.length; i++) {
                result[i] = new OsigConfigureUnitDelegating(configs[i]);
            }
            return result;
        } catch (InvalidSyntaxException e) {
            // ignore.
        }
        return new ConfigureUnit[]{} ;
    }

}
