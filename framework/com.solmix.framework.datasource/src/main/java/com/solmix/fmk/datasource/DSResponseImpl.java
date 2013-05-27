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

package com.solmix.fmk.datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.data.DSResponseData;
import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2011-1-3 solmix-ds
 */
@SuppressWarnings("unchecked")
public class DSResponseImpl implements DSResponse
{

    private static final Logger log = LoggerFactory.getLogger(DSResponseImpl.class);

    private DataSource dataSource;

    private DSResponseData data;

    public DSResponseImpl()
    {
        data = new DSResponseData();
        data.init();
    }

    public DSResponseImpl(Status status)
    {
        this();
        data.setStatus(status);
    }

    public DSResponseImpl(Object data)
    {
        this();
        this.data.setData(data);
    }

    public DSResponseImpl(Object data, Status status)
    {
        this();
        this.data.setData(data);
        this.data.setStatus(status);
    }

    public DSResponseImpl(DataSource dataSource)
    {
        this();
        setDataSource(dataSource);
    }

    public DSResponseImpl(DataSource dataSource,DSRequest request)
    {
        this();
        setDataSource(dataSource);
        if(request!=null){
            getContext().setOperationType(request.getContext().getOperationType().value());
        }
    }

    public DSResponseImpl(DataSource dataSource, Status status)
    {
        this(dataSource);
        data.setStatus(status);
    }

    public DSResponseImpl(DataSource dataSource, Object data)
    {
        this(dataSource);
        this.data.setData(data);
    }

    public DSResponseImpl(DataSource dataSource, Object data, Status status)
    {
        this(dataSource, data);
        this.data.setStatus(status);
    }

    /**
     * @return the dataSource
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the data
     */
    @Override
    public DSResponseData getContext() {
        return data;
    }

    /**
     * @param data the data to set
     */
    @Override
    public void setContext(DSResponseData data) {
        this.data = data;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * @see com.solmix.api.datasource.DSResponse#getJSResponse()
     */
    @Override
    public Object getJSResponse() throws SlxException {
        return data;
        // Map< String ,Object > __return = null;
        // try
        // {
        // __return = DataUtil.annotationFilter( data, ResponseData.class );
        // } catch ( Exception e )
        // {
        // throw new SlxException( Tmodule.DATASOURCE, Texception.DS_BEAN_FILTER_EXCEPTION, "bean filter exception,", e
        // );
        // }
        // try
        // {
        // __return = (Map) JSConvert.instance().convert(__return);
        // if (__return.get("status") != null)
        // __return.put("status", ((Status) __return.get("status")).value());
        // } catch (Exception e)
        // {
        // throw new SlxException(Tmodule.DATASOURCE, Texception.DS_BEAN_CONVERT_EXCEPTION,
        // "return data convert exception", e);
        // }
        // return __return;
    }

    @Override
    public Map<Object, Object> getRecord() {
        if (getDataSource() == null) {
            log.warn("Attempted to call getRecord() on a DSResponse with null DataSource");
            return null;
        }
        Object data = this.data.getData();
        if (data instanceof List<?>) {
            if (((List<?>) data).size() > 0)
                data = ((List<?>) data).get(0);
            else
                data = null;
        }
        Map<Object, Object> record = getDataSource().getProperties(data);
        return record;
    }

    @Override
    public List<Map<Object, Object>> getRecords() {
        if (getDataSource() == null) {
            log.warn("Attempted to call getRecord() on a DSResponse with null DataSource");
            return null;
        }
        List<Map<Object, Object>> target = new ArrayList<Map<Object, Object>>();
        Object data = this.data.getData();
        List<Object> sources;
        if (!(data instanceof List<?>)) {
            sources = new ArrayList<Object>();
            sources.add(data);
        } else {
            sources = (List<Object>) data;
        }
        for (Object source : sources) {
            if (source instanceof Map<?, ?>) {
                target.add((Map<Object, Object>) source);
            } else {
                target.add(getDataSource().getProperties(source));
            }
        }
        return target;
    }

    protected Map<String, Object> JSResponseTransform(Map<String, Object> origMap) {
        if (origMap.get("status") != null)
            origMap.put("status", ((Status) origMap.get("status")).value());
        return origMap;

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.datasource.DSResponse#getStatus()
     */
    @Override
    public Status getStatus() {
        return data.getStatus();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.datasource.DSResponse#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        Status _s = this.getStatus();
        return _s == Status.STATUS_SUCCESS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.datasource.DSResponse#setStatus(com.solmix.api.datasource.DSResponse.Status)
     */
    @Override
    public void setStatus(Status status) {
        data.setStatus(status);

    }

}
