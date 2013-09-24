package org.solmix.web.servlets;
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



import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.rpc.RPCResponseImpl;
import org.solmix.fmk.servlet.RequestContext;
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

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-11 gwt
 */
public class DataBindingCall extends BaseServlet {

    /**
     * General serial ID
     */
    private static final long serialVersionUID = 5382863205460863312L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) {
      response.setCharacterEncoding( "UTF-8" );
        RequestTimer requestTimer = new RequestTimer(request);
        RPCManagerFactory rpcService = null;
        RPCManager rpc = null;
        RequestContext context = null;
        try {
            context = RequestContext.instance(this, request, response);
            rpcService = Util.getRPCMService();
            if (rpcService != null)
                rpc = rpcService.getRPCManager(context);
            log.info("Performing " + rpc.requestCount() + " operation(s) ");
            if (rpc.getRequests() != null)
                for (RequestType req : rpc.getRequests()) {
                    if (req instanceof RPCRequest) {
                        RPCRequest rpcRequest = (RPCRequest) req;
                        try {
                            rpc.send(rpcRequest, handleRPCRequest(rpcRequest, rpc, context));
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
                            rpc.send(dsRequest, handleDSRequest(dsRequest, rpc, context));
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
                }
        } catch (SlxException e) {
            log.error("request Exception: ", e.getMessage());
        } finally {
            requestTimer.stop();
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
                handleError(response, ignored);
            }
        }

    }

    /**
     * @param rpcRequest
     * @param rpc
     * @param context
     * @return
     */
    private RPCResponse handleRPCRequest(RPCRequest rpcRequest, RPCManager rpc, RequestContext context) {
        try {
            return rpcRequest.execute();
        } catch (Exception e) {
            RequestContext _tmp = context;
            RequestContext.staticLog.warn("rpcRequest.execute() failed: ", e);
            RPCResponse rpcResponse = new RPCResponseImpl();
            rpcResponse.setStatus(RPCResponse.STATUS_FAILURE);
            rpcResponse.setData(e.getMessage());
            return rpcResponse;
        }
    }

    /**
     * @param dsRequest
     * @param rpc
     * @param context 
     *        Servlet Request context
     * @return
     * @throws SlxException
     */
    private DSResponse handleDSRequest(DSRequest dsRequest, RPCManager rpc, RequestContext context) throws SlxException {
        try {
            return dsRequest.execute();
        } catch (Exception e) {
            RequestContext.staticLog.warn("DSRequest.execute() failed: ", e);
            DSResponse dsResponse = new DSResponseImpl(dsRequest != null ? dsRequest.getDataSource() : (DataSource) null);
            dsResponse.getContext().setStatus(Status.STATUS_FAILURE);
            dsResponse.getContext().setData(e.getMessage());
            return dsResponse;
        }
    }

}
