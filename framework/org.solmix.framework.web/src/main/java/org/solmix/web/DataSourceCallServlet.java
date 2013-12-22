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

package org.solmix.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DataSourceCall;
import org.solmix.api.call.DataSourceCallFactory;
import org.solmix.api.call.HttpServletRequestParser;
import org.solmix.api.call.RPCRequest;
import org.solmix.api.call.RPCResponse;
import org.solmix.api.call.RequestType;
import org.solmix.api.context.SystemContext;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.call.RPCResponseImpl;
import org.solmix.fmk.datasource.DSResponseImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-5-10
 */

public class DataSourceCallServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Logger log = LoggerFactory.getLogger(DataSourceCallServlet.class);


    private HttpServletRequestParser requestParser;


    private String characterEncoding;
    
    private SystemContext sc;
    
    public DataSourceCallServlet(){
        this(null);
    }
    
    public DataSourceCallServlet(SystemContext sc){
        this.sc=sc;
    }



    /**
     * @return the requestParser
     */
    public HttpServletRequestParser getRequestParser() {
        return requestParser;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String encode = config.getInitParameter("CharacterEncoding");
        if (encode != null)
            characterEncoding = encode.trim();
        else
            characterEncoding="UTF-8";
    }

    /**
     * @param requestParser the requestParser to set
     */
    public void setRequestParser(HttpServletRequestParser requestParser) {
        this.requestParser = requestParser;
    }

  

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * 
     * @param request
     * @param response
     */
    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding(characterEncoding);
        DataSourceCall dsc = null;
        WebContext context = null;
        try {
            if(sc==null){
                sc=SlxContext.getThreadSystemContext();
            }
            DataSourceCallFactory factory=sc.getBean(DataSourceCallFactory.class);
            context = SlxContext.getWebContext();
            if (factory != null)
                dsc = factory.createRPCManager(context,this.requestParser);
            if(log.isTraceEnabled())
                log.trace("Performing " + dsc.requestCount() + " operation(s) ");
            Exception exceptionHolder=null;
            if (dsc.getRequests() != null)
                for (RequestType req : dsc.getRequests()) {
                    if (req instanceof RPCRequest) {
                        RPCRequest rpcRequest = (RPCRequest) req;
                        try {
                            dsc.send(rpcRequest, handleRPCRequest(rpcRequest, dsc, context,exceptionHolder));
                        } catch (Exception e) {
                            try {
                                dsc.sendFailure(rpcRequest, e);
                                log.error((new StringBuilder()).append("Error executing Rpc: ").append(rpcRequest.toString()).toString(), e);
                            } catch (Exception ignored) {
                            }
                        }
                    } else if (req instanceof DSRequest) {
                        DSRequest dsRequest = (DSRequest) req;
                        try {
                            dsc.send(dsRequest, handleDSRequest(dsRequest, dsc, context,exceptionHolder));
                        } catch (SlxException e) {
                            try {
                                dsc.sendFailure(dsRequest, e);
                                log.error((new StringBuilder()).append("Error executing operation: ").append(dsRequest.getContext().getOperation()).toString(),e);
                            } catch (SlxException ingnored) {
                            }
                        }
                    }
                    if(exceptionHolder!=null){
                        log.error("Error executing operation: ",exceptionHolder);
                    }
                }
        } catch (SlxException e) {
            log.error("request Exception: ", e);
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * @param dscRequest
     * @param dsc
     * @param context
     * @return
     */
    private RPCResponse handleRPCRequest(RPCRequest dscRequest, DataSourceCall dsc, WebContext context,Exception exception) {
        try {
            return dscRequest.execute();
        } catch (Exception e) {
            exception=e;
            RPCResponse rpcResponse = new RPCResponseImpl();
            rpcResponse.setStatus(RPCResponse.STATUS_FAILURE);
            rpcResponse.setData(e.getMessage());
            return rpcResponse;
        }
    }

    /**
     * @param dsRequest
     * @param rpc
     * @param context Servlet Request context
     * @return
     * @throws SlxException
     */
    private DSResponse handleDSRequest(DSRequest dsRequest, DataSourceCall rpc, WebContext context,Exception exception) throws SlxException {
        try {
            return dsRequest.execute();
        } catch (Exception e) {
            exception=e;
            DSResponse dsResponse = new DSResponseImpl(dsRequest != null ? dsRequest.getDataSource() : (DataSource) null);
            dsResponse.getContext().setStatus(Status.STATUS_FAILURE);
            dsResponse.getContext().setData(e.getMessage());
            return dsResponse;
        }
    }

   
}
