
package com.solmix.fmk.datasource;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;

public abstract class FetchOp<D> extends DsOp<D>
{

    public FetchOp(String dataSourceName)
    {
        super(dataSourceName, Eoperation.FETCH);
    }
    public FetchOp(DataSource dataSource)
    {
        super(dataSource, Eoperation.FETCH);
    }
    private Object criteria;

    @Override
    public D exe(DSRequest request) throws SlxException {
        if (criteria != null)
            request.getContext().setCriteria(criteria);
        return fetch(request);
    }

    public abstract D fetch(DSRequest request) throws SlxException;

    public FetchOp<D> withCriteria(Object criteria) {
        this.criteria = criteria;
        return this;

    }

    public static class DfFetchOp extends FetchOp<DSResponse>
    {

        public DfFetchOp(String dataSourceName)
        {
            super(dataSourceName);
        }

        @Override
        public DSResponse fetch(DSRequest request) throws SlxException {
            return request.execute();
        }
    }
}