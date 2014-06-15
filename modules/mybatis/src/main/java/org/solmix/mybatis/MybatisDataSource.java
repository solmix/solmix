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

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallCompleteCallback;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSRequestData;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.util.DataTools;
import org.solmix.runtime.SystemContext;
import org.solmix.sql.SQLDriver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月12日
 */

public class MybatisDataSource extends BasicDataSource implements DataSource,
    DSCallCompleteCallback
{

    private final static Logger log = LoggerFactory.getLogger(MybatisDataSource.class.getName());

    public static final String SERVICE_PID = "org.solmix.modules.mybatis";

    private SqlSessionFactoryProvider sqlSessionFactoryProvider;

    private SqlSession session;

    private SQLDriver sqlDriver;

    private String dbName;

    public MybatisDataSource(final SystemContext sc)
    {
        setSystemContext(sc);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCallCompleteCallback#onSuccess(org.solmix.api.call.DSCall)
     */
    @Override
    public void onSuccess(DSCall call) throws SlxException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCallCompleteCallback#onFailure(org.solmix.api.call.DSCall,
     *      boolean)
     */
    @Override
    public void onFailure(DSCall call, boolean flag) throws SlxException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getServerType() {
        return EserverType.MYBATIS.value();
    }

    public void destroy() {
        if (log.isTraceEnabled())
            log.trace("MybatisDataSource:" + this.getContext().getName()
                + " destroying!");
    }

    @Override
    public void init(DataSourceData data) throws SlxException {
        super.init(data);
    }

    @Override
    public DSResponse execute(DSRequest req) throws SlxException {
        req.registerFreeResourcesHandler(this);
        Eoperation _opType = req.getContext().getOperationType();
        DSResponse __return = null;
        if (isMybatisOperation(_opType)) {
            DSResponse validationFailure = validateDSRequest(req);
            if (validationFailure != null) {
                return validationFailure;
            }
            // if DSRequest not have a DataSource with it,use this by default.
            if (req.getDataSource() == null && req.getDataSourceName() == null) {
                req.setDataSource(this);
            }
            req.setRequestStarted(true);
            Object dsObject = null;
            Object datasources = req.getContext().getDataSourceNames();
            // may be have other datasource.if just one,used as SQL datasource.
            if (datasources != null
                && (datasources instanceof List<?> && ((List<?>) datasources).size() > 1)) {
                dsObject = datasources;
            } else {
                dsObject = this;
            }
            __return = executeMybatisDataSource(req, dsObject);
        } else {
            __return = super.execute(req);
        }

        return __return;

    }

    /**
     * @param req
     * @param dsObject
     * @return
     * @throws SlxException
     */
    private DSResponse executeMybatisDataSource(DSRequest req, Object dsObject)
        throws SlxException {
        MybatisDataSource mybatis;
        if (dsObject instanceof MybatisDataSource) {
            mybatis = (MybatisDataSource) dsObject;
        } else if (dsObject instanceof String) {
            mybatis = getDataSource((String) dsObject);
        } else {
            throw new SlxException(
                Tmodule.DATASOURCE,
                Texception.DS_DSCONFIG_ERROR,
                "in the app operation config, datasource must be set to a string or MybatisDataSource ");
        }
        if (req.getDSCall() != null && this.shouldAutoJoinTransaction(req)) {
            Object obj = this.getTransactionObject(req);
            if (!(obj instanceof SqlSession)) {
                if (log.isWarnEnabled())
                    log.warn("Mybatis DataSource transaction  should be a org.apache.ibatis.session.SqlSession instance,but is"
                        + obj.getClass().getName()
                        + " Assume the transaction object is invalid and set it to null");
                session = null;
            } else {
                session = (SqlSession) obj;
            }
            if (session == null) {
                SqlSession sqlSession = getSqlSession(false);
                if (shouldAutoStartTransaction(req, false)) {
                    session = sqlSession;
                    req.getDSCall().setAttribute(getTransactionObjectKey(),
                        sqlSession);
                    req.getDSCall().registerCallback(this);
                } else {
                    session = sqlSession;
                }
            }
            req.setJoinTransaction(true);
        } else {
            session = getSqlSession(true);
        }
        DSRequestData __requestCX = req.getContext();
        Eoperation _req = __requestCX.getOperationType();
        DSResponse __return = null;
        switch (_req) {
            case ADD:
                __return = executeAdd(req, mybatis);
                break;
            case FETCH:
                __return = executefetch(req, mybatis);
                break;
            case REMOVE:
                __return = executeRemove(req, mybatis);
                break;
            case UPDATE:
                __return = executeUpdate(req, mybatis);
                break;
            default:
                break;

        }
        return __return;
    }

    /**
     * @param req
     * @param mybatis
     * @return
     */
    private DSResponse executeUpdate(DSRequest req, MybatisDataSource mybatis) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param req
     * @param mybatis
     * @return
     */
    private DSResponse executeRemove(DSRequest req, MybatisDataSource mybatis) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param req
     * @param mybatis
     * @return
     */
    private DSResponse executefetch(DSRequest req, MybatisDataSource mybatis)
        throws SlxException {
        DSRequestData reqData = req.getContext();
        final DSResponse __return = new DSResponseImpl(req,
            Status.STATUS_SUCCESS);
        // reqData.getRawCriteria();
        ToperationBinding __bind = getContext().getOperationBinding(req);
        String mybatisStatement = null;
        if (__bind != null && __bind.getQueryClauses() != null) {
            mybatisStatement = __bind.getQueryClauses().getCustomQL();
        }
        if (mybatisStatement == null) {
            throw new SlxException("configure error:mybatis statement is null");
        }
        // Control Page.
        int totalRows = -1;
        boolean __canPage = true;
        if (!reqData.isPaged()
            && getConfig().getBoolean("customSQLReturnsAllRows", false)
            && DataUtil.isNotNullAndEmpty(DataSourceData.getCustomSQL(__bind))) {
            __canPage = false;
            log.debug("Paging disabled for full custom queries.  Fetching all rows.Set sql.customSQLReturnsAllRows: false in config to change this behavior");
        }

        if (sqlDriver == null) {
            try {
                sqlDriver = SQLDriver.instance(dbName,
                    getSqlSessionFactoryProvider().getDbType(dbName));
            } catch (Exception e) {
                throw new SlxException(Tmodule.SQL,
                    Texception.SQL_SQLEXCEPTION, "Can't instance SQLDriver", e);
            }
        }
        MybatisParameter parameter = new MybatisParameter(req, __return,
            reqData.getRawCriteria(), sqlDriver, __canPage);
        final List<Object> results = new ArrayList<Object>();
        long _$ = System.currentTimeMillis();
        session.select(mybatisStatement, parameter, new ResultHandler() {

            @Override
            public void handleResult(ResultContext context) {
                results.add(context.getResultObject());
            }
        });
        getEventWork().createAndFireTimeEvent(
            (System.currentTimeMillis() - _$),
            "SQL window query,Query total rows: " + results.size());
        Integer startRow = 0;
        Integer endRow = 0;
        if (totalRows != 0L) {
            startRow = req.getContext().getStartRow() == null ? 0
                : req.getContext().getStartRow();
            endRow = startRow + results.size();
        }
        __return.setStartRow(startRow);
        __return.setEndRow(endRow);
        __return.setRawData(results);
        return __return;
    }

    /**
     * @param req
     * @param mybatis
     * @return
     * @throws SlxException
     */
    private DSResponse executeAdd(DSRequest req, MybatisDataSource mybatis)
        throws SlxException {
        DSRequestData __requestCX = req.getContext();
        DSResponse __return = new DSResponseImpl(req.getDataSource(),
            Status.STATUS_SUCCESS);

        return null;
    }

    /**
     * @param atuoCommit
     * @return
     * @throws SlxException
     */
    private SqlSession getSqlSession(boolean atuoCommit) throws SlxException {
        dbName = data.getTdataSource() == null ? null
            : data.getTdataSource().getDbName();

        SqlSessionFactoryProvider provider = getSqlSessionFactoryProvider();
        Assert.isNotNull(provider);
        SqlSessionFactory factory = provider.createSqlSessionFactory(dbName);
        return factory.openSession(atuoCommit);
    }

    /**
     * @param dsObject
     * @return
     */
    private MybatisDataSource getDataSource(String datasourceName)
        throws SlxException {
        DataSource datasource = DefaultDataSourceManager.getDataSource(datasourceName);
        if (datasource instanceof MybatisDataSource) {
            return (MybatisDataSource) datasource;
        } else {
            throw new SlxException("the datasource [" + datasource.toString()
                + "] cannot processed by Mybatis DataSource.");
        }
    }

    private boolean isMybatisOperation(Eoperation operationType) {
        return DataTools.isFetch(operationType)
            || DataTools.isAdd(operationType)
            || DataTools.isRemove(operationType)
            || DataTools.isUpdate(operationType)
            || DataTools.isReplace(operationType);
    }

    /**
     * @return the sqlSessionFactoryProvider
     */
    public SqlSessionFactoryProvider getSqlSessionFactoryProvider() {
        return sqlSessionFactoryProvider;
    }

    /**
     * @param sqlSessionFactoryProvider the sqlSessionFactoryProvider to set
     */
    public void setSqlSessionFactoryProvider(
        SqlSessionFactoryProvider sqlSessionFactoryProvider) {
        this.sqlSessionFactoryProvider = sqlSessionFactoryProvider;
    }

    @Override
    public DataSource instance(DataSourceData data) throws SlxException {
        MybatisDataSource ds = new MybatisDataSource(sc);
        if (this.getSqlSessionFactoryProvider() != null) {
            ds.setSqlSessionFactoryProvider(getSqlSessionFactoryProvider());
        }
        ds.init(data);
        return ds;
    }

}
