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

package org.solmix.web;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.js.ISCJavaScript;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-12-19
 */

public class LoadSchemaServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** default log */
    private final Logger log = LoggerFactory.getLogger(LoadSchemaServlet.class);

    private DataSourceManager dsmService;
    private String characterEncoding;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String encode = config.getInitParameter("CharacterEncoding");
        if (encode != null)
            characterEncoding = encode.trim();
        else
            characterEncoding="UTF-8";
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // List<DataSource> dsToFree = new ArrayList<DataSource>();
        response.setCharacterEncoding(characterEncoding);
        if (dsmService == null)
            dsmService =SlxContext.getThreadSystemContext().getBean(DataSourceManager.class);
        try {
            String ids = request.getParameter("dataSource");
            String multipleIDs[] = ids.split(",");
            for (String id : multipleIDs) {
                DataSource ds = null;
                try {
                    ds = dsmService.get(id);
                    if (ds == null)
                        throw new SlxException(Tmodule.SERVLET, Texception.DS_LOAD_NOT_LOADING, (new StringBuilder()).append(
                            "Unable to load DataSource for ID: ").append(id).toString());
                    // dsToFree.add(ds);
                    response.setContentType("text/javascript");
//                    SlxContext.getWebContext().setNoCacheHeaders();
                    //TODO
                    Writer writer = response.getWriter();
                    ISCJavaScript.get().toDataSource(writer, ds, "AdvanceDataSource");
                    if(log.isTraceEnabled()){
                        Writer out = new StringWriter();
                        ISCJavaScript.get().toDataSource(out, ds, "AdvanceDataSource");
                        log.trace(out.toString());
                    }
                } finally {
                    if (ds != null)
                        dsmService.free(ds);
                }
            }
        } catch (Throwable e) {
            log.error("Exception while attempting to load a DataSource", e);
            throw new ServletException(DataUtils.getStackTrace(e));
        } finally {
            response.flushBuffer();
        }

    }

}
