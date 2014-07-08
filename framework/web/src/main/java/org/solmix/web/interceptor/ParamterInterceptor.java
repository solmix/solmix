/*
 * Copyright 2013 The Solmix Project
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

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.solmix.api.call.DSCall;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.datasource.DSRequestImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月3日
 */

public class ParamterInterceptor extends AbstractRestInterceptor
{

    public static String BIN_PATH = '/' + RequestType.BIN.value() + '/';

    public static String COMET_PATH = '/' + RequestType.EVENT.value() + '/';

    @Override
    public void prepareRequest(DSCall dsCall, WebContext context)
        throws SlxException {
        HttpServletRequest request = context.getRequest();
        Object protocol = request.getParameter(PROTOCOL);
        if (protocol != null
            && PROTOCOL_PARAM.equalsIgnoreCase(protocol.toString())) {

            ConfigBean cf = getDataSourceFromURL(request);
            Roperation operation = new Roperation();
            if (cf.getOperationId() == null) {
                if (cf.getDataSourceName() != null
                    && cf.getOperationType() != null)
                    operation.setOperationId(cf.getDataSourceName() + "_"
                        + cf.getOperationType());
            } else {
                operation.setOperationId(cf.getOperationId());
            }
            operation.setDataSource(cf.getDataSourceName());
            operation.setOperationType(cf.getOperationType());
            if (cf.getValues() != null)
                operation.setValues(cf.getValues());
            if (cf.getCriteria() != null)
                operation.setCriteria(cf.getCriteria());
            DSRequest dsr = new DSRequestImpl(operation,
                SlxContext.getWebContext());
            dsr.getContext().setIsClientRequest(true);
            dsr.setFreeOnExecute(true);
            dsr.setCanJoinTransaction(false);
            dsr.setDSCall(dsCall);
            dsr.setRequestContext(context);
            dsCall.addRequest(dsr);
        }
    }

    public static ConfigBean getDataSourceFromURL(HttpServletRequest request) {

        String reqPath = request.getRequestURI();
        if (DataUtils.isNullOrEmpty(reqPath)) {
            reqPath = request.getRequestURI();
        }
        String type = null;
        String datasource = null;
        ConfigBean _return = new ConfigBean();
        try {
            String tmp = "";
            if (reqPath.indexOf(BIN_PATH) != -1) {
                tmp = reqPath.substring(reqPath.indexOf(BIN_PATH)
                    + BIN_PATH.length());
                _return.setTRequest(RequestType.BIN);
                type = tmp.substring(0, tmp.indexOf("/"));
                datasource = tmp.substring(tmp.indexOf("/") + 1);
                if (datasource.indexOf(".ds") != -1) {
                    datasource = datasource.substring(0,
                        datasource.indexOf(".ds"));
                }
                _return.setDataSourceName(datasource);
                _return.setOperationType(type);
            } else if (reqPath.indexOf(COMET_PATH) != -1) {
                tmp = reqPath.substring(reqPath.indexOf(COMET_PATH)
                    + COMET_PATH.length());
                _return.setTRequest(RequestType.EVENT);
            } else {
                throw new java.lang.IllegalArgumentException("Request Path:"
                    + reqPath + " is not validate");
            }
            parserParameters(request, _return);
        } catch (Exception e2) {
            throw new java.lang.IllegalArgumentException("Request Path:"
                + reqPath + " is not validate");
        }
        return _return;
    }

    /**
     * @param request
     * @param _return
     */
    private static void parserParameters(HttpServletRequest request,
        ConfigBean bean) {
        @SuppressWarnings("unchecked")
        Enumeration<String> e = request.getParameterNames();
        if (e == null)
            return;
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            String opType = bean.getOperationType();
            if (ConfigBean.OP_ID.equals(key))
                bean.setOperationId(request.getParameter(key));
            else if (Eoperation.FETCH.value().equals(opType)
                || Eoperation.REMOVE.value().equals(opType))
                bean.getCriteria().put(key,
                    request.getParameter(key).toString());
            else if (Eoperation.UPDATE.value().equals(opType)
                || Eoperation.ADD.value().equals(opType))
                bean.getValues().put(key, request.getParameter(key).toString());
            else
                bean.getCriteria().put(key,
                    request.getParameter(key).toString());
        }
    }
}
