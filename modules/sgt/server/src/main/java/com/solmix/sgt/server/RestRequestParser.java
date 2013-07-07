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

package com.solmix.sgt.server;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.solmix.api.context.WebContext;
import com.solmix.api.datasource.DSRequest;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;
import com.solmix.api.jaxb.request.Request;
import com.solmix.api.jaxb.request.Roperation;
import com.solmix.api.rpc.HttpServletRequestParser;
import com.solmix.api.rpc.RPCManager;
import com.solmix.api.serialize.JSParser;
import com.solmix.commons.util.DataUtil;
import com.solmix.commons.util.IOUtil;
import com.solmix.fmk.context.SlxContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-4
 */

public class RestRequestParser implements HttpServletRequestParser
{

    DocumentBuilder domBuilder;

    public static String BIN_PATH = '/' + RequestType.BIN.value() + '/';

    public static String COMET_PATH = '/' + RequestType.EVENT.value() + '/';

    protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (domBuilder == null) {
            DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
            domBuilder = domfac.newDocumentBuilder();
        }
        return domBuilder;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.rpc.HttpServletRequestParser#parseRequest(com.solmix.api.rpc.RPCManager,
     *      javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void parseRequest(RPCManager rpc, WebContext webContext) throws SlxException {

        HttpServletRequest request = webContext.getRequest();
        String viewType = request.getParameter("viewType");

        String encode = request.getCharacterEncoding();
        if ("fchart".equals(viewType)) {
            ConfigBean cf = getDataSourceFromURL(request);
            DSRequest dsrequest = SlxContext.getDataSourceManager().createDSRequest(cf.getDataSourceName(), Eoperation.FETCH);
            if (cf.getOperationId() == null)
                dsrequest.getContext().setOperation(cf.getDataSourceName() + "_" + cf.getOperationType());
            else
                dsrequest.getContext().setOperation(cf.getOperationId());
            if (cf.getCriteria() != null && cf.getCriteria().size() > 0)
                dsrequest.getContext().setCriteria(cf.getCriteria());
            if (cf.getValues() != null && cf.getValues().size() > 0)
                dsrequest.getContext().setValues(cf.getValues());
            dsrequest.getContext().setIsClientRequest(true);
            dsrequest.setRpc(rpc);
            dsrequest.setRequestContext(webContext);
            rpc.addRequest(dsrequest);

        } else {
            try {
                StringWriter out = new StringWriter();
                String queryStr = request.getParameter("_transaction");
                if (queryStr == null) {
                    IOUtil.copyCharacterStreams(request.getReader(), out);
                    queryStr = out.toString();
                }
                if (queryStr == null || queryStr.length() == 0) {
                    throw new java.lang.IllegalStateException("This may be not a Solmix Internal Framework request.");
                }
                JSParser parser = rpc.getJsParser();
                Request dsRequest = parser.toJavaObject(queryStr, Request.class);
                if (dsRequest != null) {
                    List<Roperation> operations = dsRequest.getOperations().getElem();
                    {
                        if (operations != null) {
                            boolean freeOnExecute = operations.size() <= 1;
                            for (Roperation operation : operations) {
                                ConfigBean cf = getDataSourceFromURL(request);
                                if (operation.getOperationId() == null) {
                                    if(operation.getDataSource()!=null&&operation.getOperationType()!=null)
                                        operation.setOperationId(operation.getDataSource()+"_"+operation.getOperationType());
                                    else
                                    operation.setOperationId(cf.getDataSourceName() + "_" + cf.getOperationType());
                                }
                                if (operation.getDataSource() == null)
                                    operation.setDataSource(cf.getDataSourceName());
                                DSRequest dsr = SlxContext.getDataSourceManager().createDSRequest(operation, SlxContext.getWebContext());
                                dsr.getContext().setIsClientRequest(true);
                                dsr.getContext().setFreeOnExecute(freeOnExecute);
                                dsr.setJoinTransaction(!freeOnExecute);
                                dsr.setRpc(rpc);
                                dsr.setRequestContext(webContext);
                                rpc.addRequest(dsr);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ConfigBean getDataSourceFromURL(HttpServletRequest request) {

        String reqPath = request.getRequestURI();
        if (DataUtil.isNullOrEmpty(reqPath)) {
            reqPath = request.getRequestURI();
        }
        String type = null;
        String datasource = null;
        ConfigBean _return = new ConfigBean();
        try {
            String tmp = "";
            if (reqPath.indexOf(BIN_PATH) != -1) {
                tmp = reqPath.substring(reqPath.indexOf(BIN_PATH) + BIN_PATH.length());
                _return.setTRequest(RequestType.BIN);
                type = tmp.substring(0, tmp.indexOf("/"));
                datasource = tmp.substring(tmp.indexOf("/") + 1);
                if (datasource.indexOf(".ds") != -1) {
                    datasource = datasource.substring(0, datasource.indexOf(".ds"));
                }
                _return.setDataSourceName(datasource);
                _return.setOperationType(type);
            } else if (reqPath.indexOf(COMET_PATH) != -1) {
                tmp = reqPath.substring(reqPath.indexOf(COMET_PATH) + COMET_PATH.length());
                _return.setTRequest(RequestType.EVENT);
            } else {
                throw new java.lang.IllegalArgumentException("Request Path:" + reqPath + " is not validate");
            }
            parserParameters(request, _return);
        } catch (Exception e2) {
            throw new java.lang.IllegalArgumentException("Request Path:" + reqPath + " is not validate");
        }
        return _return;
    }

    /**
     * @param request
     * @param _return
     */
    private static void parserParameters(HttpServletRequest request, ConfigBean bean) {
        @SuppressWarnings("unchecked")
        Enumeration<String> e = request.getParameterNames();
        if (e == null)
            return;
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            if (ConfigBean.OP_ID.equals(key))
                bean.setOperationId(request.getParameter(key));
            else if (key.startsWith(ConfigBean.CRITERIA_PREFIX))
                bean.getCriteria().put(key.substring(ConfigBean.CRITERIA_PREFIX.length() - 1, key.length()), request.getParameter(key).toString());
            else if (key.startsWith(ConfigBean.VALUES_PREFIX))
                bean.getValues().put(key.substring(ConfigBean.VALUES_PREFIX.length() - 1, key.length()), request.getParameter(key).toString());
        }
    }

    /**
     * @param node
     * @return
     */
    protected Map<String, Object> parser(Element e) {
        Map<String, Object> __return = null;
        __return = new HashMap<String, Object>();

        for (Node node = e.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("data".equals(node.getNodeName())) {
                    __return.put(node.getNodeName(), parser((Element) node));
                } else {
                    __return.put(node.getNodeName(), node.getTextContent().trim());
                }
            }

        }
        return __return;
    }

}