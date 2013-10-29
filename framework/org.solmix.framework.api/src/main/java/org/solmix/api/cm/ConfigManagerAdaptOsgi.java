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
package org.solmix.api.cm;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.solmix.commons.collections.DataTypeMap;


/**
 * Make the config manager can used in osgi container.
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-21
 */

public abstract class ConfigManagerAdaptOsgi implements ConfigManager, ManagedService
{
    protected static DataTypeMap allConfig;
    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
     */
    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        if (properties != null) {
            Enumeration en = properties.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                allConfig.put(key, properties.get(key));
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigManager#updateConfig(java.util.Properties)
     */
    @Override
    public void updateConfig(Properties properties) {
        if (properties != null) {
            Enumeration<Object> en = properties.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                allConfig.put(key.toString(), properties.get(key));
            }
        }

    }

}
