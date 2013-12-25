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

package org.solmix.web.interceptor;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.DSRequestImpl;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.fmk.js.ISCJavaScript;
import org.solmix.fmk.util.ServletTools;

/**
 * Interceptor for smartclient server(isomorphic).
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-25
 */

public class IscInterceptor extends DSCallRestInterceptor
{

    private static final Logger LOG = LoggerFactory.getLogger(IscInterceptor.class.getName());

    public static final String PAYLOAD_NAME = "_transaction";
    public static final String JS_CALLBACK = "JSCALLBACK";
    private static final String structuredRPCStart = "//isc_RPCResponseStart-->";

    private static final String structuredRPCEnd = "//isc_RPCResponseEnd";
    private String iscServerVersion;

    private String customHTML;

    @Override
    public void configure(DataTypeMap config) {
        super.configure(config);
        this.iscServerVersion = config.getString("iscServerVersion");
        this.customHTML = config.getString("customHTML", null);
    }

    @Override
    public void inspect(DSCall dsCall, WebContext context) throws SlxException {
        HttpServletRequest request = context.getRequest();
        if (!isRPC(request))
            return;
        Map<String, String> queryParamsMap = ServletTools.parseQueryString(request.getQueryString());
        String rawTransactionData = queryParamsMap.get(PAYLOAD_NAME);
        if (rawTransactionData == null)
            rawTransactionData = request.getParameter(PAYLOAD_NAME);
        if (DataUtil.isNullOrEmpty(rawTransactionData)) {
            Writer out = context.getOut();
            ServletTools.sendHTMLStart(out);
            LOG.warn("Detected zero-length IDA transaction, asking client to retry.");
            LOG.warn("Outputting extra debug information:");
            LOG.warn(context.getCookiesAsString());
            LOG.warn(context.getHeadersAsString());
            try {
                LOG.warn(context.getParamsAsString());
            } catch (Exception e) {
                LOG.warn("Couldn't log params", e);
            }
            try {
                writeDocumentDomain(out, request);
                out.write("<SCRIPT>");
                out.write("parent.isc.RPCManager.retryOperation(window.name);");
                out.write("</SCRIPT>");
                ServletTools.sendHTMLEnd(out);
                out.flush();
                context.getResponse().flushBuffer();
            } catch (IOException e) {
                throw new SlxException(Tmodule.SERVLET, Texception.IO_EXCEPTION, e);
            }
            throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_CLIENT_MUST_RESUBMIT, "Client must resubmit");
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(">>request data:\n" + rawTransactionData);
        }
        String dataFormat = getDataformat(request);
        Request transactionData = null;
        if ("json".equalsIgnoreCase(dataFormat)) {
            transactionData = dsCall.getJSParser().toJavaObject(rawTransactionData, Request.class);
        } else if ("xml".equalsIgnoreCase(dataFormat)) {
            rawTransactionData = rawTransactionData.replace("xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"",
                "xmlns=\"http://www.solmix.org/xmlns/requestdata/v1.0.1\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"");
            transactionData = dsCall.getXMLParser().unmarshalReq(new StringReader(rawTransactionData));
        }
        if (transactionData == null)
            throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_REQ_TRANSACTION_IS_NULL,
                "Invalid request transaction : transaction data is null");
        String _jscallback = transactionData.getJscallback();
        if (DataUtil.isNotNullAndEmpty(_jscallback))
            dsCall.setAttribute(JS_CALLBACK, _jscallback);
        List<Roperation> operations = transactionData.getOperations().getElem();
        if (LOG.isDebugEnabled())
            LOG.debug((new StringBuilder()).append("Processing ").append(operations.size()).append(" requests.").toString());
        int requestNum = 0;
        if (operations != null) {
            boolean freeOnExcute = operations.size() <= 1;
            for (Roperation operation : operations) {
                requestNum++;
                if (operation.getAppID() != null) {
                    DSRequest dsRequest = new DSRequestImpl(operation, context);
                    dsRequest.setFreeOnExecute(freeOnExcute);
                    dsRequest.getContext().setIsClientRequest(true);
                    dsRequest.setDSCall(dsCall);
                    // for debug log
                    if (LOG.isDebugEnabled()) {
                        String __requestjs = ISCJavaScript.get().printRequestData(operation);
                        if (LOG.isDebugEnabled())
                            LOG.debug(new StringBuilder().append("Request #").append(requestNum).append(" (DSRequest) payload: ").append(__requestjs).toString());
                    }
                    dsCall.addRequest(dsRequest);
                }
            }
        }// End (Toperation operation : operations) cycle
    }

    @Override
    public ReturnType postInspect(DSCall dsCall, WebContext context) throws SlxException {
        HttpServletRequest request = context.getRequest();
        boolean isXMLHttp = isXmlHttp(request);
        String contentType = isXMLHttp ? "text/plain" : "text/html";
        if (charset != null && !charset.trim().equals(""))
            contentType = (new StringBuilder()).append(contentType).append("; charset=").append(charset).toString();
        context.setContentType(contentType);
        if (LOG.isTraceEnabled())
            LOG.trace("Content type for ISC transaction :{}" , contentType);
        List<Object> orderedResponseList = new ArrayList<Object>();
        for(DSRequest req:dsCall.getRequests()){
            DSResponse res= dsCall.getResponse(req);
            orderedResponseList.add(res.getClientResponse());
        }
        Writer _out;
        try {
            if(showClientOutPut)
                _out=new StringWriter();
            else
                _out = context.getOut();
            String dataFormat = getDataformat(request);
            if("json".equalsIgnoreCase(dataFormat)){
            //Data must no cached.
            context.setNoCacheHeaders();
            _out.write(structuredRPCStart);
            dsCall.getJSParser().toJSON(_out, orderedResponseList);
            _out.write(structuredRPCEnd);
            }else if("xml".equalsIgnoreCase(dataFormat)){
                //XXX
            }else{
                try {
                    _out.write("<HTML>\n");
                    writeDocumentDomain(_out,request);
                    if (customHTML != null)
                        _out.write(customHTML);
                    _out.write((new StringBuilder()).append("<BODY ONLOAD='var results = document.formResults.results.value;").append(
                        dsCall.getAttribute(JS_CALLBACK)).append("'>").toString());
                    _out.write("<BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR>");
                    _out.write("<FORM name='formResults'><TEXTAREA readonly name='results'>\n");
                        _out.write(structuredRPCStart);
                    dsCall.getJSParser().toJSON(_out, orderedResponseList);
                    // jsTrans.toJS(data, out);
                    _out.write(structuredRPCEnd);
                    _out.write("</TEXTAREA>");
                    _out.write("</FORM>\n");
                    _out.write("</BODY></HTML>");
                } catch (Exception e) {
                    throw new SlxException(Tmodule.DATASOURCE, Texception.IO_EXCEPTION, e);
                }
            }
            _out.flush();
            if(showClientOutPut){
                String output = _out.toString();
                int outputSize = output.length();
                if(LOG.isTraceEnabled()){
                   LOG.trace((new StringBuilder()).append("Uncompressed result size: ").append(outputSize).append(" bytes").toString());
                   LOG.trace("output String :\n" + _out.toString());
                }
                context.getOut().write(output);
                context.getOut().flush();
            }
                
        } catch (IOException e) {
            throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
        }
        return ReturnType.CANCELLED;
    }
   
    public static boolean isXmlHttp(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return false;
        else
            return queryString.indexOf("isc_xhr=1") != -1 || queryString.indexOf("xmlHttp=true") != -1;
    }

    public static String getDataformat(HttpServletRequest request) {
        String queryString = request.getParameter("isc_dataFormat");
        if (queryString == null)
            return "json";
        else
            return queryString;
    }

    /**
     * Write document form client as a JavaScript
     * 
     * @param out
     * @throws IOException
     */
    private void writeDocumentDomain(final Writer out, HttpServletRequest request) throws IOException {
        String documentDomain = getDocumentDomain(request);
        if (documentDomain != null)
            out.write((new StringBuilder()).append("<SCRIPT>document.domain = '").append(documentDomain).append("';</SCRIPT>\n").toString());
    }

    /**
     * Return documentDomain from {@link javax.servlet.http.HttpServletRequest request}
     * 
     * <pre>
     * queryString name：<b> isc_dd</b> or <b>docDomain</b>
     * </pre>
     * 
     * @param request
     * @return
     */
    private static String getDocumentDomain(HttpServletRequest request) {
        String dd = null;
        try {
            Map<String, String> queryParams = ServletTools.parseQueryString(request.getQueryString());
            dd = queryParams.get("isc_dd");
            if (dd == null)
                dd = queryParams.get("docDomain");
        } catch (Exception e) {
            LOG.warn((new StringBuilder()).append("Error decoding query string: ").append(request.getQueryString()).append(
                " - can't determine docDomain").toString());
        }
        return dd;
    }

    public static boolean isRPC(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return false;
        else
            return queryString.indexOf("isc_rpc=1") != -1 || queryString.indexOf("is_isc_rpc=true") != -1;
    }

    protected void versionCheck(HttpServletRequest request) {
        String _clientVersion = getClientVersion(request);
        if (iscServerVersion == null || iscServerVersion.trim().equals(""))
            iscServerVersion = DatasourceCM.SLX_VERSION_NUMBER;
        String _machClient = DatasourceCM.CLIENT_VERSION_NUMBER;
        if (_clientVersion == null || !_machClient.equals(_clientVersion)) {
            if (_clientVersion == null)
                _clientVersion = "SC_SNAPSHOT-2011-12-05";
            LOG.warn((new StringBuilder()).append("client/server version mismatch. The server version :").append(iscServerVersion).append(
                " would matched Client  version: ").append(_machClient).append(", but the client version is: ").append(_clientVersion).append(
                " - mixing mismatch client/server versions is generally not supported.").append(
                "  If you've installed a more recent client version, try clearing").append(" the browser cache and reloading the page.").toString());
        }// check version pair
    }

    private static String getClientVersion(HttpServletRequest request) {
        String version = null;
        try {
            Map<String, String> queryParams = ServletTools.parseQueryString(request.getQueryString());
            version = queryParams.get("isc_v");
            if (version == null) {
                version = queryParams.get("isc_clientVersion");
            }
        } catch (Exception e) {
            LOG.warn((new StringBuilder()).append("Error decoding query string: ").append(request.getQueryString()).append(
                " - can't determine client version").toString());
        }
        return version;
    }
}
