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

package org.solmix.api.datasource;

import java.util.List;

import org.solmix.api.call.DataSourceCall;
import org.solmix.api.context.Context;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.pool.PoolManager;

/**
 * 
 * @version 110035
 */
public interface DataSourceManager
{

    /**
     * Get Pool Manager.
     */
    PoolManager getPoolManager();

    /**
     * Rest Pool Manager.
     */
    void restartPoolManager();

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

    void free(DataSource ds);

    /**
     * Get datasource providers.
     * @return
     */
    List<DataSource> getProviders();

    /**
     * Create a void DSResponse,from some situation,such as return datasource call.
     * 
     * @return
     */
    DSResponse createDSResponse();

    /**
     * Create a void DSRequest.
     * 
     * @return
     */
    DSRequest createDSRequest();

    DSRequest createDSRequest(Roperation operation, Context context) throws SlxException;

    DSRequest createDSRequest(String dataSourceName, Eoperation opType, String operationID);

    DSRequest createDSRequest(String dataSourceName, Eoperation opType);

    DSRequest createDSRequest(String dataSourceName, Eoperation opType, DataSourceCall rpc);

    DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType, String operationID);

    DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType);

    DSRequest createDSRequest(DataSource dataSourceName, Eoperation opType, DataSourceCall rpc);

    /**
     * @param context
     * @return
     * @throws SlxException
     */
    DataSource generateDataSource(DataSourceData context) throws SlxException;
}
