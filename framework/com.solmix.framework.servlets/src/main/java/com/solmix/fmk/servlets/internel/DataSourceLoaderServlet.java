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

package com.solmix.fmk.servlets.internel;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.datasource.DataSource;
import com.solmix.api.datasource.DataSourceManager;
import com.solmix.api.exception.SlxException;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.js.ISCJavaScript;

/**
 * 
 * @author Administrator
 * @version 110035 2012-5-10
 */

public class DataSourceLoaderServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** default log */
    private final Logger log = LoggerFactory.getLogger(DataSourceLoaderServlet.class);

    private DataSourceManager dsmService;

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
                    Writer writer = response.getWriter();

                    // OutputStream os = response.getOutputStream();
                    Writer out = new StringWriter();
                    // (new JacksonJSParserImpl()).toJavaScript(writer, ds.getContext().getTdataSource());
                    ISCJavaScript.get().toDataSource(writer, ds);
                    ISCJavaScript.get().toDataSource(out, ds);
                    System.out.println(out.toString());
                } finally {
                    if (ds != null)
                        dsmService.free(ds);
                }
            }
        } catch (Throwable e) {
            log.error("Exception while attempting to load a DataSource", e);
            throw new ServletException(DataUtil.getStackTrace(e));
        } finally {
            response.flushBuffer();
        }

    }

    /**
     * @return the dsmService
     */
    public DataSourceManager getDsmService() {
        return dsmService;
    }

    /**
     * @param dsmService the dsmService to set
     */
    public void setDsmService(DataSourceManager dsmService) {
        this.dsmService = dsmService;
    }

}
