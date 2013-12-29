/*
 * ========THE SOLMIX PROJECT=====================================
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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.SlxConstants;
import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.Context;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.pool.PoolManager;
import org.solmix.api.pool.PoolManagerFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.SlxContext;

/**
 * DataSourceManager service implements.
 * 
 * @version 0.0.4
 */
public class DefaultDataSourceManager implements DataSourceManager
{

    private final List<DataSource> providers = new CopyOnWriteArrayList<DataSource>();

    public volatile PoolManager manager;

    private PoolManagerFactory poolManagerFactory;

    private static Logger log = LoggerFactory.getLogger(DataSourceManager.class.getName());

    private SystemContext sc;

    public DefaultDataSourceManager()
    {
        this(null);

    }

    public DefaultDataSourceManager(SystemContext sc)
    {
        setSystemContext(sc);
        providers.add(new BasicDataSource(sc));
        providers.add(new FileSystemDataSource(sc));
    }

    @Resource
    public void setSystemContext(SystemContext sc) {
        this.sc = sc;
        if (sc != null) {
            sc.setBean(this, DataSourceManager.class);
        }
    }

    /**
     * @return the poolServiceFactory
     */
    public synchronized PoolManagerFactory getPoolManagerFactory() {
        if (poolManagerFactory == null && sc != null) {
            poolManagerFactory = sc.getBean(PoolManagerFactory.class);
        }
        return poolManagerFactory;
    }

    /**
     * @param poolServiceFactory the poolServiceFactory to set
     */
    public void setPoolManagerFactory(PoolManagerFactory poolManagerFactory) {
        this.poolManagerFactory = poolManagerFactory;
    }

    public static void freeDataSource(DataSource ds) {
        SystemContext sc = SlxContext.getThreadSystemContext();
        sc.getBean(DataSourceManager.class).free(ds);

    }

    @Override
    public void free(DataSource ds) {
        if (ds == null) {
            return;
        } else if (ds.getContext().isWaitForFree()) {
            getPoolManager().returnObject(ds.getName(), ds);
            ds.getContext().setWaitForFree(false);
        } else {
            ds = null;
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.solmix.api.datasource.DataSourceManagerService#get(java.lang.String)
     */
    @Override
    public DataSource get(String name) throws SlxException {
        return get(name, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.solmix.api.datasource.DataSourceManagerService#getUnpooledDataSource(java.lang.String)
     */
    @Override
    public DataSource getUnpooledDataSource(String name) throws SlxException {

        return (DataSource) getPoolManager().borrowUnpooledObject(name);
    }

    @Override
    public synchronized PoolManager getPoolManager() {
        if (manager == null) {
            log.debug("Initial & create DataSource Pool");
            manager = getPoolManagerFactory().createPoolManager(SlxConstants.MODULE_DS_NAME, new PoolableDataSourceFactory(sc));
        }
        return manager;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#getProviders()
     */
    @Override
    public List<DataSource> getProviders() {
        if (sc != null) {
            ConfiguredBeanProvider cbp = sc.getBean(org.solmix.api.bean.ConfiguredBeanProvider.class);
            Collection<? extends DataSource> cc = cbp.getBeansOfType(DataSource.class);
            for (DataSource c : cc) {
                if (!providers.contains(c))
                    providers.add(c);
            }

        }
        return providers;
    }

    public void unregister(DataSource datasource) {
        providers.remove(datasource);
    }

    public void register(DataSource datasource) {
        String serverType = datasource.getServerType();
        if (serverType == null)
            return;
        for (DataSource ds : providers) {
            if (serverType.equalsIgnoreCase(ds.getServerType()))
                providers.remove(ds);
        }
        providers.add(datasource);
    }

    public static DataSource getDataSource(String name) throws SlxException {
        return getDataSource(name, null);
    }

    public static DataSource getDataSource(String name, DSRequest request) throws SlxException {
        SystemContext sc = SlxContext.getThreadSystemContext();
        return sc.getBean(DataSourceManager.class).get(name, request);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#get(java.lang.String,
     *      org.solmix.api.datasource.DSRequest)
     */
    @Override
    public DataSource get(String name, DSRequest request) throws SlxException {
        DataSource _return = null;
        try {
            _return = (DataSource) getPoolManager().borrowObject(name, request);
            if (_return != null)
                _return.getContext().setWaitForFree(true);
        } catch (Exception e1) {
            log.warn("Borrow object from pool failed, try to used unpooled object", e1);
        }
        try {
            // Re try.
            if (_return == null)
                return (DataSource) getPoolManager().borrowUnpooledObject(name);
        } catch (Exception trye) {
            throw new SlxException(Tmodule.POOL, Texception.POOL_BORROW_OBJECT_FAILD, "borrow unpooled Object faild ", trye);
        }
        return _return;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#restartPoolManager()
     */
    @Override
    public void restartPoolManager() {
        log.info("Restart DataSource Pool");
        manager = getPoolManagerFactory().createPoolManager(SlxConstants.MODULE_DS_NAME, new PoolableDataSourceFactory(sc));

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManager#createDSRequest()
     */
    @Override
    public DSRequest createDSRequest() {
        return new DSRequestImpl();
    }

    @Override
    public DSRequest createDSRequest(String dataSourceName, Eoperation opType, DSCall rpc) {
        return new DSRequestImpl(dataSourceName, opType, rpc);
    }

    @Override
    public DSRequest createDSRequest(Roperation operation, Context context) throws SlxException {
        return new DSRequestImpl(operation, context);
    }

    @Override
    public DSRequest createDSRequest(String dataSourceName, Eoperation opType) {
        return new DSRequestImpl(dataSourceName, opType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManager#createDSRequest(java.lang.String,
     *      org.solmix.api.jaxb.Eoperation, java.lang.String)
     */
    @Override
    public DSRequest createDSRequest(String dataSourceName, Eoperation opType, String operationID) {
        return new DSRequestImpl(dataSourceName, opType, operationID);
    }

    @Override
    public DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType, String operationID) {
        return new DSRequestImpl(dataSourceName, opType, operationID);
    }

    @Override
    public DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType) {
        return new DSRequestImpl(dataSourceName, opType);
    }

    @Override
    public DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType, DSCall rpc) {
        return new DSRequestImpl(dataSourceName, opType, rpc);
    }

    @Override
    public DSResponse createDSResponse() {
        return new DSResponseImpl();
    }

    /**
     * @param context
     * @return
     * @throws SlxException
     */
    @Override
    public DataSource generateDataSource(DataSourceData context) throws SlxException {
        if (context == null)
            throw new java.lang.IllegalArgumentException("DataSourceData must be not null.");
        if(context.getServerType()==null)
            throw new java.lang.IllegalArgumentException("DataSourceData must setting serverType.");
        EserverType serverType = context.getServerType();
        DataSourceGenerator generator=null;
        for (DataSource ds : getProviders()) {
            if (ds.getServerType().equals(serverType.value())) {
                 generator = ds.getDataSourceGenerator();
                 break;
            }
        }
        if(generator!=null){
            DataSource instance = generator.generateDataSource(context);
            // not from pool.
            instance.getContext().setWaitForFree(false);
            return instance;
        }else{
           throw new SlxException(Tmodule.DATASOURCE,Texception.DS_DSCONFIG_OBJECT_TYPE_ERROR,"No found Datasource generator"); 
        }
    }

}
