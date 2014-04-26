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

import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;

/**
 * 
 * @version 110035
 */
public interface DataSource extends FreeResourcesHandler
{

    /**
     * this datasource name.
     * 
     * @return
     */
    String getName();

    /**
     * DataSource provider type.
     * <p>
     * <b>NOTE:</b> this type name must the same with service filter name.like：filter = (org.solmix.service.ds.provider
     * = name)
     * 
     * @return
     */
    String getServerType();

    /**
     * This attribute only can initialized at {@link #init(DataSourceData) init(data)}. so no <code>Set</code> method.
     * 
     * @return
     */
    DataSourceData getContext();

    DSResponse execute(DSRequest req) throws SlxException;

    DSResponse execute(Eoperation operationBindingType, String operationBindingID) throws SlxException;

    void clearState();

    /**
     * @param realFieldName
     * @param value
     * @return
     */
    boolean hasRecord(String realFieldName, Object value);

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    boolean shouldAutoJoinTransaction(DSRequest req) throws SlxException;

    /**
     * @param req
     * @return
     * @throws Exception
     */
    Object getTransactionObject(DSRequest req) throws SlxException;

    /**
     * @return
     * @throws SlxException
     */
    String getTransactionObjectKey() throws SlxException;

    /**
     * custom transform field value,If you want to transform field value satisfied yourself purpose,you can override the
     * method in sub class.
     * 
     * @param field
     * @param obj
     * @return
     */
    Object transformFieldValue(Tfield field, Object obj);

    /**
     * return Datasource Before-processor,if no set return the default processor.Called by DataSource processor.
     * 
     * @param data
     * @return
     * @throws SlxException
     */
    DataSource instance(DataSourceData data) throws SlxException;

    /**
     * @param value
     * @param field
     * @return
     */
    String escapeValue(Object value, Object field);

    /**
     * filter values
     * 
     * @param data
     * @return
     */
    Map<Object, Object> getProperties(Object data);

    /**
     * @param id
     * @return
     * @throws Exception
     */
    Object fetchById(Object id) throws Exception;

    /**
     * @param whereStructure
     * @return
     */
    boolean isAdvancedCriteria(Map<String, ?> whereStructure);

    /**
     * @param data
     * @throws SlxException
     */
    void init(DataSourceData data) throws SlxException;

    /**
     * @param context
     */
    void setContext(DataSourceData context);

    /**
     * @return
     */
    DataSourceGenerator getDataSourceGenerator();

    /**
     * @param dataSourceGenerator
     */
    void setDataSourceGenerator(DataSourceGenerator dataSourceGenerator);

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    DSResponse validateDSRequest(DSRequest req) throws SlxException;

    /**
     * @param _opType
     * @return
     */
    String getAutoOperationId(Eoperation _opType);

    /**
     * conert the datasource context to client maps.
     * 
     * @return
     */
    Map<String, ?> toClientValueMap();

}
