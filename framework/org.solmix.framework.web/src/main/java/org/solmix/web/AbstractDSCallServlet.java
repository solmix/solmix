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
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallInterceptor;
import org.solmix.api.call.DSCallManager;
import org.solmix.api.call.DSCallManagerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.fmk.SlxContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-5-10
 */

public abstract class AbstractDSCallServlet extends HttpServlet
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Logger log = LoggerFactory.getLogger(AbstractDSCallServlet.class);

    private DSCallManager dsCallManager;

    private String characterEncoding;

    private final SystemContext sc;

    public AbstractDSCallServlet()
    {
        this(null);
    }

    public AbstractDSCallServlet(SystemContext sc)
    {
        this.sc = sc;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String encode = config.getInitParameter("CharacterEncoding");
        if (encode != null)
            characterEncoding = encode.trim();
        else
            characterEncoding = "UTF-8";
        SystemContext sc = SlxContext.getThreadSystemContext();
        DSCallManagerFactory dmf = sc.getBean(DSCallManagerFactory.class);
        dsCallManager = dmf.createDSCallManager();
        @SuppressWarnings("rawtypes")
        Enumeration em = config.getInitParameterNames();
        DataTypeMap servletCfg = new DataTypeMap();
        while (em.hasMoreElements()) {
            String e = em.nextElement().toString();
            servletCfg.put(e, config.getInitParameter(e));
        }
        dsCallManager.setConfig(servletCfg);
        dsCallManager.addInterceptors(configuredInterceptors());
    }

    /**
     * @return
     */
    abstract DSCallInterceptor[] configuredInterceptors();

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
        try {
            DSCall dsc = dsCallManager.getDSCall(SlxContext.getWebContext());
            dsc.run();
        } catch (SlxException e) {
            log.error("request Exception: ", e);
        } finally {
            try {
                response.flushBuffer();
            } catch (IOException ignored) {
            }
        }

    }
}
