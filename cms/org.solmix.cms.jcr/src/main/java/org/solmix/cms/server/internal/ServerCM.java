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

package org.solmix.cms.server.internal;

import static org.solmix.commons.util.DataUtil.getString;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author Administrator
 * @version 0.1.1 2012-8-17
 */
@SuppressWarnings("rawtypes")
public class ServerCM implements ManagedService
{

    public static String adminUser;

    public static String adminPassword;

    public static String anonymousUser;

    public static String anonymousPassword;

    public static String defaultWorkspace;

    public static String jndi;

    public static String rmi;

    public static String repository_home;

    public static String repository_config;

    private static DataTypeMap allConfig = new DataTypeMap();

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
     */
    @Override
    public void updated(Dictionary p) throws ConfigurationException {
        if (p != null) {
            Enumeration en = p.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                allConfig.put(key, p.get(key));
            }
            if (p != null) {
                adminUser = getString(p, "admin.user", "admin");
                adminPassword = getString(p, "admin.password", "admin");
                anonymousUser = getString(p, "anonymous.user", "admin");
                anonymousPassword = getString(p, "anonymous.password", "admin");
                defaultWorkspace = getString(p, "default.workspace", "");
                adminPassword = getString(p, "admin.password", "admin");
                jndi = getString(p, "jndi", "");
                rmi = getString(p, "rmi", "");
                repository_home = getString(p, "repository.home", "");
                repository_config = getString(p, "repository.config", "");
            }
        }
    }

}
