/*
 *  Copyright 2012 The Solmix Project
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.fmk.SlxContext;

/**
 * Interceptor for common Rest-style ajax communication.
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-24
 */

public class DefaultRestInterceptor extends AbstractRestInterceptor
{
    private static  final Logger LOG = LoggerFactory.getLogger(DefaultRestInterceptor.class);
    public static final String PAYLOAD_NAME = "__payload";
    public static final String DATAFORMAT = "dsc_dataFormat";

    public static final String DSC_TRANSPORT = "dsc_transport";
    public static final String XML_PREFIX = "<";

    public static final String JSON_PREFIX = "{";

    public static final String JSON = "json";

    public static final String XML = "xml";

    public static final String REST_PARM = "_isRest";

    private String payloadName=PAYLOAD_NAME;

    @Override
    public void configure(DataTypeMap config) {
        super.configure(config);
        payloadName=config.getString("payloadName",  payloadName);
    }
    
    @Override
    public void prepareRequest(DSCall dsCall, WebContext context) throws SlxException {
        HttpServletRequest request = context.getRequest();
        try {
            String payload = request.getParameter(payloadName);
            Request dsRequest = null;
            if (payload != null) {
                payload = payload.trim();
                if (payload.startsWith(JSON_PREFIX)) {
                    dsRequest = dsCall.getJSParser().toJavaObject(payload, Request.class);
                } else if (payload.startsWith(XML_PREFIX)) {
                    dsRequest = dsCall.getXMLParser().unmarshalReq(new StringReader(payload));
                }
            } else {
                if (JSON.equalsIgnoreCase(getDataformat(request))) {
                    dsRequest = dsCall.getJSParser().toJavaObject(request.getReader(), Request.class);
                } else if (XML.equalsIgnoreCase(getDataformat(request))) {
                    dsRequest = dsCall.getXMLParser().unmarshalReq(request.getReader());
                }
            }
            if (dsRequest != null) {
                List<Roperation> operations = dsRequest.getOperations().getElem();
                if (operations != null) {
                    boolean freeOnExecute = operations.size() <= 1;
                    for (Roperation operation : operations) {
                        DSRequest dsr = SlxContext.getThreadSystemContext().getBean(DataSourceManager.class).createDSRequest(operation,
                            SlxContext.getWebContext());
                        dsr.getContext().setIsClientRequest(true);
                        dsr.setFreeOnExecute(freeOnExecute);
                        dsr.setCanJoinTransaction(!freeOnExecute);
                        dsr.setDSCall(dsCall);
                        dsr.setRequestContext(context);
                        dsCall.addRequest(dsr);
                    }
                }
            }
        } catch (IOException e) {
            throw new SlxException(Tmodule.DSC, Texception.DEFAULT, e);
        }
    }

    @Override
    public Action postInspect(DSCall dsCall, WebContext context) throws SlxException {
//        Object isRest = context.getAttribute(REST_PARM, Scope.LOCAL);
        HttpServletRequest request= context.getRequest();
        if (/*isRest == null||*/request.getParameter(DSC_TRANSPORT)!=null)
            return Action.CONTINUE;
        String dataFormat = getTransport(context.getRequest());
        String contentType = new StringBuilder().append("text/").append(dataFormat).append("; charset=").append(charset).toString();
        context.setContentType(contentType);
        if(LOG.isTraceEnabled())
            LOG.trace("Rest style output used ContentType:"+contentType);
        //find client responses
        List<Object> orderedResponseList = new ArrayList<Object>();
        for(DSRequest req:dsCall.getRequests()){
            DSResponse res= dsCall.getResponse(req);
            orderedResponseList.add(getClientResponse(res));
        }
        Writer _out;
        try {
            if(showClientOutPut)
                _out=new StringWriter();
            else
                _out = context.getOut();
            //Data must no cached.
            context.setNoCacheHeaders();
            Map<String,Object> restContainer = new HashMap<String,Object>();
            if(orderedResponseList.size()==1)
                restContainer.put("response", orderedResponseList.get(0));
            else
                restContainer.put("responses", orderedResponseList);
            if (JSON.equalsIgnoreCase(dataFormat)) {
                dsCall.getJSParser().toJSON(_out, restContainer);
            }else if(XML.equalsIgnoreCase(dataFormat)){
                dsCall.getXMLParser().toXML(_out, restContainer);
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
        return Action.CANCELLED;
    }

    /**
     * @param request
     * @return
     */
    private String getTransport(HttpServletRequest request) {
        String queryString = request.getParameter(DATAFORMAT);
        if (queryString == null)
            return "json";
        else
            return queryString;
    }

    private Boolean isRest(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return false;
        else
            return queryString.indexOf("dsc_rest=1") != -1 || queryString.indexOf("dsc_rest=true") != -1;

    }

    public  String getDataformat(HttpServletRequest request) {
        String queryString = request.getParameter(DATAFORMAT);
        if (queryString == null)
            return "json";
        else
            return queryString;
    }

    /**
     * @return the payloadName
     */
    public String getPayloadName() {
        return payloadName;
    }

    /**
     * @param payloadName the payloadName to set
     */
    public void setPayloadName(String payloadName) {
        this.payloadName = payloadName;
    }

}
