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

package org.solmix.sgt.server;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Administrator
 * @version 110035 2012-12-19
 */

public class ConfigBean
{

    public static final String OP_ID = "_operaionId";

    public static final String CRITERIA_PREFIX = "_c.";

    public static final String VALUES_PREFIX = "_v.";
    private RequestType requestType;

    private String dataSourceName;

    private String operationType;

    private String operationId;

    private Map<String, String> criteria;

    private Map<String, String> values;

    public RequestType getTRequest() {
		return requestType;
	}

    public ConfigBean(){
    	
    }
	public void setTRequest(RequestType requestType) {
		this.requestType = requestType;
	}

	/**
     * @param datasource
     * @param type
     */
    public ConfigBean(String datasource, String type)
    {
        this.dataSourceName = datasource;
        this.operationType = type;
    }

    /**
     * @return the dataSourceName
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * @param dataSourceName the dataSourceName to set
     */
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    /**
     * @return the operationType
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Map<String, String> getCriteria() {
        if (criteria == null)
            criteria = new HashMap<String, String>();
        return criteria;
    }

    public Map<String, String> getValues() {
        if (values == null)
            values = new HashMap<String, String>();
        return values;
    }

}
