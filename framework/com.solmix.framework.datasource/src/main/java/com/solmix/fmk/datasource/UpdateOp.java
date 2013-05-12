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

package com.solmix.fmk.datasource;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-25
 * @param <D>
 */

public abstract class UpdateOp<D> extends DsOp<D>
{

    private Object criteria;

    private Object values;

    /**
     * @param dataSourceName
     * @param type
     */
    public UpdateOp(String dataSourceName)
    {
        super(dataSourceName, Eoperation.UPDATE);
    }
    public UpdateOp(DataSource dataSource)
    {
        super(dataSource, Eoperation.UPDATE);
    }
    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.datasource.DsOp#exe(com.solmix.api.datasource.DSRequest)
     */
    @Override
    public D exe(DSRequest request) throws SlxException {
        if (criteria != null)
            request.getContext().setCriteria(criteria);
        if (values != null)
            request.getContext().setValues(values);
        return update(request);
    }

    public UpdateOp<D> withCriteria(Object criteria) {
        this.criteria = criteria;
        return this;

    }

    public UpdateOp<D> withValues(Object values) {
        this.values = values;
        return this;

    }

    /**
     * @param request
     * @return
     */
    public abstract D update(DSRequest request) throws SlxException;

    public static class DfUpdateOp extends UpdateOp<DSResponse>
    {

        public DfUpdateOp(String dataSourceName)
        {
            super(dataSourceName);
        }

        /**
         * {@inheritDoc}
         * 
         * @see com.solmix.fmk.datasource.UpdateOp#update(com.solmix.api.datasource.DSRequest)
         */
        @Override
        public DSResponse update(DSRequest request) throws SlxException {
            return request.execute();
        }

    }
}
