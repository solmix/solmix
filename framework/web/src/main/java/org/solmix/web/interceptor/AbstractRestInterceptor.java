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

package org.solmix.web.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.solmix.api.call.DSCallWebInterceptor;
import org.solmix.api.datasource.DSResponse;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.internal.DatasourceCM;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月25日
 */

public class AbstractRestInterceptor extends DSCallWebInterceptor
{

    public static final String JSON = "json";

    public static final String XML = "xml";

    public static final String PAYLOAD_NAME = "__payload";

    public static final String DATAFORMAT = "_dataFormat";

    public static final String PROTOCOL = "_protocol";

    public static final String PROTOCOL_POSTXML = "postXML";

    public static final String PROTOCOL_PARAM = "param";

    public static final String PROTOCOL_POSTMESSAGE = "postMessage";

    public static final String DATASTYLE = "_dataStyle";

    protected String charset;

    protected boolean showClientOutPut;

    @Override
    public void configure(DataTypeMap config) {
        charset = config.getString("defaultCharset", "UTF-8");
        showClientOutPut = config.getBoolean(DatasourceCM.P_SHOW_CLIENT_OUTPUT,
            false);
    }

    protected RestResponseData getClientResponse(DSResponse res) {
        RestResponseData _return = new RestResponseData();
        _return.setStatus(res.getStatus().value());
        if(res.getEndRow()!=null)
        _return.setEndRow(res.getEndRow());
        if(res.getErrors()!=null)
        _return.setErrors(res.getErrors());
        _return.setInvalidateCache(res.getInvalidateCache());
        _return.setIsDSResponse(true);
        if(res.getStartRow()!=null)
        _return.setStartRow(res.getStartRow());
        if(res.getTotalRows()!=null)
        _return.setTotalRows(res.getTotalRows());
        Object rawData = res.getRawData();
        if (rawData != null) {
            if (DataUtil.isArray(rawData)) {
                _return.setData(res.getRecordList());
            } else {
                _return.setData(res.getSingleRecord());
            }
        }

        return _return;

    }

    public String getDataformat(HttpServletRequest request) {
        String queryString = request.getParameter(DATAFORMAT);
        if (queryString == null)
            return "json";
        else
            return queryString;
    }
}
