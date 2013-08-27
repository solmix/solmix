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

package org.solmix.fmk.datasource;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-12-25
 * @param <D>
 */

public abstract class AddOp<D> extends DsOp<D>
{

    private Object values;

    /**
     * @param dataSourceName
     */
    public AddOp(String dataSourceName)
    {
        super(dataSourceName, Eoperation.ADD);
    }

    public AddOp(DataSource dataSource)
    {
        super(dataSource, Eoperation.ADD);
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.fmk.datasource.DsOp#exe(org.solmix.api.datasource.DSRequest)
     */
    @Override
    public D exe(DSRequest request) throws SlxException {
        if (values != null)
            request.getContext().setValues(values);
        return add(request);
    }

    /**
     * @param request
     * @return
     */
    public abstract D add(DSRequest request) throws SlxException;

    public AddOp<D> withValues(Object values) {
        this.values = values;
        return this;

    }

    public static class DfAddOp extends AddOp<DSResponse>
    {

        public DfAddOp(String dataSourceName)
        {
            super(dataSourceName);
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SlxException
         * 
         * @see org.solmix.fmk.datasource.AddOp#add(org.solmix.api.datasource.DSRequest)
         */
        @Override
        public DSResponse add(DSRequest request) throws SlxException {
            return request.execute();
        }

    }
}
