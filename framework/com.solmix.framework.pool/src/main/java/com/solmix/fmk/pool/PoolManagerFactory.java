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

package com.solmix.fmk.pool;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.service.cm.ManagedService;

import com.solmix.api.cm.ConfigManager;
import com.solmix.api.pool.IPoolableObjectFactory;
import com.solmix.api.pool.PoolService;
import com.solmix.api.pool.PoolServiceFactory;
import com.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author Administrator
 * @version 0.0.4
 * @since 0.0.4
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PoolManagerFactory implements PoolServiceFactory , ManagedService, ConfigManager
{

    public static String SERVICE_PID = "com.solmix.framework.pool";

    /**
     * Note:Modify this properties needed to update the default configuration file
     */
    private static final Map DEFAULT_CONFIG;

    private static Map<String, PoolConf> cache = Collections.synchronizedMap(new HashMap<String, PoolConf>());

    private final  Map<String, Object> propertiesCache = Collections.synchronizedMap(new HashMap<String, Object>());

    static {
        DEFAULT_CONFIG = new HashMap();
        DEFAULT_CONFIG.put("maxActive", -1);
        DEFAULT_CONFIG.put("maxIdle", -1);
        DEFAULT_CONFIG.put("maxWait", -1);
        DEFAULT_CONFIG.put("whenExhaustedAction", "grow");
        DEFAULT_CONFIG.put("testOnBorrow", "true");
        DEFAULT_CONFIG.put("testWhileIdle", "true");
        DEFAULT_CONFIG.put("timeBetweenEvictionRunsMillis", -1);
        DEFAULT_CONFIG.put("minEvictableIdleTimeMillis", -1);
        DEFAULT_CONFIG.put("numTestsPerEvictionRun", -1);
        DEFAULT_CONFIG.put("enabled", "true");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.pool.PoolServiceFactory#createPoolService(java.lang.String,
     *      com.solmix.api.pool.IPoolableObjectFactory)
     */
    @Override
    public PoolService createPoolService(String name, IPoolableObjectFactory factory) {
        Map temp = null;
        if (propertiesCache != null) {
            temp = new DataTypeMap(propertiesCache).getSubtree(name);
        }
        if (temp == null||temp.isEmpty()) {
            temp = new HashMap();
            temp.putAll(DEFAULT_CONFIG);
        }
        return createPoolService(name, factory, temp);
    }

    public PoolService createPoolService(String name, IPoolableObjectFactory factory, Map config) {
        PoolConf conf = cache.get(name);
        PoolService cached;
        if (conf == null || conf.service == null) {
            cached = new PoolManager(name, factory, config);
            if (cached != null)
                cache.put(name, new PoolConf(cached, factory));
        } else {
            cached = conf.service;
        }
        return cached;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void updated(Dictionary properties) {
        if (properties == null)
            return;
      
        Enumeration em = properties.keys();
        Map<String, Object> config = new HashMap<String, Object>();
        while (em.hasMoreElements()) {
            String key = (String) em.nextElement();
            config.put(key, properties.get(key));
        }
        this.propertiesCache.putAll(config);
//        this.updatePoolService(config);
    }

    /**
     * @param pid
     * @param properties
     */
    public void updatePoolService(String name, Map properties) {
        if (cache.get(name) != null && cache.get(name).service != null) {
            PoolConf c = cache.remove(name);
            c.service.destroy();
            c.service = null;
            c.service = createPoolService(name, cache.get(name).factory, properties);
        }

    }

    private class PoolConf
    {

        PoolConf(PoolService service, IPoolableObjectFactory factory)
        {
            this.service = service;
            this.factory = factory;
        }

        public PoolService service;

        public IPoolableObjectFactory factory;
    }

    @Override
    public void destroy() {
        if (cache != null) {
            for (PoolConf pc : cache.values()) {
                if (pc.service != null)
                    pc.service.destroy();
            }

        }
    }

    @Override
    public void updateConfig(Properties properties) {
        if (properties == null)
            return;
      
        Enumeration em = properties.keys();
        Map<String, Object> config = new HashMap<String, Object>();
        while (em.hasMoreElements()) {
            String key = (String) em.nextElement();
            config.put(key, properties.get(key));
        }
        this.propertiesCache.putAll(config);
        
    }

    @Override
    public String getPid() {
        return SERVICE_PID;
    }

}
