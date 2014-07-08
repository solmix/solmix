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

import static org.solmix.commons.util.DataUtils.booleanValue;
import static org.solmix.web.ServletTools.encodeParameter;
import static org.solmix.web.ServletTools.mimeTypeForContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallWebInterceptor;
import org.solmix.api.call.InterceptorOrder;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.IOUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-24
 */

public class DownloadInterceptor extends DSCallWebInterceptor
{

    private static final Logger LOG = LoggerFactory.getLogger(DownloadInterceptor.class);

    @Override
    public Action postInspect(DSCall dsCall,WebContext context) throws SlxException {
        List<DSRequest> requests = dsCall.getRequests();
        if (requests != null && !requests.isEmpty()) {
            if (requests.size() > 1)
                LOG.warn("DownLoad DSRequest must be single .");
            DSRequest req = requests.get(0);
            if (booleanValue(req.getContext().getIsDownload())) {
                String mimeType = null;
                try {
                    mimeType = mimeTypeForContext(context);
                    if (mimeType != null)
                        context.setContentType(mimeType);
                } catch (Exception e) {
                    throw new SlxException(Tmodule.SERVLET, Texception.SERVLET_MIME_TYPE_ERROR, e);
                }
                DSResponse res= dsCall.getResponse(req);
                Map<?, ?> data =  res.getSingleRecord();
                String fileName = req.getContext().getDownloadFileName();
                String fieldName = req.getContext().getDownloadFieldName();
                long contentLength = Long.valueOf(data.get((new StringBuilder()).append(fieldName).append("_filesize").toString()).toString()).longValue();
                InputStream is = (InputStream) data.get(fieldName);
                String fileNameEncoding = encodeParameter("fileName", fileName);
                if (req.getContext().getOperationType() == Eoperation.DOWNLOAD_FILE)
                    context.getResponse().addHeader("content-disposition",
                        (new StringBuilder()).append("attachment; ").append(fileNameEncoding).toString());
                else
                    context.getResponse().addHeader("content-disposition",
                        (new StringBuilder()).append("inline; ").append(fileNameEncoding).toString());
                context.getResponse().setContentLength((int) contentLength);
                try {
                    OutputStream os = context.getResponse().getOutputStream();
                    IOUtils.copyStreams(is, os);
                    os.flush();
                } catch (IOException e) {
                    throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
                }
                return Action.CANCELLED;
            }
            
        }
        return Action.CONTINUE;
    }
    @Override
    public PRIORITY priority() {
        return InterceptorOrder.BEFORE_DEFAULT;
    }
}
