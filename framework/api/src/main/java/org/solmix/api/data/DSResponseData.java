/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.ResponseData;
import org.solmix.commons.util.DataUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-api
 */
@ResponseData
public class DSResponseData
{

    private static final Logger log = LoggerFactory.getLogger(DSResponseData.class.getName());

    public enum Type
    {
        REUTRN(0);

        private final int value;

        Type(int value)
        {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    @ResponseData
    private Boolean invalidateCache;

    @ResponseData
    private Boolean isDSResponse;

    @ResponseData
    private Integer startRow;

    @ResponseData
    private Integer endRow;

    @ResponseData
    private Integer totalRows;

    @ResponseData
    private Object[] errors;

    @ResponseData
    private Status status;

    @ResponseData
    private Object data;

    private Boolean isExport;

    private Boolean dropExtraFields;

    Long affectedRows;
    
    private String operationType;

    private boolean requestConnectionClose;

    public void init() {
        this.status = Status.UNSET;
        this.requestConnectionClose = false;
        this.invalidateCache = false;
        this.isDSResponse = true;
    }

   
    /**
     * @return the invalidateCache
     */
    public Boolean getInvalidateCache() {
        return invalidateCache;
    }

    /**
     * @param invalidateCache the invalidateCache to set
     */
    public void setInvalidateCache(Boolean invalidateCache) {
        this.invalidateCache = invalidateCache;
    }

    /**
     * @return the isDSResponse
     */
    public Boolean getIsDSResponse() {
        return isDSResponse;
    }

    /**
     * @param isDSResponse the isDSResponse to set
     */
    public void setIsDSResponse(Boolean isDSResponse) {
        this.isDSResponse = isDSResponse;
    }

    /**
     * @return the startRow
     */
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow the startRow to set
     */
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public boolean statusIsError() {
        return status.value() < 0;
    }

    /**
     * @return the endRow
     */
    public Integer getEndRow() {
        return endRow;
    }

    /**
     * @param endRow the endRow to set
     */
    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    /**
     * @return the totalRows
     */
    public Integer getTotalRows() {
        return totalRows;
    }

    /**
     * @param totalRows the totalRows to set
     */
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

   

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Return the data as the type <code>T</code>.
     * 
     * @param type
     * @return
     */
    public <T> T getData(Class<T> type) {
        return _getData(type, getData());
    }

    @SuppressWarnings("unchecked")
    protected <T> T _getData(Class<T> type, Object data) {
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
                if (data instanceof Map<?, ?>) {
                    Object instance = type.newInstance();
                    DataUtils.setProperties((Map<?, ?>) data, instance, false);
                    return (T) instance;
                } else if (data instanceof List<?>) {
                    List<Object> datas = (List<Object>) data;
                    int size=datas.size();
                    if(size>0){
                        Object one=datas.get(0);
                        T _return=null;
                        if(one.getClass().isAssignableFrom(type)){
                            _return= (T) one;
                        }else if(one instanceof Map<?, ?>){
                            _return = type.newInstance();
                            DataUtils.setProperties((Map<?, ?>)one, _return, false);
                        }
                        if(size>1){
                            log.warn("The data is more than one map or bean, used the first one and drop other " + (datas.size() - 1) + "(s)");
                        }
                        return _return;
                        
                    }else{
                        log.warn("The data is List is empty ,return object is null "); 
                        return null;
                    }
                   
                } else {
                    return (T) DataUtils.castValue(data, type);
                }
            } catch (Exception ee) {
                log.debug((new StringBuilder()).append("Tried to convert inbound nested Map to: ").append(type.getName()).append(
                    " but DataTools.setProperties() on instantiated class failed").append(" with the following error: ").append(ee.getMessage()).toString());
            }
        }
        throw new IllegalArgumentException((new StringBuilder()).append("Can't convert value of type ").append(data.getClass().getName()).append(
            " to target type ").append(type.getName()).toString());
    }

    @JsonIgnore
    public <T> List<T> getDataList(Class<T> type) {
        Object data = getData();
        List<T> _return = new ArrayList<T>();
        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                _return.add(_getData(type, obj));

            }
        }else {
            try {
                _return.add(DataUtils.convertType(type, data));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return _return;
    }
    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    /**
     * @return the isExport
     */
    @JsonIgnore
    public Boolean getIsExport() {
        return isExport;
    }

    /**
     * @param isExport the isExport to set
     */
    public void setIsExport(Boolean isExport) {
        this.isExport = isExport;
    }

    /**
     * @return the dropExtraFields
     */
    public Boolean getDropExtraFields() {
        return dropExtraFields;
    }

    /**
     * @param dropExtraFields the dropExtraFields to set
     */
    public void setDropExtraFields(Boolean dropExtraFields) {
        this.dropExtraFields = dropExtraFields;
    }

    /**
     * @return the affectedRows
     */
    public Long getAffectedRows() {
        return affectedRows;
    }

    /**
     * @param affectedRows the affectedRows to set
     */
    public void setAffectedRows(Long affectedRows) {
        this.affectedRows = affectedRows;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the errors
     */
    public Object[] getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(Object[] errors) {
        this.errors = errors;
    }

    /**
     * @return the requestConnectionClose
     */
    public boolean isRequestConnectionClose() {
        return requestConnectionClose;
    }

    /**
     * @param requestConnectionClose the requestConnectionClose to set
     */
    public void setRequestConnectionClose(boolean requestConnectionClose) {
        this.requestConnectionClose = requestConnectionClose;
    }

    
    public String getOperationType() {
        return operationType;
    }

    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

}
