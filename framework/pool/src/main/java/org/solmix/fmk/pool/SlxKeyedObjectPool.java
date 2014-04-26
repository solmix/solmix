/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.pool;

import java.util.Map;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.pool.SlxKeyedPoolableObjectFactory;
import org.solmix.commons.collections.DataTypeMap;

/**
 * Extends {@link org.apache.commons.pool.impl.GenericKeyedObjectPool GenericKeyedObjectPool}
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035
 */
public class SlxKeyedObjectPool extends GenericKeyedObjectPool
{

    private final SlxKeyedPoolableObjectFactory objectFactory;

    private static final Logger log =  LoggerFactory.getLogger(SlxKeyedObjectPool.class);

    public SlxKeyedObjectPool(SlxKeyedPoolableObjectFactory factory)
    {
        super(factory);
        this.objectFactory = factory;
    }

    @SuppressWarnings("unchecked")
    public SlxKeyedObjectPool(SlxKeyedPoolableObjectFactory factory, Map thisConfig)
    {
        super(factory);
        this.objectFactory = factory;
        String whenExhaustedAction = (String) thisConfig.remove("whenExhaustedAction");
        Byte action = null;
        if (whenExhaustedAction != null) {
            whenExhaustedAction = whenExhaustedAction.toLowerCase().trim();
            if ("grow".equals(whenExhaustedAction))
                action = new Byte((byte) 2);
            if ("block".equals(whenExhaustedAction))
                action = new Byte((byte) 1);
            if ("fail".equals(whenExhaustedAction))
                action = new Byte((byte) 0);
        }
        if (action != null)
            thisConfig.put("whenExhaustedAction", action);
        else
            log.error((new StringBuilder()).append("Ignoring unknown value: ").append(whenExhaustedAction).append(" for whenExhaustedAction.").append(
                " Valid values are: grow, block, fail.").toString());
        try {
            // this is a fussy configuration process.we can use DataUtil.setProperties() method to simple this process.
            // but when I checked the commons-pool sources found this config bean is not the standard java bean ,so
            // reflect
            // value failed.the latest source is used standard java bean .
            DataTypeMap data = new DataTypeMap(thisConfig);
            Config config = new Config();
            if (thisConfig.get("maxActive") != null)
                config.maxActive = data.getInt("maxActive", 8);
            if (thisConfig.get("lifo") != null)
                config.lifo = data.getBoolean("lifo", true);
            if (thisConfig.get("maxIdle") != null)
                config.maxIdle = data.getInt("maxIdle", 8);
            if (thisConfig.get("maxTotal") != null)
                config.maxTotal = data.getInt("maxTotal", 8);
            if (thisConfig.get("maxWait") != null)
                config.maxWait = data.getLong("maxWait", 12000);
            if (thisConfig.get("minEvictableIdleTimeMillis") != null)
                config.minEvictableIdleTimeMillis = data.getLong("minEvictableIdleTimeMillis", 500);
            if (thisConfig.get("minIdle") != null)
                config.minIdle = data.getInt("minIdle", 0);
            if (thisConfig.get("numTestsPerEvictionRun") != null)
                config.numTestsPerEvictionRun = data.getInt("numTestsPerEvictionRun", 1);
            if (thisConfig.get("testOnBorrow") != null)
                config.testOnBorrow = data.getBoolean("testOnBorrow", true);
            if (thisConfig.get("testOnReturn") != null)
                config.testOnReturn = data.getBoolean("testOnReturn", false);
            if (thisConfig.get("testWhileIdle") != null)
                config.testWhileIdle = data.getBoolean("testWhileIdle", false);
            if (thisConfig.get("timeBetweenEvictionRunsMillis") != null)
                config.timeBetweenEvictionRunsMillis = data.getLong("timeBetweenEvictionRunsMillis", 2000);
            if (thisConfig.get("timeBetweenEvictionRunsMillis") != null)
                config.whenExhaustedAction = data.getByte("whenExhaustedAction", (byte) 2);

            this.setConfig(config);
        } catch (Exception e) {
            log.warn("Problem setting requested config parameters on pool.", e);
        }
    }

    /**
     * @return the objectFactory
     */
    public SlxKeyedPoolableObjectFactory getObjectFactory() {
        return objectFactory;
    }

}
