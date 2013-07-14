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

package org.solmix.api.datasource;

import java.util.List;
import java.util.Map;

import org.solmix.api.data.DSResponseData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.rpc.ResponseType;

/**
 * 
 * @version 110035
 */
public interface DSResponse extends ResponseType
{

    /**
     * Define the return Status of Datasource.
     * 
     * @author solomon
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
     * Return datasource response context;
     * 
     * @return
     */
    DSResponseData getContext();

    /**
     * Set the Datasource Response context.
     * 
     * @param dat
     */
    void setContext(DSResponseData dat);

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
     * get the response values which will be send to client as JSON format.
     * 
     * @return
     * @throws SlxException
     */
    Object getJSResponse() throws SlxException;

    /**
     * get the record form {@link org.solmix.api.data.DSResponseData#getData() getData()} and filter data by
     * {@link org.solmix.api.datasource.DataSource#getProperties(Object)} if need original data ,use
     * <code> getContext().getData()</code>
     * 
     * @return
     */
    Map<Object, Object> getRecord();

    /**
     * get the record form {@link org.solmix.api.data.DSResponseData#getData() getData()} and filter data by
     * {@link org.solmix.api.datasource.DataSource#getProperties(Object)} if need original data ,use
     * <code> getContext().getData()</code>
     * 
     * @return
     */
    List<Map<Object, Object>> getRecords();

    Status getStatus();

    void setStatus(Status status);

    boolean isSuccess();
}
