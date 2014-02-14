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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.IPoolableObjectFactory;
import org.solmix.api.pool.PoolManager;
import org.solmix.api.pool.SlxKeyedPoolableObjectFactory;
import org.solmix.api.pool.SlxPoolableObjectFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;

/**
 * {@link org.solmix.api.pool.PoolService PoolService} implement.
 * 
 * @version 0.0.4
 * @since 0.0.1
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PoolManagerImpl implements PoolManager
{

    private final Logger log =  LoggerFactory.getLogger(PoolManagerImpl.class.getName());

    protected IPoolableObjectFactory factory;

    protected String symbolicName;

    protected Object source;

    protected Map sources;

    private final Map config;

    protected String name;

    protected boolean enablePool;

    // private static final String FILTER = SlxConstants.SERVICE_CM_NAME + "=" + SlxConstants.MODULE_DS_NAME;
    //
    // @Inject
    // @Reference(id = "ds-config", serviceInterface = ConfigRealm.class, timeout = 100, filter = FILTER)

    public PoolManagerImpl(String name, IPoolableObjectFactory factory, Map config)
    {
        sources = new HashMap();
        this.name = name;
        this.factory = factory;
        this.config = config;
        symbolicName = getClass().getName();
    }

    @Override
    public Object borrowObject(Object key) throws SlxException {
        return borrowObject(key, null);
    }

    /**
     * This method only used for datasource pool.
     * 
     * @param name
     * @param request {@link org.solmix.api.datasource.DSRequest}
     * @return
     * @throws SlxException
     */
    @Override
    public Object borrowObject(Object key, DSRequest request) throws SlxException {
        Object objectSource = getObjectSource(key);
        if (objectSource instanceof SlxKeyedObjectPool)
            try {
                return ((SlxKeyedObjectPool) objectSource).borrowObject(key);
            } catch (Exception e) {
                    throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow  Object faild", e);
            }
        if (objectSource instanceof SlxObjectPool)
            try {
                return ((SlxObjectPool) objectSource).borrowObject();
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Object faild", e);
            }
        if (objectSource instanceof SlxPoolableObjectFactory)
            try {
                return ((SlxPoolableObjectFactory) objectSource).makeObject();
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Object faild", e);
            }
        if (objectSource instanceof SlxKeyedPoolableObjectFactory)
            try {
                SlxKeyedPoolableObjectFactory source = (SlxKeyedPoolableObjectFactory) objectSource;
                // if (source instanceof PoolableDataSourceFactory)
                // ((PoolableDataSourceFactory) source).setDsRequest(request);
                return source.makeObject(key);
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Object faild", e);
            }
        else
            throw new SlxException(Tmodule.POOL, Texception.POOL_INVALID_OBJECT_TYPE, "Invalid objectSource type:"
                + objectSource.getClass().getName(), new IllegalArgumentException());
    }

    @Override
    public Object borrowUnpooledObject(Object key) throws SlxException {
        Object objectSource = getObjectSource(key);
        if (objectSource instanceof SlxKeyedObjectPool)
            try {
                return ((SlxKeyedObjectPool) objectSource).getObjectFactory().makeUnpooledObject(key);
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Unpool Object faild", e);
            }
        if (objectSource instanceof SlxObjectPool)
            try {
                return ((SlxObjectPool) objectSource).getObjectFactory().makeUnpooledObject();
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Unpool Object faild", e);
            }
        if (objectSource instanceof SlxPoolableObjectFactory)
            try {
                return ((SlxPoolableObjectFactory) objectSource).makeUnpooledObject();
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Unpool Object faild", e);
            }
        if (objectSource instanceof SlxKeyedPoolableObjectFactory)
            try {
                return ((SlxKeyedPoolableObjectFactory) objectSource).makeUnpooledObject(key);
            } catch (Exception e) {
                throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow Unpool Object faild", e);
            }
        else
            throw new SlxException(Tmodule.POOL, Texception.POOL_INVALID_OBJECT_TYPE, "Invalid objectSource type:"
                + objectSource.getClass().getName(), new IllegalArgumentException());
    }

    @Override
    public Object borrowNewObject(Object key) throws Exception {
        Object objectSource = getObjectSource(key);
        if (objectSource instanceof SlxKeyedObjectPool)
            return ((SlxKeyedObjectPool) objectSource).getObjectFactory().makeObject(key);
        if (objectSource instanceof SlxObjectPool)
            return ((SlxObjectPool) objectSource).getObjectFactory().makeObject();
        if (objectSource instanceof SlxPoolableObjectFactory)
            return ((SlxPoolableObjectFactory) objectSource).makeObject();
        if (objectSource instanceof SlxKeyedPoolableObjectFactory)
            return ((SlxKeyedPoolableObjectFactory) objectSource).makeObject(key);
        else
            throw new Exception((new StringBuilder()).append("Invalid objectSource type: ").append(objectSource.getClass().getName()).toString());
    }

    @Override
    public void returnObject(Object key, Object obj) {
        try {
            Object objectSource = getObjectSource(key);
            if (objectSource instanceof SlxObjectPool)
                ((SlxObjectPool) objectSource).returnObject(obj);
            else if (objectSource instanceof SlxKeyedObjectPool)
                ((SlxKeyedObjectPool) objectSource).returnObject(key, obj);
            else if (objectSource instanceof SlxPoolableObjectFactory)
                ((SlxPoolableObjectFactory) objectSource).destroyObject(obj);
            else if (objectSource instanceof SlxKeyedPoolableObjectFactory)
                ((SlxKeyedPoolableObjectFactory) objectSource).destroyObject(key, obj);
        } catch (Exception e) {
            log.error("Error while freeing object - ignored.", e);
        }
    }

    public synchronized Object getObjectSource(Object key) throws SlxException {
        if (factory instanceof SlxKeyedPoolableObjectFactory) {
            if (this.source == null)
                this.source = makeSource(key);
            return this.source;
        }
        Object source = sources.get(key);
        if (source == null) {
            source = makeSource(key);
            sources.put(key, source);
        }
        return source;
    }

    protected Object makeSource(Object key) throws SlxException {
        DataTypeMap mergConfig = new DataTypeMap(config);
        IPoolableObjectFactory factory = this.factory.newInstance(key);
        source = factory;
        SlxPoolableObjectFactory iscFactory = null;
        if (factory instanceof SlxPoolableObjectFactory)
            iscFactory = (SlxPoolableObjectFactory) factory;
        if (mergConfig.getBoolean("enabled", false) && (iscFactory == null || !iscFactory.poolDisabled)) {
            enablePool = DataUtil.asBoolean(mergConfig.remove("enabled"));
            Object pool;
            if (factory instanceof SlxKeyedPoolableObjectFactory)
                pool = new SlxKeyedObjectPool((SlxKeyedPoolableObjectFactory) factory, mergConfig);
            else
                pool = new SlxObjectPool((SlxPoolableObjectFactory) factory, mergConfig);
            factory.setPool(pool);
            source = pool;
        } else {
            log.info((new StringBuilder()).append("Disabled for pooling '").append(key.toString()).append("' objects").toString());
        }
        if (source == null)
            throw new SlxException(Tmodule.POOL, Texception.POOL_UNABLE_BIND_OBJECT, "Unable to bind Object for key:" + key);
        else
            return source;
    }

    @Override
    public void destroy() {
        if (source == null)
            return;
        try {
            if (this.source instanceof SlxKeyedObjectPool) {

                ((SlxKeyedObjectPool) source).close();

            } else {
                ((SlxObjectPool) source).close();
            }
        } catch (Exception e) {
            // ignored.
        }
    }

    @Override
    public boolean isEnablePool() {
        return enablePool;
    }

    @Override
    public Object getPool() {
        if (isEnablePool()) {
            return source;
        }
        return null;
    }

}
