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
import java.util.Map;

import org.solmix.api.context.Context;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.types.TransactionPolicy;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 0.1.1 2011-1-1 solmix-api
 */
public interface DSCall
{

    /**
     * Get the requests in the call.
     * 
     * @return
     */
    List<DSRequest> getRequests();

    int requestCount();

    void addRequest(DSRequest req);

    void registerCallback(DSCallCompleteCallback callback);

    DSResponse getResponse(DSRequest req);

    void freeDataSources() throws SlxException;

    void applyEarlierResponseValues(DSRequest dsReq) throws SlxException;

    /**
     * Remote Process Call (DSC)，just only can handled WebContext.but {@link DSRequest} can used all Context to init.
     * 
     * @return
     */
    Context getRequestContext();

    /**
     * @return
     * @throws SlxException
     */
    boolean requestQueueIncludesUpdates(DSRequest request) throws SlxException;

    /**
     * @param transactionPolicy
     * @throws SlxException
     */
    void setTransactionPolicy(TransactionPolicy transactionPolicy) throws SlxException;

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

    JSParser getJSParser();

    XMLParser getXMLParser();

    void run() throws SlxException;

    /**
     * @param context
     * @throws SlxException
     */
    void setRequestContext(Context context) throws SlxException;

    Object getAttribute(Object key);

    void setAttribute(Object key, Object value);

    void removeAttribute(Object key);

    Map<String, Object> getTemplateContext();
}
