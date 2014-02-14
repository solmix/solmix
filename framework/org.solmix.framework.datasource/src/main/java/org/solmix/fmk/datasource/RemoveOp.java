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
package org.solmix.fmk.datasource;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;


public abstract class RemoveOp<D> extends DsOp<D>
{
    public RemoveOp(String dataSourceName)
    {
        super(dataSourceName, Eoperation.REMOVE);
    }

    public RemoveOp(DataSource dataSource)
    {
        super(dataSource, Eoperation.REMOVE);
    }

    private Object criteria;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.fmk.datasource.DsOp#exe(org.solmix.api.datasource.DSRequest)
     */
    @Override
    public D exe(DSRequest request) throws SlxException {
        if (criteria != null)
            request.getContext().setCriteria(criteria);
        return remove(request);
    }

    /**
     * @param request
     * @return
     */
    public abstract D remove(DSRequest request) throws SlxException;

    public RemoveOp<D> withCriteria(Object criteria) {
        this.criteria = criteria;
        return this;

    }

    public static class DfRemoveOp extends RemoveOp<DSResponse>
    {

        public DfRemoveOp(String dataSourceName)
        {
            super(dataSourceName);
        }
        public DfRemoveOp(DataSource datasource)
        {
            super(datasource);
        }
     
        @Override
        public DSResponse remove(DSRequest request) throws SlxException {
            return request.execute();
        }

    }
}
