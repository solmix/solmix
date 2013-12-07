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

package org.solmix.fmk.datasource;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.VelocityExpression;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.SystemContext;
import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.ClientParameter;
import org.solmix.api.datasource.ConvertDSContextToMap;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.datasource.IType;
import org.solmix.api.event.IValidationEvent.Level;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.Tsecurity;
import org.solmix.api.jaxb.Tvalidator;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.types.ClientParameterType;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.api.types.TransactionPolicy;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.datasource.ValidationContext.Vtype;
import org.solmix.fmk.event.EventWorker;
import org.solmix.fmk.event.EventWorkerFactory;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.fmk.js.JSExpression;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.util.DataTools;
import org.solmix.fmk.util.DefaultValidators;
import org.w3c.dom.Element;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-26 solmix-ds
 */
@SuppressWarnings("unchecked")
public class BasicDataSource implements DataSource
{

    private static final Logger log = LoggerFactory.getLogger(BasicDataSource.class.getName());

    protected  static JSParser jsParser;

    private String dsName;

    protected DataSourceData data;

    protected DataSourceGenerator dataSourceGenerator;

    protected EventWorker worker;

    protected DataTypeMap config;

    protected  SystemContext sc;

    final static Map<Object, Object> buildInValidator;
    static {
        buildInValidator = new HashMap<Object, Object>();
        buildInValidator.put(Efield.TEXT, "isString");
        buildInValidator.put(Efield.BOOLEAN, "isBoolean");
        buildInValidator.put(Efield.INTEGER, "isInteger");
        buildInValidator.put(Efield.FLOAT, "isFloat");
        buildInValidator.put(Efield.DATE, "isDate");
        buildInValidator.put(Efield.TIME, "isTime");
        buildInValidator.put(Efield.DATETIME, "isTime");
        buildInValidator.put(Efield.ENUM, null);
        buildInValidator.put(Efield.INT_ENUM, "integer");
        buildInValidator.put(Efield.SEQUENCE, "isInteger");
        buildInValidator.put(Efield.LINK, null);
        buildInValidator.put(Efield.IMAGE, null);
        buildInValidator.put(Efield.BINARY, null);
        buildInValidator.put(Efield.IMAGE_FILE, null);
        buildInValidator.put(Efield.MODIFIER, null);
        buildInValidator.put(Efield.MODIFIER_TIMESTAMP, null);
        buildInValidator.put(Efield.PASSWORD, null);
    }

    public BasicDataSource()
    {
        this(SlxContext.getThreadSystemContext());
    }

    public BasicDataSource(SystemContext sc) 
    {
        setSystemContext(sc);
    }
    
    @Resource
    public void setSystemContext(SystemContext sc){
        this.sc=sc;
    }
    public static synchronized JSParser getJsParser() {
        if (jsParser == null) {
            JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
            jsParser = jsFactory.get();
        }
        return jsParser;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#getProviderType()
     */
    @Override
    public String getServerType() {
        return EserverType.BASIC.value();
    }

    @Override
    public DataSource instance(DataSourceData data) throws SlxException {
        BasicDataSource basic = new BasicDataSource(sc);
        basic.init(data);
        return basic;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see org.solmix.api.datasource.DataSource#init(org.solmix.api.data.DataSourceData)
     */
    @Override
    public void init(DataSourceData data) throws SlxException {
        if (data == null)
            return;
        this.config=getConfig();
        this.dataSourceGenerator = null;
        this.dsName = null;
        this.data = null;
        this.setContext(data);
        jsParser =getJsParser();
        if (log.isTraceEnabled())
            log.trace((new StringBuilder()).append("Creating instance of DataSource '").append(data.getName()).append("'").toString());
        // If dataSource used as other build in datasource ,will not contain a TdataSouece.
        if (data.getTdataSource() != null && DataUtil.isNotEqual(data.getTdataSource().getServerType(), EserverType.CUSTOM)) {
            autoFitDS(this).buildDS(this).validateDS(this);
        }
    }

    /**
     * @return
     */
   protected DataTypeMap getConfig() throws SlxException{
           ConfigureUnitManager cum = sc.getBean(org.solmix.api.cm.ConfigureUnitManager.class);
           ConfigureUnit cu=null;
           try {
               cu = cum.getConfigureUnit(getPID());
           } catch (IOException e) {
               throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
           }
           if (cu != null)
               return cu.getProperties();
           else
               return new DataTypeMap();
       }
    /**
     * @return
     */
    protected  String getPID() {
        return "org.solmix.framework.datasource";
    }

    /**
     * Auto fit the datasource.<br>
     * <b>Note:</b><br>
     * as a part of process to create a new datasource. sub datasource want to custom design would be override it.
     * 
     * @param ds
     * @return
     * @throws SlxException
     */
    protected BasicDataSource autoFitDS(BasicDataSource ds) throws SlxException {
        _autoGenerateSchema(ds)._autoGetSuperDS(ds);
        return ds;

    }

    protected BasicDataSource buildDS(BasicDataSource ds) throws SlxException {
        _buildSuperDS(ds)._buildRequires(ds)._buildRequireRoles(ds)._buildAllFields(ds)._buildNativeFiles(ds);
        return ds;

    }

    protected BasicDataSource validateDS(BasicDataSource ds) throws SlxException {
        if (ds.getContext() == null)
            throw new SlxException(Tmodule.DATASOURCE, Texception.DEFAULT, "Datasource must init with a context");
        return ds;

    }

    @Override
    public Map fetchById(Object id) throws Exception {
        if (data.getPrimaryKeys() == null) {
            throw new Exception("Cannot fetch by ID - DataSource has no primary key field");
        } else {
            Map criteria = new HashMap();
            criteria.put(data.getPrimaryKey(), id);
            DSRequest req = new DSRequestImpl(getName(), Eoperation.FETCH);
            req.getContext().setCriteria(criteria);
            // req.context =this
            DSResponse resp = req.execute();
            return resp.getRecord();
        }
    }

    /**
     * @return the dataSourceGenerator
     */
    @Override
    public synchronized DataSourceGenerator getDataSourceGenerator() {
        if (dataSourceGenerator == null)
            dataSourceGenerator = new BasicGenerator(this);
        return dataSourceGenerator;
    }

    /**
     * @param dataSourceGenerator the dataSourceGenerator to set
     */
    @Override
    public void setDataSourceGenerator(DataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.IType#getName()
     */
    @Override
    public String getName() {
        if (dsName == null)
            dsName = data.getName();
        return dsName;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#getDataSourceData()
     */
    @Override
    public DataSourceData getContext() {
        assert data != null;
        return data;
    }

    @Override
    public void setContext(DataSourceData context) {
        this.data = context;
    }

    @Override
    public DSResponse execute(Eoperation operationBindingType, String operationBindingID) throws SlxException {
        DSRequest req = new DSRequestImpl();
        req.getContext().setOperation(operationBindingID);
        req.getContext().setOperationType(operationBindingType);
        return execute(req);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see org.solmix.api.datasource.DataSource#execute(org.solmix.api.datasource.DSRequest)
     */
    @Override
    public DSResponse execute(DSRequest req) throws SlxException {
        if (req == null)
            return null;
        req.registerFreeResourcesHandler(this);
        try {
            // avoid a dsrequest without datasource.used for the processor from datasource not from dsrequest.
            // for example:
            if (req.getDataSource() == null && req.getDataSourceName() == null) {
                req.setDataSource(this);
            }
            if (!req.isBeenThroughDMI()) {
                DSResponse _dsResponse = ServiceDataSource.execute(req, req.getRpc(), req.getRequestContext());
                if (_dsResponse != null)
                    return _dsResponse;
            }
            Eoperation _opType = req.getContext().getOperationType();
            if (_opType != Eoperation.CUSTOM) {
                DSResponse validationFailure = validateDSRequest(req);
                if (validationFailure != null)
                    return validationFailure;
            }
            req.setRequestStarted(true);

            if (DataTools.isFetch(_opType)) {
                return executeFetch(req);
            } else if (DataTools.isRemove(_opType)) {
                return executeRemove(req);

            } else if (DataTools.isUpdate(_opType)) {
                return executeUpdate(req);
            } else if (DataTools.isAdd(_opType)) {
                return executeAdd(req);

            } else if (DataTools.isReplace(_opType)) {
                return executeReplace(req);
            } else if (DataTools.isDownload(_opType)) {
                return executeDownload(req);
            } else if (DataTools.isValidate(_opType)) {
                return executeValidate(req);
            } else {
                return executeCustomer(req);
            }
        } finally {
            if (req.getContext().isFreeOnExecute()) {
                req.freeResources();
            }
        }
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeCustomer(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     */
    public DSResponse executeValidate(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeDownload(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeReplace(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeRemove(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeUpdate(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    public DSResponse executeFetch(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    protected DSResponse notSupported(DSRequest req) throws SlxException {
        throw new SlxException(Tmodule.DATASOURCE, Texception.DS_NO_SUPPORT_OPERATION_TYPE, (new StringBuilder()).append("Operation type '").append(
            req.getContext().getOperationType()).append("' not supported by this DataSource (").append(getServerType()).append(")").toString());
    }

    public DSResponse executeAdd(DSRequest req) throws SlxException {
        return notSupported(req);
    }

    @Override
    public DSResponse validateDSRequest(DSRequest req) throws SlxException {
        if (req.isBeenThroughValidation())
            return null;
        req.setBeenThroughValidation(true);
        List<Object> errors = validateDSRequst(this, req);
        if (errors != null) {
            LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).info((new StringBuilder()).append("Validation error: ").append(DataTools.prettyPrint(errors)).toString());
            DSResponse dsResponse = new DSResponseImpl(this,req);
            dsResponse.getContext().setStatus(Status.STATUS_VALIDATION_ERROR);
            dsResponse.getContext().setErrors(errors);
            return dsResponse;
        } else {
            return null;
        }
    }

    /**
     * Validation DataSource and relative DSRequest data.this Datasource operation must be a modification operation (
     * {@link org.solmix.fmk.util.DataTools#isModificationOperation DataTools.isModificationOperation} ) or
     * {@link org.solmix.api.jaxb.Eoperation#VALIDATE VALIDATE} operation. and
     * {@link org.solmix.api.data.DataSourceData#isValidateRecords()} is true.
     * 
     * @param ds basic datasource.
     * @param request datasource request.
     * @return
     * @throws SlxException
     */
    public List<Object> validateDSRequst(BasicDataSource ds, DSRequest request) throws SlxException {
        Eoperation _opType = request.getContext().getOperationType();
        if (!DataTools.isModificationOperation(_opType) && _opType != Eoperation.VALIDATE)
            return null;
        if (!ds.getContext().isValidateRecords())
            return null;
        // Initial Validation context.
        ValidationContext vcontext = ValidationContext.instance();
        if (request.getContext().getValidationMode() != null && request.getContext().getValidationMode().equals("partial"))
            vcontext.setPropertiesOnly(true);
        vcontext.setVtype(Vtype.DS_REQUEST);
        vcontext.setRpcManager(request.getRpc());
        vcontext.setRequestContext(request.getRequestContext());
        vcontext.setDSRequstContext(request.getContext());
        vcontext.setVfactory(ValidationEventFactory.getFieldValidator());
        if (request.getContext().getOperationType() == Eoperation.UPDATE)
            vcontext.setPropertiesOnly();
        request.setValidatedValues(ds.toRecords(request.getContext().getValueSets(), vcontext));
        if (log.isDebugEnabled())
            log.debug("post-validation valueSet: \n" + DataTools.prettyPrint(request.getContext().getValueSets()));
        Map<String, Object> errors = vcontext.getErrors();
        if (errors != null)
            return new ArrayList<Object>(errors.values());
        else
            return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#clearState()
     */
    @Override
    public void clearState() {
        if (data.getSuperDS() != null) {
            DefaultDataSourceManager.freeDataSource(data.getSuperDS());
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#freeResources()
     */
    @Override
    public void freeResources() {
        DefaultDataSourceManager.freeDataSource(this);

    }

    /**
     * Indicate whether join transaction or not.request transaction stage override datasource transaction stage.
     * 
     * @param req
     * @return
     */
    @Override
    public boolean shouldAutoJoinTransaction(DSRequest req) throws SlxException {
        if (req != null && req.getRpc() != null) {
            Boolean reqOverride = req.getJoinTransaction();
            if (reqOverride != null)
                return reqOverride.booleanValue();
        }
        Boolean work = autoJoinAtOperationLevel(req);
        if (work == null) {
            // check datasource level
            work = autoJoinAtDataSourceLevel();
            if (work == null) {
                if (req != null && req.getRpc() != null) {
                    TransactionPolicy policy = req.getRpc().getTransactionPolicy();
                    if (policy == TransactionPolicy.NONE)
                        return false;
                    if (policy == TransactionPolicy.ALL)
                        return true;
                }
                work = autoJoinAtProviderLevel(req);
                if (work == null)
                    work = autoJoinAtGlobalLevel(req);
            }
        }
        if (work == null)
            return false;
        else
            return work.booleanValue();

    }

    @Override
    public Object getTransactionObject(DSRequest req) throws SlxException {
        if (req == null)
            throw new SlxException(Tmodule.DATASOURCE, Texception.OBJECT_IS_NULL, "Datasource request is null");
        if (req.getRpc() == null)
            return null;
        else
            return req.getRpc().getContext().getAttribute(getTransactionObjectKey());
    }

    /**
     * Used this key to cache transaction object in context,the subclass may override this method to used it.
     * @return
     */
    @Override
    public String getTransactionObjectKey() throws SlxException {
        return null;
    }

    /**
     * @param req
     * @return
     * @throws SlxException
     */
    protected Boolean autoJoinAtProviderLevel(DSRequest req) throws SlxException {
        return false;
    }

    /**
     * @return
     */
    protected Boolean autoJoinAtDataSourceLevel() {
        return data.getTdataSource().isAutoJoinTransactions();
    }

    protected Boolean autoJoinAtGlobalLevel(DSRequest req) throws SlxException {
        String autoJoin = DatasourceCM.getProperties()
            .getString(DatasourceCM.P_AUTO_JOIN_TRANSACTIONS);
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

    /**
     * @param req
     * @return
     */
    protected Boolean autoJoinAtOperationLevel(DSRequest req) {
        Eoperation opType = req.getContext().getOperationType();
        String opId = req.getContext().getOperationId();
        ToperationBinding operationBinding = data.getOperationBinding(opType, opId);
        if (operationBinding == null)
            return null;
        else
            return operationBinding.isAutoJoinTransactions();
    }

    protected boolean policyShouldOverrideConfig(DSRequest req) throws SlxException {
        if (req != null && req.getRpc() != null) {
            Boolean reqOverride = req.getJoinTransaction();
            if (reqOverride != null)
                return reqOverride.booleanValue();
        }
        Boolean work = autoJoinAtOperationLevel(req);
        if (work == null)
            work = autoJoinAtDataSourceLevel();
        return work == null;
    }

    /**
     * @param req
     * @param b
     * @return
     * @throws SlxException
     */
    public boolean shouldAutoStartTransaction(DSRequest req, boolean ignoreExistingTransaction) throws SlxException {
        if (req == null)
            return false;
        if (!shouldAutoJoinTransaction(req))
            return false;
        if (req.getRpc() == null)
            return false;
        boolean isUpdate = DataTools.isModificationRequest(req);
        boolean shouldOverride = policyShouldOverrideConfig(req);
        TransactionPolicy policy = req.getRpc().getTransactionPolicy();
        if (isUpdate) {
            if (shouldOverride) {
                if (policy == TransactionPolicy.NONE)
                    return false;
            }
            return true;
        } else {
            if (shouldOverride) {
                switch (policy) {
                    case NONE:
                        return false;
                    case ALL:
                        return true;
                    case ANY_CHANGE:
                        return req.getRpc().requestQueueIncludesUpdates();
                    case FROM_FIRST_CHANGE:
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#toRecords(java.lang.Object, java.lang.Object)
     */

    public Object toRecords(Object data, ValidationContext vcontext) throws SlxException {
        if (data instanceof List<?>) {
            List<Object> _recordList = (List) data;
            long start = System.currentTimeMillis();
            Object result = toRecords(_recordList, vcontext);
            long end = System.currentTimeMillis();
            String __info = new StringBuilder().append("Done validating ").append(_recordList.size()).append(" '").append(getName()).append(
                "'s at path '").append(vcontext.getPath()).append("': ").append(end - start).append("ms").append(
                _recordList.size() != 0 ? (new StringBuilder()).append(" (avg ").append((end - start) / _recordList.size()).append(")").toString()
                    : "").toString();
            getEventWork().createAndFireTimeEvent(end - start, __info);
            LoggerFactory.getLogger(SlxLog.TIME_LOGNAME).debug(__info);
            return result;
        }
        long start = System.currentTimeMillis();
        Object result = toRecord(data, vcontext);
        long end = System.currentTimeMillis();
        String __info = (new StringBuilder()).append("Done validating a '").append(getName()).append("' at path '").append(vcontext.getPath()).append(
            "': ").append(end - start).append("ms").toString();
        getEventWork().createAndFireTimeEvent(end - start, __info);
       LoggerFactory.getLogger(SlxLog.TIME_LOGNAME).debug(__info);
        return result;
    }
    public EventWorker getEventWork() {
        if (worker == null) {
            EventWorkerFactory factory = EventWorkerFactory.getInstance();
            worker = factory.createWorker(sc);
        }
        return worker;
    }
    public Object toRecords(List<Object> data, ValidationContext context) throws SlxException {
        if (data == null)
            return null;
        if (log.isDebugEnabled())
            log.debug((new StringBuilder()).append("Validating ").append(data.size()).append(" '").append(getName()).append("'s at path '").append(
                context.getPath()).append("'").toString());
        List<Object> records = new ArrayList<Object>();
        for (int i = 0; i < data.size(); i++)
            records.add(toRecord(data.get(i), context));

        return records;
    }

    /**
     * format the object to datasource record.
     * 
     * @param object
     * @param context
     * @return
     */
    protected Object toRecord(Object data, ValidationContext vcontext) throws SlxException {
        if (data == null)
            return null;
        if (log.isDebugEnabled())
            log.debug((new StringBuilder()).append("Validating a '").append(getName()).append("' at path '").append(vcontext.getPath()).append("'").toString());
        if (data instanceof Element) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.NO_SUPPORT, "no support " + data.getClass().getName()
                + " used as a request object.");
            // TODO
        } else if (data instanceof Map<?, ?>) {
            Map<Object, Object> record = (Map<Object, Object>) data;
            if (record.keySet().size() == 1 && (record.get("ref") instanceof String))
                return new JSExpression((String) record.get("ref"));
            vcontext.addPath(getName());
            vcontext.addToTemplateContext("datasource", this);
            for (Object key : record.keySet()) {
                String fieldName = (String) key;
                Tfield field = this.data.getField(fieldName);
                Object value = record.get(fieldName);
                if (field == null)
                    handleExtraValue(record, fieldName, value, vcontext);
                else if (value != null && !(value instanceof JSExpression))
                    record.put(fieldName, validateFieldValue(record, field, value, vcontext));
            }// END record CYCLE
            checkStructure(record, vcontext);
            checkAutoConstruct(record, vcontext);
            vcontext.removePathSegment();
            return record;
        } else {
            if (!vcontext.isIdAllowed() || !(data instanceof String))
                LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).warn((new StringBuilder()).append("Unexpected Java type '").append(data.getClass()).append(
                    "' passed to DataSource '").append(getName()).append("'").append(" at path '").append(vcontext.getPath()).append("'").toString());
            return data;
        }
    }

    /**
     * @param record
     * @param vcontext
     */
    protected void checkAutoConstruct(Map<Object, Object> record, ValidationContext vcontext) {
        // TODO Auto-generated method stub
    }

    /**
     * @param record
     * @param vcontext
     * @throws SlxException
     */
    protected void checkStructure(Map<Object, Object> record, ValidationContext vcontext) throws SlxException {
        List<String> fieldNames = this.data.getFieldNames();
        if (fieldNames != null)
            for (String name : fieldNames) {
                Tfield field = data.getField(name);
                Object value = record.get(name);
                if (checkRequired(record, field, value, vcontext) && (value != null || record.containsKey(name))) {
                    if (value instanceof JSExpression)
                        return;
                    if (field.isMultiple()!=null&&field.isMultiple())
                        value = DataUtil.makeListIfSingle(value);
                    // if (field.getUniqueProperty() != null) {
                    // value=DataUtil.indexOnProperty(DataUtil.makeListIfSingle(value), field.getUniqueProperty());
                    // }
                }
            }

    }

    /**
     * @param record
     * @param field
     * @param value
     * @param vcontext
     * @return
     * @throws SlxException
     */
    protected boolean checkRequired(Map<Object, Object> record, Tfield field, Object value, ValidationContext vcontext) throws SlxException {
        if (field.isRequired() !=null&&field.isRequired()&& ("".equals(value) || value == null && (!vcontext.isPropertiesOnly() || record.containsKey(field.getName())))) {

            vcontext.addError(field.getName(), DefaultValidators.localizedErrorMessage(new ErrorMessage("%validator_requiredField"), vcontext));
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param record
     * @param field
     * @param value
     * @param vcontext
     * @return
     * @throws SlxException
     */
    protected Object validateFieldValue(Map<Object, Object> record, Tfield field, Object value, ValidationContext vcontext) throws SlxException {
        return validateFieldValue(record, field, null, value, vcontext);
    }

    /**
     * @param record
     * @param name
     * @param object
     * @param value
     * @param vcontext
     * @return
     * @throws SlxException
     */
    protected Object validateFieldValue(Map<Object, Object> record, Tfield field, IType declaredType, Object value, ValidationContext vcontext)
        throws SlxException {
        String _fieldName = field.getName();
        vcontext.addPath(_fieldName);
        vcontext.addToTemplateContext("field", field);
        vcontext.addToTemplateContext("record", record);
        IType _fieldType = getFieldType(field, vcontext);
        IType type = declaredType == null ? _fieldType : declaredType;
        if (type == null && field != null) {
            String __vinfo = (new StringBuilder()).append("No such type '").append(field.getType()).append("', not processing field value at ").append(
                vcontext.getPath()).toString();
            getEventWork().createFieldValidationEvent(Level.WARNING, __vinfo);
            vcontext.removePathSegment();
            return value;
        }
        if (LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).isDebugEnabled()) {
            String __vinfo = (new StringBuilder()).append("Validating field:").append(vcontext.getPath()).append(" as ").append(getName()).append(".").append(
                _fieldName).append(" type: ").append(type.getName()).toString();
            getEventWork().createFieldValidationEvent(Level.DEBUG, __vinfo);
        }
        if (field != null && !vcontext.isIdAllowed()) {
            vcontext.setIdAllowed(true);
        }
        vcontext.setCurrentRecord(record);
        vcontext.setCurrentDataSource(this);
        Object __return = type.create(value, vcontext);
        vcontext.removePathSegment();
        return __return;
    }

    protected IType getFieldType(Tfield field, ValidationContext context) {
        String _fieldName = field.getName();
        String _typeId = field.getType().value();
        IType type = this.data.getCachedFiledType(_fieldName);
        if (type != null)
            return type;

        boolean _isEnumType = false;
        if (field.getType() == Efield.ENUM)
            _isEnumType = true;
        boolean _explicitExists = false;

        List<Tvalidator> fieldValidators = null;
        if (field.getValidators() != null)
            fieldValidators = field.getValidators().getValidator();

        if (field.getForeignKey() == null)
            _explicitExists = false;
        if (fieldValidators == null) {
            _explicitExists = false;
        }
        BulidInType baseType = getBuildInType(field.getType(), context);
        Object typeValidators = null;
        if (baseType != null)
            typeValidators = baseType.getValidators();
        List<Object> allValidators = DataUtil.makeListIfSingle(DataUtil.combineAsLists(typeValidators, fieldValidators));
        if (allValidators == null)
            allValidators = new ArrayList();
        boolean _foundvm = false;
        List list = null;
        // TODO
        // found in validator.
        if (_isEnumType && !_foundvm)
            if (field.getValueMap() != null) {
                list = field.getValueMap().getValue();
                if (field.getValueMap().getValue() == null || field.getValueMap().getValue().isEmpty()) {
                    log.warn((new StringBuilder()).append("invalid field of enum type has no field.valueMap at field: ").append(_fieldName).append(
                        " of type: ").append(_typeId).toString());
                } else {
                    if (log.isDebugEnabled())
                        log.debug((new StringBuilder()).append("for field: ").append(_fieldName).append(
                            " adding automatically generated isOneOf validator with values: ").append(DataTools.prettyPrint(list)).toString());
                    allValidators.add(0, DataUtil.buildMap("type", "isOneOf", "valueMapList", list));
                }
            }
        if (LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).isDebugEnabled())
            LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).debug((new StringBuilder()).append("Creating field validator for field ").append(getName()).append(".").append(
                field.getName()).append(", of simple type: ").append(field.getType()).append(", with inline validators: ").append(fieldValidators).append(
                ", and type validators: ").append(typeValidators).toString());
        type = new BulidInType(_typeId, allValidators);
        this.data.addCachedFieldType(_fieldName, type);
        return type;
    }

    protected BulidInType getBuildInType(Efield fieldType, ValidationContext context) {
        Object validator = buildInValidator.get(fieldType);
        if (validator != null)
            return new BulidInType(fieldType.value(), validator);
        else
            return null;

    }

    /**
     * Handled extra value not in the datasource configuration context.
     * 
     * @param record
     * @param fieldName
     * @param value
     * @param vcontext
     */
    private void handleExtraValue(Map<Object, Object> record, String fieldName, Object value, ValidationContext vcontext) {
        if (value != null) {
            LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).debug((new StringBuilder()).append("Value provided for unknown field: ").append(getName()).append(".").append(fieldName).append(
                ": value is: ").append(value).toString());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#hasRecord(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean hasRecord(String realFieldName, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#transformFieldValue(org.solmix.api.jaxb.Tfield, java.lang.Object)
     */
    @Override
    public Object transformFieldValue(Tfield field, Object obj) {
        return obj;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#escapeValueForWhereClause(java.lang.Object, java.lang.Object)
     */
    @Override
    public String escapeValue(Object value, Object field) {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#getProperties(java.lang.Object)
     */
    @Override
    public Map<Object, Object> getProperties(Object data) {
        return getProperties(data, true, true);
    }

    public Map<Object, Object> getProperties(Object obj, boolean dropExtraFields, boolean dropIgnoredFields) {
        return getProperties(obj, ((Collection) (null)), dropExtraFields, dropIgnoredFields);
    }

    public Map<Object, Object> getProperties(Object obj, boolean dropExtraFields, boolean dropIgnoredFields, ValidationContext validationContext) {
        return getProperties(obj, null, dropExtraFields, dropIgnoredFields, validationContext);
    }

    public Map<Object, Object> getProperties(Object obj, Collection<String> propsToKeep) {
        return getProperties(obj, propsToKeep, false, false);
    }

    public Map<Object, Object> getProperties(Object obj, Collection<String> propsToKeep, boolean dropExtraFields, boolean dropIgnoredFields) {
        return getProperties(obj, propsToKeep, dropExtraFields, dropIgnoredFields, null);
    }

    public Map<Object, Object> getProperties(Object data, Collection<String> popToKeep, boolean dropExtraFields, boolean dropIgnoreFields,
        ValidationContext validationContext) {
        Map<Object, Object> result = new LinkedMap();
        if (data == null)
            return null;
        Map<Object, Object> source = null;
        Set<String> outProperties = new HashSet<String>();
        if (popToKeep != null)
            outProperties.addAll(popToKeep);
        List<String> prop = new ArrayList<String>();

        List<Tfield> __f = this.data.getFields();
        if (__f == null)
            return Collections.emptyMap();
        for (Tfield field : __f) {
            if (dropIgnoreFields && DataUtil.booleanValue(field.isIgnore()))
                continue;
            if (dropExtraFields && field.getType() == Efield.UNKNOWN)
                continue;
            prop.add(field.getName());
        }
        if (prop != null)
            outProperties.addAll(prop);
        if (data instanceof Map<?, ?>) {
            source = (Map<Object, Object>) data;
            for (Object key : source.keySet()) {
                if (outProperties.contains(key)) {
                    result.put(key, source.get(key));
                }
            }
        } else {
            try {
                result = DataUtil.getProperties(data, outProperties);
            } catch (Exception e) {
                result = null;
                log.warn("transform bean object to map failed .caused by" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#isAdvancedCriteria(java.util.Map)
     */
    @Override
    public boolean isAdvancedCriteria(Map<String, ?> criteria) {
        if (criteria == null)
            return false;
        String constructor = (String) criteria.get("_constructor");
        if ("AdvancedCriteria".equals(constructor))
            return true;
        String fieldName = (String) criteria.get("fieldName");
        String operator = (String) criteria.get("operator");
        return data.getField("fieldName") == null && data.getField("operator") == null && data.getField(fieldName) != null && operator != null;
    }

    /**
     * Auto generate DataSource,if set <b>autoDeriveSchema = true</b>
     * 
     * @param ds
     * @return
     * @throws SlxException
     */
    protected BasicDataSource _autoGenerateSchema(BasicDataSource ds) throws SlxException {
        DataSourceData data = ds.getContext();
        // auto generate schema.
        if (DataUtil.booleanValue(data.getTdataSource().isAutoDeriveSchema())) {
            DataSourceGenerator gen = getDataSourceGenerator();
            DataSource autoSchema = null;
            if (gen == null) {
                String __info = "Config file of " + data.getUrlString() + " set autoDeriveSchema is true, but the DataSource type:["
                    + this.getServerType() + "] not supprote auto derive schema.";
                throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR, __info);
            } else {
                if(log.isTraceEnabled()){
                    log.trace(new StringBuilder().append("the datasource set autoDeriveSchema is true,used DataSourceGenerator:")
                        .append(gen.getClass().toString()).append(" to generate schema").toString());
                }
                autoSchema = gen.deriveSchema(data);
            }
            //cache auto derived datasource schema
            if (autoSchema != null) {
                data.setAutoDeriveSchema(autoSchema);
            }
        }
        return ds;

    }

    protected BasicDataSource _autoGetSuperDS(BasicDataSource ds) throws SlxException {
        String _superName = ds.getContext().getTdataSource().getInheritsFrom();
        if (DataUtil.isNotNullAndEmpty(_superName)) {

            DataSource _super = DefaultDataSourceManager.getDataSource(_superName);
            if (_super != null) {
                data.setSuperDSName(_superName);
                data.setSuperDS(_super);
                if(log.isTraceEnabled())
                    log.trace("Found the super datasource:"+_superName+" for "+ds.getName());
            } else {
                log.warn("can not found the super datasource [" + _superName + "] of datasource [" + data.getName() + "]");
            }
        }
        return ds;
    }

    /**
     * Used the auto derived schema as a super datasource.
     * @return
     * @throws SlxException
     */
    protected BasicDataSource _buildSuperDS(BasicDataSource ds) throws SlxException {
        DataSourceData context = ds.getContext();
        boolean autoDerive = DataUtil.booleanValue(ds.getContext().getTdataSource().isAutoDeriveSchema());
        Object schema = ds.getContext().getAutoDeriveSchema();
        // pro process datasource
        if (autoDerive && schema != null) {
            if (ds.getContext().getSuperDS() == null && schema instanceof DataSource) {
                // pro process super datasource
                DataSource _after = (DataSource) schema;
                ds.getContext().setSuperDS(_after);
                ds.getContext().setSuperDSName(_after.getName());
            }
        }

        // Check out super datasource name setting?
        if (context.getSuperDS() != null && context.getSuperDSName() == null) {
            context.setSuperDSName(context.getSuperDS().getName());
        }
        if (context.getSuperDSName() != null && context.getTdataSource().getInheritsFrom() == null) {
            context.getTdataSource().setInheritsFrom(context.getSuperDSName());
        }
        return ds;

    }

    /**
     * @param ds
     */
    protected BasicDataSource _buildRequires(BasicDataSource ds) throws SlxException {
        DataSourceData data = ds.getContext();
        /** requires */
        Tsecurity sec = data.getTdataSource().getSecurity();
        if (data.getRequires() == null && sec != null && DataUtil.isNotNullAndEmpty(sec.getRequires())) {

            String value = sec.getRequires();
            data.setRequires(new VelocityExpression(value));
        }
        return ds;

    }

    protected BasicDataSource _buildRequireRoles(BasicDataSource ds) throws SlxException {
        DataSourceData data = ds.getContext();
        Tsecurity sec = data.getTdataSource().getSecurity();
        /** requireRols */
        if (data.getRequiresRoles() == null && sec != null && DataUtil.isNotNullAndEmpty(sec.getRequireRoles())) {
            List<String> equiresRoles = null;

            String req[] = sec.getRequireRoles().split(";");
            for (String key : req) {
                equiresRoles = new ArrayList<String>();
                equiresRoles.add(key);
            }
            data.setRequiresRoles(equiresRoles);
        }
        DataSource _superDS = ds.getContext().getSuperDS();
        if (_superDS == null)
            return ds;
        /*****************************************************************
         * Requires Roles.
         ****************************************************************/
        List<String> _sRequireRoles = _superDS.getContext().getRequiresRoles();
        if (DataUtil.isNotNullAndEmpty(_sRequireRoles))
            ds.getContext().getRequiresRoles().addAll(_sRequireRoles);
        return ds;

    }

    protected BasicDataSource _buildAllFields(BasicDataSource ds) throws SlxException {
        _buildFields(ds);
        DataSource _superDS = ds.getContext().getSuperDS();

        if (_superDS == null)
            return ds;
        _buildFields(_superDS);
        /****************************************************************
         * build fields.
         ***************************************************************/
        Map<String, Tfield> mapFields = new LinkedHashMap<String, Tfield>();
        Map<String, Tfield> _fields = ds.getContext().getMapFields();
        Map<String, Tfield> _sfields = _superDS.getContext().getMapFields();
        if (DataUtil.booleanValue(ds.getContext().getTdataSource().isShowLocalFieldsOnly())) {
            mapFields = _fields;
        } else {
            if (DataUtil.booleanValue(ds.getContext().getTdataSource().isUseParentFieldOrder())) {
                DataUtil.mapMerge(_sfields, mapFields);
                DataUtil.mapMerge(_fields, mapFields);
            } else {
                DataUtil.mapMerge(_fields, mapFields);
                DataUtil.mapMerge(_sfields, mapFields);
            }
        }
        if (DataUtil.isNullOrEmpty(mapFields))
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR, "ds config file must have a filed at last");
        ds.getContext().setMapFields(mapFields);
        return ds;
    }

    protected BasicDataSource _buildNativeFiles(BasicDataSource ds) throws SlxException {
        Map<String, Tfield> fields = ds.getContext().getMapFields();
        if (fields == null)
            return ds;
        for (String fieldName : fields.keySet()) {
            Tfield __f = ds.getContext().getMapFields().get(fieldName);
            if (__f.getName() == null) {
                __f.setName(fieldName);
            }
            if (__f.getType() == null) {
                __f.setType(Efield.TEXT);
            }
            if (__f.isPrimaryKey())
                ds.getContext().addToPrimaryKeys(fieldName);
            if (__f.getNativeName() == null) {
                ds.getContext().setDs2NativeFieldMap(fieldName, fieldName);
            } else {
                ds.getContext().setDs2NativeFieldMap(fieldName, __f.getNativeName());
            }
        }
        /** Native2DSFieldMap */
        ds.getContext().setNative2DSFieldMap(DataUtil.reverseMap(ds.getContext().getDs2NativeFieldMap()));
        return ds;
    }

    protected DataSource _buildFields(DataSource ds) throws SlxException {
        DataSourceData _tmp = ds.getContext();
        /** fields */
        if (DataUtil.isNullOrEmpty(_tmp.getMapFields()) && _tmp.getTdataSource().getFields() != null
            && DataUtil.isNotNullAndEmpty(_tmp.getTdataSource().getFields().getField())) {
            Map<String, Tfield> _tmpFields = new LinkedHashMap<String, Tfield>();

            List<Tfield> fields = _tmp.getTdataSource().getFields().getField();
            for (Tfield _field : fields) {
                String _name = _field.getName();
                // field name is unique
                if (_tmpFields.containsKey(_name)) {
                    Tfield old = _tmpFields.get(_name);
                    String __vinfo = new StringBuilder().append("Field name is unique.").append("the old Field is").append(jsParser.toJavaScript(old)).append(
                        "will replace by:").append(jsParser.toJavaScript(_field)).toString();
                    getEventWork().createFieldValidationEvent(Level.WARNING, __vinfo);
                }
                if (_field.getType() == null) {
                    _field.setType(Efield.TEXT);
                    getEventWork().createFieldValidationEvent(Level.DEBUG, "DS Field not set type used text by default");
                }
                _tmpFields.put(_field.getName(), _field);

                Efield _type = _field.getType();
                String _title = _field.getTitle();
                // add fields
                if (DataTools.isBinaryType(_type)) {
                    Tfield filename = new Tfield();
                    filename.setName(_name + "_filename");
                    filename.setType(Efield.TEXT);
                    filename.setLength(255);
                    filename.setTitle("Name");
                    filename.setHidden(new Boolean(true));
                    filename.setCanEdit(new Boolean(false));
                    filename.setCustomSQL(_field.isCustomSQL());
                    _tmpFields.put(filename.getName(), filename);
                    Tfield filesize = new Tfield();
                    filesize.setName(_name + "_filesize");
                    filesize.setType(Efield.INTEGER);
                    filesize.setTitle("Size");
                    filesize.setHidden(new Boolean(true));
                    filesize.setCanEdit(new Boolean(false));
                    filesize.setCustomSQL(_field.isCustomSQL());
                    _tmpFields.put(filesize.getName(), filesize);
                    Tfield date_created = new Tfield();
                    date_created.setName(_name + "_date_created");
                    date_created.setType(Efield.DATE);
                    date_created.setTitle("Date Created");
                    date_created.setHidden(new Boolean(true));
                    date_created.setCanEdit(new Boolean(false));
                    date_created.setCustomSQL(_field.isCustomSQL());
                    _tmpFields.put(date_created.getName(), date_created);
                }// end Add Fields
            }
            _tmp.setMapFields(_tmpFields);
        }// END BUILD FIELD.
        return ds;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DataSource#getAutoOperationId(org.solmix.api.jaxb.Eoperation)
     */
    @Override
    public String getAutoOperationId(Eoperation _opType) {

        return DataTools.autoCreateOperationID(getName(), _opType);
    }

    protected Map<String, ?> toClientValueMap(Object tds, JXPathContext jxpc, Map<String, Object> parent, boolean isRoot) {
        Map<String, Object> context;
        if (isRoot) {
            context = parent;
        } else {
            context = new HashMap<String, Object>();
        }
        isRoot = false;
        Field[] _fields = tds.getClass().getDeclaredFields();
        if (_fields == null)
            return null;

        Map<String, PropertyDescriptor> properties = null;
        try {
            properties = DataUtil.getPropertyDescriptors(tds);
        } catch (Exception e) {
            // just ignore it.
            return null;
        }

        Set<String> fieldNames = properties.keySet();
        List<String> list = new ArrayList<String>(fieldNames);
        Collections.sort(list);
        for (Field f : _fields) {
            ClientParameter cpara = f.getAnnotation(ClientParameter.class);
            if (cpara != null) {
                ClientParameterType type = cpara.type();
                String path = cpara.path();
                String property = f.getName();
                PropertyDescriptor pd = properties.get(property);
                Method method = pd.getReadMethod();
                Object value = null;
                if (method != null)
                    try {
                        value = method.invoke(tds);
                    } catch (Exception e) {
                        // just ignore it.
                    }
                if (value != null) {
                    switch (type) {
                        case DEFAULT: {
                            if ("./".equals(path)) {
                                context.put(property, value);
                            } else {
                                jxpc.createPathAndSetValue(path, value);
                            }
                        }
                            break;
                        case MAP: {
                            Map<String, ?> child = toClientValueMap(value, jxpc, context, isRoot);
                            if (child != null && child.size() > 0)
                                if ("./".equals(path)) {
                                    context.put(property, child);
                                } else {
                                    jxpc.createPathAndSetValue(path, child);
                                }
                        }
                            break;
                        case ARRAY: {
                            List<?> child = toClientArray(value, jxpc);
                            if ("./".equals(path)) {
                                context.put(property, child);
                            } else {
                                jxpc.createPathAndSetValue(path, child);
                            }
                        }
                            break;
                    }
                }

            }// END if (cpara != null) {
        }
        return context;
    }

    protected List<?> toClientArray(Object list, JXPathContext jxpc) {
        List<Object> _return = null;
        if (list instanceof List<?>) {
            List tmp = (List) list;
            _return = new ArrayList<Object>();
            for (Object o : tmp) {
                _return.add(this.toClientValueMap(o, jxpc, null, false));
            }

        }

        return _return;

    }

    @Override
    public Map<String, ?> toClientValueMap() {
        long _s = System.currentTimeMillis();

        TdataSource tds = data.getTdataSource();
        Map<String, ?> context = ConvertDSContextToMap.toClientValueMap(tds);
        long s_ = System.currentTimeMillis();
        getEventWork().createAndFireTimeEvent(s_ - _s, "time used to convert datasource context to map value");
        return context;

    }

}
