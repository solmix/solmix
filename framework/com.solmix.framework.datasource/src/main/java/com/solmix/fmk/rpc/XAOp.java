
package com.solmix.fmk.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.datasource.DataSourceManager;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Eoperation;
import com.solmix.api.rpc.RPCManager;
import com.solmix.fmk.context.SlxContext;
import com.solmix.fmk.datasource.DsOp;

public abstract class XAOp
{

    private static final Logger log = LoggerFactory.getLogger(DsOp.class);

    private String dataSourceName;

    private Eoperation type;

    private Object criteria;

    private Object values;

    protected String opId;

    private DataSource dataSource;
    private DSRequest request;

    public XAOp(DataSource dataSource, Eoperation type)
    {
        this.dataSource = dataSource;
        this.type = type;
    }

    public XAOp(String dataSourceName, Eoperation type)
    {
        this.dataSourceName = dataSourceName;
        this.type = type;
    }

    protected DataSourceManager _dataSourceManager;

    public DSResponse exe() throws SlxException {

        try {
            if (dataSourceName != null && dataSource == null) {
                _dataSourceManager = SlxContext.getDataSourceManager();
                dataSource = _dataSourceManager.get(dataSourceName);
            }
            request = _dataSourceManager.createDSRequest();
            request.setDataSource(dataSource);
            if (opId != null)
                request.getContext().setOperation(opId);
            if (criteria != null)
                request.getContext().setCriteria(criteria);
            if (values != null)
                request.getContext().setValues(values);
            request.getContext().setOperationType(type);
            request.setJoinTransaction(true);
            request.setRpc(rpc);
        } catch (Exception e) {
            log.error("Find and instance Datasource:" + dataSourceName + " failed,Exception is" + e.getMessage());
        }
        DSResponse d = null;
        try {
            d = exe(request);
        } finally {
            if (dataSource != null) {
                _dataSourceManager.free(dataSource);
            }
        }
        return d;
    }
    
    public DSRequest getRequest(){
        return this.request;
    }

    public abstract DSResponse exe(DSRequest request) throws SlxException;

    private RPCManager rpc;

    protected void setRpc(RPCManager rpc) {
        this.rpc = rpc;
    }

    public XAOp withOpId(String opId) {
        this.opId = opId;
        return this;

    }

    public XAOp withCriteria(Object criteria) {
        this.criteria = criteria;
        return this;

    }

    public XAOp withValues(Object values) {
        this.values = values;
        return this;

    }
    
    public static class DfXAOp extends XAOp
    {

        public DfXAOp(String dataSourceName, Eoperation type)
        {
            super(dataSourceName,type);
        }

   

        @Override
        public DSResponse exe(DSRequest request) throws SlxException {
            return request.execute();
        }
    }
}
