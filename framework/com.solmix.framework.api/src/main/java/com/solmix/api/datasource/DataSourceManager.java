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

package com.solmix.api.datasource;

import java.util.List;

import com.solmix.api.context.Context;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;
import com.solmix.api.jaxb.request.Roperation;
import com.solmix.api.pool.PoolService;
import com.solmix.api.rpc.RPCManager;

/**
 * 
 * @version 110035
 */
public interface DataSourceManager
{

    /**
     * Get Pool Manager.
     */
    PoolService getPoolService();

    /**
     * Rest Pool Manager.
     */
    void restartPoolService();

    /**
     * Return the DataSource by datasource's name.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    // IDataSource getDataSource(String name) throws Exception;

    /**
     * Return the DataSource by datasource's name.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    DataSource get(String name) throws SlxException;

    DataSource get(String name, DSRequest request) throws SlxException;

    /**
     * No pooled data source.
     * 
     * @param name
     * @return
     * @throws Exception
     */
    DataSource getUnpooledDataSource(String name) throws SlxException;

    /**
     * free DS resource.
     * 
     * @param ds
     */
    // void freeDataSource(IDataSource ds);

    void free(DataSource ds);

    List<DataSource> getProviders();

    DSRequest createDSRequest();
    DSResponse createDSResponse();
    
    DSRequest createDSRequest(DataSource dataSource);

    DSRequest createDSRequest(String dataSourceName, Eoperation opType, RPCManager rpc);

    DSRequest createDSRequest(Roperation operation, Context context) throws SlxException;

    DSRequest createDSRequest(String dataSourceName, Eoperation opType);

    DSRequest createDSRequest(String dataSourceName, Eoperation opType, String operationID);
}
