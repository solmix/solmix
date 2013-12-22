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

package org.solmix.api.call;

import java.util.List;

import org.solmix.api.context.WebContext;
import org.solmix.api.data.DSCManagerData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.types.TransactionPolicy;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 0.1.1 2011-1-1 solmix-api
 */
public interface DataSourceCall
{

    void setContext(DSCManagerData data);

    int requestCount();

    void addRequest(DSRequest req);

    void addRequest(RPCRequest req);

    DSCManagerData getContext();

    void send(DSRequest dsRequest, DSResponse dsResponse) throws SlxException;

    void send(DSRequest dsRequest, Object data) throws SlxException;

    void send(Object data) throws SlxException;

    void send(RPCRequest rpcRequest, Object data) throws SlxException;

    void send(RPCRequest rpcRequest, RPCResponse rpcResponse) throws SlxException;

    void send(RPCResponse rpcResponse) throws SlxException;

    void sendFailure(Object request, String error) throws SlxException;

    void sendFailure(Object request, Throwable t) throws SlxException;

    void sendSuccess(RPCRequest rpcRequest) throws SlxException;

    void sendXMLString(DSRequest dsRequest, String xml) throws SlxException;

    void sendXMLString(RPCRequest rpcRequest, String xml) throws SlxException;

    void registerCallback(DataSourceCallCompleteCallback callback);

    RPCRequest getRequest();

    List<RequestType> getRequests();

    DSResponse getResponse(DSRequest req);

    RPCResponse getResponse(RPCRequest req);

    void freeDataSources() throws SlxException;

    void applyEarlierResponseValues(DSRequest dsReq) throws SlxException;

    /**
     * Remote Process Call (RPC)，just only can handled WebContext.but {@link DSRequest} can used all Context to init.
     * 
     * @return
     */
    WebContext getRequestContext();

    void setRequestContext(WebContext context) throws SlxException;

    /**
     * @return
     * @throws SlxException
     */
    boolean requestQueueIncludesUpdates() throws SlxException;

    /**
     * @param transactionPolicy
     * @throws SlxException
     */
    void setTransactionPolicy(TransactionPolicy transactionPolicy) throws SlxException;

    /**
     * @return
     */
    Boolean getRequestProcessingStarted();

    /**
     * @param requestProcessingStarted
     */
    void setRequestProcessingStarted(Boolean requestProcessingStarted);

    /**
     * @return
     */
    Long getTransactionNum();

    /**
     * @param transactionNum
     */
    void setTransactionNum(Long transactionNum);

    /**
     * @return
     */
    TransactionPolicy getTransactionPolicy();

    JSParser getJsParser();
}
