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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.solmix.api.call.DSCManager;
import org.solmix.api.call.HasDSCHandler;
import org.solmix.api.context.WebContext;
import org.solmix.api.data.DSResponseData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-10
 */

public class FChartResponse implements DSResponse, HasDSCHandler
{

    private final DSResponse resp;
    private final String characterEncoding;

    public FChartResponse(DSResponse response,String characterEncoding)
    {
        this.resp = response;
        this.characterEncoding=characterEncoding;
    }

    @Override
    public void handler(DSCManager rpc, DSRequest req, DSResponse resp) throws SlxException {
        WebContext ctx = rpc.getRequestContext();
        String orgEncode = ctx.getResponse().getCharacterEncoding();
        ctx.getResponse().setCharacterEncoding(characterEncoding);
        Writer _out = ctx.getOut();

        try {
            _out.write(resp.getContext().getData(String.class));
            _out.flush();
        } catch (IOException e) {
            throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
        } finally {
            ctx.getResponse().setCharacterEncoding(orgEncode);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getContext()
     */
    @Override
    public DSResponseData getContext() {
        return resp.getContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setContext(org.solmix.api.data.DSResponseData)
     */
    @Override
    public void setContext(DSResponseData dat) {
        resp.setContext(dat);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getDataSource()
     */
    @Override
    public DataSource getDataSource() {
        return resp.getDataSource();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setDataSource(org.solmix.api.datasource.DataSource)
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        resp.setDataSource(dataSource);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getJSResponse()
     */
    @Override
    public Object getJSResponse() throws SlxException {
        return resp.getJSResponse();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getRecord()
     */
    @Override
    public Map<Object, Object> getRecord() {
        return resp.getRecord();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getRecords()
     */
    @Override
    public List<Map<Object, Object>> getRecords() {
        return resp.getRecords();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getStatus()
     */
    @Override
    public Status getStatus() {
        return resp.getStatus();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setStatus(org.solmix.api.datasource.DSResponse.Status)
     */
    @Override
    public void setStatus(Status status) {
        resp.setStatus(status);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        return resp.isSuccess();
    }

}
