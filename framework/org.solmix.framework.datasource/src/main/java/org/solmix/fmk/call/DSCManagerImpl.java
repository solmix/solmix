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

package org.solmix.fmk.call;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCManager;
import org.solmix.api.call.DSCManagerCompletionCallback;
import org.solmix.api.call.HasDSCHandler;
import org.solmix.api.call.HttpServletRequestParser;
import org.solmix.api.call.RPCRequest;
import org.solmix.api.call.RPCResponse;
import org.solmix.api.call.RequestType;
import org.solmix.api.call.ResponseType;
import org.solmix.api.context.WebContext;
import org.solmix.api.data.DSRequestData;
import org.solmix.api.data.RPCManagerData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.export.IExport;
import org.solmix.api.jaxb.EexportAs;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.TfieldNameValue;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.api.types.TransactionPolicy;
import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.IOUtil;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.datasource.DSRequestImpl;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.export.ExportManagerImpl;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.fmk.js.ISCJavaScript;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.serialize.XMLParserFactoryImpl;
import org.solmix.fmk.util.DataTools;
import org.solmix.fmk.util.ServletTools;
import org.solmix.fmk.velocity.ServletRequestAttributeMapFacade;
import org.solmix.fmk.velocity.SessionAttributeMapFacade;
import org.solmix.fmk.velocity.Velocity;

/**
 * complex relationship at this class,ant simple configuration at data class {@link org.solmix.api.data.RPCManagerData
 * RPCManagerData}.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110040 2011-1-1 solmix-ds
 */
public class DSCManagerImpl implements DSCManager
{

    private static Logger log = LoggerFactory.getLogger(DSCManagerImpl.class.getName());

    private static final String structuredRPCStart = "//isc_RPCResponseStart-->";

    private static final String structuredRPCEnd = "//isc_RPCResponseEnd";

    private List<DataSource> dsToFree;

    private List<RequestType> requests;

    private final HashSet<DSCManagerCompletionCallback> callbacks;

    private  RPCManagerData data;

    private WebContext context;

    private final JSParser jsParser;

    private  Map<RequestType, ResponseType> responseMap;

    private Long transactionNum;

    private TransactionPolicy transactionPolicy;

    private Boolean requestProcessingStarted;
    private boolean transactionStarted;

    public DSCManagerImpl() throws SlxException
    {
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        jsParser = jsFactory.get();
        callbacks = new HashSet<DSCManagerCompletionCallback>();
        data = null;
        setContext(new RPCManagerData());
        setTransactionPolicy(TransactionPolicy.ANY_CHANGE);
        setRequestProcessingStarted(false);
    }

    public DSCManagerImpl(WebContext context) throws SlxException
    {
        this(context, null);
    }

    public DSCManagerImpl(WebContext context, HttpServletRequestParser parser) throws SlxException
    {
        this.context = context;
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        jsParser = jsFactory.get();
        callbacks = new HashSet<DSCManagerCompletionCallback>();
        responseMap = new HashMap<RequestType, ResponseType>();
        data = null;
        setContext(new RPCManagerData());
        data.setCharset(DatasourceCM.getProperties().getString("rpc.defaultCharset", "UTF-8"));
        data.setCloseConnection(false);
        data.setOmitNullMapValuesInResponse(DatasourceCM.getProperties().getBoolean("rpc.omitNullMapValuesInResponse", false));
        data.setPrettyPrintResponse(DatasourceCM.getProperties().getBoolean("rpc.prettyPrintResponse", false));
        data.setCustomHTML(DatasourceCM.getProperties().getString("rpc.customHTML", null));
        data.setServerVersion(DatasourceCM.getProperties().getString("rpc.serverVersionNumber"));
        setTransactionPolicy(TransactionPolicy.ANY_CHANGE);
        setRequestProcessingStarted(false);
        HttpServletRequest request = context == null ? null : context.getRequest();
        if (request != null) {
            if (parser != null)
                parser.parseRequest(this, context);
            else
                parseRequest();
            data.addToTemplateContext("servletRequest", new ServletRequestAttributeMapFacade(request));
            data.addToTemplateContext("session", new SessionAttributeMapFacade(request.getSession()));
        } else if (request == null && context.getRequest() != null) {
            throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_UPLOAD_FILE, "DSCManager constructor was passed a null HttpServletRequest");
        }

    }

    /**
     * @return the jsParser
     */
    @Override
    public JSParser getJsParser() {
        return jsParser;
    }

    /**
     * Add requst to a request list,principally for mulitply request
     * 
     * @param req
     */
    @Override
    public void addRequest(DSRequest req) {
        if (requests == null)
            requests = new ArrayList<RequestType>();
        requests.add(req);
    }

    /**
     * Add requst to a request list,principally for mulitply request
     * 
     * @param req
     */
    @Override
    public void addRequest(RPCRequest req) {
        if (requests == null)
            requests = new ArrayList<RequestType>();
        requests.add(req);
    }

    public DataSource getDataSource(String dsName) throws Exception {
        DataSource ds = DefaultDataSourceManager.getDataSource(dsName);
        if (ds != null)
            dsToFree.add(ds);
        return ds;
    }

    /**
     * check the client and server version pairing well
     */
    protected void versionCheck() {
        String _clientVersion = getClientVersion(context.getRequest());
        String _serverVersion = data.getServerVersion();
        if (_serverVersion == null || _serverVersion.trim().equals(""))
            _serverVersion = DatasourceCM.SLX_VERSION_NUMBER;
        String _machClient = DatasourceCM.CLIENT_VERSION_NUMBER;
        if (_clientVersion == null || !_machClient.equals(_clientVersion)) {
            if (_clientVersion == null)
                _clientVersion = "SC_SNAPSHOT-2011-12-05";
            log.warn((new StringBuilder()).append("client/server version mismatch. The server version :").append(_serverVersion).append(
                " would matched Client  version: ").append(_machClient).append(", but the client version is: ").append(_clientVersion).append(
                " - mixing mismatch client/server versions is generally not supported.").append(
                "  If you've installed a more recent client version, try clearing").append(" the browser cache and reloading the page.").toString());
        }// check version pair
    }

    /**
    * 
    */
    private void parseRequest() throws SlxException {
        if (!isRPC(context.getRequest()))
            throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_NO_RPC_REQUEST, "The request is not Rpc request!");
        versionCheck();
        Map<String, String> queryParamsMap = ServletTools.parseQueryString(context.getRequest().getQueryString());
        String rawTransactionData = queryParamsMap.get("_transaction");
        if (rawTransactionData == null)
            rawTransactionData = context.getRequest().getParameter("_transaction");
        if (rawTransactionData == null || rawTransactionData.equals("")) {
            Writer out = context.getOut();
            ServletTools.sendHTMLStart(out);
            log.warn("Detected zero-length IDA transaction, asking client to retry.");
            log.warn("Outputting extra debug information:");
            log.warn(context.getCookiesAsString());
            log.warn(context.getHeadersAsString());
            try {
                log.warn(context.getParamsAsString());
            } catch (Exception e) {
                log.warn("Couldn't log params", e);
            }
            try {
                writeDocumentDomain(out);
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
        if (log.isDebugEnabled()) {
            log.debug(">>>>>>>>request data:\n" + rawTransactionData);
            log.debug(">>>>>>>>>>END");
        }

        if (rawTransactionData.trim().startsWith("{")) {
            // TODO Request data is JSON format
        } else {
            // Request data is XML(XML-OVER-HTTP) format
            rawTransactionData = rawTransactionData.replace("xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"",
                "xmlns=\"http://www.solmix.org/xmlns/requestdata/v1.0.1\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"");
            Request transactionData = null;
            XMLParserFactory xmlFactory = XMLParserFactoryImpl.getInstance();
            XMLParser xmlParser = xmlFactory.get();
            transactionData = xmlParser.unmarshalReq(new StringReader(rawTransactionData));
            if (transactionData == null)
                throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_REQ_TRANSACTION_IS_NULL,
                    "Invalid request transaction : transaction data is null");
            /********************************************
             * Initialize RPC transaction
             ********************************************/
            Boolean nullValuesHandling = transactionData.isOmitNullMapValuesInResponse();
            if (nullValuesHandling != null)
                data.setOmitNullMapValuesInResponse(nullValuesHandling);
            this.setTransactionNum(transactionData.getTransactionNum());
            String _jscallback = transactionData.getJscallback();
            if (DataUtil.isNotNullAndEmpty(_jscallback))
                data.setJsCallback(_jscallback);
            List<Roperation> operations = transactionData.getOperations().getElem();
            if (log.isDebugEnabled())
                log.debug((new StringBuilder()).append("Processing ").append(operations.size()).append(" requests.").toString());
            int requestNum = 0;
            if (operations != null) {
                boolean freeOnExcute = operations.size() <= 1;
                for (Roperation operation : operations) {
                    requestNum++;
                    if (operation.getAppID() != null) {
                        DSRequest dsRequest = new DSRequestImpl(operation, context);
                        dsRequest.setFreeOnExecute(freeOnExcute);
                        dsRequest.getContext().setIsClientRequest(true);
                        dsRequest.setRPC(this);
                        // authentication
                        Boolean auth = (Boolean) context.getRequest().getAttribute("authenticationEnabled");
                        if (Boolean.TRUE.equals(auth)) {
                            //XXX
//                            String user = Authentication.getUsername(context);
//                            if (user != null)
//                                dsRequest.getContext().setUserId(user);
                        }
                        // for debug log
                        if (log.isDebugEnabled()) {
                            String __requestjs = ISCJavaScript.get().printRequestData(operation);
                            if (log.isDebugEnabled())
                                log.debug(new StringBuilder().append("Request #").append(requestNum).append(" (DSRequest) payload: ").append(
                                    __requestjs).toString());
                        }
                        addRequest(dsRequest);
                    }
                }
            }// End (Toperation operation : operations) cycle
        }// End process XMl type request data.

    }

    /**
     * Write document form client as a JavaScript
     * 
     * @param out
     * @throws IOException
     */
    private void writeDocumentDomain(final Writer out) throws IOException {
        String documentDomain = getDocumentDomain(context.getRequest());
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
            log.warn((new StringBuilder()).append("Error decoding query string: ").append(request.getQueryString()).append(
                " - can't determine docDomain").toString());
        }
        return dd;
    }

    /**
     * Return the current client version .
     * 
     * @param request
     * @return
     */
    private static String getClientVersion(HttpServletRequest request) {
        String version = null;
        try {
            Map<String, String> queryParams = ServletTools.parseQueryString(request.getQueryString());
            version = queryParams.get("isc_v");
            if (version == null) {
                version = queryParams.get("isc_clientVersion");
            }
        } catch (Exception e) {
            log.warn((new StringBuilder()).append("Error decoding query string: ").append(request.getQueryString()).append(
                " - can't determine client version").toString());
        }
        return version;
    }

    /**
     * When true, indicates that this is a ISC RPC Request.
     * 
     * <pre>
     * <code> if contains isc_rpc=1 or is_isc_rpc=true return true</code>
     * </pre>
     * 
     * @param request
     * @return
     */
    public static boolean isRPC(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return false;
        else
            return queryString.indexOf("isc_rpc=1") != -1 || queryString.indexOf("is_isc_rpc=true") != -1;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(org.solmix.api.datasource.DSRequest,
     *      org.solmix.api.datasource.DSResponse)
     */
    @Override
    public void send(DSRequest dsRequest, DSResponse dsResponse) throws SlxException {
        if (data.getIsDownload() == null)
            data.setIsDownload(dsRequest.getContext().getIsDownload());
        if (data.getIsExport() == null)
            data.setIsExport(dsRequest.getContext().getIsExport());
        // if (data.getIsExport() == null) {
        // ToperationBinding __op = DataTools.getOperationBindingFromDSByRequest(dsRequest.getDataSource(), dsRequest);
        // data.setIsExport(__op == null ? null : __op.isExportResults());
        // }
        if(this.transactionStarted){
            throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_MUST_END_BEFORE_SEND,"Transaction must end before send a DSRequest");
        }
        responseMap.put(dsRequest, dsResponse);
        if (responseMap.size() == requestCount()) {
            completeResponse();
        }
    }

    /**
     * Called when request complete.
     * <p>
     * 1.free resource <br>
     * 2.download file stream process.<br>
     * 3.export if not set {@link com.DSResponseImpl.web.core.datasource.DSResponse#setExportFields() setExportFields()}
     * use {@link com.DataSource1.web.core.datasource.DataSource#getFieldNames() getFieldNames()}
     * 
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void completeResponse() throws SlxException {
        try {
            for (Object request : requests) {
                // process DSRequest
                if (request instanceof DSRequest) {
                    DSResponse response = (DSResponse) responseMap.get(request);
                    if (response instanceof HasDSCHandler) {
                        ((HasDSCHandler) response).handler(this, (DSRequest)request, response);
                        // Only can handle one DsRequest.
                        return;
                    }
                }
            }
            // process file download
            if (data.getIsDownload()) {
                String mimeType = null;
                try {
                    mimeType = ServletTools.mimeTypeForContext(context);
                } catch (Exception e) {
                    throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_MIME_TYPE_ERROR, e);
                }
                if (mimeType != null)
                    context.setContentType(mimeType);
                DSRequest dsRequest = (DSRequest) getRequests().get(0);
                DSResponse dsResponse = (DSResponse) responseMap.get(dsRequest);
                Map data = (Map) dsResponse.getContext().getData();
                String fileName = dsRequest.getContext().getDownloadFileName();
                String fieldName = dsRequest.getContext().getDownloadFieldName();
                long contentLength = Long.valueOf(data.get((new StringBuilder()).append(fieldName).append("_filesize").toString()).toString()).longValue();
                InputStream is = (InputStream) data.get(fieldName);
                String fileNameEncoding = encodeParameter("fileName", fileName);
                if (dsRequest.getContext().getOperationType() == Eoperation.DOWNLOAD_FILE)
                    context.getResponse().addHeader("content-disposition",
                        (new StringBuilder()).append("attachment; ").append(fileNameEncoding).toString());
                else
                    context.getResponse().addHeader("content-disposition",
                        (new StringBuilder()).append("inline; ").append(fileNameEncoding).toString());
                context.getResponse().setContentLength((int) contentLength);
                try {
                    OutputStream os = context.getResponse().getOutputStream();
                    IOUtil.copyStreams(is, os);
                    os.flush();
                } catch (IOException e) {
                    throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
                }
                return;
            }// end isdownlaod.

            // export process
            if (data.getIsExport()) {
                DSRequest dsRequest = (DSRequest) getRequests().get(0);
                DSResponse dsResponse = (DSResponse) responseMap.get(dsRequest);
                // put request export relative info to response.
                processExport(dsRequest, dsResponse);
                // used filter from datasource.if not ,can used dsResponse.getContext().getDataList(Map.class);
                List<Map<Object, Object>> data = dsResponse.getRecords();

                Map<String, String> fieldMap = new HashMap<String, String>();
                DataSource ds = dsResponse.getDataSource() == null ? dsRequest.getDataSource() : dsResponse.getDataSource();
                List<String> fieldNames = dsResponse.getContext().getExportFields();
                List<String> finalFields = new ArrayList<String>();
                // if no defined export fields used datasource's fields.
                if (fieldNames == null || fieldNames.isEmpty())
                    fieldNames = ds.getContext().getFieldNames();
                EexportAs exportAs = EexportAs.fromValue(dsResponse.getContext().getExportAs());
                String separatorChar = dsResponse.getContext().getExportTitleSeparatorChar();
                List<String> efields = dsResponse.getContext().getExportFields();
                // List<String> efieldName = dsResponse.getContext().gete
                if (efields == null) {
                    ToperationBinding __op = DataTools.getOperationBindingFromDSByRequest(ds, dsRequest);
                    if (__op != null) {
                        String fields = __op.getExport() != null ? __op.getExport().getExportFields() : null;
                        efields = DataUtil.isNotNullAndEmpty(fields) ? DataUtil.simpleSplit(fields, ",") : null;
                    }
                }
                // loop fieldName.
                for (int i = 0; i < fieldNames.size(); i++) {
                    String fieldName = fieldNames.get(i);
                    String fieldTitle = null;
                    Tfield field = ds.getContext().getField(fieldName);
                    if (field != null && !field.isHidden() && (field.isCanExport() == null || field.isCanExport())) {
                        fieldTitle = field.getTitle();
                        if (fieldTitle == null) {
                            fieldTitle = fieldName;
                        }
                        if (exportAs == EexportAs.XML) {
                            if (separatorChar == null)
                                separatorChar = "";
                            fieldTitle = fieldTitle.replaceAll("[$&<>() ]", separatorChar);
                        }
                        fieldMap.put(fieldName, fieldTitle);
                        finalFields.add(fieldName);
                    }
                }
                if (efields == null) {
                    efields = finalFields;
                }
                int lineBreakStyleId = 4;
                if (dsResponse.getContext().getLineBreakStyle() != null) {
                    String lineBreakStyle = dsResponse.getContext().getLineBreakStyle().toLowerCase();
                    lineBreakStyleId = lineBreakStyle.equals("mac") ? 1 : ((int) (lineBreakStyle.equals("unix") ? 2
                        : ((int) (lineBreakStyle.equals("dos") ? 3 : 4))));
                }

                String delimiter = dsResponse.getContext().getExportDelimiter();
                if (delimiter == null || delimiter == "")
                    delimiter = ",";
                // get export provider.
                Map<String, Object> conf = new HashMap<String, Object>();
                conf.put(IExport.LINE_BREAK_STYLE, lineBreakStyleId);
                conf.put(IExport.EXPORT_DELIMITER, delimiter);
                conf.put(IExport.ORDER, efields);
                String exportHeader = dsResponse.getContext().getExportHeader();
                if (exportHeader != null) {
                    conf.put(IExport.EXPORT_HEADER_STRING, exportHeader);
                }
                String exportFooter = dsResponse.getContext().getExportFooter();
                if (exportFooter != null) {
                    conf.put(IExport.EXPORT_FOOTER_STRING, exportFooter);
                }
                IExport export = ExportManagerImpl.get(exportAs, conf);
                try {
                    ServletOutputStream os = context.getResponse().getOutputStream();
                    BufferedOutputStream bufferedOS = new BufferedOutputStream(os);
                    String fileNameEncoding = encodeParameter("filename", dsResponse.getContext().getExportFilename());
                    if (dsResponse.getContext().getExportDisplay().equals("download")) {
                        context.getResponse().addHeader("content-disposition", "attachment;" + fileNameEncoding);
                        String contentType = null;
                        switch (exportAs) {
                            case XML: // '\003'
                                contentType = "unknown";
                                break;

                            case JSON: // '\002'
                                contentType = "application/json";
                                break;

                            case XLS: // '\004'
                                contentType = "application/vnd.ms-excel";
                                break;

                            case OOXML: // '\005'
                                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                                break;

                            case CSV: // '\001'
                                contentType = "text/comma-separated-values";
                                break;

                            default:
                                contentType = "text/csv";
                                break;
                        }
                        context.setContentType(contentType);
                    } else {
                        context.getResponse().addHeader("content-disposition",
                            (new StringBuilder()).append("inline; ").append(fileNameEncoding).toString());
                    }
                    export.exportResultSet(data, fieldMap, bufferedOS);
                    // String streamData = writer.toString(); int contentLength = streamData.length();
                    // context.getResponse().setContentLength(contentLength);
                    bufferedOS.flush();
                    os.flush();
                } catch (IOException e) {
                    throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
                }
                return;
            }// end Export process

            boolean isXMLHttp = context.getResponse() != null && isXmlHttp(context.getRequest());
            String restDataForm = getDataformat(context.getRequest());
            boolean isRest = DataUtil.booleanValue(data.isRest());

            String contentType = isXMLHttp ? "text/plain" : "text/html";
            if (data.getCharset() != null && !data.getCharset().trim().equals(""))
                contentType = (new StringBuilder()).append(contentType).append("; charset=").append(data.getCharset()).toString();
            context.setContentType(contentType);
            if (log.isDebugEnabled())
                log.debug((new StringBuilder()).append("Content type for RPC transaction: ").append(contentType).toString());
            Writer _out;
            if (DatasourceCM.getProperties().getBoolean(DatasourceCM.P_SHOW_CLIENT_OUTPUT, Boolean.FALSE))
                _out = new StringWriter();
            else
                _out = context.getOut();
            boolean _failure = oneFailureAllFailure();
            try {
                if (_failure)
                    onFailure();
                else
                    onSuccess();
            } catch (Exception e) {
                log.warn(DataUtil.getStackTrace(e));
            }
            //
            List<Object> orderedResponseList = new ArrayList<Object>();
            for (Object request : requests) {
                Object resp = responseMap.get(request);
                if (resp == null)
                    throw new SlxException(Tmodule.DATASOURCE, Texception.DS_NO_RESPONSE_DATA, (new StringBuilder()).append(
                        "No response for request: ").append(request.toString()).toString());
                if (resp instanceof RPCResponse) {
                    Map<String, Object> payload = new HashMap<String, Object>();
                    payload.put("data", ((RPCResponse) resp).getData());
                    payload.put("status", new Integer(((RPCResponse) resp).getStatus()));
                    orderedResponseList.add(payload);
                    if (((RPCResponse) resp).getStatus() < 0)
                        _failure = true;
                } else if (resp instanceof DSResponse) {
                    DSResponse dsResponse = (DSResponse) resp;
                    if (dsResponse.getContext().isRequestConnectionClose())
                        data.setCloseConnection(true);
                    if (dsResponse.getContext().getStatus().value() < 0)
                        _failure = true;
                    Object jsResponse = dsResponse.getJSResponse();
                    // DSRequest dsRequest = (DSRequest) request;

                    orderedResponseList.add(jsResponse);
                }
            }// END LOOP REQUESTS.
            try {
                if (!isXMLHttp || "json".equals(restDataForm)) {
                    context.setNoCacheHeaders();

                    if (!isRest)
                        _out.write(structuredRPCStart);

                    if (isRest) {
                        Map restContainer = new LinkedMap();
                        restContainer.put("response", orderedResponseList.get(0));
                        jsParser.toJSON(_out, restContainer);
                    } else {
                        jsParser.toJSON(_out, orderedResponseList);
                    }
                    if (!isRest)
                        _out.write(structuredRPCEnd);
                } else if (isXMLHttp || "xml".equals(restDataForm)) {

                } else {
                    iframeWrite(_out, true, orderedResponseList);
                }
                _out.flush();
                if (_out instanceof StringWriter) {
                    String output = _out.toString();
                    int outputSize = output.length();
                    if (log.isDebugEnabled())
                        log.debug((new StringBuilder()).append("Uncompressed result size: ").append(outputSize).append(" bytes").toString());
                    context.getOut().write(output);
                    context.getOut().flush();
                    if (DatasourceCM.getProperties().getBoolean(DatasourceCM.P_DEVELOPMENT, Boolean.FALSE)) {
                        log.trace("output String :\n" + _out.toString());
                    }
                }
            } catch (IOException e) {
                throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
            }
        } finally {
            /**
             * loop requests to free resource.because some request support transaction,so it's datasource is not free no
             * execute ,but now a RPC session is complete,the DataSource borrow from PoolManager must return .
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
    private boolean oneFailureAllFailure(){
        boolean _failure=false;
        for (Object request : requests) {
            Object resp = responseMap.get(request);
            if ((resp instanceof RPCResponse) && ((RPCResponse) resp).getStatus() < 0) {
                _failure = true;
                break;
            }
            if ((resp instanceof DSResponse) && ((DSResponse) resp).getContext().getStatus().value() < 0) {
                _failure = true;
                break;
            }
        }// end loop requests
        return _failure;
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
        if (requestProcessingStarted == null ? false : requestProcessingStarted.booleanValue())
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_REQUEST_ALREADY_STARTED, "dsRequest already started.");
        this.transactionPolicy = transactionPolicy;
    }

    /**
     * @return the requestProcessingStarted
     */
    @Override
    public Boolean getRequestProcessingStarted() {
        return requestProcessingStarted;
    }

    /**
     * @param requestProcessingStarted the requestProcessingStarted to set
     */
    @Override
    public void setRequestProcessingStarted(Boolean requestProcessingStarted) {
        this.requestProcessingStarted = requestProcessingStarted;
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

    private void processExport(DSRequest dsreq, DSResponse dsresp) {
        dsresp.getContext().setIsExport(true);
        if (dsresp.getContext().getExportAs() == null || dsresp.getContext().getExportAs().equals(""))
            dsresp.getContext().setExportAs(dsreq.getContext().getExportAs());
        if (dsresp.getContext().getExportDelimiter() == null || dsresp.getContext().getExportDelimiter().equals(""))
            dsresp.getContext().setExportDelimiter(dsreq.getContext().getExportDelimiter());
        if (dsresp.getContext().getExportDisplay() == null || dsresp.getContext().getExportDisplay().equals(""))
            dsresp.getContext().setExportDisplay(dsreq.getContext().getExportDisplay());
        if (dsresp.getContext().getExportFilename() == null || dsresp.getContext().getExportFilename().equals(""))
            dsresp.getContext().setExportFilename(dsreq.getContext().getExportFilename());
        if (dsresp.getContext().getExportFields() == null || ("".equals(dsresp.getContext().getExportFields())))
            dsresp.getContext().setExportFields(dsreq.getContext().getExportFields());
        if (dsresp.getContext().getLineBreakStyle() == null || dsresp.getContext().getLineBreakStyle().equals(""))
            dsresp.getContext().setLineBreakStyle(dsreq.getContext().getLineBreakStyle());
        if (dsresp.getContext().getExportHeader() == null || dsresp.getContext().getExportHeader().equals(""))
            dsresp.getContext().setExportHeader(dsreq.getContext().getExportHeader());
        if (dsresp.getContext().getExportFooter() == null || dsresp.getContext().getExportFooter().equals(""))
            dsresp.getContext().setExportFooter(dsreq.getContext().getExportFooter());
        if (dsresp.getContext().getExportTitleSeparatorChar() == null || dsresp.getContext().getExportTitleSeparatorChar().equals(""))
            dsresp.getContext().setExportTitleSeparatorChar(dsreq.getContext().getExportTitleSeparatorChar());
    }

    protected void onFailure() throws Exception {
        boolean _transactionFailure = false;
        if (requests == null)
            return;
        for (Object requestObj : requests) {
            if (requestObj instanceof DSRequest) {
                DSRequest req = (DSRequest) requestObj;
                DSResponse resp = getResponse(req);
                _transactionFailure= isXAFailure(req,resp);
            }
        }
        if (_transactionFailure) {
            for (Object obj : requests) {
                if (obj instanceof DSRequest) {
                    DSRequest req = (DSRequest) obj;
                    if (req.isJoinTransaction()) {
                        DSResponse resp = getResponse(req);
                        if (resp != null && resp.getContext().getStatus() == DSResponse.Status.STATUS_SUCCESS)
                            resp.getContext().setStatus(DSResponse.Status.STATUS_TRANSACTION_FAILED);
                    }
                }
            }
        }
        if (callbacks != null)
            for (DSCManagerCompletionCallback callback : callbacks) {
                callback.onFailure(this, _transactionFailure);
            }
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    private boolean requestQueueIncludesPriorUpdate(DSRequest req) throws SlxException {
        return _requestQueueIncludesPriorUpdate(req);
    }

    protected void onSuccess() throws Exception {
        for (DSCManagerCompletionCallback callback : callbacks) {
            callback.onSuccess(this);
        }
    }

    private boolean _requestQueueIncludesPriorUpdate(DSRequest req) throws SlxException {
        if (requests == null)
            return false;
        for (Object request : requests) {
            if (request.equals(request)) {
                return false;
            }
            if (request instanceof DSRequest) {
                DSRequest dsrequest = (DSRequest) request;
                if (dsrequest.isModificationRequest(dsrequest))
                    return true;
            }
        }
        return false;
    }

    public String encodeParameter(String name, String value) {
        Pattern tspecials = Pattern.compile("[<()@,;:/?={} >\"\\[\\]\\t\\\\]");
        Matcher matcher = tspecials.matcher(value);
        if (value.length() <= 78)
            if (!matcher.find())
                return (new StringBuilder()).append(name).append("=").append(value).toString();
            else
                return (new StringBuilder()).append(name).append("=").append("\"").append(value).append("\"").toString();
        int counter = 0;
        String returnVal = "";
        for (; value.length() > 78; value = value.substring(78)) {
            String work = value.substring(0, 78);
            matcher.reset(work);
            if (matcher.find())
                work = (new StringBuilder()).append("\"").append(work).append("\"").toString();
            if (counter > 0)
                returnVal = (new StringBuilder()).append(returnVal).append("; ").toString();
            returnVal = (new StringBuilder()).append(returnVal).append(name).append("*").append(counter).append("=").append(work).toString();
            counter++;
        }

        matcher.reset(value);
        if (matcher.find())
            value = (new StringBuilder()).append("\"").append(value).append("\"").toString();
        if (counter > 0)
            returnVal = (new StringBuilder()).append(returnVal).append("; ").toString();
        returnVal = (new StringBuilder()).append(returnVal).append(name).append("*").append(counter).append("=").append(value).toString();
        return returnVal;
    }

    private void iframeWrite(Writer out, boolean structured, List data) throws SlxException {
        try {
            out.write("<HTML>\n");
            writeDocumentDomain(out);
            if (getContext().getCustomHTML() != null)
                out.write(getContext().getCustomHTML());
            out.write((new StringBuilder()).append("<BODY ONLOAD='var results = document.formResults.results.value;").append(
                this.data.getJsCallback()).append("'>").toString());
            out.write("<BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR><BR>");
            out.write("<FORM name='formResults'><TEXTAREA readonly name='results'>\n");
            if (structured)
                out.write(structuredRPCStart);
            jsParser.toJSON(out, data);
            // jsTrans.toJS(data, out);
            if (structured)
                out.write(structuredRPCEnd);
            out.write("</TEXTAREA>");
            out.write("</FORM>\n");
            out.write("</BODY></HTML>");
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.IO_EXCEPTION, e);
        }
    }

    /**
     * If the QueryString contains "isc_xhr=1" or "xmlHttp=true"
     * 
     * @param request
     * @return
     */
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
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(org.solmix.api.datasource.DSRequest, java.lang.Object)
     */
    @Override
    public void send(DSRequest dsRequest, Object data) throws SlxException {
        DSResponse dsResponse = new DSResponseImpl((dsRequest != null ? dsRequest.getDataSource() : (DataSource) null),dsRequest);
        dsResponse.getContext().setData(data);
        dsResponse.getContext().setStatus(Status.STATUS_SUCCESS);
        send(dsRequest, dsResponse);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(java.lang.Object)
     */
    @Override
    public void send(Object data) throws SlxException {
        send(new RPCResponseImpl(data));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(org.solmix.api.call.RPCRequest, java.lang.Object)
     */
    @Override
    public void send(RPCRequest rpcRequest, Object data) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(org.solmix.api.call.RPCRequest, org.solmix.api.call.RPCResponse)
     */
    @Override
    public void send(RPCRequest rpcRequest, RPCResponse rpcResponse) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#send(org.solmix.api.call.RPCResponse)
     */
    @Override
    public void send(RPCResponse rpcResponse) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#sendFailure(java.lang.Object, java.lang.String)
     */
    @Override
    public void sendFailure(Object request, String error) throws SlxException {
        if (request instanceof DSRequest) {
            DSResponse dsResponse = new DSResponseImpl(((DSRequest) request).getDataSource(),request);
            dsResponse.getContext().setData(error);
            send((DSRequest) request, dsResponse);
        } else {
            RPCResponse rpcResponse = new RPCResponseImpl(error);
            rpcResponse.setStatus(RPCResponseImpl.STATUS_FAILURE);
            send((RPCRequest) request, rpcResponse);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#sendFailure(java.lang.Object, java.lang.Throwable)
     */
    @Override
    public void sendFailure(Object request, Throwable t) throws SlxException {
        sendFailure(request, Reflection.getRealTargetException(t).getMessage());

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#sendSuccess(org.solmix.api.call.RPCRequest)
     */
    @Override
    public void sendSuccess(RPCRequest rpcRequest) throws SlxException {
        RPCResponse rpcResponse = new RPCResponseImpl("success");
        send(rpcRequest, rpcResponse);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#sendXMLString(org.solmix.api.datasource.DSRequest, java.lang.String)
     */
    @Override
    public void sendXMLString(DSRequest dsRequest, String xml) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#sendXMLString(org.solmix.api.call.RPCRequest, java.lang.String)
     */
    @Override
    public void sendXMLString(RPCRequest rpcRequest, String xml) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#getRequest()
     */
    @Override
    public RPCRequest getRequest() {
        int requestCount = requestCount();
        if (requestCount > 1)
            log.warn((new StringBuilder()).append("getRequest() on multiop RPC (").append(requestCount).append(" requests pending) ").toString(),
                new Exception());
        return (RPCRequest) requests.get(0);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#getRequests()
     */
    @Override
    public List<RequestType> getRequests() {

        return requests;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#getResponse(org.solmix.api.datasource.DSRequest)
     */
    @Override
    public DSResponse getResponse(DSRequest req) {
        Object resp = responseMap.get(req);
        if (resp instanceof DSResponse)
            return (DSResponse) resp;
        else
            return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#getResponse(org.solmix.api.call.RPCRequest)
     */
    @Override
    public RPCResponse getResponse(RPCRequest req) {
        Object resp = responseMap.get(req);
        if (resp instanceof RPCResponse)
            return (RPCResponse) resp;
        else
            return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#registerCallback(org.solmix.api.call.DSCManagerCompletionCallback)
     */
    @Override
    public void registerCallback(DSCManagerCompletionCallback callback) {
        if (!callbacks.contains(callback))
            callbacks.add(callback);

    }

    /**
     * When isc_rpc_logging = off, take the logger off.
     * 
     * @param request
     */
    protected void initLog(HttpServletRequest request) {
        if ("off".equals(request.getParameter("isc_rpc_logging")))
            ;
        // log.setInstanceLevel(Logger.OFF);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#getContext()
     */
    @Override
    public RPCManagerData getContext() {
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#setConf(org.solmix.api.data.RPCManagerData)
     */
    @Override
    public void setContext(RPCManagerData data) {
        this.data = data;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#requestCount()
     */
    @Override
    public int requestCount() {
        return requests.size();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManager#freeDataSources()
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
     * @see org.solmix.api.call.DSCManager#applyEarlierResponseValues(org.solmix.api.datasource.DSRequest)
     */
    @Override
    public void applyEarlierResponseValues(DSRequest dsReq) throws SlxException {
        setRequestProcessingStarted(true);
        DSRequestData _reqData = dsReq.getContext();
        Date _currentDate = new Date();
        data.addToTemplateContext("currentDate", _currentDate);
        if (data.getFromTemplateContext("transactionDate") == null)
            data.addToTemplateContext("transactionDate", _currentDate);
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
        for (TfieldNameValue criteria : criterias) {
            String fieldName = criteria.getFieldName();
            String value = criteria.getValue();
            if (fieldName != null && value != null) {
                Object evaluation = Velocity.evaluate(value, params);
                dsReq.getContext().addToCriteria(fieldName, evaluation);
            }
        }
        for (TfieldNameValue v : values) {
            String fieldName = v.getFieldName();
            String value = v.getValue();
            if (fieldName != null && value != null) {
                Object evaluation = Velocity.evaluate(value, params);
                dsReq.getContext().addToCriteria(fieldName, evaluation);
            }
        }
    }

    @Override
    public WebContext getRequestContext() {
        return context;
    }

    @Override
    public void setRequestContext(WebContext context) throws SlxException {
        this.context = context;
    }

    @Override
    public boolean requestQueueIncludesUpdates() throws SlxException {
        return _requestQueueIncludesPriorUpdate(null);
    }
    
    public void beginTransaction(){
        transactionStarted=true;
    }

    public DSResponse execute(final XAOp op) throws SlxException {
        op.setRpc(this);
        DSResponse t=null;
        try {
            t = op.exe();
        } catch (SlxException e) {
            try {
                transactionFailed(op.getRequest(),t);
            } catch (Exception e1) {
                throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_ROLLBACK_FAILTURE,"transaction rollback failure with rollback Exception:"+e1.getMessage()+" with Root Exception:"+e.getFullMessage());
            }
            throw e;
        }
        boolean _transactionFailure = isXAFailure(op.getRequest(),t);
        if(_transactionFailure){
            transactionFailed(op.getRequest(),t);
            throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_BREAKEN,"transaction breaken because of one request failure.");
            
        }
        return t;
        
    }

    
    public DSResponse transactionExecute(DSRequest request)throws SlxException{
        if(!transactionStarted){
            throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_NOT_STARTED,"Transaction not started ,you should call method startTransaction()");
        }
        request.setRPC(this);
        request.setCanJoinTransaction(true);
//        setRequestProcessingStarted(true);
        DSResponse res=null;
        try {
            res = request.execute();
        } catch (SlxException e) {
            try {
                transactionFailed(request,res);
            } catch (Exception e1) {
                throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_ROLLBACK_FAILTURE,"transaction rollback failure with rollback Exception:"+e1.getMessage()+" with Root Exception:"+e.getFullMessage());
            }
            throw e;
        }
        boolean _transactionFailure = isXAFailure(request,res);
        if(_transactionFailure){
            transactionFailed(request,res);
            throw new SlxException(Tmodule.RPC,Texception.TRANSACTION_BREAKEN,"transaction breaken because of one request failure.");
            
        }
        return res;
         
    }
    
    private void transactionFailed(DSRequest request, DSResponse resp) throws SlxException {
        if (request.isJoinTransaction()) {
            if (resp != null && resp.getContext().getStatus() == DSResponse.Status.STATUS_SUCCESS)
                resp.getContext().setStatus(DSResponse.Status.STATUS_TRANSACTION_FAILED);
        }
        if (callbacks != null)
            for (DSCManagerCompletionCallback callback : callbacks) {
                callback.onFailure(this, true);
            }
    }

    private boolean isXAFailure(DSRequest req, DSResponse res) throws SlxException {
        boolean _transactionFailure = false;
        if (res != null && res.getContext().getStatus().value() < 0)
            if (req.isRequestStarted()) {
                if (req.isJoinTransaction())
                    _transactionFailure = true;
            } else {
                BasicDataSource ds = (BasicDataSource) req.getDataSource();
                if (ds.shouldAutoJoinTransaction(req) && (ds.shouldAutoStartTransaction(req, true) || requestQueueIncludesPriorUpdate(req)))
                    _transactionFailure = true;
            }
        return _transactionFailure;

    }

    public void endTransaction()  {

        transactionStarted = false;
        try {
            onSuccess();
        } catch (Exception e) {
            log.debug("exception when end transaction", e);
        }
    }

    public void rollback() throws SlxException {
        transactionStarted = false;
        if (callbacks != null)
            for (DSCManagerCompletionCallback callback : callbacks) {
                callback.onFailure(this, true);
            }
    }
    


}
