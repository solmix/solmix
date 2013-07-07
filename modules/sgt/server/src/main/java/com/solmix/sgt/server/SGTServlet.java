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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;
import com.solmix.fmk.context.SlxContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-15
 */

public class SGTServlet  extends HttpServlet
{

    LoadSchemaServlet loadSchemaService;

    DSCallServlet dsCallService;

    Servlet cometServlet;
    
    private static final Logger log = LoggerFactory.getLogger(SGTServlet.class);

    @Override
    public void init() throws ServletException {
        preLoading();
    }
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException 
     * @throws IOException 
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConfigBean bean = RestRequestParser.getDataSourceFromURL(request);
        if (bean.getTRequest() == RequestType.BIN) {
            Eoperation eType = Eoperation.fromValue(bean.getOperationType());
//            String viewType = request.getParameter("viewType");
            switch (eType) {
                case LOAD_SCHEMA:
                    if (loadSchemaService == null) {
                        loadSchemaService = new LoadSchemaServlet();
                        loadSchemaService.init(getServletConfig());
                    }
                    loadSchemaService.service(request, response);
                    break;
                default:
                    if (dsCallService == null) {
                        dsCallService = new DSCallServlet();
                        dsCallService.init(getServletConfig());
                    }
                    dsCallService.service(request, response);
                    break;

            }
        } else if (bean.getTRequest() == RequestType.EVENT) {
            if (cometServlet == null) {
                cometServlet = new org.atmosphere.cpr.AtmosphereServlet();
                cometServlet.init(getServletConfig());
            }
            try {
                response.setCharacterEncoding("GBK");
                cometServlet.service(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    
    private void preLoading(){
        try {
                    DataSource ds =SlxContext.getDataSourceManager().get("init");
                    SlxContext.getDataSourceManager().free(ds);
              } catch (SlxException e) {
                    log.error("PreLoading failed:", e);
              }
      }
}
