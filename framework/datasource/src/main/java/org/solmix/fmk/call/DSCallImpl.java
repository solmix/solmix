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

package org.solmix.fmk.call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallCompleteCallback;
import org.solmix.api.call.DSCallInterceptor;
import org.solmix.api.call.DSCallInterceptor.Action;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSRequestData;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.TfieldNameValue;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.api.types.TransactionPolicy;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.serialize.XMLParserFactoryImpl;
import org.solmix.fmk.velocity.Velocity;
import org.solmix.runtime.Context;

/**
 * complex relationship at this class,ant simple configuration at data class {@link org.solmix.api.data.DSCManagerData
 * DSCManagerData}.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110040 2011-1-1 solmix-ds
 */
public class DSCallImpl implements DSCall
{

    private static final Logger log = LoggerFactory.getLogger(DSCallImpl.class.getName());

    protected final DSCallInterceptor[] interceptors;// = new LinkedList<DSCallInterceptor>();

    private List<DataSource> dsToFree;

    private final HashSet<DSCallCompleteCallback> callbacks = new HashSet<DSCallCompleteCallback>();

    private List<DSRequest> requests;

    private Context context;

    private JSParser jsParser;

    private XMLParser xmlParser;

    private Map<Object, Object> attributes;

    public Map<String, Object> templateContext;

    private final Map<DSRequest, DSResponse> responseMap = new HashMap<DSRequest, DSResponse>();;

    private Long transactionNum;

    private TransactionPolicy transactionPolicy;

    private enum STATUS
    {
        INIT , BEGIN , SUCCESS , FAILED , END;
    }

    private  STATUS status = STATUS.INIT;

    public DSCallImpl(DSCallInterceptor... callInterceptors)
    {
        this.interceptors = callInterceptors;
    }

    public DataSource getDataSource(String dsName) throws Exception {
        DataSource ds = DefaultDataSourceManager.getDataSource(dsName);
        if (ds != null)
            dsToFree.add(ds);
        return ds;
    }

    /**
     * @return the transactionPolicy
     */
    @Override
    public TransactionPolicy getTransactionPolicy() {
        return transactionPolicy;
    }

    /**
     * @param transactionPolicy the transactionPolicy to set
     * @throws SlxException
     */
    @Override
    public void setTransactionPolicy(TransactionPolicy transactionPolicy) throws SlxException {
        if (status == STATUS.BEGIN)
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_REQUEST_ALREADY_STARTED, "dsRequest already started.");
        this.transactionPolicy = transactionPolicy;
    }

    /**
     * @return the transactionNum
     */
    @Override
    public Long getTransactionNum() {
        return transactionNum;
    }

    /**
     * @param transactionNum the transactionNum to set
     */
    @Override
    public void setTransactionNum(Long transactionNum) {
        this.transactionNum = transactionNum;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCall#requestCount()
     */
    @Override
    public int requestCount() {
        return requests.size();
    }

    @Override
    public void applyEarlierResponseValues(DSRequest dsReq) throws SlxException {
        // setRequestProcessingStarted(true);
        DSRequestData _reqData = dsReq.getContext();
        // Date _currentDate = new Date();
        // addToTemplateContext("currentDate", _currentDate);
        // if (getFromTemplateContext("transactionDate") == null)
        // addToTemplateContext("transactionDate", _currentDate);
        DataSource ds = dsReq.getDataSource();
        if (ds == null)
            return;
        Eoperation opType = _reqData.getOperationType();
        String opId = _reqData.getOperationId();
        ToperationBinding opBinding = ds.getContext().getOperationBinding(opType, opId);
        if (opBinding == null)
            return;
        List<TfieldNameValue> criterias = opBinding.getCriteria();
        List<TfieldNameValue> values = opBinding.getValues();
        if (criterias == null && values == null)
            return;
        Map<String, Object> params = Velocity.getStandardContextMap(dsReq);
        Map<String, Object> c = dsReq.getContext().getCriteria();
        for (TfieldNameValue criteria : criterias) {
            String fieldName = criteria.getFieldName();
            String value = criteria.getValue();
            if (fieldName != null && value != null) {
                if (c.get(fieldName) == null) {
                    Object evaluation = Velocity.evaluate(value, params);
                    dsReq.getContext().addToCriteria(fieldName, evaluation);
                }
            }
        }
        Map<String, Object> vs = dsReq.getContext().getValues();
        for (TfieldNameValue v : values) {
            String fieldName = v.getFieldName();
            String value = v.getValue();
            if (fieldName != null && value != null) {
                if (vs.get(fieldName) == null) {
                    Object evaluation = Velocity.evaluate(value, params);
                    dsReq.getContext().addToCriteria(fieldName, evaluation);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCall#run()
     */
    @Override
    public void run() throws SlxException {
        for (DSCallInterceptor i : interceptors) {
            i.prepareRequest(this, context);
        }
        for (DSCallInterceptor i : interceptors) {
            i.inspect(this, context);
        }
        List<DSRequest> reqs = getRequests();
        boolean _failure = false;
        status = STATUS.BEGIN;
        if (log.isTraceEnabled())
            log.trace("Performing " +requestCount() + " operation(s) ");
        try {
            for (DSRequest req : reqs) {
                DSResponse res=null;
                try {
                    res = req.execute();
                } catch (SlxException e) {
                    res = new DSResponseImpl(req);
                    res.setRawData(e.getMessage());
                    res.setStatus(Status.STATUS_FAILURE);
                    log.error("DSRequest execute Failed:",e);
                }
                if (res != null)
                    responseMap.put(req, res);
            }
            if (responseMap.size() != requestCount()) {
                throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_EXCEPTION,
                    new StringBuilder().append("Having ").append(requestCount()).append(" requests,But having ").append(responseMap.size()).append(
                        " responses").toString());
            }
            for (DSRequest req : reqs) {
                DSResponse res = getResponse(req);
                if (res.getStatus().value() < 0) {
                    _failure = true;
                    break;
                }
            }
            try {
                if (_failure)
                    onFailure();
                else
                    onSuccess();
            } catch (Exception e) {
                log.warn(DataUtils.getStackTrace(e));
            }
            // post inspect
            for (DSCallInterceptor i : interceptors) {
                Action res = i.postInspect(this, context);
                if (res == Action.CANCELLED)
                    break;
            }
        } finally {
            /**
             * loop requests to free resource.because some request support transaction,so it's datasource is not free no
             * execute ,but now a DSC session is complete,the DataSource borrow from PoolManager must return .
             */
            for (Object request : requests) {
                // process DSRequest
                if (request instanceof DSRequest) {
                    DSRequest dsRequest = (DSRequest) request;
                    if (!dsRequest.isFreeOnExecute())
                        dsRequest.freeResources();
                }
            }
        }
    }

    /**
     * Add requst to a request list,principally for mulitply request
     * 
     * @param req
     */
    @Override
    public void addRequest(DSRequest req) {
        if (requests == null)
            requests = new ArrayList<DSRequest>();
        requests.add(req);
    }

    @Override
    public List<DSRequest> getRequests() {

        return requests;
    }

    @Override
    public DSResponse getResponse(DSRequest req) {
        return this.responseMap.get(req);
    }

    @Override
    public Context getRequestContext() {
        return context;
    }

    @Override
    public void setRequestContext(Context context) throws SlxException {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCall#freeDataSources()
     */
    @Override
    public void freeDataSources() {
        if (dsToFree != null)
            for (DataSource ds : dsToFree)
                DefaultDataSourceManager.freeDataSource(ds);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCall#registerCallback(org.solmix.api.call.DSCallCompleteCallback)
     */
    @Override
    public void registerCallback(DSCallCompleteCallback callback) {
        if (!callbacks.contains(callback))
            callbacks.add(callback);

    }

    public DSResponse execute(final XAOp op) throws SlxException {
        op.setRpc(this);
        DSResponse t = null;
        try {
            t = op.exe();
        } catch (Exception e) {
            try {
                transactionFailed(op.getRequest(), t);
            } catch (Exception e1) {
                throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_ROLLBACK_FAILTURE, "transaction rollback failure with rollback Exception:"
                    + e1.getMessage() + " with Root Exception:" + e.getMessage());
            }
            throw wrapperException(e);
        }
        boolean _transactionFailure = isXAFailure(op.getRequest(), t);
        if (_transactionFailure) {
            transactionFailed(op.getRequest(), t);
            throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_BREAKEN, "transaction breaken because of one request failure.");
        }
        return t;
    }

    private SlxException wrapperException(Exception e) {
        if(e instanceof SlxException)
            return (SlxException)e;
        return new SlxException(Tmodule.DSC, Texception.TRANSACTION_EXCEPTION,e);
    }

    private boolean isXAFailure(DSRequest req, DSResponse res) throws SlxException {
        boolean _transactionFailure = false;
        if (res != null && res.getStatus().value() < 0)
            if (req.isRequestStarted()) {
                if (req.isJoinTransaction())
                    _transactionFailure = true;
            } else {
                BasicDataSource ds = (BasicDataSource) req.getDataSource();
                if (ds.shouldAutoJoinTransaction(req) && (ds.shouldAutoStartTransaction(req, true) || requestQueueIncludesUpdates(req)))
                    _transactionFailure = true;
            }
        return _transactionFailure;
    }

    /*
     * @Override public void send(DSRequest dsRequest, DSResponse dsResponse) throws SlxException { if
     * (data.getIsDownload() == null) data.setIsDownload(dsRequest.getContext().getIsDownload()); if (data.getIsExport()
     * == null) data.setIsExport(dsRequest.getContext().getIsExport()); // if (data.getIsExport() == null) { //
     * ToperationBinding __op = DataTools.getOperationBindingFromDSByRequest(dsRequest.getDataSource(), dsRequest); //
     * data.setIsExport(__op == null ? null : __op.isExportResults()); // } if(status!=STATUS.BEGIN){ throw new
     * SlxException
     * (Tmodule.DSC,Texception.TRANSACTION_MUST_END_BEFORE_SEND,"Transaction must end before send a DSRequest"); }
     * responseMap.put(dsRequest, dsResponse); if (responseMap.size() == requestCount()) { completeResponse(); } }
     */

    @Override
    public boolean requestQueueIncludesUpdates(DSRequest req) throws SlxException {
        if (requests == null)
            return false;
        for (DSRequest request : requests) {
            if (request.equals(req)) {
                return false;
            }
            if (request.isModificationRequest())
                return true;
        }
        return false;
    }

    public void beginTransaction() throws SlxException {

        if (status != STATUS.INIT)
            throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_EXCEPTION, "Transaction have been started");
        status = STATUS.BEGIN;
    }

    public void endTransaction() {
        status = STATUS.SUCCESS;
        try {
            onSuccess();
        } catch (Exception e) {
            log.debug("exception when end transaction", e);
        }
    }

    public void rollback() throws SlxException {
        status = STATUS.FAILED;
        if (callbacks != null)
            for (DSCallCompleteCallback callback : callbacks) {
                callback.onFailure(this, true);
            }
    }

    protected void onSuccess() throws Exception {
        status = STATUS.SUCCESS;
        for (DSCallCompleteCallback callback : callbacks) {
            callback.onSuccess(this);
        }
    }

    protected void onFailure() throws Exception {
        status = STATUS.FAILED;
        boolean _transactionFailure = false;
        if (requests == null)
            return;
        for (DSRequest req : requests) {
            DSResponse resp = getResponse(req);
            _transactionFailure = isXAFailure(req, resp);
        }
        if (_transactionFailure) {
            for (DSRequest req : requests) {
                if (req.isJoinTransaction()) {
                    DSResponse resp = getResponse(req);
                    if (resp != null && resp.getStatus() == DSResponse.Status.STATUS_SUCCESS)
                        resp.setStatus(DSResponse.Status.STATUS_TRANSACTION_FAILED);
                }
            }
        }
        if (callbacks != null)
            for (DSCallCompleteCallback callback : callbacks) {
                callback.onFailure(this, _transactionFailure);
            }
    }

    public DSResponse transactionExecute(DSRequest request) throws SlxException {
        if (status != STATUS.BEGIN) {
            throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_NOT_STARTED,
                "Transaction not started ,you should call method startTransaction()");
        }
        request.setDSCall(this);
        request.setCanJoinTransaction(true);
        DSResponse res = null;
        try {
            res = request.execute();
        } catch (Exception e) {
            try {
                transactionFailed(request, res);
            } catch (Exception e1) {
                throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_ROLLBACK_FAILTURE, "transaction rollback failure with rollback Exception:"
                    + e1.getMessage() + " with Root Exception:" + e.getMessage());
            }
            throw wrapperException(e);
        }
        boolean _transactionFailure = isXAFailure(request, res);
        if (_transactionFailure) {
            transactionFailed(request, res);
            throw new SlxException(Tmodule.DSC, Texception.TRANSACTION_BREAKEN, "transaction breaken because of one request failure.");

        }
        return res;

    }

    private void transactionFailed(DSRequest request, DSResponse resp) throws SlxException {
        if (request.isJoinTransaction()) {
            if (resp != null && resp.getStatus() == DSResponse.Status.STATUS_SUCCESS)
                resp.setStatus(DSResponse.Status.STATUS_TRANSACTION_FAILED);
        }
        rollback();
    }

    /**
     * @return the jsParser
     */
    @Override
    public JSParser getJSParser() {
        if (jsParser == null) {
            JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
            jsParser = jsFactory.get();
        }
        return jsParser;
    }

    @Override
    public XMLParser getXMLParser() {
        if (xmlParser == null) {
            XMLParserFactory factory = XMLParserFactoryImpl.getInstance();
            xmlParser = factory.get();
        }
        return xmlParser;
    }

    @Override
    public Object getAttribute(Object key) {
        if (attributes != null)
            return attributes.get(key);
        return null;
    }

    @Override
    public void setAttribute(Object key, Object value) {
        if (attributes == null)
            attributes = new LinkedHashMap<Object, Object>();
        attributes.put(key, value);

    }

    @Override
    public void removeAttribute(Object key) {
        if (attributes != null)
            attributes.remove(key);

    }

    @Override
    public Map<String, Object> getTemplateContext() {
        if(templateContext==null)
            templateContext=Collections.emptyMap();
        return templateContext;
    }

}
