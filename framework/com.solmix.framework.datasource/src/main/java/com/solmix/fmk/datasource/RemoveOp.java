package com.solmix.fmk.datasource;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;


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
     * @see com.solmix.fmk.datasource.DsOp#exe(com.solmix.api.datasource.DSRequest)
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

     
        @Override
        public DSResponse remove(DSRequest request) throws SlxException {
            return request.execute();
        }

    }
}
