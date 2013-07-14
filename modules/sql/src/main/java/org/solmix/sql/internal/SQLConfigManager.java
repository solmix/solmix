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

package org.solmix.sql.internal;

import static org.solmix.commons.util.DataUtil.getBoolean;
import static org.solmix.commons.util.DataUtil.getInteger;
import static org.solmix.commons.util.DataUtil.getString;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import org.solmix.api.cm.ConfigManager;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-22
 */

public class SQLConfigManager implements ManagedService, ConfigManager
{

    public static String SERVICE_PID = "org.solmix.modules.sql";

    public static String defaultDatabase = "HSQL";

    public static int forceConnectionClosedPeriod = 30000;

    public static String jndi_autoDetectSpace = "jdbc/";

    public static String defaultDateFormat = "YYYY-MM-DD";

    public static String defaultDateTimeFormat = "YYYY-MM-DD HH24:MI:SS";

    public static boolean jndi_autoDetect = true;

    public static boolean development = true;

    private static DataTypeMap allConfig;
    static {
        allConfig = new DataTypeMap();
        allConfig.put("HSQLDB.database.type", "hsqldb");
        allConfig.put("HSQLDB.interface.type", "jndiosgi");
        allConfig.put("HSQLDB.used.pool", "true");
        allConfig.put("HSQLDB.autoJoinTransactions", "true");
        allConfig.put("HSQLDB.jndiosgi", "(osgi.jndi.service.name=jdbc/NoTxHsqlDataSource)");
        allConfig.put("ORACLE.database.type", "oracle");
        allConfig.put("ORACLE.interface.type", "jndiosgi");
        allConfig.put("ORACLE.used.pool", "true");
        allConfig.put("ORACLE.autoJoinTransactions", "true");
        allConfig.put("ORACLE.jndiosgi", "(osgi.jndi.service.name=jdbc/NoTxOracleDataSource)");
        allConfig.put("pool.enabled", "true");
        allConfig.put("pool.timeBetweenEvictionRunsMillis", "-1");
        allConfig.put("pool.numTestsPerEvictionRun", "-1");
        allConfig.put("pool.testOnBorrow", "true");
        allConfig.put("pool.testWhileIdle", "false");
        allConfig.put("pool.minEvictableIdleTimeMillis", "30000");
        allConfig.put("monitorOpenConnections", false);
    }

    public SQLConfigManager()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void updated(Dictionary p) throws ConfigurationException {

        if (p != null) {
            Enumeration en = p.keys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                allConfig.put(key, p.get(key));
            }
            defaultDatabase = getString(p, "defaultDatabase", "HSQL");
            forceConnectionClosedPeriod = getInteger(p, "forceConnectionClosedPeriod", 3000).intValue();
            jndi_autoDetectSpace = getString(p, "jndi.autoDetectSpace", "false");
            jndi_autoDetect = getBoolean(p, "jndi.autoDetect", true).booleanValue();
            development = getBoolean(p, "development", true).booleanValue();
            defaultDateFormat = getString(p, "defaultDateFormat", "YYYY-MM-DD");
            defaultDateTimeFormat = getString(p, "defaultDateTimeFormat", "YYYY-MM-DD HH24:MI:SS");

        }
    }

    public static DataTypeMap getConfig() {
        return allConfig == null ? new DataTypeMap() : allConfig;
    }

    public void updateConfig(Properties p) {

        if (p != null) {
            Enumeration<Object> en = p.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                allConfig.put(key.toString(), p.get(key));
            }
        }

    }

    public String getPid() {
        return SERVICE_PID;
    }

}
