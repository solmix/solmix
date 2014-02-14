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

package org.solmix.sgt.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCallInterceptor;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.fmk.SlxContext;
import org.solmix.web.AbstractDSCallServlet;
import org.solmix.web.DSCallServlet;
import org.solmix.web.LoadSchemaServlet;
import org.solmix.web.interceptor.DefaultRestInterceptor;
import org.solmix.web.interceptor.DownloadInterceptor;
import org.solmix.web.interceptor.ExportInterceptor;
import org.solmix.web.interceptor.UploadInterceptor;

import com.smartgwt.extensions.fusionchart.server.FusionChartInterceptor;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-15
 */

public class SGTServlet extends HttpServlet
{

    private volatile LoadSchemaServlet loadSchemaService;

    private volatile AbstractDSCallServlet dsCallService;


    private static final Logger log = LoggerFactory.getLogger(SGTServlet.class);


    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        loadSchemaService = new LoadSchemaServlet();
        loadSchemaService.init(getServletConfig());
        dsCallService = new DSCallServlet(){
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected DSCallInterceptor[] configuredInterceptors() {
                DSCallInterceptor[] re={
                    new DownloadInterceptor(), 
                    new UploadInterceptor(), 
                    new ExportInterceptor(), 
                    new FusionChartInterceptor(),
                    new DefaultRestInterceptor()};
                return  re;
            }
        };
        dsCallService.init(getServletConfig());
       
      }
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
            // String viewType = request.getParameter("viewType");
            switch (eType) {
                case LOAD_SCHEMA:
                    loadSchemaService.service(request, response);
                    break;
                default:
                    dsCallService.service(request, response);
                    break;

            }
        } 

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void preLoading() {
        try {
            DataSourceManager dsm = SlxContext.getThreadSystemContext().getBean(DataSourceManager.class);
            DataSource ds = dsm.get("init");
            dsm.free(ds);
        } catch (SlxException e) {
            log.error("PreLoading failed:", e);
        }
    }
}
