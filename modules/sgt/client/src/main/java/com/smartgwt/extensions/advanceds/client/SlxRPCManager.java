
package com.smartgwt.extensions.advanceds.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;

public class SlxRPCManager
{

	public static final String SLX_BIN_PREFIX="data/bin/";
	public static final String SLX_DS_SUFF=".ds";
    public static void send(Roperation oper) {
        // if (RootPanel.get("__slx_hidden_form") != null) {
        // Widget frame = (Widget) RootPanel.get("__slx_hidden_form");
        // frame.removeFromParent();
        // }
        String action = SLX_BIN_PREFIX + oper.getOperationType().getValue() + "/" + oper.getDataSource() +SLX_DS_SUFF;
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

    public static void send(Roperation oper, final SlxRPCCallBack callback) {
        RPCRequest f = new RPCRequest();
        f.setUseSimpleHttp(true);
        String action = SLX_BIN_PREFIX + oper.getOperationType().getValue() + "/" + oper.getDataSource() +SLX_DS_SUFF;
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

    public static void send(Roperation oper[], final SlxRPCCallBack callback) {
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

    public static void send(DSRequest dsRequest, Criteria criteria) {
        Roperation op = new Roperation();
        transform(dsRequest, criteria, op);
        send(op);
    }

    public static void send(DSRequest dsRequest, Criteria criteria, SlxRPCCallBack callback) {

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
