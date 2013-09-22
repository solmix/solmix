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

package org.solmix.fmk.internal;

import static org.solmix.commons.util.DataUtil.getBoolean;
import static org.solmix.commons.util.DataUtil.getInteger;
import static org.solmix.commons.util.DataUtil.getString;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.solmix.SlxConstants;
import org.solmix.api.cm.ConfigManager;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-8
 */
@SuppressWarnings("rawtypes")
public class DSConfigManager implements ManagedService, ConfigManager
{

    public static String frameworkVersion = "0.1";

    /**
     * Note:Modify this properties needed to update the default configuration file
     */
    public static String defaultParser = "default";

    public static int maxUploadFilesize = 1024 * 100;

    public static String autoJoinTransactions = "false";

    public static String velocityTemplateDir = "./";

    public static String slxVersionNumber = SlxConstants.FRAMEWORK_VERSION;

    public static String clientVersionNumber = "SC_SNAPSHOT-2011-12-05";

    /**
     * Note:Modify this properties needed to update the default configuration file
     */
    public static boolean showClientOutput = false;

    /**
     * Note:Modify this properties needed to update the default configuration file
     */
    public static boolean development = true;

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
                defaultParser = getString(p, "defaultparser", "default");
                maxUploadFilesize = getInteger(p, "maxUploadFileSize", 1024 * 100).intValue();
                autoJoinTransactions = getString(p, "autoJoinTransactions", "false");
                showClientOutput = getBoolean(p, "showClientOutput", false).booleanValue();
                development = getBoolean(p, "development", true).booleanValue();
                try {
                    velocityTemplateDir = getString(p, "velocityTemplateDir", (new File("")).getCanonicalPath());
                } catch (IOException e) {// ignore.
                }
            }
        }
    }

    public static DataTypeMap getConfig() {
        return allConfig == null ? new DataTypeMap() : allConfig;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigManager#updateConfig(java.util.Properties)
     */
    @Override
    public void updateConfig(Properties p) {

        if (p != null) {
            Enumeration<Object> en = p.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                allConfig.put(key.toString(), p.get(key));
            }
        }
        if (p != null) {
            defaultParser = getString(p, "defaultparser", "default");
            maxUploadFilesize = getInteger(p, "maxUploadFileSize", 1024 * 100).intValue();
            autoJoinTransactions = getString(p, "autoJoinTransactions", "false");
            showClientOutput = getBoolean(p, "showClientOutput", false).booleanValue();
            development = getBoolean(p, "development", true).booleanValue();
            try {
                velocityTemplateDir = getString(p, "velocityTemplateDir", (new File("")).getCanonicalPath());
            } catch (IOException e) {// ignore.
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.cm.ConfigManager#getPid()
     */
    @Override
    public String getPid() {
        return "org.solmix.framework.datasource";
    }
}
