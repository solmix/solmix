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

package com.solmix.fmk.engine.internel.request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.solmix.api.request.RequestProgressTracker;
import com.solmix.api.request.SlxHttpServletRequest;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class SlxHttpServletRequestImpl extends HttpServletRequestWrapper implements SlxHttpServletRequest
{

    private RequestData requestData;

    private final String pathInfo;

    private String responseContentType;

    /**
     * @param request
     */
    public SlxHttpServletRequestImpl(RequestData requestData, HttpServletRequest request)
    {
        super(request);
        this.requestData = requestData;
        // prepare the pathInfo property
        String pathInfo = request.getServletPath();
        if (request.getPathInfo() != null) {
            pathInfo = pathInfo.concat(request.getPathInfo());
        }
        this.pathInfo = pathInfo;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.adapter.Adaptable#adaptTo(java.lang.Class)
     */
    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return getRequestData().adaptTo(this, type);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.request.SlxHttpServletRequest#getRequestProgressTracker()
     */
    @Override
    public RequestProgressTracker getRequestProgressTracker() {
        return getRequestData().getRequestProgressTracker();
    }

    /**
     * @return the requestData
     */
    public RequestData getRequestData() {
        return requestData;
    }

    /**
     * @param requestData the requestData to set
     */
    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

}
