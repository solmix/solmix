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

import com.solmix.api.context.Context;
import com.solmix.api.data.DSRequestData;
import com.solmix.api.exception.SlxException;
import com.solmix.api.rpc.RPCManager;
import com.solmix.api.rpc.RequestType;

/**
 * </b> DataSource Binding Request</b>
 * <p>
 * Implements the main datasource request function.
 * 
 * @version 110035
 */
public interface DSRequest extends RequestType
{

    DSRequestData getContext();

    void setContext(DSRequestData data);

    /**
     * Execute the datasource request.
     * 
     * @return datasource response
     * @throws SlxException
     */
    DSResponse execute() throws SlxException;

    /**
     * request holder
     * 
     * @return
     */
    Context getRequestContext();

    void setRequestContext(Context context) throws SlxException;

    /**
     * {@link com.solmix.api.rpc.RPCManager} for this request.
     * 
     * @return
     */
    RPCManager getRpc();

    DataSource getDataSource() throws SlxException;

    void setDataSource(DataSource dataSource);

    void setRpc(RPCManager rpc);

    /**
     * release datasource
     */
    void freeResources();

    void registerFreeResourcesHandler(FreeResourcesHandler handler);

    boolean isModificationRequest(DSRequest req) throws SlxException;

    /**
     * find out datasource name.
     * 
     * @return
     */
    String getDataSourceName();

    /**
     * setting datasource name.use this attribute to find datasource configuration object for usually.
     * 
     * @param dataSourceName
     */
    void setDataSourceName(String dataSourceName);

    /**
     * @param values
     */
    void setValidatedValues(Object values);

    /**
     * @param requestStarted
     */
    void setRequestStarted(boolean requestStarted);

    /**
     * @return
     */
    boolean isRequestStarted();

    /**
     * @param joinTransaction
     * @throws SlxException
     */
    void setJoinTransaction(Boolean joinTransaction) throws SlxException;

    /**
     * Global transaction support,Indicate This Request is auto join a Transaction.
     * @return
     */
    Boolean getJoinTransaction();

    /**
     * @param partOfTransaction
     */
    void setPartOfTransaction(boolean partOfTransaction);

    /**
     * @return
     */
    boolean isPartOfTransaction();

    /**
     * @param beenThroughValidation
     */
    void setBeenThroughValidation(boolean beenThroughValidation);

    /**
     * @return
     */
    boolean isBeenThroughValidation();

    /**
     * @return
     */
    boolean isBeenThroughDMI();

    /**
     * @param beenThroughDMI
     */
    void setBeenThroughDMI(boolean beenThroughDMI);

}
