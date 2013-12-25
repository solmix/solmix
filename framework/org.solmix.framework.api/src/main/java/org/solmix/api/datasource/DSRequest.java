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

import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallCompleteCallback;
import org.solmix.api.call.RequestType;
import org.solmix.api.context.Context;
import org.solmix.api.data.DSRequestData;
import org.solmix.api.exception.SlxException;

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
     * {@link org.solmix.api.call.DSCall} for this request.
     * 
     * @return
     */
    DSCall getDSCall();

    DataSource getDataSource() throws SlxException;

    void setDataSource(DataSource dataSource);

    void setDSCall(DSCall dsc);

    /**
     * release datasource
     */
    void freeResources();

    void registerFreeResourcesHandler(FreeResourcesHandler handler);

    boolean isModificationRequest() throws SlxException;

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
     * @param canJoinTransaction
     * @throws SlxException
     */
    void setCanJoinTransaction(Boolean canJoinTransaction) throws SlxException;

    /**
     * Global transaction support,Indicate This Request is auto join a Transaction.
     * @return
     */
    Boolean isCanJoinTransaction();

    /**
     * Indicate this request join a transaction.
     * @param joinTransaction
     */
    void setJoinTransaction(boolean joinTransaction);

    /**
     * @return
     */
    boolean isJoinTransaction();

    /**
     * If true indicate the ds-request have been validated.
     * @param validated.
     */
    void setValidated(boolean validate);

    /**
     * Checkout the Ds-request is validated or not.
     * @return
     */
    boolean isValidated();

    /**
     * @return
     */
    boolean isServiceCalled();

    /**
     * Pointed out that this DSRequest is passed through ServiceObject checking or not.
     * @param beenThroughDMI
     */
    void setServiceCalled(boolean aerviceCalled);
    /**
     * Used to support DSC transaction.
     * <p>
     * If this value is true ,will not free datasource utile manual free it. if used sql or jpa datasource,must used rpc
     * with {@link DSCallCompleteCallback} to commit the transaction. if not,should commit it yourself.
     * 
     * @return the freeOnExecute
     */
    public boolean isFreeOnExecute() ;

    /**
     * If <code>true<code>,this request will free datasource at the end of this request process.
     * <P>
     * <b>NOTE:</B> if set this value is <code>false<code>,you must free datasource manual.
     * 
     * @see #isFreeOnExecute()
     * @param freeOnExecute the freeOnExecute to set
     */
    public void setFreeOnExecute(boolean freeOnExecute) ;

}
