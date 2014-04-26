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

package org.solmix.fmk.cm.osgi;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.Configuration;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class OsigConfigureUnitDelegating implements ConfigureUnit
{

    private final Configuration configure;

    public OsigConfigureUnitDelegating(Configuration config)
    {
        this.configure = config;
    }

    @Override
    public DataTypeMap getProperties() {

        DataTypeMap config = new DataTypeMap();
        Dictionary<String, Object> properties = configure.getProperties();
        if (properties != null) {
            Enumeration<String> en = properties.keys();
            while (en.hasMoreElements()) {
                String key = en.nextElement();
                config.put(key, properties.get(key));
            }
        }
        return config;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnit#delete()
     */
    @Override
    public void delete() throws IOException {
        configure.delete();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnit#update()
     */
    @Override
    public void update() throws IOException {
        configure.update();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnit#update(java.util.Dictionary)
     */
    @Override
    public void update(Dictionary<String, ?> properties) throws IOException {
        configure.update();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigureUnit#getPid()
     */
    @Override
    public String getPid() {
        return configure.getPid();
    }

}
