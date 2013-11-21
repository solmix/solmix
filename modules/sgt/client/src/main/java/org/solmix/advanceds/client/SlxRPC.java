/*
 * SOLMIX PROJECT
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

package org.solmix.advanceds.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.DataBoundComponent;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-17
 */

public class SlxRPC
{

    public static final String SLX_BIN_PREFIX = "data/bin/";

    public static final String SLX_DS_SUFF = ".ds";

    public static void send(Roperation oper) {
        String action = SLX_BIN_PREFIX + oper.getOperationType().getValue() + "/" + oper.getDataSource() + SLX_DS_SUFF;
        if (oper.getExportFilename() != null) {
            action = action + "/" + oper.getExportFilename();
        }
        HiddenForm f = new HiddenForm();
        f.setAction(action);
        Request request = new Request();
        request.setRoperations(oper);
        f.setData(JSON.encode(request.getJsObj()));
        f.submit();
    }
    public static void send(Roperation oper, final JSCallBack callback) {
        RPCRequest f = new RPCRequest();
        f.setUseSimpleHttp(true);
        String action = SLX_BIN_PREFIX + oper.getOperationType().getValue() + "/" + oper.getDataSource() + SLX_DS_SUFF;
        f.setActionURL(action);
        Request request = new Request(true);
        request.setRoperations(oper);
        f.setData(JSON.encode(request.getJsObj()));
        RPCManager.sendRequest(f, new RPCCallback() {

            @Override
            public void execute(RPCResponse response, Object rawData, RPCRequest request) {
                if (rawData instanceof JavaScriptObject) {
                    response.setJavaScriptObject((JavaScriptObject) rawData);
                    callback.execute(response, (JavaScriptObject) rawData, request);
                } else {
                    JavaScriptObject result = JSOHelper.getAttributeAsJavaScriptObject(JSOHelper.eval(rawData.toString()), "response");
                    response.setJavaScriptObject(result);
                    callback.execute(response, result, request);
                }
            }

        });
    }
    public static void send(Roperation oper, final XMLCallBack callback) {
        RPCRequest f = new RPCRequest();
        f.setUseSimpleHttp(true);
        String action = SLX_BIN_PREFIX + oper.getOperationType().getValue() + "/" + oper.getDataSource() + SLX_DS_SUFF;
        f.setActionURL(action);
        Request request = new Request(true);
        request.setRoperations(oper);
        f.setData(JSON.encode(request.getJsObj()));
        RPCManager.sendRequest(f, new RPCCallback() {

            @Override
            public void execute(RPCResponse response, Object rawData, RPCRequest request) {
                if(rawData==null){
                    SC.warn("Server return null Value,Please checkout!");
                    return;
                }
               callback.execute(response,rawData.toString(),request);
            }

        });
    }
    public static void send(Roperation oper[],final JSCallBack callback) {
        if (oper == null || oper.length < 0)
            return;
        RPCRequest f = new RPCRequest();
        f.setUseSimpleHttp(true);
        String action = SLX_BIN_PREFIX + oper[0].getOperationType().getValue() + "/" + oper[0].getDataSource() + SLX_DS_SUFF;
        f.setActionURL(action);
        Request request = new Request(true);
        request.setRoperations(oper);
        f.setData(JSON.encode(request.getJsObj()));
        RPCManager.sendRequest(f, new RPCCallback() {

            @Override
            public void execute(RPCResponse response, Object rawData, RPCRequest request) {
                if (rawData instanceof JavaScriptObject) {
                    response.setJavaScriptObject((JavaScriptObject) rawData);
                    callback.execute(response, (JavaScriptObject) rawData, request);

                } else {
                    JavaScriptObject result = JSOHelper.getAttributeAsJavaScriptObject(JSOHelper.eval(rawData.toString()), "response");
                    response.setJavaScriptObject(result);
                    callback.execute(response, result, request);
                }
            }

        });
    }
    /**
     * Used {@link #send(Roperation, JSCallBack)}.
     * @param oper
     * @param callback
     */
    @Deprecated
    public static void send(Roperation oper, final SlxRPCCallBack callback) {
       send(oper,(JSCallBack)callback);

    }

    /**
     * Used {@link #send(Roperation[], JSCallBack)}.
     * @param oper
     * @param callback
     */
    @Deprecated
    public static void send(Roperation oper[], final SlxRPCCallBack callback) {
       
        send(oper,(JSCallBack)callback);
    }

    public static void send(DSRequest dsRequest, Criteria criteria) {
        Roperation op = new Roperation();
        transform(dsRequest, criteria, op);
        send(op);
    }
    public static void bind(final String datasourceName, final DataBoundComponent component, final Function callback) {
        DataSource ds = DataSource.get(datasourceName);
        if (ds == null) {
            DataSource.load(datasourceName, new Function() {

                @Override
                public void execute() {
                    DataSource _ds = DataSource.get(datasourceName);
                    component.setDataSource(_ds);
                    if (callback != null)
                        callback.execute();
                }
            }, true);
        } else {
            component.setDataSource(ds);
            if (callback != null)
                callback.execute();
        }
    }

    @SuppressWarnings("unchecked")
    public static Roperation transform(DSRequest ds, Criteria criteria, Roperation t) {
        t.setAppID("defaultApplication");
        if (ds.getComponentId() != null)
            t.setComponentId(ds.getComponentId());
        if (ds.getOperationId() != null)
            t.setOperationId(ds.getOperationId());
        if (ds.getOutputs() != null)
            t.setOutputs(ds.getOutputs());
        if (ds.getStartRow() != null)
            t.setStartRow(ds.getStartRow());
        if (ds.getEndRow() != null)
            t.setEndRow(ds.getEndRow());
        if (ds.getSortBy() != null)
            t.setSortBy(ds.getSortBy());
        if (ds.getTextMatchStyle() != null)
            t.setTextMatchStyle(ds.getTextMatchStyle());
        if (ds.getRequestId() != null)
            t.setRequestId(ds.getRequestId());
        if (ds.getExportResults() != null) {
            t.setExportResults(ds.getExportResults());
            if (ds.getExportResults()) {
                if (ds.getExportAs() != null)
                    t.setExportAs(ds.getExportAs());
                if (ds.getExportFilename() != null)
                    t.setExportFilename(ds.getExportFilename());
                if (ds.getLineBreakStyle() != null)
                    t.setLineBreakStyle(ds.getLineBreakStyle());
                if (ds.getExportDelimiter() != null)
                    t.setExportDelimiter(ds.getExportDelimiter());
                if (ds.getExportDatesAsFormattedString() != null)
                    t.setExportDatesAsFormattedString(ds.getExportDatesAsFormattedString());
                if (ds.getExportTitleSeparatorChar() != null)
                    t.setExportTitleSeparatorChar(ds.getExportTitleSeparatorChar());
                if (ds.getExportDisplay() != null)
                    t.setExportDisplay(ds.getExportDisplay());
                if (ds.getExportHeader() != null)
                    t.setExportHeader(ds.getExportHeader());
                if (ds.getExportFooter() != null)
                    t.setExportFooter(ds.getExportFooter());
                if (ds.getExportFields() != null) {
                    t.setExportFields(ds.getExportFields());
                }
                if (ds.getExportTitleSeparatorChar() != null)
                    t.setExportTitleSeparatorChar(ds.getExportTitleSeparatorChar());
            }
        }
        if (ds.getDataSource() != null) {
            t.setDataSource(ds.getDataSource());
        }
        if (ds.getOperationType() != null)
            t.setOperationType(ds.getOperationType());
        if (criteria != null) {
            t.setCriteria(criteria);
        } else if (ds.getCriteria() != null) {
            if (ds.getOperationType() == DSOperationType.FETCH || ds.getOperationType() == DSOperationType.REMOVE)
                t.setCriteria(ds.getCriteria());
            else
                t.setValues(ds.getCriteria());
        }
        if (ds.getOldValues() != null) {
            t.setOldValues(ds.getOldValues().toMap());
        }

        return t;
    }
}
