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

package org.solmix.web.servlets;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.js.ISCJavaScript;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-12 gwt
 */
public class DataSourceLoader extends BaseServlet
{

    /**
    * 
    */
    private static final long serialVersionUID = 1739983335902410863L;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Use UTF-8 as character coding.
        response.setCharacterEncoding("UTF-8");
        RequestTimer requestTimer = new RequestTimer(request);
        List<DataSource> dsToFree = new ArrayList<DataSource>();
        try {
            String ids = request.getParameter("dataSource");
            String multipleIDs[] = ids.split(",");
            for (String id : multipleIDs) {
                DataSource ds = null;
                try {
                    ds = Util.getDSMService().get(id);
                    if (ds == null)
                        throw new SlxException(Tmodule.SERVLET, Texception.DS_LOAD_NOT_LOADING, (new StringBuilder()).append(
                            "Unable to load DataSource for ID: ").append(id).toString());
                    dsToFree.add(ds);
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
                        Util.getDSMService().free(ds);
                }
            }
        } catch (Throwable e) {
            log.error("Exception while attempting to load a DataSource", e);
            throw new ServletException(DataUtil.getStackTrace(e));
        } finally {
            response.flushBuffer();

            requestTimer.stop();
        }

    }
}
