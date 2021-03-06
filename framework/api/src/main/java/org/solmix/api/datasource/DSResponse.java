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

package org.solmix.api.datasource;

import java.util.List;
import java.util.Map;

import org.solmix.api.call.ResponseType;

/**
 * 
 * @version 110035
 */
public interface DSResponse extends ResponseType
{

    /**
     * Define the return Status of Datasource.
     * 
     * @author solmix.f@gmail.com
     * @version 110035 2011-3-20
     */
    public enum Status
    {
        /**
       * 
       */
        STATUS_SUCCESS(0) ,
        UNSET(1) ,
        STATUS_FAILURE(-1) ,
        STATUS_AUTHORIZATION_FAILURE(-3) ,
        STATUS_VALIDATION_ERROR(-4) ,
        STATUS_LOGIN_INCORRECT(-5) ,
        STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED(-6) ,
        STATUS_LOGIN_REQUIRED(-7) ,
        STATUS_LOGIN_SUCCESS(-8) ,
        UPDATE_WITHOUT_PK(-9) ,
        STATUS_TRANSACTION_FAILED(-10);

        private final int value;

        Status(int value)
        {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static Status fromValue(int v) {
            for (Status c : Status.values()) {
                if (c.value == v) {
                    return c;
                }
            }
            throw new IllegalArgumentException("illegal dsresponse status");
        }
    }

    /**
     * Return the DataSource
     * 
     * @return
     */
    DataSource getDataSource();

    /**
     * Set the datasource of current datasource response instance.
     * 
     * @param dataSource
     */
    void setDataSource(DataSource dataSource);

    /**
     * filter data by {@link org.solmix.api.datasource.DataSource#getProperties(Object)} if need original data ,use
     * <code> getContext().getData()</code>
     * 
     * @return
     */
    Map<Object, Object> getSingleRecord();

    /**
     * filter data by {@link org.solmix.api.datasource.DataSource#getProperties(Object)} if need original data ,use
     * <code> getContext().getData()</code>
     * 
     * @return
     */
    List<Map<Object, Object>> getRecordList();

    Status getStatus();

    void setStatus(Status status);

    boolean isSuccess();

    public Object getRawData();

    /**
     * Setting the raw data to this response.the raw data may be transformed by {@link #getSingleRecord()},
     * {@link #getRecordList()},{@link #getSingleResult(Class)},{@link #getRecordList()}.
     * 
     * @param rawData the rawData to set
     */
    public void setRawData(Object rawData);

    public <T> T getSingleResult(Class<T> type);

    public <T> List<T> getResultList(Class<T> type);

    /**
     * @return
     */
    Object[] getErrors();

    /**
     * @param errors
     */
    void setErrors(Object... errors);

    /**
     * @return
     */
    Integer getTotalRows();

    /**
     * @param totalRows
     */
    void setTotalRows(Integer totalRows);

    /**
     * @return
     */
    Integer getEndRow();

    /**
     * @param endRow
     */
    void setEndRow(Integer endRow);

    /**
     * @return
     */
    Integer getStartRow();

    /**
     * @param startRow
     */
    void setStartRow(Integer startRow);

    /**
     * @param long1
     */
    void setAffectedRows(Long long1);
    
    
    Long getAffectedRows(Long long1);

    /**
     * @param _invalidateCache
     */
    void setInvalidateCache(boolean _invalidateCache);

    /**
     * @return
     */
    boolean getInvalidateCache();

    /**
     * @param value
     */
    void setOperationType(String value);

    /**
     * @return
     */
    String getOperationType();
    
    void setHandlerName(String handlerName);
    
    String getHandlerName();
}
