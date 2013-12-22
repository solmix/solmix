
package org.solmix.fmk.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCManager;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.datasource.DsOp;

/**
 * Used with transaction operations.
 * 
 * @author solmix.f@gmail.com
 * @version 110082 2013-8-27
 */
public abstract class XAOp
{

    private static final Logger log = LoggerFactory.getLogger(DsOp.class);

    private String dataSourceName;

    private final Eoperation type;

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
                _dataSourceManager = getDataSourceManager();
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
            request.setCanJoinTransaction(true);
            request.setRPC(rpc);
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

    public DSRequest getRequest() {
        return this.request;
    }

    public abstract DSResponse exe(DSRequest request) throws SlxException;

    private DSCManager rpc;

    protected void setRpc(DSCManager rpc) {
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
            super(dataSourceName, type);
        }

        @Override
        public DSResponse exe(DSRequest request) throws SlxException {
            return request.execute();
        }
    }

    private DataSourceManager getDataSourceManager() {
        SystemContext sc = SlxContext.getThreadSystemContext();
        return sc.getBean(DataSourceManager.class);
    }
}
