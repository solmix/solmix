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

package org.solmix.fmk.pool;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.solmix.api.pool.IPoolableObjectFactory;
import org.solmix.api.pool.PoolManager;
import org.solmix.api.pool.PoolManagerFactory;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.4
 * @since 0.0.4
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PoolManagerFactoryImpl implements PoolManagerFactory
{

    public static String SERVICE_PID = "org.solmix.framework.pool";

    /**
     * Note:Modify this properties needed to update the default configuration file
     */
    private static final Map DEFAULT_CONFIG;
    private SystemContext sc;
    private DataTypeMap configProperties;

    private final  Map<String, PoolConf> cache = Collections.synchronizedMap(new HashMap<String, PoolConf>());


    static {
        DEFAULT_CONFIG = new HashMap();
        DEFAULT_CONFIG.put("maxActive", -1);
        DEFAULT_CONFIG.put("maxIdle", -1);
        DEFAULT_CONFIG.put("maxWait", 120000);
        DEFAULT_CONFIG.put("whenExhaustedAction", "grow");
        DEFAULT_CONFIG.put("testOnBorrow", "true");
        DEFAULT_CONFIG.put("testWhileIdle", "true");
        DEFAULT_CONFIG.put("testOnReturn", "false");
        DEFAULT_CONFIG.put("timeBetweenEvictionRunsMillis", 120000);
        DEFAULT_CONFIG.put("minEvictableIdleTimeMillis", -1);
        DEFAULT_CONFIG.put("numTestsPerEvictionRun", -1);
        DEFAULT_CONFIG.put("enabled", "true");
    }
    
    public PoolManagerFactoryImpl(final SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
        if(sc!=null){
            sc.setExtension(this, PoolManagerFactory.class);
           
        }
    } 
    private synchronized DataTypeMap  getConfig(){
        if(configProperties==null&&sc!=null){
            ConfigureUnitManager cum= sc.getExtension(ConfigureUnitManager.class);
            ConfigureUnit cu=null;
            try {
                cu = cum.getConfigureUnit(SERVICE_PID);
            } catch (IOException e) {
                //ignore
            }
            if(cu!=null){
                configProperties=cu.getProperties();
            }
        }
        return configProperties;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.PoolServiceFactory#createPoolService(java.lang.String,
     *      org.solmix.api.pool.IPoolableObjectFactory)
     */
    @Override
    public PoolManager createPoolManager(String name, IPoolableObjectFactory factory) {
        Map temp = null;
        if (getConfig() != null) {
            temp = getConfig().getSubtree(name);
        }
        if (temp == null||temp.isEmpty()) {
            temp = new HashMap();
            temp.putAll(DEFAULT_CONFIG);
        }
        return createPoolManager(name, factory, temp);
    }

    public PoolManager createPoolManager(String name, IPoolableObjectFactory factory, Map config) {
        PoolConf conf = cache.get(name);
        PoolManager cached;
        if (conf == null || conf.service == null) {
            cached = new PoolManagerImpl(name, factory, config);
            if (cached != null)
                cache.put(name, new PoolConf(cached, factory));
        } else {
            cached = conf.service;
        }
        return cached;
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
            c.service = createPoolManager(name, cache.get(name).factory, properties);
        }

    }

    private class PoolConf
    {

        PoolConf(PoolManager service, IPoolableObjectFactory factory)
        {
            this.service = service;
            this.factory = factory;
        }

        public PoolManager service;

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

        
 

}
