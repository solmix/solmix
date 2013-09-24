/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.sql;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.SlxConstants;
import org.solmix.api.data.DSRequestData;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.datasource.ISQLDataSource;
import org.solmix.api.datasource.annotation.SQLCacheData;
import org.solmix.api.event.EventManager;
import org.solmix.api.event.MonitorEventFactory;
import org.solmix.api.event.TimeMonitorEvent;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.TqueryClauses;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.rpc.RPCManagerCompletionCallback;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.datasource.DSRequestImpl;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.servlet.SlxFileItem;
import org.solmix.fmk.util.DataTools;
import org.solmix.fmk.velocity.Velocity;
import org.solmix.sql.EscapedValuesMap.Mode;
import org.solmix.sql.internal.Activator;
import org.solmix.sql.internal.SQLConfigManager;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 0.1.1 2012-12-16 solmix-sql
 */
@SuppressWarnings("unchecked")
public final class SQLDataSource extends BasicDataSource implements ISQLDataSource, RPCManagerCompletionCallback
{

    private static final Logger log = LoggerFactory.getLogger(SQLDataSource.class.getName());

    protected static String DEFAULT_SEQUENCE_NAME = "__default";

    protected static JSParser jsParser;

    protected volatile SQLDriver driver;

    protected volatile SQLTable table;

    public static final String INTERFACE_TYPE = "interface.type";

    public static final String USED_POOL = "used.pool";

    public static final String PREFIX = "sql";

    public static final String DEFAULTDATABASE = "defaultDatabase";

    public static final String SQL_DEFAULTDATABASE = "sql.defaultDatabase";

    @SQLCacheData
    private Object lastRow;

    @SQLCacheData
    private Map<Object, Object> lastPrimaryKeysData;

    @SQLCacheData
    private Map<Object, Object> lastPrimaryKeys;

    private DSRequest downloadDsRequest;

    private static synchronized JSParser getJsParser() {
        if (jsParser == null) {
            JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
            jsParser = jsFactory.get();
        }
        return jsParser;
    }

    public SQLDataSource(DataSourceData data) throws SlxException
    {
        this.init(data);

    }

    /**
    * 
    */
    public SQLDataSource()
    {
    }

    @Override
    public DSResponse execute(DSRequest req) throws SlxException {
        req.registerFreeResourcesHandler(this);
        Eoperation _opType = req.getContext().getOperationType();
        DSResponse __return = null;
        if (isSQLOperationType(_opType)) {
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
            if (datasources != null && (datasources instanceof List && ((List) datasources).size() > 1)) {
                dsObject = datasources;
            } else {
                dsObject = this;
            }
            __return = executeSQLDataSource(req, dsObject);
        } else {
            __return = super.execute(req);
        }
        return __return;
    }

    @Override
    public DSResponse executeDownload(DSRequest req) throws SlxException {
        // String fieldName = req.getContext().getDownloadFieldName();
        Map criteria = req.getContext().getCriteria();
        downloadDsRequest = new DSRequestImpl(getName(), Eoperation.FETCH);
        downloadDsRequest.getContext().setCriteria(criteria);
        downloadDsRequest.getContext().setFreeOnExecute(false);
        DSResponse resp = downloadDsRequest.execute();
        resp.getContext().setData(forceSingle(resp.getContext().getDataList(Map.class)));
        return resp;

    }

    protected Map<Object, Object> forceSingle(List<Map> result) throws SlxException {
        if (result.size() == 0)
            return null;
        if (result.size() > 1)
            throw new SlxException(Tmodule.SQL, Texception.OBJECT_TYPE_NOT_ADAPTED, "Fetched multiple results when trying single");
        else
            return result.get(0);
    }

    /**
     * Execute Sql operation,default is Fetch/Add/Replace/Update/Remove
     * 
     * @param req
     * @param dsObject
     * @return
     * @throws SlxException
     */
    public DSResponse executeSQLDataSource(DSRequest req, Object dsObject) throws SlxException {
        DSResponse __return;
        // req
        if (req.getContext() == null)
            return null;
        // boolean batchUpdate = globalConfig().getBoolean("batchUpdate", false);
        Eoperation _opType = req.getContext().getOperationType();
        DSRequestData _dsContext = req.getContext();
        if (log.isDebugEnabled()) {
            List values = req.getContext().getValueSets();
            StringBuilder __info = new StringBuilder();
            switch(_opType){
                case ADD:{
                    if (values != null) {
                        if (values.size() == 1) {
                            __info.append("\t values: ").append(getJsParser().toJavaScript(values.get(0)));
                        } else {
                            __info.append("\t values: ").append( values.size()).append(" valuesSets");
                        }
                    }
                }
                    break;
                case FETCH:
                case REMOVE:case REPLACE:
                {
                    if (req.getContext().getRawCriteria() != null)
                        __info.append("\t Criteria: ").append(getJsParser().toJavaScript(req.getContext().getCriteria()));
                }
                    break;
                case UPDATE:{
                    if (req.getContext().getRawCriteria() != null)
                        __info.append("\t Criteria: ").append(getJsParser().toJavaScript(req.getContext().getCriteria()));
                    if (values != null) {
                        if (values.size() == 1) {
                            __info.append("\t values: ").append(getJsParser().toJavaScript(values.get(0)));
                        } else {
                            __info.append("\t values: ").append( values.size()).append(" valuesSets");
                        }
                    }
                }
                    break;
                default:
                    break;
                
            }
            if (req.getContext().getConstraints() != null)
                __info.append("\t constraints: ").append(req.getContext().getConstraints().toString());
            if (req.getContext().getOutputs() != null)
                __info.append("\t outputs: ").append(req.getContext().getOutputs().toString());
            if (req.getContext().getRawCriteria() != null)
            if(__info.toString().length()>2){
                log.debug(new StringBuilder().append("Performing ").append(_opType).append(" operation with \n").append(__info.toString()).toString());
            }
           
        }
        // dsObject
        if (dsObject == null) {
            if (_dsContext.getDataSourceNames() == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.REQ_NO_DATASOURCE,
                    "no datasources specified in argument and no operation config to look them up; can't proceed");
            dsObject = _dsContext.getDataSourceNames();
            if(log.isDebugEnabled())
                log.debug((new StringBuilder()).append("No point out datasource find") .append( dsObject.toString() ).append(" in request configuration.").toString());
        }
        List<SQLDataSource> _datasources;
        if ((dsObject instanceof String) || (dsObject instanceof SQLDataSource)) {
            _datasources = getDataSources(DataUtil.makeListIfSingle(dsObject));
        } else if (dsObject instanceof List) {
            _datasources = getDataSources((List) dsObject);
        } else {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR,
                "in the app operation config, datasource must be set to a string or list");
        }
        SQLDataSource _firstDS = _datasources.get(0);
        List _valueSets = req.getContext().getValueSets();
        /*******************************************************************
         * NOTE:[UPDATE] multiple insert support. for normal insert.
         ******************************************************************/
        if (DataTools.isAdd(_opType) && _valueSets != null && _valueSets.size() > 1)
            return executeMultipleInsert(req, _valueSets, _datasources);
        /*******************************************************************
         * NOTE:[REPLACE] NATIVE SUPPORT REPLACE .
         *******************************************************************/
        if (Eoperation.REPLACE == _opType && !_firstDS.getDriver().isSupportsNativeReplace()) {
            req.getContext().setOperationType(Eoperation.REMOVE);
            executeSQLDataSource(req, _datasources);
            req.getContext().setOperationType(Eoperation.ADD);
            return executeSQLDataSource(req, _datasources);
        }
        ToperationBinding __bind = req.getDataSource().getContext().getOperationBinding(req.getContext().getOperationType(),
            req.getContext().getOperationId());
        /*******************************************************************
         * NOTE:Proccess customer configuration for generate SQL .
         *******************************************************************/
        List<String> _customCriteriaFields = null;
        List<String> _customValueFields = null;
        List<String> _excludeCriteriaFields = null;
        List<String> _customFields = null;
        if (__bind != null) {
            String cuscf = __bind.getCustomCriteriaFields();
            if (cuscf != null) {
                _customCriteriaFields = new ArrayList<String>();
                for (String str : cuscf.split(","))
                    _customCriteriaFields.add(str.trim());
            }
            String cusvf = __bind.getCustomValueFields();
            if (cusvf != null) {
                _customValueFields = new ArrayList<String>();
                for (String str : cusvf.split(","))
                    _customValueFields.add(str.trim());
            }
            String exccf = __bind.getExcludeCriteriaFields();
            if (exccf != null) {
                _excludeCriteriaFields = new ArrayList<String>();
                for (String str : exccf.split(","))
                    _excludeCriteriaFields.add(str.trim());
            }
            String cf = __bind.getCustomFields();
            if (cf != null) {
                _customFields = new ArrayList<String>();
                for (String str : cf.split(","))
                    _customFields.add(str.trim());
            }
            // override
            if (_customCriteriaFields == null)
                _customCriteriaFields = _customFields;

        }
        // customer criteria / customer values / exclude criteria

        /*******************************************************************************
         * Whether to qualify columnNames with table,first find in the operationBinding config,if not found then find in
         * dataSource config.
         ******************************************************************************/
        Boolean qualifyColumnNames = (Boolean) DataUtil.getProperties("qualifyColumnNames", __bind, _firstDS.getContext().getTdataSource());
        if (qualifyColumnNames == null) {
            qualifyColumnNames = autoQualifyColumnNames(_datasources);
        }
        boolean __qualifyColumnNames = DataUtil.booleanValue(qualifyColumnNames);
        /*****************************************************************************
         * Prepare for generate sql statement.
         ******************************************************************************/
        Map<String, Object> context = getClausesContext(req, _datasources, false, __qualifyColumnNames, _customCriteriaFields, _customValueFields,
            _excludeCriteriaFields, __bind);

        if ((DataTools.isAdd(_opType) || DataTools.isUpdate(_opType) || DataTools.isReplace(_opType))
            && (__bind == null || __bind.getQueryClauses()==null||__bind.getQueryClauses().getCustomSQL() == null) && context.get("defaultValuesClause") == null) {
            String __info;
            if(req.getContext().getRawValues()==null)
                __info = "Insert, update or replace operation requires non-empty values; check submitted values parameter";
            else
                __info="Auto generate  Insert, update or replace sql  requires non-empty  ValuesClause; check submitted values in DataSource fields";
            log.warn(__info);
            throw new SlxException(Tmodule.SQL, Texception.SQL_BUILD_SQL_ERROR, __info);
        }
        if (__bind != null) {
            context.putAll(getVariablesContext(req, _datasources));
        }
        String statement = generateSQLStatement(req, context);
        /*******************************************************************
         * NOTE:[UPDATE]OR [AND] batch update.
         *******************************************************************/
        // if (batchUpdate && (DataTools.isAdd(_opType) || DataTools.isUpdate(_opType))) {
        // List<String> valueMap = (List<String>) context.get("batchUpdateReturnValue");
        // return executeBatchUpdate(req, _valueSets, statement, valueMap, _firstDS);
        // }
        // log.info("SQL Statement: "+statement);
        __return = new DSResponseImpl(_firstDS,req);
        if (DataUtil.isNullOrEmpty(statement))
            __return.getContext().setStatus(Status.STATUS_SUCCESS);
        /*******************************************************************
         * NOTE:[FETCH]
         *******************************************************************/
        if ((DataTools.isFetch(_opType))) {
            boolean __canPage = true;
            if (!req.getContext().isPaged()
                || (SQLConfigManager.getConfig().getBoolean("customSQLReturnsAllRows", false) && DataUtil.isNotNullAndEmpty(DataSourceData.getCustomSQL(__bind)))) {
                __canPage = false;
                log.warn("Paging disabled for full custom queries.  Fetching all rows.Set sql.customSQLReturnsAllRows: false in config to change this behavior");
            }
            /*******************************************************************
             * NOTE:[FETCH] Windows Fetch
             *******************************************************************/
            if (__canPage) {
                long start = System.currentTimeMillis();
                __return = executeWindowedSelect(req, _datasources, context, statement);
                long end = System.currentTimeMillis();
                createAndFireTMEvent(end - start, "SQL QueryTime");
            } else {
                /*******************************************************************
                 * NOTE:[FETCH] Normal Fetch
                 *******************************************************************/
                List results = _firstDS.executeNativeQuery(statement, _firstDS, __bind, req);
                if (results != null) {
                    __return.getContext().setData(results);
                    __return.getContext().setTotalRows(results.size());
                    __return.getContext().setStartRow(0);
                    __return.getContext().setEndRow(results.size());
                }
            }
        } else {
            /*******************************************************************
             * NOTE:[UPDATE]OR [AND] ONE RECORD PER OPERATION.
             *******************************************************************/
            _firstDS.clearCache();
            List streams = new ArrayList();
            List binaryStreams = getUploadedFileStreams(req);
            /*******************************************************************
             * NOTE:[UPDATE]OR [AND] FOR BIN TYPE
             *******************************************************************/
            if (binaryStreams != null) {
                int binaryStreamsIndex = 0;
                for (String name : _firstDS.getContext().getFieldNames()) {
                    boolean skipCustomCheck = false;
                    Tfield __f = _firstDS.getContext().getField(name);
                    if (_firstDS.getDriver().fieldAssignableInline(__f))
                        continue;
                    if (DataTools.isBinary(__f))
                        skipCustomCheck = false;
                    if (_customValueFields != null) {
                        for (String str : _customValueFields)
                            if (str.equals(__f.getName()))
                                skipCustomCheck = true;
                    }
                    if ((skipCustomCheck || !__f.isCustomSQL()) && binaryStreams != null && binaryStreams.size() > binaryStreamsIndex)
                        streams.add(binaryStreams.get(binaryStreamsIndex++));
                    else {
                        Map values = req.getContext().getValues();
                        if (values != null && values.get(name) != null)
                            streams.add(new StringBuffer((String) values.get(name)));
                    }

                }
            }
            int rowsAffected = _firstDS.executeNativeUpdate(statement, streams, req);
            __return.getContext().setAffectedRows(new Long(rowsAffected));
            __return.getContext().setData(rowsAffected);
            /*******************************************************************
             * NOTE:[UPDATE]OR [AND] SHOW THE AFFECTED ROW.
             *******************************************************************/
            if (!DataTools.isCustomer(_opType) && !data.getTdataSource().isSimpleReturn()) {
                if (rowsAffected > 0) {
                    log.debug((new StringBuilder()).append(_opType).append(" operation affected ").append(rowsAffected).append(" rows").toString());
                    if (shouldInvalidateCache(req, _firstDS.getDriver())) {
                        __return.getContext().setInvalidateCache(true);
                    } else {
                        Map storeValues = req.getContext().getCriteria();
                        if (DataTools.isAdd(_opType)&&storeValues!=null) {
                            Iterator i1 = storeValues.keySet().iterator();
                            do {
                                if (!i1.hasNext())
                                    break;
                                String fieldName = (String) i1.next();
                                Tfield field = _firstDS.getContext().getField(fieldName);
                                if (field != null && (field.getType() == Efield.SEQUENCE))
                                    i1.remove();
                            } while (true);
                        }
                        _firstDS.setLastPrimaryKeysData(storeValues);
                        __return.getContext().setData(
                            DataUtil.makeListIfSingle(DataTools.isRemove(_opType) ? ((Object) (_firstDS.getLastPrimaryKeys(req)))
                                : _firstDS.getLastRow(req, __qualifyColumnNames)));
                    }
                } else {
                    // __return.getContext().setData(new ArrayList());
                    log.warn((new StringBuilder()).append(_opType).append(" operation affected no rows").toString());
                }
            }
        }
        return __return;
    }

    /**
     * @param _datasources
     * @return
     */
    private static Boolean autoQualifyColumnNames(List<SQLDataSource> _datasources) {
        if (_datasources == null)
            return null;
        Boolean __return = null;
        for (SQLDataSource ds : _datasources) {
            List<Tfield> fields = ds.getContext().getFields();
            if (fields != null) {
                for (Tfield field : fields) {
                    if (DataUtil.isNotNullAndEmpty(field.getForeignKey())) {
                        __return = Boolean.TRUE;
                        break;
                    }
                }
            }
            if (__return != null)
                break;
        }
        return __return;
    }

    @Override
    public Object transformFieldValue(Tfield field, Object obj) {
        return driver.transformFieldValue(field, obj);
    }

    /**
     * execute window selected.
     * 
     * @param req
     * @param datasources
     * @param context
     * @param statement
     * @return
     * @throws SlxException
     */
    private DSResponse executeWindowedSelect(DSRequest req, List<SQLDataSource> dataSources, Map<String, Object> context, String query)
        throws SlxException {
        // Constructed return DSResponse.
        DSResponse __return = new DSResponseImpl(dataSources.get(0),req);
        SQLDataSource _firstDS = dataSources.get(0);
        SQLDriver _driver = _firstDS.getDriver();
        Eoperation __opType = req.getContext().getOperationType();
        Roperation __opID = req.getContext().getRoperation();
        // DataSource ds = req.getDataSource();
        ToperationBinding __bind = req.getDataSource().getContext().getOperationBinding(req);
        boolean _useRowCount = false;
        TqueryClauses clauses = __bind != null ? __bind.getQueryClauses() : null;
        // default use row count;not customSQL used;
        if (DataUtil.isNotNullAndEmpty(getCustomSQLClause(__opID, clauses, __opType, null)))
            _useRowCount = true;

        long _$ = System.currentTimeMillis();
        if (_useRowCount) {
            String queryString = _driver.getRowCountQueryString(query);
            log.debug("Executing row count query", queryString);

            String eQuery = Velocity.evaluateAsString(queryString, context, __opType.value(), _firstDS, true);
            // eQuery = applySandboxNames(eQuery, req);
            log.debug("After Velocity query String", eQuery);
            Object objCount = _driver.executeScalar(eQuery, req);
            Integer count = new Integer(objCount == null ? "0" : objCount.toString());
            long $_ = System.currentTimeMillis();
            __return.getContext().setTotalRows(count);
            createAndFireTMEvent(($_ - _$), "SQL window query,Query total rows: " + count.intValue());
            if (__return.getContext().getTotalRows() == 0L) {
                __return.getContext().setData(Collections.EMPTY_LIST);
                __return.getContext().setStartRow(0);
                __return.getContext().setEndRow(0);
                return __return;
            }
            int _sRow = req.getContext().getStartRow();
            int _eRow = req.getContext().getEndRow();
            int _batch = req.getContext().getBatchSize();
            if (_sRow > _eRow || (count.intValue() - _sRow) < _batch) {
                int newStartRow = Math.max(count.intValue() - _batch, 0);
                req.getContext().setStartRow(newStartRow);
            }
            query = _driver.limitQuery(query, req.getContext().getStartRow(), req.getContext().getBatchSize(), null);
            queryWindowSelect(req, dataSources, query, __return, __bind);

        } else {
            String selectClause = getSelectClause(__opID, clauses, __opType, "$defaultSelectClause");
            String valuesClause = getValuesClause(__opID, clauses, __opType, "$defaultValuesClause");
            String tableClause = getTableClause(__opID, clauses, __opType, "$defaultTableClause");
            String whereClause = getWhereClause(__opID, clauses, __opType, "$defaultWhereClause");
            String orderClause = getOrderClause(__opID, clauses, __opType, "$defaultOrderClause");
            String groupClause = getGroupClause(__opID, clauses, __opType, "$defaultGroupClause");
            String groupWhereClause = getGroupWhereClause(__opID, clauses, __opType, "$defaultGroupWhereClause");
            String queryString = _driver.getRowCountQueryString(selectClause, tableClause, whereClause, groupClause, groupWhereClause, context);
            log.debug("Executing row count query", queryString);

            String eQuery = Velocity.evaluateAsString(queryString, context, __opType.value(), _firstDS, true);
            // eQuery = applySandboxNames(eQuery, req);
            log.debug("After Velocity query String", eQuery);
            Object objCount = _driver.executeScalar(eQuery, req);
            Integer count = new Integer(objCount == null ? "0" : objCount.toString());
            long $_ = System.currentTimeMillis();
            __return.getContext().setTotalRows(count);
            createAndFireTMEvent(($_ - _$), "SQL window query,Query total rows: " + count.intValue());
            if (__return.getContext().getTotalRows() == 0L) {
                __return.getContext().setData(new ArrayList());
                __return.getContext().setStartRow(0);
                __return.getContext().setEndRow(0);
                return __return;
            }
            int _sRow = req.getContext().getStartRow();
            int _eRow = req.getContext().getEndRow();
            int _batch = req.getContext().getBatchSize();
            if (_sRow > _eRow || (count.intValue() - _sRow) < _batch) {
                int newStartRow = Math.max(count.intValue() - _batch, 0);
                req.getContext().setStartRow(newStartRow);
            }
            // ||_firstDS.getContext().getTdataSource()
            if (!_driver.supportsSQLLimit() /* || __bind.isUseSQLLimit() */) {
                Map<String, String> remap = new HashMap<String, String>();
                for (SQLDataSource ds : dataSources) {
                    remap = DataUtil.orderedMapUnion(remap, ds.getContext().getDs2NativeFieldMap());
                }
                List contraints = (List) req.getContext().getConstraints();
                if (contraints != null)
                    remap = DataUtil.subsetMap(remap, contraints);
                List<String> outputs = req.getContext().getOutputs();
                if (outputs != null)
                    remap = DataUtil.subsetMap(remap, outputs);
                if (_driver.limitRequiresSQLOrderClause()) {
                    if (orderClause == null || orderClause.equals("")) {
                        List<String> pkList = _firstDS.getContext().getPrimaryKeys();
                        if (_driver instanceof OracleDriver)
                            orderClause = "rownum";
                        else if (!pkList.isEmpty()) {
                            orderClause = pkList.get(0);
                            log.debug((new StringBuilder()).append("Using PK as default sorter: ").append(orderClause).toString());
                        } else {
                            Iterator<String> i = remap.keySet().iterator();
                            if (i.hasNext())
                                orderClause = i.next();
                            orderClause = (String) DataUtil.enumToList(remap.values().iterator()).get(0);
                            log.debug((new StringBuilder()).append("Using first field as default sorter: ").append(orderClause).toString());
                        }
                    }
                    query = _driver.limitQuery(query, req.getContext().getStartRow(), req.getContext().getBatchSize(),
                        DataUtil.enumToList(remap.values().iterator()), orderClause);
                } else {
                    query = _driver.limitQuery(query, req.getContext().getStartRow(), req.getContext().getBatchSize(),
                        DataUtil.enumToList(remap.values().iterator()));
                }// END ?LIMITSQL

                queryWindowSelect(req, dataSources, query, __return, __bind);

            } else {
                // TODO support DataBase Driver used SQL limite.
            }
        }// END ?_useRowCount
        return __return;
    }

    private void queryWindowSelect(DSRequest req, List<SQLDataSource> dataSources, String query, DSResponse __return, ToperationBinding __bind)
        throws SlxException {
        SQLDataSource _firstDS = dataSources.get(0);
        SQLDriver _driver = _firstDS.getDriver();
        // DataSource ds = req.getDataSource();
        if (log.isDebugEnabled())
            log.debug(
                (new StringBuilder()).append("SQL windowed select rows ").append(req.getContext().getStartRow()).append("->").append(
                    req.getContext().getEndRow()).append(", result size ").append(req.getContext().getBatchSize()).append(". Query").toString(),
                query);
        boolean __userTransaction = true;
        Connection __currentConn = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            try {
                __currentConn = _firstDS.getTransactionalConnection(req);
                if (__currentConn == null) {
                    __currentConn = ConnectionManager.getConnection(_driver.getDbName());
                    __userTransaction = false;
                }
                s = _driver.createFetchStatement(__currentConn);
                rs = s.executeQuery(query);
            } catch (SQLException e) {
                if (__userTransaction) {
                    try {
                        ConnectionManager.freeConnection(__currentConn);
                        __currentConn = ConnectionManager.getNewConnection(_driver.getDbName());
                        s = _driver.createFetchStatement(__currentConn);
                        rs = s.executeQuery(query);
                    } catch (SQLException sql1) {
                        ConnectionManager.freeConnection(__currentConn);
                        throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, sql1);
                    }
                } else {
                    throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
                }
            }
            LoggerFactory.getLogger(SlxLog.TIME_LOGNAME).debug("");
            List<Object> rows = new ArrayList<Object>();
            try {
                rows = SQLTransform.toListOfMapsOrBeans(rs, _driver, dataSources, __bind);
            } catch (SQLException e) {
                throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);
            }
            __return.getContext().setData(rows);
            __return.getContext().setEndRow(req.getContext().getStartRow() + rows.size());
            __return.getContext().setStartRow(req.getContext().getStartRow());
            if (rows.size() < req.getContext().getBatchSize())
                __return.getContext().setTotalRows(__return.getContext().getEndRow());
        } finally {
            try {
                s.close();
                rs.close();
            } catch (Exception ignored) {
            }
            if (!__userTransaction)
                ConnectionManager.freeConnection(__currentConn);
        }
    }

    /**
     * @param statement
     * @param streams
     * @param req
     * @return
     */
    public int executeNativeUpdate(String statement, List data, DSRequest req) throws SlxException {
        return driver.executeUpdate(statement, data, req);
    }

    public int executeNativeUpdate(String statement) throws SlxException {
        return executeNativeUpdate(statement, null);
    }

    /**
     * @param statement
     * @param object
     * @return
     * @throws SlxException
     */
    private int executeNativeUpdate(String statement, DSRequest req) throws SlxException {
        return driver.executeUpdate(statement, req);
    }

    /**
     * @param statement
     * @param firstDS
     * @param bind
     * @param req
     * @return
     * @throws SlxException
     */
    public List executeNativeQuery(String statement, SQLDataSource ds, ToperationBinding opConfig, DSRequest req) throws SlxException {
        if (ds == null)
            return executeNativeQuery(statement, (List) null, opConfig, req);
        else
            return executeNativeQuery(statement, DataUtil.makeList(ds), opConfig, req);
    }

    /**
     * @param statement
     * @param list
     * @param opConfig
     * @param req
     * @return
     * @throws SlxException
     */
    public List executeNativeQuery(String statement, List dataSources, ToperationBinding opConfig, DSRequest req) throws SlxException {
        return driver.executeQuery(statement, dataSources, opConfig, req);
    }

    public List executeNativeQuery(String nativeCommand) throws Exception {
        return executeNativeQuery(nativeCommand, (List) null, null);
    }

    public List executeNativeQuery(String nativeCommand, DSRequest req) throws Exception {
        return executeNativeQuery(nativeCommand, (List) null, req);
    }

    public List executeNativeQuery(String nativeCommand, List dataSources, DSRequest req) throws Exception {
        return executeNativeQuery(nativeCommand, dataSources, null, req);
    }

    /**
     * @param req
     * @param context
     * @return
     * @throws SlxException
     */
    public String generateSQLStatement(DSRequest req, Map context) throws SlxException {
        DataSource ds = req.getDataSource();
        Roperation __opID = req.getContext().getRoperation();
        Eoperation __opType = req.getContext().getOperationType();
        ToperationBinding __bind = req.getDataSource().getContext().getOperationBinding(req);
        TqueryClauses clauses = __bind == null ? null : __bind.getQueryClauses();
        String customSQL = getCustomSQLClause(__opID, clauses, __opType, null);
        if (customSQL != null)
            return Velocity.evaluateAsString(customSQL, context, __opType.value(), ds, true);
        String selectClause = getSelectClause(__opID, clauses, __opType, "$defaultSelectClause");
        String tableClause = getTableClause(__opID, clauses, __opType, "$defaultTableClause");
        String whereClause = getWhereClause(__opID, clauses, __opType, "$defaultWhereClause");
        String valuesClause = getValuesClause(__opID, clauses, __opType, "$defaultValuesClause");
        String groupClause = getGroupClause(__opID, clauses, __opType, "$defaultGroupClause");
        String groupWhereClause = getGroupWhereClause(__opID, clauses, __opType, "$defaultGroupWhereClause");
        String orderClause = getOrderClause(__opID, clauses, __opType, "$defaultOrderClause");
        String statement;
        if (DataTools.isFetch(__opType)) {
            statement = (new StringBuilder()).append("SELECT ").append(selectClause).append(" FROM ").append(tableClause).toString();
            if (!"$defaultWhereClause".equals(whereClause) || context.get("defaultWhereClause") != null)
                statement = (new StringBuilder()).append(statement).append(" WHERE ").append(whereClause).toString();
            if (!"$defaultGroupClause".equals(groupClause))
                statement = (new StringBuilder()).append(statement).append(" GROUP BY ").append(groupClause).toString();
            if (!"$defaultGroupWhereClause".equals(groupWhereClause))
                statement = (new StringBuilder()).append("SELECT * FROM (").append(statement).append(") work WHERE ").append(groupWhereClause).toString();
            if (req.getContext().getRawSortBy() != null){
            	Object o = req.getContext().getRawSortBy();
            	StringBuilder s = (new StringBuilder()).append(statement);
            	if(o instanceof List){
            		int size =((List)o).size();
            		if(size>0){
            			s.append(" ORDER BY ");
            			for(int i=0;i<size;i++){
            				s.append(((List)o).get(i).toString());
            				if(i<size)
            				s.append(", ");
            			}
            		}
            	}
            	statement=s.toString();
            }else if( !"$defaultOrderClause".equals(orderClause))
                statement = (new StringBuilder()).append(statement).append(" ORDER BY ").append(orderClause).toString();
            log.debug((new StringBuilder()).append("derived query: ").append(statement).toString());
        } else if (DataTools.isAdd(__opType))
            statement = (new StringBuilder()).append("INSERT INTO ").append(tableClause).append(" ").append(valuesClause).toString();
        else if (DataTools.isUpdate(__opType))
            statement = (new StringBuilder()).append("UPDATE ").append(tableClause).append(" SET ").append(valuesClause).append(" WHERE ").append(
                whereClause).toString();
        else if (DataTools.isRemove(__opType))
            statement = (new StringBuilder()).append("DELETE FROM ").append(tableClause).append(" WHERE ").append(whereClause).toString();
        else if (DataTools.isReplace(__opType))
            statement = (new StringBuilder()).append("REPLACE INTO ").append(tableClause).append(" ").append(valuesClause).toString();
        // else if (DataTools.isCustomer(__opType))
        // statement = "";
        else
            throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, DataUtil.getNoSupportString(__opType));

        return Velocity.evaluateAsString(statement, context, __opType.value(), ds, true);
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected String getOrderClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getOrderCaluse() == null ? null : clauses.getOrderCaluse().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected static String getGroupClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getGroupClause() == null ? null : clauses.getGroupClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected static String getGroupWhereClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getGroupWhereClause() == null ? null : clauses.getGroupWhereClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected static String getValuesClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getValuesClause() == null ? null : clauses.getValuesClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected static String getWhereClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getWhereClause() == null ? null : clauses.getWhereClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op
     * @param bind
     * @param opType
     * @param string
     * @return
     */
    protected static String getTableClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        if (clauses == null)
            return defaultValue;
        __return = clauses.getTableClause() == null ? null : clauses.getTableClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
     * @param op request operation
     * @param bind
     * @param opType
     * @param object
     * @return
     */
    protected static String getCustomSQLClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        // request operation customer sql
        // operationbinding customer sql
        if (clauses == null)
            return defaultValue;
        __return = clauses.getCustomSQL() == null ? null : clauses.getCustomSQL().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    protected static String getSelectClause(Roperation reqOperation, TqueryClauses clauses, Eoperation opType, String defaultValue) {
        String __return = null;
        // // request operation customer sql
        // // operationbinding customer sql
        if (clauses == null)
            return defaultValue;
        __return = clauses.getSelectClause() == null ? null : clauses.getSelectClause().trim();
        if (__return != null)
            return __return;
        else
            return defaultValue;
    }

    /**
    * 
    */
    private void clearCache() {
        lastRow = null;
        lastPrimaryKeys = null;
        lastPrimaryKeysData = null;

    }

    /**
     * Once the garbage collector frees memory space occupied by the object,
     *  the first call this method.
     * 
     */
    @Override
    public void finalize() throws Throwable {
        if (driver != null)
            driver.clearState();
    }

    @Override
    public void clearState() {
        clearCache();
        if (driver != null)
            driver.clearState();
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    private Map getLastPrimaryKeys(DSRequest req) throws SlxException {
        if (lastPrimaryKeys != null)
            return lastPrimaryKeys;
        if (lastPrimaryKeysData == null)
            throw new SlxException(Tmodule.SQL, Texception.SQL_DATASOURCE_CACHE_EXCEPTION,
                "getLastPrimaryKeys() called before valid insert/replace/update operation has been performed");
        Map submittedPrimaryKeys = DataUtil.subsetMap(lastPrimaryKeysData, data.getPrimaryKeys());
        if (submittedPrimaryKeys == null)
            return null;
        Iterator i = submittedPrimaryKeys.keySet().iterator();
        do {
            if (!i.hasNext())
                break;
            String keyName = (String) i.next();
            if (submittedPrimaryKeys.get(keyName) == null)
                submittedPrimaryKeys.remove(keyName);
        } while (true);
        List sequencesNotPresent = DataUtil.setDisjunction(data.getPrimaryKeys(), DataUtil.keysAsList(submittedPrimaryKeys));
        if (sequencesNotPresent.isEmpty())
            return lastPrimaryKeys = submittedPrimaryKeys;
        else
            return lastPrimaryKeys = driver.fetchLastPrimaryKeys(submittedPrimaryKeys, sequencesNotPresent, this, req);
    }

    /**
     * @param storeValues
     */
    private void setLastPrimaryKeysData(Map storeValues) {
        if (DataUtil.isNullOrEmpty(data.getPrimaryKeys()))
            lastPrimaryKeysData = storeValues;
        else
            lastPrimaryKeysData = DataUtil.subsetMap(storeValues, data.getPrimaryKeys());

    }

    /**
     * @param req
     * @param driver2
     * @return
     */
    private static boolean shouldInvalidateCache(DSRequest req, SQLDriver driver2) {
        // if (req.forceInvalidateCache())
        // return true;
        return false;
    }

    /**
     * @param req
     * @param qualifyColumnNames
     * @return
     * @throws SlxException
     */
    public Object getLastRow(DSRequest req, boolean qualifyColumnNames) throws SlxException {
        if (lastRow != null)
            return lastRow;
        Map primaryKeys = getLastPrimaryKeys(req);
        boolean printSQL = SQLConfigManager.getConfig().getBoolean("printSQL", false);
        if (printSQL) {
            log.debug((new StringBuilder()).append("primaryKeys: ").append(primaryKeys).toString());
        }

        String schema = data.getTdataSource().getSqlSchema();
        String _schemaClause = "";
        if (schema != null)
            _schemaClause = (new StringBuilder()).append(schema).append(getDriver().getQualifiedSchemaSeparator()).toString();
        {
            // for operation cache.
        }
        // used to primary key is auto generated by sequence.
        if (DataUtil.isNullOrEmpty(primaryKeys)) {
            primaryKeys = req.getContext().getCriteria();
        }
        String lastRowQuery = new StringBuffer().append("SELECT ").append((new SQLSelectClause(req, this, qualifyColumnNames)).getSQLString()).append(
            " FROM ").append(_schemaClause).append(table.getName()).append(" WHERE ").append(
            (new SQLWhereClause(qualifyColumnNames, primaryKeys, this)).getSQLString()).toString();
        // lastRowQuery = applySandboxNames(lastRowQuery, req);
        ToperationBinding opConfig = data.getOperationBinding(Eoperation.FETCH, null);
        List results = executeNativeQuery(lastRowQuery, this, opConfig, req);
        if (results.isEmpty()) {
            log.warn((new StringBuilder()).append(driver.getDbName()).append(" getLastRow(): empty result set fetching last row").toString());
            return lastRow = null;
        } else {
            return lastRow = results.get(0);
        }
    }

    /**
     * @param req
     * @return
     */
    private static List getUploadedFileStreams(DSRequest req) {
        List _files = req.getContext().getUploadedFiles();
        if (DataUtil.isNullOrEmpty(_files))
            return null;
        List __return = new ArrayList();
        for (Object o : _files) {
            SlxFileItem file = (SlxFileItem) o;
            String fieldName = file.getFieldName();
            if (req.getContext().getValues().get(fieldName) instanceof InputStream)
                __return.add(req.getContext().getValues().get(fieldName));
            else
                __return.add(file.getInputStream());
        }
        return __return;
    }

    /**
     * Get the {@link org.solmix.sql.SQLDataSource SQLDataSource} context variables.used these variables to generate
     * velocity expression.
     * 
     * @param req {@link org.solmix.api.datasource.DSRequest DSRequest}
     * @param datasources {@link org.solmix.sql.SQLDataSource SQLDataSource}
     * @return
     * @throws SlxException
     */
    private static Map<String, Object> getVariablesContext(DSRequest req, List<SQLDataSource> datasources) throws SlxException {
        // Eoperation __type = req.getContext().getOperationType();
        Map<String, Object> context = Velocity.getStandardContextMap(req);
        context.put("criteria", req.getContext().getCriteria());
        context.put("filter", new EscapedValuesMap(req.getContext().getCriteria(), datasources, Mode.FITER));
        context.put("equals", new EscapedValuesMap(req.getContext().getCriteria(), datasources, Mode.EQUAL));
        context.put("substringMatches", new EscapedValuesMap(req.getContext().getCriteria(), datasources, Mode.SUBSTRING));
        Map<String, Object> fields = new HashMap<String, Object>();
        Map<String, Object> qfields = new HashMap<String, Object>();
        SQLDataSource firstDS = datasources.get(0);
        Map<String, Object> remapTable = getField2ColumnMap(datasources);
        Map<String, Object> column2TableMap = getColumn2TableMap(datasources);
        fields = remapTable;
        for (Iterator<String> i = firstDS.getContext().getFieldNames().iterator(); i.hasNext();) {
            String key = i.next();
            String columnName = (String) remapTable.get(key);
            String tableName = (String) column2TableMap.get(columnName);
            if (tableName == null)
                tableName = firstDS.getTable().getName();
            // int j = 0;
            // do {
            // if (j >= datasources.size())
            // break;
            // DataSource ds = datasources.get(j);
            // Tfield field = ds.getContext().getField(key);
            // if (field != null)
            // {
            // if (field.get("tableName") != null)
            // tableName = field.get("tableName").toString();
            // break;
            // }
            // j++;
            // } while (true);
            qfields.put(key, firstDS.getDriver().sqlOutTransform(columnName, key, tableName));
        }

        context.put("fields", fields);
        context.put("qfields", qfields);
        Map<String, Object> rawValue = new HashMap<String, Object>();
        for (Iterator<String> i = context.keySet().iterator(); i.hasNext();) {
            String key = i.next().toString();
            rawValue.put(key, context.get(key));
        }
        context.put("rawValue", rawValue);
        return context;
    }

    public static Map getColumn2TableMap(List<SQLDataSource> dataSources) {
        return getColumn2TableMap(dataSources, false);
    }

    public static Map getColumn2TableMap(List<SQLDataSource> dataSources, boolean primaryKeysOnly) {
        Map _column2TableMap = new HashMap();
        for (SQLDataSource ds : dataSources) {
            Map singleRemap = ds.getContext().getDs2NativeFieldMap();
            if (singleRemap == null)
                continue;
            if (primaryKeysOnly)
                singleRemap = DataUtil.subsetMap(singleRemap, ds.getContext().getPrimaryKeys());
            for (Object column : singleRemap.keySet()) {
                if (!_column2TableMap.containsKey(column)) {
                    _column2TableMap.put(column, ds.getTable().getName());
                }
            }
        }
        return _column2TableMap;
    }

    private static Map<String, Object> getClausesContext(DSRequest req, List<SQLDataSource> dataSources, boolean batchUpdate,
        boolean qualifyColumnNames, List<String> customCriteriaFields, List<String> customValueFields, List<String> excludeCriteriaFields,
        ToperationBinding operationBinding) throws SlxException {
        Eoperation __op = req.getContext().getOperationType();
        Map<String, Object> context = new HashMap<String, Object>();
        // related tables.
        List<String> relateTables = null;
        List<String> relateCriterias = null;
        {
            SQLDataSource firstDS = dataSources.get(0);
            List<Tfield> fields = firstDS.getContext().getFields();
            String selfTableName = firstDS.getTable().getName();
            String _primaryKey = null;
            for (Tfield field : fields) {
                String foreign = field.getForeignKey();
                if (foreign != null) {
                    if (relateTables == null)
                        relateTables = new ArrayList<String>();
                    relateTables.add(foreign.substring(0, foreign.indexOf(".")));
                    if (relateCriterias == null)
                        relateCriterias = new ArrayList<String>();
                    String fieldName = null;
                    switch (__op) {
                        case FETCH:
                            fieldName = field.getCustomSelectExpression();
                            break;
                        case UPDATE:
                            fieldName = field.getCustomUpdateExpression();
                            break;
                    }
                    if (fieldName == null)
                        fieldName = field.getName();
                    if(field.getTableName()!=null)
                    	selfTableName=field.getTableName();
                    relateCriterias.add(new StringBuilder().append(selfTableName).append(".").append(fieldName).append(" = ").append(foreign).toString());
                }
            }
        }
        SQLTableClause tableClause = new SQLTableClause(dataSources);
        tableClause.setRelatedTables(relateTables);
        context.put("defaultTableClause", tableClause.getSQLString());
        ArrayList includeDataSources = new ArrayList(dataSources);
        for (int i = 0; i < dataSources.size(); i++) {
            BasicDataSource ds = dataSources.get(i);
            // if (ds.getContext()context.autoDeriveDS instanceof BasicDataSource)
            // includeDataSources.add(ds.autoDeriveDS);
        }
        if (__op == Eoperation.FETCH || __op == Eoperation.CUSTOM) {
            SQLSelectClause selectClause = new SQLSelectClause(req, includeDataSources, qualifyColumnNames);
            selectClause.setCustomValueFields(customValueFields);
            context.put("defaultSelectClause", selectClause.getSQLString());
            SQLOrderClause orderClause = new SQLOrderClause(req, dataSources, qualifyColumnNames);
            orderClause.setCustomValueFields(customValueFields);
            if (orderClause.size() > 0)
                context.put("defaultOrderClause", orderClause.getSQLString());
        }
        if (DataTools.isAdd(__op) || DataTools.isUpdate(__op) || DataTools.isReplace(__op)) {
            SQLValuesClause valuesClause = new SQLValuesClause(req, dataSources.get(0), batchUpdate);
            if (valuesClause.size() > 0)
                if (DataTools.isUpdate(__op)) {
                    context.put("defaultValuesClause", valuesClause.getSQLStringForUpdate());
                } else {
                    context.put("defaultValuesClause", valuesClause.getSQLStringForInsert());
                }
            context.put("batchUpdateReturnValue", valuesClause.getReturnValues());
        }
        if (!DataTools.isAdd(__op)) {
            boolean __isFilter = DataTools.isFilter(__op);
            String textMatchStyle = null;
            if (req.getContext().getRoperation() != null)
                textMatchStyle = req.getContext().getRoperation().getTextMatchStyle();
            SQLWhereClause whereClause = new SQLWhereClause(qualifyColumnNames, req, dataSources, __isFilter, textMatchStyle);
            whereClause.setCustomCriteriaFields(customCriteriaFields);
            whereClause.setExcludeCriteriaFields(excludeCriteriaFields);
            whereClause.setRelatedCriterias(relateCriterias);
            if (DataTools.isRemove(__op) && whereClause.isEmpty()
                && (operationBinding == null || !(DataSourceData.getCustomSQL(operationBinding) != null)))
                throw new SlxException(Tmodule.SQL, Texception.SQL_DELE_WITH_NO_CONDITION,
                    "empty where clause on delete operation - would  destroy table - ignoring.");
            context.put("defaultWhereClause", whereClause.getSQLString());
        }
        return context;
    }

    public SQLTable getTable() {
        return table;
    }

    public SQLDriver getDriver() {
        return driver;
    }

    @Override
    public Connection getConnection() throws SlxException {
        Connection conn = null;
        if (driver != null) {
            conn = driver.getConnection();
            if (conn == null)
                conn = ConnectionManager.getConnection(driver.getDbName());
        }
        return conn;
    }

    public void setDriver(SQLDriver driver) {
        this.driver = driver;
    }

    /**
     * 批量更新.
     * 
     * @param req
     * @param valueSets
     * @param dataSources
     * @return
     * @throws SlxException
     */
    private DSResponse executeMultipleInsert(DSRequest req, List valueSets, List<SQLDataSource> dataSources) throws SlxException {
        DSResponse _return = null;
        List _result = new ArrayList();
        boolean _invalidateCache = false;
        for (int i = 0; i < valueSets.size(); i++) {
            if (!(valueSets.get(i) instanceof Map)) {
                throw new SlxException(Tmodule.BASIC, Texception.NO_SUPPORT, "values must be set to a map or list of maps; was set to list of "
                    + valueSets.get(i).getClass().getName());
            }
            req.getContext().setValues(valueSets.get(i));
            _return = executeSQLDataSource(req, dataSources);
            List _curResultSet = _return.getContext().getDataList(Map.class);
            if (_curResultSet != null && !_curResultSet.isEmpty()) {
                // for insert ,the result should be one result(success or failure)
                _result.add(_curResultSet.get(0));
            }
            if (_return.getContext().getInvalidateCache())
                _invalidateCache = true;
            try {
                if (i % 1000 == 0)
                    driver.dbConnection.commit();
            } catch (SQLException e) {
                throw new SlxException(Tmodule.DATASOURCE, Texception.SQL_SQLEXCEPTION, e);
            }
        }
        _return.getContext().setAffectedRows(new Long(valueSets.size()));
        _return.getContext().setData(_result);
        _return.getContext().setInvalidateCache(_invalidateCache);
        return _return;
    }

    /**
     * @param makeListIfSingle
     * @return
     * @throws SlxException
     */
    private static List<SQLDataSource> getDataSources(List<Object> list) throws SlxException {
        List<SQLDataSource> _return = new ArrayList<SQLDataSource>();
        if (list == null)
            return null;
        for (Object ds : list) {
            if (ds instanceof SQLDataSource) {
                _return.add((SQLDataSource) ds);
            } else {
                DataSource datasource = DefaultDataSourceManager.getDataSource((String) ds);
                if (datasource instanceof SQLDataSource) {
                    _return.add((SQLDataSource) datasource);
                } else {
                    log.warn("the datasource [" + ds.toString() + "] cannot processed by SQL DataSource.");
                }
            }
        }
        return _return;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#freeResources()
     */
    @Override
    public void freeResources() {
        super.freeResources();
        if (downloadDsRequest != null)
            try {
                DefaultDataSourceManager.freeDataSource(downloadDsRequest.getDataSource());
            } catch (Exception e) {
                log.warn("Exception whilst freeing download DSRequest", e);
            }

    }

    @Override
    public void freeConnection(Connection conn) throws SlxException {
        ConnectionManager.freeConnection(conn);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#getServerType()
     */
    @Override
    public String getServerType() {
        return EserverType.SQL.value();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#init(org.solmix.api.data.DataSourceData)
     */
    @Override
    public void init(DataSourceData data) throws SlxException {
        if (data == null)
            return;
        super.init(data);
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        jsParser = jsFactory.get();
        table = findSQLTable();
        String databaseName = data.getTdataSource().getDbName();
        if (databaseName == null) {
            databaseName = SQLConfigManager.defaultDatabase;
        }
        if (databaseName != null) {
            driver = SQLDriver.instance(databaseName, table);
        } else {
            String __info =(new StringBuilder()).append("datasource [").append( databaseName
                ).append( "] does not define a target db,and the sql.defaultDatabase is not specified in the config.").toString();
            throw new SlxException(Tmodule.SQL, Texception.SQL_NO_DEFINED_DBNAME, __info);
        }

    }

    @Override
    public DataSource instance(DataSourceData data) throws SlxException {
        return new SQLDataSource(data);
    }

    public String getConfigRealmName() {
        return SlxConstants.MODULE_SQL_NAME;
    }

    /**
     * Return SQL table.
     * 
     * @return
     */
    private SQLTable findSQLTable() {
        Map<String, Efield> _fieldTypes = new HashMap<String, Efield>();
        Map<String, String> _sequence = new HashMap<String, String>();
        String _tableName;
        if (data.getNative2DSFieldMap() != null) {
            Iterator e = data.getNative2DSFieldMap().keySet().iterator();
            while (e.hasNext()) {
                String _columnName = (String) e.next();
                String _fieldName = (String) data.getDs2NativeFieldMap().get(_columnName);
                Tfield _field = data.getField(_fieldName);
                Efield __type = _field.getType();
                _fieldTypes.put(_columnName, __type);
                if (__type == Efield.SEQUENCE) {
                    String __name = _field.getSequenceName();
                    if (__name == null)
                        __name = DEFAULT_SEQUENCE_NAME;
                    _sequence.put(_columnName, __name);
                }
            }// END WHILE.
        }
        _tableName = data.getTdataSource().getTableName();
        if (_tableName == null) {
            log.debug("can not found table name defined ,try to use datasource name as default table name");
            _tableName = data.getName();
        }
        String dsQuotedColumnNames = "";
        return new SQLTable(_tableName, data.getPrimaryKeys(), _fieldTypes, data.getNative2DSFieldMap(), _sequence, dsQuotedColumnNames);
    }

    /**
     * @param operationType
     * @return
     */
    private boolean isSQLOperationType(Eoperation operationType) {
        return DataTools.isAdd(operationType) || DataTools.isRemove(operationType) || DataTools.isUpdate(operationType)
            || DataTools.isReplace(operationType) || DataTools.isFetch(operationType);
    }

    /**
     * @param dataSources
     * @return
     */
    public static Map getField2ColumnMap(List<SQLDataSource> dataSources) {
        return getField2ColumnMap(dataSources, false);
    }

    /**
     * @param dataSources
     * @param b
     * @return
     */
    public static Map getField2ColumnMap(List<SQLDataSource> dataSources, boolean primaryKeysOnly) {
        Map _combineRemap = new HashMap();
        for (SQLDataSource ds : dataSources) {
            Map singleRemap = ds.getContext().getExpandedDs2NativeFieldMap();
            if (primaryKeysOnly)
                singleRemap = DataUtil.subsetMap(singleRemap, ds.getContext().getPrimaryKeys());
            _combineRemap = DataUtil.orderedMapUnion(_combineRemap, singleRemap);
        }

        return _combineRemap;
    }

    /**
     * @param dataSources
     * @param sortBy
     * @return
     */
    public static Map getCombinedValueMaps(List<SQLDataSource> dataSources, List<String> sortBy) {

        Map valueMaps = new HashMap();
        for (SQLDataSource ds : dataSources) {
            valueMaps = DataUtil.orderedMapUnion(valueMaps, ds.getContext().getValueMaps(sortBy));
        }
        return valueMaps;
    }

    /**
     * @return
     */
    public Map getSequences() {
        Map seq = new HashMap();
        if (getContext().getSuperDS() != null)
            seq = ((SQLDataSource) getContext().getSuperDS()).getSequences();
        DataUtil.mapMerge(getTable().getSequences(), seq);
        return seq;
    }

    public String escapeColumnName(String columnName) {
        return driver.escapeColumnName(columnName);
    }

    public String escapeValue(String value) {
        return driver.escapeValue(value);
    }

    /**
     * @param fieldName
     * @param object
     * @return
     */
    public String sqlValueForFieldValue(String columnName, Object columnValue) {
        Tfield field = data.getField(columnName);
        String __type = field.getType().value();
        if (columnValue == null)
            return "NULL";
        if (DataTools.typeIsNumeric(__type)) {
            if ("".equals(columnValue))
                return "NULL";
            else
                return columnValue.toString();
        } else {
            return driver.sqlInTransform(columnValue, field);
        }
    }

    public String escapeValueForFilter(Object value) {
        return driver.escapeValueForFilter(value, null);
    }

    public String escapeValueForWhereClause(Object value, Object field) {
        return valueForWhereClause(value, field, false);
    }

    @Override
    public String escapeValue(Object value, Object field) {
        if (value instanceof String)
            return value.toString();
        else
            return escapeValueForWhereClause(value, field);
    }

    public String valueForWhereClause(Object value, Object field) {
        return valueForWhereClause(value, field, false);
    }

    /**
     * @param rawValue
     * @param column
     * @param b
     * @return
     */
    public String valueForWhereClause(Object value, Object fieldName, boolean filter) {
        Tfield __f = data.getField(fieldName.toString());
        String _columnType = null;
        if (__f != null)
            _columnType = __f.getType().value();
        if (_columnType == null) {
            if (value instanceof Date)
                _columnType = "date";
            else if (value instanceof Number) {
                if ((value instanceof Float) || (value instanceof Double))
                    _columnType = "float";
                else
                    _columnType = "integer";
            } else {
                _columnType = "text";

            }
        }
        if ("text".equals(_columnType) || "string".equals(_columnType))
            if (!filter)
                return driver.sqlInTransform(value, __f);
            else
                return driver.escapeValueForFilter(value.toString().toLowerCase(), null);
        if (DataUtil.typeIsNumeric(_columnType)) {
            if (value instanceof String)
                try {
                    if (DataUtil.typeIsDecimal(_columnType))
                        value = (new BigDecimal((String) value)).toString();
                    else
                        value = (new BigInteger((String) value)).toString();
                } catch (Exception e) {
                    log.warn((new StringBuilder()).append("Got non-numeric value '").append(value).append("' for numeric column '").append(
                        fieldName.toString()).append("', creating literal false expression").toString());
                    return "'0'='1'";
                }
            return value.toString();
        } else {
            return driver.sqlInTransform(value, __f);
        }
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    @Override
    public Connection getTransactionalConnection(DSRequest req) throws SlxException {
        Connection conn = null;
        if (shouldAutoJoinTransaction(req)) {
            Object tvalue = this.getTransactionObject(req);
            if (tvalue instanceof Connection)
                conn = (Connection) tvalue;
            if (conn == null && shouldAutoStartTransaction(req, false)) {
                SQLTransaction.startTransaction(req.getRpc(), driver.getDbName());
                conn = (Connection) getTransactionObject(req);
                if (req != null && req.getRpc() != null)
                    req.getRpc().registerCallback(this);
            }
            if (conn != null && req != null)
                req.setPartOfTransaction(true);
        }
        return conn;
    }

    @Override
    protected Boolean autoJoinAtProviderLevel(DSRequest req) throws SlxException {
        String dbName = data.getTdataSource().getDbName();
        if (dbName == null)
            dbName = SQLConfigManager.defaultDatabase;
        String autoJoin = SQLConfigManager.getConfig().getString((new StringBuilder()).append(dbName).append(".autoJoinTransactions").toString());
        if (autoJoin == null)
            return null;
        if (autoJoin.toLowerCase().equals("true") || autoJoin.toLowerCase().equals("ALL"))
            return Boolean.TRUE;
        if (autoJoin.toLowerCase().equals("false") || autoJoin.toLowerCase().equals("NONE"))
            return Boolean.FALSE;
        if (req != null && req.getRpc() != null) {
            if (autoJoin.equals("FROM_FIRST_CHANGE"))
                return Boolean.valueOf(req.getRpc().requestQueueIncludesUpdates());
            if (autoJoin.equals("ANY_CHANGE"))
                return Boolean.valueOf(req.getRpc().requestQueueIncludesUpdates());
        }
        return null;
    }

    @Override
    public String getTransactionObjectKey() throws SlxException {
        return (new StringBuilder()).append(SQLTransaction.CONNECTION_ATTR_KEY).append("_").append(driver.getDbName()).toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.rpc.RPCManagerCompletionCallback#onFailure(org.solmix.api.rpc.RPCManager, boolean)
     */
    @Override
    public void onFailure(RPCManager rpcmanager, boolean isFailed) throws SlxException {
        if (isFailed)
            SQLTransaction.rollbackTransaction(rpcmanager, driver.getDbName());
        else
            SQLTransaction.commitTransaction(rpcmanager, driver.getDbName());

        SQLTransaction.endTransaction(rpcmanager, driver.getDbName());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.rpc.RPCManagerCompletionCallback#onSuccess(org.solmix.api.rpc.RPCManager)
     */
    @Override
    public void onSuccess(RPCManager rpcmanager) throws SlxException {
        SQLTransaction.commitTransaction(rpcmanager, driver.getDbName());
        SQLTransaction.endTransaction(rpcmanager, driver.getDbName());

    }

    public String getNextSequenceValue(String columnName) throws SlxException {
        return driver.getNextSequenceValue(columnName, this);
    }

    /**
     * @return the dataSourceGenerator
     */
    @Override
    public DataSourceGenerator getDataSourceGenerator() {
        if (dataSourceGenerator == null)
            dataSourceGenerator = new SQLDataSourceGenerator();
        return dataSourceGenerator;
    }

    protected static void createAndFireTMEvent(long time, String msg) {
        createAndFireTMEvent(time, msg, null);
    }

    protected static void createAndFireTMEvent(long time, String msg, String query) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(TimeMonitorEvent.TOTAL_TIME, time);
        properties.put(TimeMonitorEvent.MESSAGE, msg);
        if (query != null)
            properties.put("Query String", query);
        MonitorEventFactory mef;
        if (Activator.getContext() == null)
            mef = MonitorEventFactory.getDefault();
        else
            mef = MonitorEventFactory.getInstance(Activator.getContext());

        TimeMonitorEvent event = mef.createTimeMonitorEvent(properties);
        // TODO
        EventManager em = SlxContext.getEventManager();
        if (em != null)
            em.postEvent(event);
        else
            LoggerFactory.getLogger(SlxLog.TIME_LOGNAME).debug((new StringBuilder()).append(event.getProperty(TimeMonitorEvent.MESSAGE)).append("used :[").append(
                event.getProperty(TimeMonitorEvent.TOTAL_TIME)).append("]ms").toString());
    }

}
