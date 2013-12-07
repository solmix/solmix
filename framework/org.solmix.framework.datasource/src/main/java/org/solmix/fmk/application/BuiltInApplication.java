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

package org.solmix.fmk.application;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.application.Application;
import org.solmix.api.application.ApplicationManager;
import org.solmix.api.application.ApplicationSecurity;
import org.solmix.api.context.Context;
import org.solmix.api.data.DSRequestData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.util.DataTools;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-20 solmix-ds
 */
@SuppressWarnings("unchecked")
public class BuiltInApplication implements Application
{

    public static final String P_AUTHENTICATION_ENABLED="authenticationEnabled";
    
    public static final String P_AUTHORIZATION_ENABLED="authorizationEnabled";
    
    protected   boolean authorizationEnabled;

    protected   boolean authenticationEnabled;
    /**
     * application ID.
     */
    protected String appID = BUILT_IN_APPLICATION;

    private final static Logger log = LoggerFactory.getLogger(BuiltInApplication.class.getName());

    /**
     * datasource request data which contains most of context for ds-request.
     */
    private DSRequestData reqData;

    /**
     * application configuration for this unit.
     */
    protected DataTypeMap appConfig;

    protected UserType definedUserTypes;

    /**
     * http servlet context.
     */
    protected Context context;

    /**
     * operation id.
     */
    protected String operation;

    protected String operationType;

    /**
     * DataSource request
     */
    private DSRequest request;

    /**
     * DataSource response holder.
     */
    private DSResponse result;

    /**
     * Cache operations
     */
    protected Map<String, Roperation> operationsMap;

    /**
     * this application leased other datasource.
     */
    Map<String, DataSource> leasedDataSources;

    
    private ApplicationSecurity security;

    private final String[] fmkDefinedOperations;

    public BuiltInApplication()
    {
        this(null);
    }
    /**
     * @param sc
     */
    public BuiltInApplication(DataTypeMap config)
    {
        this.appConfig=config;
        leasedDataSources = new HashMap<String, DataSource>(2);
        authorizationEnabled=appConfig.getBoolean(P_AUTHORIZATION_ENABLED, false);
        authenticationEnabled=appConfig.getBoolean(P_AUTHENTICATION_ENABLED, false);
        String operations = appConfig.getString("definedOperations", "*");
        fmkDefinedOperations = operations.split(",");
    }


    @Override
    public DataSource getDataSource(String dsName) throws SlxException {
        DataSource ds = leasedDataSources.get(dsName);
        if (ds == null) {
            ds = DefaultDataSourceManager.getDataSource(dsName);
            leasedDataSources.put(dsName, ds);
        }
        return ds;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.Application#execute(org.solmix.api.datasource.DSRequest, java.lang.Object)
     */
    @Override
    public DSResponse execute(DSRequest request, Context context) throws SlxException {
        this.request = request;
        this.context = context;
        reqData = request.getContext();
        result = new DSResponseImpl(request != null ? request.getDataSource() : (DataSource) null, request);
        //check is authenticated
        if(this.authenticationEnabled){
            if(this.security==null){
               throw new java.lang.IllegalStateException("BuildIn application configured enable authentication,but the runtime ENV not have applicationSecurity instance");
            }else{
               if( !security.isAuthenticated()){
                   result.getContext().setStatus(Status.STATUS_LOGIN_REQUIRED);
                   return result;
               }
            }
        }
        operation = reqData.getOperationId();
        String dataSourceName = request.getDataSourceName();
        if (operation == null) {
            Eoperation opType = reqData.getOperationType();
            canPerformAutoOperation(opType);
            operation = DataTools.autoCreateOperationID(dataSourceName, opType);
            if (log.isDebugEnabled()) {
                log.debug("can not found the operationID ,auto created ID:[" + operation + "]");
                MDC.put(SlxLog.LOG_CONTEXT, (new StringBuilder()).append(reqData.getAppID()).append("#").append(operation).toString());
            }
        } else {
            if (log.isTraceEnabled()) {
                MDC.put(
                    SlxLog.LOG_CONTEXT,
                    (new StringBuilder()).append(reqData.getAppID()).append(".").append(dataSourceName.replace('/', '.')).append('#').append(
                        operation).toString());
            }
        }
        DSResponse dsresponse = null;
        /*
         * find the application's special configuration, if no find,use the auto operation.
         */
        try {

            Roperation operationConfig = getOperationConfig(this.operation);
            if (operationConfig == null || operationConfig.getDataSource() == null) {
                log.trace("can not found operation configuration try to perform auto operation");

                String _dsID = request.getDataSourceName();
                Eoperation _opType = reqData.getOperationType();
                canPerformAutoOperation(_opType);
                if (_dsID == null || _opType == null) {
                    String __info = (new StringBuilder()).append("Auto-operation name (").append(operation).append(") must either be of the format ").append(
                        "dataSourceId_operationType or a public zero-argument method").toString();
                    throw new SlxException(Tmodule.DATASOURCE, Texception.DS_NO_OPERATION_DEFINED, __info);
                }
                Map<String, Roperation> operations = getOperationsMap();
                operationConfig = createAutoOperation(operationType, reqData.getRoperation(), request.getDataSourceName());
                operations.put(this.operation, operationConfig);
                request.getContext().setRoperation(operationConfig);
                // request.getContext().getToperation()
            } else {
                this.operationType = operationConfig.getOperationType();
            }
            // no permission
            if (!isPermitted(request, context)) {
                log.warn((new StringBuilder()).append("User does not qualify for any userTypes that are allowed to perform this operation ('").append(
                    operation).append("')").toString());
                result.getContext().setStatus(Status.STATUS_AUTHORIZATION_FAILURE);
            } else {
                executeAppOperation();

            }
            if (result != null && result.getContext().getStatus() == Status.UNSET) {
                result.getContext().setStatus(Status.STATUS_SUCCESS);
            }
            // if ( !result.getContext().statusIsError() )
            dsresponse = result;
        } finally {
            if (log.isTraceEnabled()) {
                MDC.remove(SlxLog.LOG_CONTEXT);
            }
            freeDataSources();
        }
        return dsresponse;
    }
    protected Roperation getOperationConfig(String operation) {
        if (reqData.getRoperation() != null)
            return reqData.getRoperation();
        Roperation opConfig = getOperationsMap().get(operation);
        if (opConfig == null)
            return new Roperation();
        else
            return opConfig;
    }
    public Map<String, Roperation> getOperationsMap() {
        Object opmap = appConfig.get("operations");
        if (opmap == null)
            appConfig.put("operations", new HashMap<Object, Roperation>(4));
        if (operationsMap == null)
            operationsMap = ((Map<String, Roperation>) appConfig.get("operations"));
        return operationsMap;
    }
    public void freeDataSources() throws SlxException {
        if (leasedDataSources.values() != null)
            for (DataSource ds : leasedDataSources.values())
                DefaultDataSourceManager.freeDataSource(ds);

    }
    protected void canPerformAutoOperation(Eoperation opType) throws SlxException {
        if (appID.equals(BUILT_IN_APPLICATION) || appID.equals(DEFAULT_APPLICATION))
            if (definedUserTypes == UserType.ANONY_USER) {
                throw new SlxException(Tmodule.APP, Texception.SECURITY_DENIED,
                    (new StringBuilder()).append("DENIED attempt to execute auto operation '").append(operation).append(
                        "' bound to the auto-generated default application ").append("because authorization is currently enabled").toString());
            } else {
                Boolean definedOperationsOnly = appConfig.getBoolean("definedOperationsOnly", false);

                Roperation opConfig = getOperationConfig(operation);
                if (definedOperationsOnly != null && definedOperationsOnly.booleanValue() && !containsOperationType(opType)
                    && (opConfig == null || opConfig.getDataSource() == null))
                    throw new SlxException(Tmodule.APP, Texception.APP_CONFIG_DENIED, new StringBuilder().append(
                        "DENIED attempt to execute auto operation '").append(operation).append("' bound to the application '").append(appID).append(
                        "' because").append(" this application is configued for defined operations").append(
                        " only and there is no definition for this operation").append(" in the app file").toString());
            }

    }
    protected boolean containsOperationType(Eoperation opType) {
        if (opType == null)
            return false;
        if (fmkDefinedOperations[0].equals("*"))
            return true;
        for (String op : fmkDefinedOperations) {
            if (op.equals(opType.value())) {
                return true;
            }
        }
        return false;
    }
    protected Roperation createAutoOperation(String operationType, Roperation operation, String dataSourceName) {

        if (operation == null) {
            operation = new Roperation();
        }
        operation.setDataSource(dataSourceName);
        operation.setOperationType(operationType);

        return operation;
    }
    private void executeAppOperation() throws SlxException {
        String _dsName = request.getDataSourceName();
        if (_dsName == null || _dsName.isEmpty()) {
            String __info = (new StringBuilder()).append("No public zero-argument method named '_").append(operation).append(
                " and request does not specify a DataSource to use for a default operation").append(" - unable to proceed.").toString();
            throw new SlxException(Tmodule.APP, Texception.APP_NO_DS_OR_OPERATION_DEFIEND, __info);
        }
        executeDefaultDSOperation();

    }
    private void executeDefaultDSOperation() throws SlxException {
        String _dsName = request.getDataSourceName();
        DataSource _ds = request.getDataSource();
        if (_ds == null) {
            String __vinfo = (new StringBuilder()).append("Can't find dataSource: ").append(_dsName).append(" - please make sure that you have a ").append(
                _dsName).append(".ds.xml").append(" file for it in dsrepo").toString();
            throw new SlxException(Tmodule.APP, Texception.DS_NO_FONUN_DATASOURCE, __vinfo);
        }
        Eoperation _operationType = request.getContext().getOperationType();
        /**
         * Validation request.
         */
        if (request.getContext().getIsClientRequest() && (_operationType == Eoperation.REMOVE || _operationType == Eoperation.UPDATE)) {
            Boolean allowMultiUpdate = Boolean.FALSE;
            ToperationBinding _opBinding = _ds.getContext().getOperationBinding(_operationType, request.getContext().getOperationId());
            if (_opBinding != null)
                allowMultiUpdate = _opBinding.isAllowMultiUpdate();
            if (DataUtil.isNullOrEmpty(_ds.getContext().getPrimaryKeys()) && DataUtil.asBoolean(allowMultiUpdate)) {
                String __info = (new StringBuilder()).append(operationType).append(" operation received ").append("from client for DataSource '").append(
                    _ds.getName()).append("', ").append("operationId '").append(request.getContext().getOperation()).append("'. This ").append(
                    "is not allowed because the DataSource has no ").append("primaryKey.  Either declare a primaryKey or ").append(
                    "set allowMultiUpdate to true on the OperationBinding").toString();
                throw new SlxException(Tmodule.APP, Texception.DS_UPDATE_WITHOUT_PK, __info);
            }
        }
        result = _ds.execute(request);
    }
   /*
    public boolean havePermission(DSRequest request, Context context) throws SlxException {
         check need authenticated or not 
        if (!authenticationEnabled)
            return true;
         find user subject,make sure this thread is been authenticated 
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
        } catch (Exception e) {
        }
         no authentication return false 
        if (subject == null || subject.isAuthenticated())
            return false;
         if not require authorization,return true 
        if (!authorizationEnabled)
            return true;
        if (userTypePermission()) {
            Eoperation opType = request.getContext().getOperationType();
            if (definedOperationPermission(opType)) {
                DataSource ds = request.getDataSource();
                if (ds == null)
                    return true;
                List<String> roles = ds.getContext().getRequiresRoles();
                String opId = request.getContext().getOperationId();
                Tsecurity security = ds.getContext().getOperationBinding(opType, opId).getSecurity();

                String binRoles = security == null ? null : security.getRequireRoles();
                List<String> bRols;
                if (binRoles == null || binRoles.length() == 0)
                    bRols = null;
                bRols = DataUtil.simpleSplit(binRoles, ";");
                if (DataUtil.isNullOrEmpty(roles) && DataUtil.isNullOrEmpty(bRols))
                    return true;
                if (DataUtil.isNotNullAndEmpty(bRols)) {
                    return haveRoles(subject, bRols);
                } else {
                    return haveRoles(subject, roles);
                }

            }
        }
        return false;

    }
    
    protected DataTypeMap loadCustomConfig() {
        appConfig = new DataTypeMap();
        // fmkDefinedOperations
        String operations = appConfig.getString("definedOperations", "*");
        definedUserTypes = UserType.fromValue(appConfig.getInt("userType", 1));
        fmkDefinedOperations = operations.split(",");
        adminUser = appConfig.getString("adminUser", "root");
        return appConfig;
    }
  
    protected boolean userTypePermission() {
        if (definedUserTypes == null) {
            log.trace("No userTypes defined, allowing anyone access to all operations for this application");
            return true;
        }
        switch (definedUserTypes) {
            case ANONY_USER:
                return true;
            case ADMIN_USER: {
                return Authentication.getUsername() == null ? false : Authentication.getUsername().equalsIgnoreCase(adminUser);
            }
            case AUTH_USER: {
                return Authentication.getUsername() == null ? false : true;
            }
        }
        return false;

    }

    protected boolean definedOperationPermission(Eoperation opType) {
        if (opType == null)
            return true;
        return this.containsOperationType(opType);
    }

    public boolean datasourcePermission(DataSource ds) {
        ds.getContext();
        return false;

    }
  
    protected boolean haveRoles(Subject subject, List<String> roles) {
        boolean[] checkRoleResult = subject.hasRoles(roles);
        boolean havePermission = false;
        for (boolean r : checkRoleResult) {
            if (r)
                havePermission = r;
        }
        return havePermission;
    }

    protected boolean haveRequires(Subject subject, List<String> requires) {
        for (String r : requires) {
            if (!subject.isPermitted(r))
                return false;
        }
        return true;
    }

    public boolean userQualifiesForType(String userType) {
        LoggerFactory.getLogger(SlxLog.AUTH_LOGNAME).info(
            "AppBase::boolean userQualifiesForType(String userType): override this method to provide custom userType qualification logic (base implementation returns true)");
        return true;
    }

    protected boolean containsOperationType(Eoperation opType) {
        if (opType == null)
            return false;
        if (fmkDefinedOperations[0].equals("*"))
            return true;
        for (String op : fmkDefinedOperations) {
            if (op.equals(opType.value())) {
                return true;
            }
        }
        return false;
    }

    *//**
     * @param operation
     * @return
     *//*
    



    *//**
     * @throws SlxException
     *//*
   

    *//**
     * @throws SlxException
     *//*
    private void executeDefaultDSOperation() throws SlxException {
        String _dsName = request.getDataSourceName();
        DataSource _ds = request.getDataSource();
        if (_ds == null) {
            String __vinfo = (new StringBuilder()).append("Can't find dataSource: ").append(_dsName).append(" - please make sure that you have a ").append(
                _dsName).append(".ds.xml").append(" file for it in dsrepo").toString();
            throw new SlxException(Tmodule.APP, Texception.DS_NO_FONUN_DATASOURCE, __vinfo);
        }
        Eoperation _operationType = request.getContext().getOperationType();
        *//**
         * Validation request.
         *//*
        if (request.getContext().getIsClientRequest() && (_operationType == Eoperation.REMOVE || _operationType == Eoperation.UPDATE)) {
            Boolean allowMultiUpdate = Boolean.FALSE;
            ToperationBinding _opBinding = _ds.getContext().getOperationBinding(_operationType, request.getContext().getOperationId());
            if (_opBinding != null)
                allowMultiUpdate = _opBinding.isAllowMultiUpdate();
            if (DataUtil.isNullOrEmpty(_ds.getContext().getPrimaryKeys()) && DataUtil.asBoolean(allowMultiUpdate)) {
                String __info = (new StringBuilder()).append(operationType).append(" operation received ").append("from client for DataSource '").append(
                    _ds.getName()).append("', ").append("operationId '").append(request.getContext().getOperation()).append("'. This ").append(
                    "is not allowed because the DataSource has no ").append("primaryKey.  Either declare a primaryKey or ").append(
                    "set allowMultiUpdate to true on the OperationBinding").toString();
                throw new SlxException(Tmodule.APP, Texception.DS_UPDATE_WITHOUT_PK, __info);
            }
        }
        result = _ds.execute(request);
    }

  */

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.Application#getServerID()
     */
    @Override
    public String getServerID() {

        return ApplicationManager.BUILT_IN_APPLICATION;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.Application#setApplicationSecurity(org.solmix.api.application.ApplicationSecurity)
     */
    @Override
    public void setApplicationSecurity(ApplicationSecurity security) {
        this.security=security;
        
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.application.Application#isPermitted(org.solmix.api.datasource.DSRequest, org.solmix.api.context.Context)
     */
    @Override
    public boolean isPermitted(DSRequest request, Context context) throws SlxException {
        if(this.security!=null){
            return security.isPermitted(request, context);
        }
        return true;
    }
}
