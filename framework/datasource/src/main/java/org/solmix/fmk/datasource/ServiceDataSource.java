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

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.solmix.api.application.Application;
import org.solmix.api.application.ApplicationManager;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.ISQLDataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.Tservice;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtils;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.base.ReflectionArgument;
import org.solmix.fmk.call.DSCallImpl;
import org.solmix.fmk.call.ServiceObject;
import org.solmix.runtime.Context;
import org.solmix.runtime.SystemContext;

/**
 * Direct Method Invoke DataSource.
 * 
 * @author solmix.f@gmail.com
 * @version 110041 modified at 2013-7-11
 * @since 0.0.1
 */

public class ServiceDataSource
{

    private static final Logger log = LoggerFactory.getLogger(ServiceDataSource.class.getName());

    public static String VT_TMP_NAME = "_slxResult";

    protected DSRequest request;

    protected DSCall dsc;

    protected Context context;

    protected Application app;

    public ServiceDataSource(DSRequest dsRequest, DSCall dsc, Context context, Application app)
    {
        this.request = dsRequest;
        this.dsc = dsc;
        this.context = context;
        this.app = app;
    }

    /**
     * Check the DataSource is DMI datasource or not ,if this is a DMI datasource execute it and return
     * <code>dsResponse</code>if not return <code>null</code>.
     * 
     * @param dsRequest
     * @param rpc
     * @param requestContext
     * @return
     * @throws SlxException
     */
    public static DSResponse execute(final DSRequest dsRequest, DSCall rpc, Context requestContext, Application app) throws SlxException {
        String appID = app.getServerID();
        String operation = dsRequest.getContext().getOperationId();
        String dataSourceName = dsRequest.getDataSourceName();
        DSResponse response;
        if (log.isDebugEnabled())
            MDC.put(SlxLog.LOG_CONTEXT,
                (new StringBuilder()).append(((Application.BUILT_IN_APPLICATION.equals(appID)||Application.DEFAULT_APPLICATION.equals(appID))?"":appID+"."))
                .append(dataSourceName.replace('/', '.'))
                .append('#').append(operation==null?"":operation)
                .toString());
        try {
            response = (new ServiceDataSource(dsRequest, rpc, requestContext, app)).execute();
        } finally {
            if (log.isDebugEnabled())
                MDC.remove(SlxLog.LOG_CONTEXT);
        }
        return response;
    }

    public static DSResponse execute(final DSRequest dsRequest, DSCall rpc, Context requestContext) throws SlxException {
        String appID = dsRequest.getContext().getAppID();
        Application app = null;
        SystemContext sc;
        if (requestContext instanceof WebContext) {
            sc = ((WebContext) requestContext).getSystemContext();
        } else {
            sc = SlxContext.getThreadSystemContext();
        }
        ApplicationManager am = sc.getExtension(ApplicationManager.class);
        if (am != null)
            app = am.findByID(appID);
        return execute(dsRequest, rpc, requestContext, app);
    }

    public DSResponse execute() throws SlxException {
        request.setServiceCalled(true);
        DataSource _ds = null;
        Eoperation _opType = null;
        String _opID = null;
        DSResponse dsResponse = null;
        Tservice dsSrvConfig = null;
        Tservice opSrvConfig = null;
        Tservice srvConfig = new Tservice();
        ServiceObject serviceObject = null;
        String operationBindingString = null;
        boolean haveExplicitBinding = false;
        boolean opLevleSrvConfig = false;
        boolean connectionIstransactional = true;

        _ds = request.getDataSource();
        if (_ds == null)
            return null;
        if (_ds.getContext().getTdataSource() == null)
            return null;

        _opType = request.getContext().getOperationType();
        _opID = request.getContext().getOperationId();
        ToperationBinding _opBinding = _ds.getContext().getOperationBinding(_opType, _opID);
        haveExplicitBinding = _opBinding != null;
        dsSrvConfig = _ds.getContext().getTdataSource().getService();
        // exclude not DMI configuration datasource
        if (haveExplicitBinding) {
            opSrvConfig = _opBinding.getService();
        }
        boolean findSrvConfig = false;
        if (opSrvConfig != null)
            findSrvConfig = true;
        else {
            if (dsSrvConfig != null) {
                if (haveExplicitBinding && _opBinding.getServerMethod() != null)
                    findSrvConfig = true;
            }
        }
        if (!findSrvConfig)
            return null;

        if (!app.isPermitted(request, context)) {
            dsResponse = new DSResponseImpl(request,Status.STATUS_AUTHORIZATION_FAILURE);
            return dsResponse;
        }
        // operation level service-object configuration override all ds level service-object configuration.
        if (opSrvConfig != null) {
            if (log.isTraceEnabled() && dsSrvConfig != null) {
                log.trace("Tservice :operation level service configuration override all datasource level service configuration");
            }
            srvConfig = opSrvConfig;
            opLevleSrvConfig = true;
        } else
            srvConfig = dsSrvConfig;
        if (log.isTraceEnabled()) {
            String inf = srvConfig.getInterface() == null ? srvConfig.getClazz() : srvConfig.getInterface();
            log.trace("Find server config object named:" + inf + " with method is:" + srvConfig.getMethod());
        }
        ReflectionArgument[] factoryOptionsArgs = { new ReflectionArgument(DSCallImpl.class, dsc, false, false),
            new ReflectionArgument(DSRequestImpl.class, request, false, false) };

        boolean operationIsAuto = _opID == null || _opID.equals(_ds.getAutoOperationId(_opType));
        operationBindingString = new StringBuilder().append("operationBinding for DataSource: ").append(_ds.getName()).append(
            " with operationType : ").append(_opType).append(!operationIsAuto && _opID != null ? ",operationId: " + _opID : "").toString();

        serviceObject = new ServiceObject(srvConfig, context, factoryOptionsArgs, operationBindingString);
        String explicitMethodName = null;
        if (opLevleSrvConfig) {
            explicitMethodName = srvConfig.getMethod();
            if (explicitMethodName != null) {
                if (_opBinding.getServerMethod() != null) {
                    if (log.isTraceEnabled()) {
                        log.trace(new StringBuilder().append("Datasource: ").append(_ds.getName()).append(", operationId: ").append(_opID).append(
                            " found a 'methodName' (").append(explicitMethodName).append(
                            ") on the service-bject configuration AND a 'serverMethod' (").append(_opBinding.getServerMethod()).append(
                            ") on operation-binding config.serviceObject.method takes precedence .and Call:").append(explicitMethodName).toString());
                    }
                }
            } else {
                explicitMethodName = _opBinding.getServerMethod();
            }
        } else {
            if (haveExplicitBinding) {
                explicitMethodName = _opBinding.getServerMethod();
            }
        }
        String methodName = explicitMethodName;
        // no explicit method.use auto generation method.
        if (methodName == null) {
            // use datasource service-object CURD generate method name.
            if (dsSrvConfig != null) {
                String serviceName = dsSrvConfig.getServiceName();
                if (serviceName != null) {
                    String key = serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1);
                    methodName = _opType.value() + key;
                }
            }// END curd generation.
        }
        Class<?> srvObjClass = null;
        try {
            srvObjClass = serviceObject.getServiceClass();
        } catch (Exception exception) {
            throw new SlxException(Tmodule.BASIC, Texception.NO_FOUND, "not found the target service:[" + serviceObject.getServiceAsString() + "]",
                exception);
        }

        if (methodName == null) {
            if (_opID != null && _opID.indexOf("methodName_") == 0) {
                methodName = _opID.substring("methodName_".length());
            } else {
                methodName = _opType.value();
            }
        }
        // validate datasource-request.
        DSResponse validationFailure = _ds.validateDSRequest(request);
        if (validationFailure != null) {
            return validationFailure;
        }

        Method method = null;
        Object srvObjInstance;
        try {
            method = serviceObject.getServiceMethod(methodName);
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.REFLECTION_EXCEPTION, "can't find method:" + methodName, e);
        }
        if (method == null) {
            if (log.isTraceEnabled()) {
                log.trace(new StringBuilder().append("DMI: no public method :").append(methodName).append(" available on class: ").append(srvObjClass).toString());
            }
        }
        try {
            srvObjInstance = serviceObject.getServiceInstance(method);
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.REFLECTION_EXCEPTION, "can't instance  class-object:" + srvObjClass, e);
        }
        ReflectionArgument requireArgs[] = null;
        ReflectionArgument optionalArgs[] = null;
        String methodArguments = null;
        Connection sqlConnection = null;
        if (haveExplicitBinding)
            methodArguments = srvConfig.getMethodArguments();
        Map<String,Object> valuesOrCriteria = request.getContext().getValues() == null ? request.getContext().getCriteria() : request.getContext().getValues();

        // making method require arguments.
        if (methodArguments != null) {
            List<String> methodArgList = DataUtils.simpleSplit(methodArguments, ",");
            requireArgs = new ReflectionArgument[methodArgList.size()];
            VelocityEngine vgen = getVelocityEngine();
            VelocityContext vContext = new VelocityContext();
            if (context != null && context instanceof WebContext) {
                WebContext tmp = (WebContext) context;
                vContext.put("context", tmp);
                vContext.put("request", tmp.getRequest());
                vContext.put("response", tmp.getResponse());
                vContext.put("servletContext", tmp.getServletContext());
            }
            if (dsc != null) {
                vContext.put("dsc", dsc);
                vContext.put("dsCall", dsc);
            }
            vContext.put("dsRequest", request);
            vContext.put("ds", _ds);
            vContext.put("dataSource", _ds);
            vContext.put("criteriaOrValues", valuesOrCriteria);
            StringWriter out = new StringWriter();
            for (int i = 0; i < requireArgs.length; i++) {
                String methodArg = methodArgList.get(i);
                try {
                    vgen.evaluate(vContext, out, "DMIDatasource",
                        new StringBuilder().append("#set($").append(VT_TMP_NAME).append(" = ").append(methodArg).append(")\n").toString());
                } catch (Exception e) {
                    throw new SlxException(Tmodule.DATASOURCE, Texception.VELOCITY_EVALUATE_EXCEPTION, "Velocity evalute exception:\n", e);
                }
                Object value = vContext.get(VT_TMP_NAME);
                if (log.isTraceEnabled())
                    log.trace(new StringBuilder().append("assigning").append(methodArg).append(" type: ").append(
                        value != null ? value.getClass().getName() : " null").toString());
                requireArgs[i] = new ReflectionArgument(value == null ? null : value.getClass(), value);
            }
        } else {
            HttpSession session = null;
            if (Reflection.methodTakesArgType(method, HttpSession.class) && context instanceof WebContext) {
                session = ((WebContext) context).getRequest().getSession(true);
            }
            if (Reflection.methodTakesArgType(method, Connection.class) && (_ds instanceof ISQLDataSource)) {
                sqlConnection = ((ISQLDataSource) _ds).getTransactionalConnection(request);
                if (sqlConnection == null) {
                    connectionIstransactional = false;
                    sqlConnection = ((ISQLDataSource) _ds).getConnection();
                }
            }

            if (context != null && context instanceof WebContext) {
                optionalArgs = new ReflectionArgument[] { 
                    new ReflectionArgument(WebContext.class, context, false, false),
                    new ReflectionArgument(HttpServletRequest.class, ((WebContext) context).getRequest(), false, false),
                    new ReflectionArgument(HttpServletResponse.class, ((WebContext) context).getResponse(), false, false),
                    new ReflectionArgument(ServletContext.class, ((WebContext) context).getServletContext(), false, false),
                    new ReflectionArgument(HttpSession.class, session, false, false),
                    new ReflectionArgument(DSCall.class, dsc, false, false),
                    new ReflectionArgument(DSRequest.class, request, false, false), 
                    new ReflectionArgument(DataSource.class, _ds, false, false),
                    new ReflectionArgument(Connection.class, sqlConnection, false, false),
                    new ReflectionArgument(Logger.class, log, false, false),
                    new ReflectionArgument(Map.class, valuesOrCriteria, false, true) };
            } else {
                optionalArgs = new ReflectionArgument[] { 
                    new ReflectionArgument(Context.class, null, false, false),
                    new ReflectionArgument(HttpServletRequest.class, null, false, false),
                    new ReflectionArgument(HttpServletResponse.class, null, false, false),
                    new ReflectionArgument(ServletContext.class, null, false, false), 
                    new ReflectionArgument(HttpSession.class, null, false, false),
                    new ReflectionArgument(DSCall.class, dsc, false, false), 
                    new ReflectionArgument(DSRequest.class, request, false, false),
                    new ReflectionArgument(DataSource.class, _ds, false, false),
                    new ReflectionArgument(Connection.class, sqlConnection, false, false), 
                    new ReflectionArgument(Logger.class, log, false, false),
                    new ReflectionArgument(Map.class, valuesOrCriteria, false, true) };

            }
        }
        request.setRequestStarted(true);
        Object returnValue=null;
        try {

            returnValue = Reflection.adaptArgsAndInvoke(srvObjInstance, method, requireArgs, optionalArgs, _ds);
        } catch (Exception e) {
            dsResponse = new DSResponseImpl(request,Status.STATUS_FAILURE);
            log.error("Reflect exception:",e);
            dsResponse.setRawData(e.getMessage());
            return dsResponse;
//            throw new SlxException(Tmodule.DATASOURCE, Texception.REFLECTION_EXCEPTION, e.getCause());
        }
        if (sqlConnection != null && !connectionIstransactional) {
            ((ISQLDataSource) _ds).freeConnection(sqlConnection);
        }
        if (returnValue == null)
            return null;
        if (returnValue!=null&&DSResponse.class.isAssignableFrom(returnValue.getClass())) {
            dsResponse = (DSResponse) returnValue;
        } else {
            dsResponse = new DSResponseImpl(request,Status.STATUS_SUCCESS);
            dsResponse.setRawData(returnValue);
        }
        return dsResponse;

    }

    protected VelocityEngine getVelocityEngine() throws SlxException {
        VelocityEngine vgen = new VelocityEngine();
        Properties properties = new Properties();
        properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        properties.put("runtime.log.logsystem.log4j.category", "org.apache.Velocity");
        try {
            vgen.init(properties);
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DEFAULT, e);
        }
        return vgen;
    }
}
