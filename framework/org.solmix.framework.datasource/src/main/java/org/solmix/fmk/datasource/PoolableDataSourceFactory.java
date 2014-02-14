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

package org.solmix.fmk.datasource;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.IPoolableObjectFactory;
import org.solmix.api.pool.SlxKeyedPoolableObjectFactory;
import org.solmix.api.repo.DSRepository;
import org.solmix.commons.io.SlxFile;

/**
 * 
 * @version 110035
 */
public class PoolableDataSourceFactory extends SlxKeyedPoolableObjectFactory
{

    private static final Logger log = LoggerFactory.getLogger(PoolableDataSourceFactory.class.getName());

    private DSRequest dsRequest;

    private SystemContext sc;

    PoolableDataSourceFactory()
    {
        this(null);
    }

    public PoolableDataSourceFactory(final SystemContext sc)
    {
        setSystemContext(sc);
       
    }

    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.SlxKeyedPoolableObjectFactory#makeUnpooledObject(java.lang.Object)
     */
    @Override
    public DataSource makeUnpooledObject(Object key) throws Exception {
        return makeUnpooledObject(key, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.SlxKeyedPoolableObjectFactory#makeUnpooledObject(java.lang.Object,
     *      org.solmix.api.datasource.DSRequest)
     */
    @Override
    public DataSource makeUnpooledObject(Object key, DSRequest request) throws Exception {
        return DataSourceProvider.forName(sc,key.toString(), request);
    }

    /**
     * @return the dsRequest
     */
    public DSRequest getDsRequest() {
        return dsRequest;
    }

    /**
     * @param dsRequest the dsRequest to set
     */
    public void setDsRequest(DSRequest dsRequest) {
        this.dsRequest = dsRequest;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.KeyedPoolableObjectFactory#activateObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public void activateObject(Object key, Object obj) throws Exception {
        numActivateObjectCalls.getAndIncrement();
        if (obj instanceof DataSource) {
            DataSource ds = (DataSource) obj;
            if (!ds.getName().equals(key.toString())) {
                throw new java.lang.IllegalStateException(
                    new StringBuilder().append("Miss match datasource,the key is ").append(key.toString()).append(" but the datasource is ").append(
                        ds.getName()).toString());
            }
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.KeyedPoolableObjectFactory#destroyObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public void destroyObject(Object key, Object obj) throws Exception {
        numDestroyObjectCalls.getAndIncrement();
        ((DataSource) obj).clearState();
        obj = null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.KeyedPoolableObjectFactory#makeObject(java.lang.Object)
     */
    @Override
    public DataSource makeObject(Object key) throws Exception {
        numMakeObjectCalls.getAndIncrement();
        return makeUnpooledObject(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.KeyedPoolableObjectFactory#passivateObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public void passivateObject(Object key, Object obj) throws Exception {
        numPassivateObjectCalls.getAndIncrement();
        ((DataSource) obj).clearState();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.pool.KeyedPoolableObjectFactory#validateObject(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean validateObject(Object key, Object obj) {
        numValidateObjectCalls.getAndIncrement();
        if (obj instanceof DataSource) {
            DataSource ds = (DataSource) obj;
           String id= ds.getContext().getRepositoryId();
           if(id.equals(DSRepository.BUILDIN_FILE))
               return true;
            long timestamp = ds.getContext().getConfigTimestamp();
            if (timestamp == 0)
                return false;
            long lastTimeStamp = 0;
            try {
                DSRepository repo = sc.getBean(org.solmix.api.repo.DSRepositoryManager.class).getRepository(id);
                String dsName = key == null ? null : key.toString() + "." + DefaultParser.DEFAULT_REPO_SUFFIX;

                Object configFile = repo.load(dsName);
                lastTimeStamp = ((SlxFile) configFile).lastModified();
            } catch (SlxException e) {
                log.warn("Get da config file failed", e);
            } catch (IOException e) {
                log.warn("Get da config file failed,IO Exception", e);
            }

            if (lastTimeStamp != timestamp)
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.pool.IPoolableObjectFactory#newInstance(java.lang.Object)
     */
    @Override
    public IPoolableObjectFactory newInstance(Object obj) throws SlxException {
        return new PoolableDataSourceFactory(sc);
    }

}
