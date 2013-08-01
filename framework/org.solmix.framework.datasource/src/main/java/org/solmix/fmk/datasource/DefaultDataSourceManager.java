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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.SlxConstants;
import org.solmix.api.context.Context;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.pool.PoolService;
import org.solmix.api.pool.PoolServiceFactory;
import org.solmix.api.repo.DSRepositoryManager;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;

/**
 * DataSourceManager service implements.
 * 
 * @version 0.0.4
 */
public class DefaultDataSourceManager implements DataSourceManager
{

    private static List<DataSource> providers;

    public static volatile PoolService manager;

    private PoolServiceFactory poolServiceFactory;

    private static volatile DSRepositoryManager repoService;

    private static Logger log;

    /**
     * @return the poolServiceFactory
     */
    public PoolServiceFactory getPoolServiceFactory() {
        return poolServiceFactory;
    }

    /**
     * @param poolServiceFactory the poolServiceFactory to set
     */
    public void setPoolServiceFactory(PoolServiceFactory poolServiceFactory) {
        this.poolServiceFactory = poolServiceFactory;
    }

    static {
        providers = new CopyOnWriteArrayList<DataSource>();
        // and the default datasource implementations.
        providers.add(new BasicDataSource());
        // providers.add( new SQLDataSource() );

    }

    public DefaultDataSourceManager()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#free(org.solmix.api.datasource.DataSource)
     */
    @Override
    public void free(DataSource ds) {
        freeDataSource(ds);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#freeDataSource(org.solmix.api.datasource.DataSource)
     */
    public static void freeDataSource(DataSource ds) {
        if (ds == null) {
            return;
        } else if (ds.getContext().isWaitForFree()) {
            manager.returnObject(ds.getName(), ds);
            ds.getContext().setWaitForFree(false);
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
        return getDataSource(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.solmix.api.datasource.DataSourceManagerService#getDataSource(java.lang.String)
     */
    public static DataSource getDataSource(String name) throws SlxException {
        DataSource _return = (DataSource) manager.borrowObject(name);
        // avoid one borrow more than one return.
        if (_return != null)
            _return.getContext().setWaitForFree(true);
        return _return;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.solmix.api.datasource.DataSourceManagerService#getUnpooledDataSource(java.lang.String)
     */
    @Override
    public DataSource getUnpooledDataSource(String name) throws SlxException {

        return (DataSource) manager.borrowUnpooledObject(name);
    }

    @Override
    public PoolService getPoolService() {
        return manager;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#getProviders()
     */
    @Override
    public List<DataSource> getProviders() {
        return providers;
    }

    public synchronized void setProviders(List<DataSource> provider) {
        
        DefaultDataSourceManager.providers =Collections.synchronizedList(provider);
    }

    public static List<DataSource> getDataSourceProviders() {
        return providers;
    }

    public void unregister(DataSource datasource) {
        synchronized (providers) {
            providers.remove(datasource);
        }
    }

    public void register(DataSource datasource) {
        String serverType = datasource.getServerType();
        if (serverType == null)
            return;
        synchronized (providers) {
            for (DataSource ds : providers) {
                if (serverType.equalsIgnoreCase(ds.getServerType()))
                    providers.remove(ds);
            }
            providers.add(datasource);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#get(java.lang.String,
     *      org.solmix.api.datasource.DSRequest)
     */
    @Override
    public DataSource get(String name, DSRequest request) throws SlxException {
        return getDataSource(name, request);
    }

    public static DataSource getDataSource(String name, DSRequest request) throws SlxException {
        DataSource _return = (DataSource) manager.borrowObject(name, request);
        _return.getContext().setWaitForFree(true);
        return _return;
    }

    public void destroy() {
        if (manager != null)
            manager.destroy();
    }

    public void init() {
        log = LoggerFactory.getLogger(DataSourceManager.class.getName());
        log.debug("Initial & create DataSource Pool");
        manager = poolServiceFactory.createPoolService(SlxConstants.MODULE_DS_NAME, new PoolableDataSourceFactory());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSourceManagerService#restartPoolService()
     */
    @Override
    public void restartPoolService() {
        log.info("Restart DataSource Pool");
        manager = poolServiceFactory.createPoolService(SlxConstants.MODULE_DS_NAME, new PoolableDataSourceFactory());

    }

    /**
     * @return the repoService
     */
    public static DSRepositoryManager getRepoService() {
        if (repoService == null) {
            try {
                throw new SlxException(Tmodule.REPO, Texception.OSGI_SERVICE_UNAVAILABLE, "osgi DSRepositoryManager is unavalable");
            } catch (SlxException e) {
                log.error(e.getMessage());
            }
        }
        return repoService;
    }

    /**
     * @param repoService the repoService to set
     */
    public void setRepoService(DSRepositoryManager repoService) {
        DefaultDataSourceManager.repoService = repoService;
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
    public DSRequest createDSRequest(String dataSourceName, Eoperation opType, RPCManager rpc) {
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
    public DSRequest createDSRequest(DataSource dataSource) {
        return new DSRequestImpl(dataSource);
    }

    @Override
    public DSResponse createDSResponse() {
        return  new DSResponseImpl();
    }

   

}
