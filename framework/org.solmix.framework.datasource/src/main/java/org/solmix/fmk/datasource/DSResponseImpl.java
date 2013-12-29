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

package org.solmix.fmk.datasource;

import static org.solmix.commons.util.DataUtil.makeListIfSingle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.commons.util.DataUtil;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-1-3 solmix-ds
 */
@SuppressWarnings("unchecked")
public class DSResponseImpl implements DSResponse
{

    private static final Logger log = LoggerFactory.getLogger(DSResponseImpl.class);

    private DataSource dataSource;

    private Object rawData;

    private Integer startRow;

    private Integer endRow;

    private Integer totalRows;

    private Object[] errors;

    private Status status;

    private Long affectedRows;

    private boolean invalidateCache;

    public DSResponseImpl()
    {
    }

    public DSResponseImpl(Status status)
    {
        this();
        setStatus(status);
    }

    public DSResponseImpl(Object data)
    {
        this();
        setRawData(data);
    }

    public DSResponseImpl(Object data, Status status)
    {
        this();
        setRawData(data);
        setStatus(status);
    }

    public DSResponseImpl(DataSource dataSource)
    {
        this();
        setDataSource(dataSource);
    }

    public DSResponseImpl(DataSource dataSource, Status status)
    {
        this(dataSource);
        setStatus(status);
    }

    public DSResponseImpl(DataSource dataSource, Object data)
    {
        this(dataSource);
        setRawData(data);
    }

    public DSResponseImpl(DataSource dataSource, Object data, Status status)
    {
        this(dataSource, data);
        setStatus(status);
    }

    /**
     * @return the startRow
     */
    @Override
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow the startRow to set
     */
    @Override
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    /**
     * @return the endRow
     */
    @Override
    public Integer getEndRow() {
        return endRow;
    }

    /**
     * @param endRow the endRow to set
     */
    @Override
    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    /**
     * @return the totalRows
     */
    @Override
    public Integer getTotalRows() {
        return totalRows;
    }

    /**
     * @param totalRows the totalRows to set
     */
    @Override
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    /**
     * @return the errors
     */
    @Override
    public Object[] getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    @Override
    public void setErrors(Object... errors) {
        this.errors = errors;
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

    @Override
    public Map<Object, Object> getSingleRecord() {
        if (getDataSource() == null) {
            throw new java.lang.IllegalStateException("Attempted to call getSingleRecord() on a DSResponse with null DataSource");
        }
        Object singleData = null;
        if (rawData instanceof List<?>) {
            if (((List<?>) rawData).size() > 0)
                singleData = ((List<?>) rawData).get(0);
            else
                singleData = null;
        } else {
            singleData = rawData;
        }
        Map<Object, Object> record = getDataSource().getProperties(singleData);
        return record;
    }

    @Override
    public List<Map<Object, Object>> getRecordList() {
        if (getDataSource() == null) {
            throw new java.lang.IllegalStateException("Attempted to call getSingleRecord() on a DSResponse with null DataSource");
        }
        List<Map<Object, Object>> target = new ArrayList<Map<Object, Object>>();
        List<Object> sources = makeListIfSingle(rawData);
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
     * @see org.solmix.api.datasource.DSResponse#getStatus()
     */
    @Override
    public Status getStatus() {
        return this.status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        Status _s = this.getStatus();
        return _s == Status.STATUS_SUCCESS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setStatus(org.solmix.api.datasource.DSResponse.Status)
     */
    @Override
    public void setStatus(Status status) {
        this.status = status;

    }

    /**
     * @return the rawData
     */
    @Override
    public Object getRawData() {
        return rawData;
    }

    /**
     * @param rawData the rawData to set
     */
    @Override
    public void setRawData(Object rawData) {
        this.rawData = rawData;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getSingleResult(java.lang.Class)
     */
    @Override
    public <T> T getSingleResult(Class<T> type) {
        return _getResult(type,rawData);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getResultList(java.lang.Class)
     */
    @Override
    public <T> List<T> getResultList(Class<T> type) {
        List<T> _return = new ArrayList<T>();
        if (List.class.isAssignableFrom(rawData.getClass())) {
            for (Object obj : List.class.cast(rawData)) {
                _return.add(_getResult(type, obj));

            }
        }else {
            try {
                _return.add(DataUtil.convertType(type, rawData));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return _return;
    }

    protected <T> T _getResult(Class<T> type, Object data) {
        if (data == null)
            return null;
        if (type.isInstance(data))
            return (T) data;
        // First, assume that the type is Map.
        if (Map.class.isAssignableFrom(type)) {
            if (data instanceof List<?>) {
                if (((List<?>) data).size() == 0) {
                    return null;
                } else if (((List<?>) data).get(0) instanceof Map<?, ?>) {
                    return (T) ((List<?>) data).get(0);
                }
            } else if (data instanceof Map<?, ?>) {
                return (T) data;
            }
            // Then,assume that the type is List.
        } else if (List.class.isAssignableFrom(type)) {
            if (data instanceof List<?>) {
                return (T) data;
            } else {
                List<Object> re = new ArrayList<Object>();
                re.add(data);
                return (T) re;
            }
        } else if (!type.isPrimitive() && !type.isInterface() && !type.isArray()) {
            try {
                if (Map.class.isAssignableFrom(data.getClass())) {
                    Object instance = type.newInstance();
                    DataUtil.setProperties(Map.class.cast(data), instance, false);
                    return (T) instance;
                } else if (List.class.isAssignableFrom(data.getClass())) {
                    List<Object> datas = List.class.cast(data);
                    int size = datas.size();
                    if (size > 0) {
                        Object one = datas.get(0);
                        T _return = null;
                        if (type.isAssignableFrom(one.getClass())) {
                            _return = type.cast(one);
                        } else if (Map.class.isAssignableFrom(one.getClass())) {
                            _return = type.newInstance();
                            DataUtil.setProperties((Map<?, ?>) one, _return, false);
                        }
                        if (size > 1) {
                            log.warn("The data is more than one map or bean, used the first one and drop other " + (datas.size() - 1) + "(s)");
                        }
                        return _return;

                    } else {
                        log.warn("The data is List is empty ,return object is null ");
                        return null;
                    }

                } else {
                    return  DataUtil.convertType(type, data);
                }
            } catch (Exception ee) {
                log.debug((new StringBuilder()).append("Tried to convert inbound nested Map to: ").append(type.getName()).append(
                    " but DataTools.setProperties() on instantiated class failed").append(" with the following error: ").append(ee.getMessage()).toString());
            }
        }
        throw new IllegalArgumentException((new StringBuilder()).append("Can't convert value of type ").append(data.getClass().getName()).append(
            " to target type ").append(type.getName()).toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setAffectedRows(java.lang.Long)
     */
    @Override
    public void setAffectedRows(Long affectedRows) {
        this.affectedRows=affectedRows;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getAffectedRows(java.lang.Long)
     */
    @Override
    public Long getAffectedRows(Long long1) {
        return affectedRows;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#setInvalidateCache(boolean)
     */
    @Override
    public void setInvalidateCache(boolean invalidateCache) {
        this.invalidateCache=invalidateCache;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSResponse#getInvalidateCache()
     */
    @Override
    public boolean getInvalidateCache() {
        return invalidateCache;
    }
}
