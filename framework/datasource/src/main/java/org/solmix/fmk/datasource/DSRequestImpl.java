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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.application.Application;
import org.solmix.api.application.ApplicationManager;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSRequestData;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.FreeResourcesHandler;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.io.SlxFile;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.fmk.upload.UploadItem;
import org.solmix.fmk.util.DataTools;
import org.solmix.fmk.util.ErrorReport;
import org.solmix.runtime.Context;
import org.solmix.runtime.SystemContext;

/**
 * Implements of {@link org.solmix.api.datasource.DSRequest DSRequest}
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-1-3 solmix-ds
 */
@SuppressWarnings("unchecked")
public class DSRequestImpl implements DSRequest
{

    private static Logger log = LoggerFactory.getLogger(DSRequestImpl.class.getName());

    private DSRequestData data;

    private Application app;

    private DSCall dsc;

    boolean requestStarted;

    private Context requestContext;

    private DataSource dataSource;

    private String dataSourceName;

    private Boolean joinTransaction;
    
    private Boolean freeOnExecute ;

    private boolean partOfTransaction;

    private boolean serviceCalled = false;

    private boolean validated = false;

    FreeResourcesHandler freeResourcesHandler;

    public DSRequestImpl()
    {
        this((Roperation) null);
        data.setAppID(Application.BUILT_IN_APPLICATION);

    }

    public DSRequestImpl(DataSource dataSource)
    {
        this(dataSource, (Eoperation) null, (String) null);
    }

    public DSRequestImpl(String dataSourceName, Eoperation opType)
    {
        this(dataSourceName, opType, (String) null);
    }

    public DSRequestImpl(DataSource dataSource, Eoperation opType)
    {
        this(dataSource, opType, (String) null);
    }

    public DSRequestImpl(DataSource dataSource, Eoperation opType, String operationID)
    {
        this();
        this.dataSource = dataSource;
        data.setOperationType(opType);
        data.setOperation(operationID);
    }

    public DSRequestImpl(String dsName, Eoperation opType, String operationID)
    {
        this();
        this.dataSourceName = dsName;
        data.setOperationType(opType);
        data.setOperation(operationID);
    }

    public DSRequestImpl(String dataSourceName, String opType)
    {
        this(dataSourceName, Eoperation.fromValue(opType));
    }

    public DSRequestImpl(Roperation operation, Context context) throws SlxException
    {
        this(operation);
        if (context != null) {
            this.requestContext = context;
            data.setIsClientRequest(true);
            if (context instanceof WebContext)
                parseUploadedFiles((WebContext) context);
        }
    }

    public DSRequestImpl(DataSource datasource, Eoperation opType, DSCall rpc2)
    {
        this(datasource, opType, (String) null);
        setDSCall(dsc);
    }

    public DSRequestImpl(String dataSourceName, Eoperation opType, DSCall rpc)
    {
        this(dataSourceName, opType, (String) null);
        setDSCall(rpc);
    }

    /**
     * construct function,initial {@link org.solmix.api.datasource.DSRequest}
     * 
     * @param operation
     */
    public DSRequestImpl(Roperation operation)
    {
        data = new DSRequestData();
        data.init();
        if (operation == null)
            return;
        data.setRoperation(operation);
        String _operation = data.getOperationId();

        // parser values.
        if (operation.getValues() != null) {
            data.setValues(operation.getValues());
        }
        if (operation.getCriteria() != null) {
            data.setCriteria(operation.getCriteria());
        }
        if (operation.getOldValues() != null) {
            data.setRawOldValues(operation.getOldValues());
        }
        // operationType and sourceName.
        if (operation.getDataSource() != null) {
            data.setOperationType(operation.getOperationType() == null ? null : Eoperation.fromValue(operation.getOperationType()));
            dataSourceName = operation.getDataSource();
            data.setRepo(operation.getRepo());
        } else if (_operation != null && _operation.indexOf('_') != -1) {
            if (log.isDebugEnabled())
                log.debug("cannot find Datasource name use " + _operation + " transform to datasource");
            data.setOperationType(Eoperation.fromValue(_operation.substring(_operation.lastIndexOf('_') + 1)));
            dataSourceName = _operation.substring(0, _operation.lastIndexOf('_'));
        }
        // Application
        if (!data.getAppID().equals("builtinApplication")) {
            // List<String> qualifiedUserTypes = DataUtils.makeList("*");
            // try {
            // DataUtils.addAll(qualifiedUserTypes, app.userIsOfTypes());
            // } catch (Exception e) {
            // log.error("Can't look up app users", e);
            // }
        }

        if (data.getOutputs() == null) {
            if (operation.getOutputs() != null)
                data.setOutputs(DataUtils.commaSeparatedStringToList(operation.getOutputs()));
        }
        if (data.getSortBy() == null && operation.getSortBy() != null && operation.getSortBy().size() > 1)
            data.setRawSortBy(operation.getSortBy());
    }

    @Override
    public boolean isFreeOnExecute() {
       if(freeOnExecute==null){
           if(getDSCall()!=null)
               return false;
           else
               return true;
       }else{
           return freeOnExecute.booleanValue();
       }
    }

    @Override
    public void setFreeOnExecute(boolean freeOnExecute) {
        this.freeOnExecute = freeOnExecute;
    }
    /**
     * @return the beenThroughDMI
     */
    @Override
    public boolean isServiceCalled() {
        return serviceCalled;
    }

    @Override
    public void setServiceCalled(boolean serviceCalled) {
        this.serviceCalled = serviceCalled;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    /**
     * @return the app
     * @throws SlxException
     */
    public synchronized Application getApp() throws SlxException {
        if (app == null) {
            SystemContext sc;
            if (requestContext instanceof WebContext) {
                sc = ((WebContext) requestContext).getSystemContext();

            } else {
                sc = SlxContext.getThreadSystemContext();
            }
            ApplicationManager am = sc.getExtension(ApplicationManager.class);
            if (am != null)
                app = am.findByID(data.getAppID());
        }
        return app;
    }

    /**
     * @param app the app to set
     */
    public void setApp(Application app) {
        this.app = app;
    }

    /**
     * @return the joinTransaction
     */
    @Override
    public Boolean isCanJoinTransaction() {
        return joinTransaction;
    }

    @Override
    public boolean isJoinTransaction() {
        return partOfTransaction;
    }

    @Override
    public void setJoinTransaction(boolean partOfTransaction) {
        this.partOfTransaction = partOfTransaction;
    }

    /**
     * @param joinTransaction the joinTransaction to set
     * @throws SlxException
     */
    @Override
    public void setCanJoinTransaction(Boolean joinTransaction) throws SlxException {
        if (requestStarted) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_REQUEST_ALREADY_STARTED,
                "Request processing has started;  join transactions setting cannot be changed");
        } else {
            this.joinTransaction = joinTransaction;
            return;
        }
    }

    /**
     * @return the requestStarted
     */
    @Override
    public boolean isRequestStarted() {
        return requestStarted;
    }

    /**
     * Indicate this request already started.
     * 
     * @param requestStarted the requestStarted to set
     */
    @Override
    public void setRequestStarted(boolean requestStarted) {
        this.requestStarted = requestStarted;
    }

    /**
     * @return the dataSourceName
     */
    @Override
    public String getDataSourceName() {
        if (this.dataSourceName == null && this.dataSource != null) {
            dataSourceName = this.dataSource.getName();
        }
        return dataSourceName;
    }

    /**
     * @param dataSourceName the dataSourceName to set
     */
    @Override
    public void setDataSourceName(String dataSourceName) {
        if (this.dataSourceName != null && !this.dataSourceName.equals(dataSourceName))
            DefaultDataSourceManager.freeDataSource(dataSource);
        this.dataSourceName = dataSourceName;
    }

    /**
     * @return the dataSource
     * @throws SlxException
     */
    @Override
    public DataSource getDataSource() throws SlxException {
        if (dataSource == null && getDataSourceName() != null) {
            dataSource = DefaultDataSourceManager.getDataSource(getDataSourceName());
            freeResourcesHandler = dataSource;
        }
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    @Override
    public void setDataSource(DataSource dataSource) {
        if (this.dataSource != null)
            DefaultDataSourceManager.freeDataSource(this.dataSource);
        this.dataSource = dataSource;
        this.dataSourceName = dataSource.getName();
    }

    /**
     * @return the dsc
     */
    @Override
    public DSCall getDSCall() {
        return dsc;
    }

    /**
     * @param dsc the dsc to set
     */
    @Override
    public void setDSCall(DSCall rpc) {
        this.dsc = rpc;
    }

    @Override
    public void setValidatedValues(Object values) {
        if (data.getBeforeValidatedValues() != null) {
            log.warn("setValidatedValues called more than once for this DSRequest object");
            return;
        } else {
            data.setBeforeValidatedValues(data.getRawValues());
            data.setValues(values);
        }
    }

    /**
     * @throws SlxException
     */
    private void parseUploadedFiles(WebContext webContext) throws SlxException {
        Map<String, Object> criteria = data.getCriteria();
        if (criteria != null && criteria.get("download_fieldname") != null) {
            data.setDownloadFieldName((String) criteria.get("download_fieldname"));
            data.setDownloadFileName((String) criteria.get("download_filename"));
        }
        if (!webContext.isMultipart())
            return;
        DataSource _ds = getDataSource();
        DataSourceData _dsData = _ds.getContext();
        int _dftMaxSize = DatasourceCM.getProperties().getInt(DatasourceCM.P_MAX_UPLOAD_FILESIZE);
        Map<String, Object> _values = data.getValues();
        Map<String, Object> _addFields = new HashMap<String, Object>();
        if (_values != null) {
            for (String fieldName : _values.keySet()) {
                Tfield field = _dsData.getField(fieldName);
                if (field != null && DataTools.isBinary(field)) {
                    String filename = SlxFile.canonicalizePath((String) _values.get(fieldName));
                    String shortFilename = filename;
                    if (shortFilename.indexOf("/") != -1)
                        shortFilename = shortFilename.substring(shortFilename.lastIndexOf("/") + 1);
                    UploadItem _file = null;
                    List<Object> errors = null;
                    if (webContext.getRequest().getParameter("singleUpload") != null) {
                        long fileSize = webContext.getRequest().getContentLength();
                        int maxSize = _dftMaxSize;
                        if (field.getMaxFileSize() != null)
                            maxSize = field.getMaxFileSize();
                        if (fileSize > maxSize) {
                            errors = new ArrayList<Object>();
                            ErrorReport errorReport = new ErrorReport();
                            errors.add(errorReport);
                            String __errorString = (new StringBuilder()).append("Size of '").append(shortFilename).append("' (").append(
                                DataUtils.formatFileSize(fileSize)).append(") exceeded maximum allowed file size of ").append(
                                DataUtils.formatFileSize(maxSize)).toString().toString();
                            log.debug(__errorString);
                            errorReport.addError(fieldName, __errorString);
                        }
                    }
                    _file = (UploadItem) webContext.getUploadedFile(fieldName, errors);
                    if (_file != null) {
                        _file.setFieldName(fieldName);
                        _file.setShortFileName(shortFilename);
                        data.addToUploadedFiles(_file);
                        _addFields.put(fieldName + "_filename", shortFilename);
                        _addFields.put(fieldName + "_filesize", new Long(_file.getSize()));
                        _addFields.put(fieldName + "_date_created", new Date());
                    }
                }
            }// end loop values
        }
        data.setValues(DataUtils.mapMerge(_addFields, _values));

    }

    private DSResponse validateDSRequest() throws SlxException {
        if (getDataSourceName() == null) {
            return createResponse(Status.STATUS_VALIDATION_ERROR, "DataSource name must be assigned");
        }
        return null;
    }

    private DSResponse createResponse(Status status, Object... errors) throws SlxException {
        DSResponse dsResponse = new DSResponseImpl(getDataSource(), this);
        dsResponse.setStatus(status);
        dsResponse.setErrors(errors);
        return dsResponse;
    }

    private DSResponse prepareReturn(DSResponse _dsResponse) throws SlxException {
        if (isFreeOnExecute()) {
            freeResources();
            if (dsc != null)
                dsc.freeDataSources();
        }
        return _dsResponse;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#execute()
     */
    @Override
    public DSResponse execute() throws SlxException {
        DSResponse _dsResponse = validateDSRequest();
        if (_dsResponse != null)
            return prepareReturn(_dsResponse);
        try {
            _dsResponse = securityChecks();
            if (_dsResponse != null)
                return prepareReturn(_dsResponse);
            List<Object> _tmpFiles = data.getUploadedFiles();
            if (_tmpFiles != null)
                for (Object o : _tmpFiles) {
                    UploadItem file = (UploadItem) o;
                    List<Object> errors = file.getErrors();
                    if (errors != null) {
                        _dsResponse = createResponse(Status.STATUS_FAILURE, errors.toArray(new Object[errors.size()]));
//                        _dsResponse.getContext().setRequestConnectionClose(true);
                    }
                }// end upload files loop
            if (_dsResponse != null) {
                return prepareReturn(_dsResponse);
            }
            /**
             * checkout datasource is passed DMI datasource or not.
             */
            if (!this.isServiceCalled()) {
                if (dsc != null)
                    dsc.applyEarlierResponseValues(this);
                if (data.getOperationType() == Eoperation.ADD || data.getOperationType() == Eoperation.UPDATE) {
                    hashFieldValues();
                    populateModifierAndCreatorFields(data.getOperationType() == Eoperation.ADD);
                }
                _dsResponse = ServiceDataSource.execute(this, dsc, requestContext, getApp());
            }
            if (_dsResponse == null)
                _dsResponse = getApp().execute(this, requestContext);
            Eoperation opType = data.getOperationType();
            String opId = data.getOperationId();
            ToperationBinding operationBinding = null;
            List<String> outputColumns = new ArrayList<String>();
            if (dataSource != null)
                operationBinding = dataSource.getContext().getOperationBinding(opType, opId);
            if (operationBinding != null && operationBinding.getOutputs() != null) {
                String outputArray[] = operationBinding.getOutputs().split(",");
                for (String str : outputArray)
                    outputColumns.add(str.trim());
            }
            // if(opType == Eoperation.LOADSCHEMA)
            // merge ds request outputs with datasource outputs
            List<String> _output = data.getOutputs();
            if (_output != null) {
                {
                    if (outputColumns.containsAll(_output))
                        outputColumns = _output;
                    else {
                        log.warn((new StringBuilder()).append(
                            "The dsRequest contains a client-specified 'outputs', but this is not a subset of the server-specified 'outputs' for this operation binding (").append(
                            opId).append("). Ignoring the client-specified ").append("value.").toString());
                    }
                }
            }
        /*} catch (Exception e) {
            log.error("execute()", e);
            _dsResponse = new DSResponseImpl(getDataSource(),this);
            _dsResponse.setRawData(e.getMessage());
            _dsResponse.setStatus(Status.STATUS_FAILURE);*/
        } finally {
            if (isFreeOnExecute()) {
                this.freeResources();
                if (dsc != null)
                    dsc.freeDataSources();
            }
        }
        return _dsResponse;
    }


    /**
     * @param b
     * @throws SlxException
     */
    private void populateModifierAndCreatorFields(boolean addMode) throws SlxException {

        if (getDataSource() == null)
            return;
        DataSourceData _dsData = getDataSource().getContext();
        String _modifier = data.getUserId();
        Date _modifierTimestamp;
        if (dsc != null)
            _modifierTimestamp = (Date) dsc.getAttribute("transactionDate");
        else
            _modifierTimestamp = new Date();
        List<Tfield> _fields = _dsData.getFields();
        for (Tfield _field : _fields) {
            if (_field.getType() == Efield.MODIFIER)
                data.getValues().put(_field.getName(), _modifier);
            else if (_field.getType() == Efield.MODIFIER_TIMESTAMP)
                data.getValues().put(_field.getName(), _modifierTimestamp);
            else if (addMode) {
                if (_field.getType() == Efield.CREATOR)
                    data.getValues().put(_field.getName(), _modifier);
                else if (_field.getType() == Efield.CREATOR_TIMESTAMP)
                    data.getValues().put(_field.getName(), _modifierTimestamp);
            }
        }

    }

    /**
     * @throws SlxException
     */
    private void hashFieldValues() throws SlxException {
        if (getDataSource() == null)
            return;
        DataSourceData _dsData = getDataSource().getContext();
        List<Tfield> fields = _dsData.getFields();
        for (Tfield field : fields) {
            if (field.getStoreWithHash() != null) {
                String value = (String) data.getValues().get(field.getName());
                if (value != null)
                    try {
                        value = DataUtils.hashValue(value, field.getStoreWithHash());
                    } catch (Exception e) {
                        throw new SlxException(Tmodule.DATASOURCE, Texception.NO_SUCH_ALGORITHM, "hash fields with no such algorithm.", e);
                    }
                data.getValues().put(field.getName(), value);
            }
        }
    }

    /**
     * Authentication checks.the check order is: OperationID &gt; OperationType &gt; DataSource &gt; Project.
     * <p>
     * {@link javax.servlet.http.HttpServletRequest#getRemoteUser() getRemoteUser} get User. validate User's roles in
     * dsc and httpServletRequest.
     * 
     * @return
     * @throws Exception
     */
    protected DSResponse securityChecks() throws SlxException {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#getContext()
     */
    @Override
    public DSRequestData getContext() {
        return data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#setConf(org.solmix.api.datasource.DSRequestData)
     */
    @Override
    public void setContext(DSRequestData data) {
        this.data = data;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#freeResources()
     */
    @Override
    public void freeResources() {
        if (freeResourcesHandler != null)
            freeResourcesHandler.freeResources();
        dataSource = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#registerFreeResourcesHandler(org.solmix.api.datasource.FreeResourcesHandler)
     */
    @Override
    public void registerFreeResourcesHandler(FreeResourcesHandler handler) {
        freeResourcesHandler = handler;
    }

    @Override
    public boolean isModificationRequest() throws SlxException {
        return DataTools.isModificationOperation(data.getOperationType());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.DSRequest#getRequestContext()
     */
    @Override
    public Context getRequestContext() {
        return requestContext;
    }

    @Override
    public void setRequestContext(Context context) throws SlxException {
        requestContext = context;
    }

}
