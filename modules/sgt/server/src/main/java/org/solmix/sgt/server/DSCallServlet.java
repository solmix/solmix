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

package org.solmix.sgt.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.rpc.RPCManagerFactory;
import org.solmix.api.rpc.RPCRequest;
import org.solmix.api.rpc.RPCResponse;
import org.solmix.api.rpc.RequestType;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.rpc.RPCResponseImpl;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-19
 */

public class DSCallServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Logger log = LoggerFactory.getLogger(DSCallServlet.class);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
        WebContext wc = SlxContext.getWebContext();
        response.setCharacterEncoding("UTF-8");
        RPCManager rpc = null;
        WebContext context = null;
        try {
            RPCManagerFactory factory = SlxContext.getRPCManagerFactory();
            rpc = factory.getRPCManager(wc, new RestRequestParser());
            log.debug("Performing " + rpc.requestCount() + " operation(s) ");
            Exception exceptionHolder=null;
            if (rpc.getRequests() != null)
                for (RequestType req : rpc.getRequests()) {
                    if (req instanceof RPCRequest) {
                        RPCRequest rpcRequest = (RPCRequest) req;
                        try {
                            rpc.send(rpcRequest, handleRPCRequest(rpcRequest, rpc, context,exceptionHolder));
                        } catch (Exception e) {
                            try {
                                rpc.sendFailure(rpcRequest, e);
                                log.error((new StringBuilder()).append("Error executing Rpc: ").append(rpcRequest.toString()).toString(), e);
                            } catch (Exception ignored) {
                            }
                        }
                    } else if (req instanceof DSRequest) {
                        DSRequest dsRequest = (DSRequest) req;
                        try {
                            rpc.getContext().setRest(Boolean.TRUE);
                            rpc.send(dsRequest, handleDSRequest(dsRequest, rpc, context,exceptionHolder));
                        } catch (SlxException e) {
                            try {
                                rpc.sendFailure(dsRequest, e);
                                log.error(
                                    (new StringBuilder()).append("Error executing operation: ").append(dsRequest.getContext().getOperation()).toString(),
                                    e);
                            } catch (SlxException ingnored) {
                            }
                        }
                    }
                    if(exceptionHolder!=null){
                        log.error("Error executing operation: ",exceptionHolder);
                    }
                }
        } catch (SlxException e) {
            log.error("request Exception: ", e.getMessage());
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
            }
        }

    }

    /**
     * @param rpcRequest
     * @param rpc
     * @param context
     * @return
     */
    private RPCResponse handleRPCRequest(RPCRequest rpcRequest, RPCManager rpc, WebContext context,Exception exception) {
        try {
            return rpcRequest.execute();
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
    private DSResponse handleDSRequest(DSRequest dsRequest, RPCManager rpc, WebContext context,Exception exception) throws SlxException {
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
