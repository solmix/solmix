
package org.solmix.fmk.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.SlxContext.Op;
import org.solmix.runtime.SystemContext;

public abstract class DsOp<D> implements Op<D, SlxException>
{

    private static final Logger log = LoggerFactory.getLogger(DsOp.class);

    private String dataSourceName;

    private final Eoperation type;

    protected String opId;

    private DataSource dataSource;

    public DsOp(DataSource dataSource, Eoperation type)
    {
        this.dataSource = dataSource;
        this.type = type;
    }

    public DsOp(String dataSourceName, Eoperation type)
    {
        this.dataSourceName = dataSourceName;
        this.type = type;
    }

    protected DataSourceManager _dataSourceManager;

    @Override
    public D exe() throws SlxException {
        DSRequest request = null;
        try {
            _dataSourceManager = getDataSourceManager();
            if (dataSourceName != null && dataSource == null) {
                dataSource = _dataSourceManager.get(dataSourceName);
            }
            request = _dataSourceManager.createDSRequest();
            request.setDataSource(dataSource);
            request.setDataSourceName(dataSource.getName());
            if (opId != null)
                request.getContext().setOperation(opId);
            request.getContext().setOperationType(type);
        } catch (Exception e) {
            log.error("Find and instance Datasource:" + dataSourceName + " failed,Exception is", e);
        }

        D d = null;
        try {
            d = exe(request);
        } finally {
            if (dataSource != null) {
                _dataSourceManager.free(dataSource);
            }
        }
        return d;
    }

    public DsOp<D> withOpId(String opId) {
        this.opId = opId;
        return this;

    }

    protected DataSourceManager getDataSourceManager() {
        SystemContext sc = SlxContext.getThreadSystemContext();
        return sc.getExtension(DataSourceManager.class);
    }

    public abstract D exe(DSRequest request) throws SlxException;
}
