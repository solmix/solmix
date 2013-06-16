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

package com.smartgwt.extensions.fusionchart.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DSResponse.Status;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Tobject;
import com.solmix.api.jaxb.ToperationBinding;
import com.solmix.fmk.util.XMLUtil;
import com.solmix.fmk.velocity.Velocity;

/**
 * 
 * @author ffz
 * @version 110035 2013-1-9
 */

public final class FChartDataSource
{

    public static final String TEMPLATE = "template";
    public static final String TEMPLATE_FILE="template-file";

    private static final Logger log =  LoggerFactory.getLogger(FChartDataSource.class.getName());

    public DSResponse fetch(DSRequest req, DataSource ds) throws SlxException {
        ToperationBinding bind = ds.getContext().getOperationBinding(req);
        Tobject object = bind.getConfiguration();
        Map<String, Object> map = XMLUtil.toMap(object);
        System.out.println(map.keySet());
        DSResponse response = ds.execute(req);
        Map context = Velocity.getStandardContextMap(req);
        context.put(Velocity.RESPONSE_DATA, response.getContext().getData());
        if (map.get(TEMPLATE_FILE) != null) {
            Object template = map.get(TEMPLATE_FILE);
            try {
                String returnValue =Velocity.evaluateTemplateFileAsString(template.toString(), context);
                if(returnValue!=null)
                    response.getContext().setData(returnValue);
            } catch (SlxException e) {
                response.setStatus(Status.STATUS_FAILURE);
                response.getContext().setData(e.getFullMessage());
            }
            
        }else if (map.get(TEMPLATE) != null) {
            Object template = map.get(TEMPLATE);
            String returnValue = Velocity.evaluateAsString(template.toString(), context);
            if (returnValue != null)
                response.getContext().setData(returnValue);
        }

        return new FChartResponse(response);

    }

}
