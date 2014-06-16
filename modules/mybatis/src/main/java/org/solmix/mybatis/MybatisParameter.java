/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.mybatis;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.sql.SQLDriver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月15日
 */

public class MybatisParameter
{

    private final DSRequest request;

    private final DSResponse response;

    private final Object criteria;
    
    private final SQLDriver sqlDriver;
    
    private final boolean canPage;

    MybatisParameter(DSRequest request, DSResponse response, Object criteria,SQLDriver sqlDriver,boolean canPage)
    {
        this.request = request;
        this.response = response;
        this.criteria = criteria;
        this.sqlDriver=sqlDriver;
        this.canPage=canPage;
    }
    
    /**
     * @return the canPage
     */
    public boolean isCanPage() {
        return canPage;
    }


    /**
     * @return the sqlDriver
     */
    public SQLDriver getSqlDriver() {
        return sqlDriver;
    }

    /**
     * @return the request
     */
    public DSRequest getRequest() {
        return request;
    }

    /**
     * @return the response
     */
    public DSResponse getResponse() {
        return response;
    }

    /**
     * @return the criteria
     */
    public Object getCriteria() {
        return criteria;
    }

}
