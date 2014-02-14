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

package com.smartgwt.extensions.fusionchart.server;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallWebInterceptor;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-27
 */

public class FusionChartInterceptor extends DSCallWebInterceptor
{

    private String characterEncoding;

    @Override
    public void configure(DataTypeMap config) {
        characterEncoding = config.getString("fchartEncoding", "UTF-8");
    }

    @Override
    public Action postInspect(DSCall dsCall, WebContext ctx) throws SlxException {
        List<DSRequest> reqs = dsCall.getRequests();
        DSResponse resp = dsCall.getResponse(reqs.get(0));
        if ("fchart".equals(resp.getHandlerName())) {
            String orgEncode = ctx.getResponse().getCharacterEncoding();
            ctx.getResponse().setCharacterEncoding(characterEncoding);
            Writer _out = ctx.getOut();
            
            try {
                _out.write(resp.getSingleResult(String.class));
                _out.flush();
            } catch (IOException e) {
                throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
            } finally {
                ctx.getResponse().setCharacterEncoding(orgEncode);
            }
            return Action.CANCELLED;
        }
        return Action.CONTINUE;

    }
}
