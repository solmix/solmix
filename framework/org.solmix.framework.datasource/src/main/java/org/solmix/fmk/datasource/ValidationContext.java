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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.context.Context;
import org.solmix.api.datasource.DSRequestData;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.util.ErrorReport;
import org.solmix.fmk.velocity.DataSourcesHandler;
import org.solmix.fmk.velocity.Util;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-27 solmix-ds
 */
@SuppressWarnings("rawtypes")
public class ValidationContext extends HashMap
{

    /**
    * 
    */
    private static final long serialVersionUID = -4357037166723523927L;

    public enum Vtype
    {
        DATASOURCE(0) , DS_REQUEST(1);

        private final int value;

        Vtype(int value)
        {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    /**
     * number of successes (nos) validate datasource.
     */
    private static AtomicLong nosValidateDS;

    /**
     * number of failed (nof) validate datasource.
     */
    private static AtomicLong nofValidateDS;
    static {
        nosValidateDS = new AtomicLong();
        nofValidateDS = new AtomicLong();
    }

    private ValidationContext()
    {
        currentDataSource = null;
        propertiesOnly = false;
        dSCall = null;
        requestContext = null;
        fieldName = "";
        DSRequstContext = null;
        path = "";
        valueIsSet = false;
        resultingValue = null;
        errors = null;
    }

    public static ValidationContext instance() {
        ValidationContext instance = new ValidationContext();
        return instance;
    }

    private boolean propertiesOnly;

    Vtype vtype;

    ValidationEventFactory vfactory;

    /**
     * Return ValidationEvent creating factory
     * 
     * @return the vfactory
     */
    public ValidationEventFactory getVfactory() {
        return vfactory;
    }

    /**
     * set the validation event factory for this context,if ENV change reset this Object.
     * 
     * @param vfactory the vfactory to set
     */
    public void setVfactory(ValidationEventFactory vfactory) {
        this.vfactory = vfactory;
    }

    DataSource currentDataSource;

    Map<Object, Object> currentRecord;

    /**
     * @return the currentRecord
     */
    public Map<Object, Object> getCurrentRecord() {
        return currentRecord;
    }

    /**
     * @param currentRecord the currentRecord to set
     */
    public void setCurrentRecord(Map<Object, Object> currentRecord) {
        this.currentRecord = currentRecord;
    }

    DSCall dSCall;

    Context requestContext;

    DSRequestData DSRequstContext;

    String path;

    boolean idAllowed;

    String fieldName;

    boolean valueIsSet;

    Map<String, Object> errors;

    protected Map<String, Object> templateContext;

    private Object resultingValue;

    public void setResultingValue(Object resultingValue) {
        this.valueIsSet = true;
        this.resultingValue = resultingValue;
    }

    /**
     * @return the idAllowed
     */
    public boolean isIdAllowed() {
        return idAllowed;
    }

    /**
     * @param idAllowed the idAllowed to set
     */
    public void setIdAllowed(boolean idAllowed) {
        this.idAllowed = idAllowed;
    }

    /**
     * <code>ResultingValue</code> is cached the current <code>Tfield</code> value which has validated.<br>
     * This method indicate the value is set or not.
     * 
     * @return
     */
    public boolean resultingValueIsSet() {
        return valueIsSet;
    }

    /**
     * <code>ResultingValue</code> is cached the current <code>Tfield</code> value which has validated.<br>
     * 
     * @return
     */
    public Object getResultingValue() {
        return resultingValue;
    }

    /**
     * <code>ResultingValue</code> is cached the current <code>Tfield</code> value which has validated.<br>
     * After we get the value or before set another <code>ResultingValue</code>,we must clear it.
     */
    public void clearResultingValue() {
        this.resultingValue = null;
        valueIsSet = false;
    }

    public void removePathSegment() {
        path = path.substring(0, path.lastIndexOf("/"));
        fieldName = path.substring(path.lastIndexOf("/") + 1, path.length());
    }

    public void addPath(String segment) {
        path = (new StringBuilder()).append(path).append("/").append(segment).toString();
        fieldName = segment;
    }

    /**
     * @return the currentDataSource
     */
    public DataSource getCurrentDataSource() {
        return currentDataSource;
    }

    /**
     * object is ErrorReport
     * 
     * @return the errors
     */
    public Map<String, Object> getErrors() {
        return errors;
    }
    /**
     * @param errors the errors to set
     */
    public void setErrors(Map<String, Object> errors) {
        this.errors = errors;
    }

    /**
     * add error to current field validator.
     * 
     * @param newErrors
     */
    public void addError(Object newErrors) {
        addError(fieldName, newErrors);
    }

    public void addError(String fieldName, Object newErrors) {
        if (newErrors == null)
            return;
        if (errors == null)
            errors = new HashMap<String, Object>();
        LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).debug((new StringBuilder()).append("Adding validation errors at path '").append(path).append("': ").append(newErrors).toString());
        String recordPath = getCurrentRecordPath(fieldName);
        ErrorReport report = (ErrorReport) errors.get(recordPath);
        if (report == null) {
            report = new ErrorReport();
            report.put("recordPath", recordPath);
            errors.put(recordPath, report);
        }
        DataUtil.putCombinedList(report, fieldName, newErrors);
    }

    /**
     * @param fieldName2
     * @return
     */
    public String getCurrentRecordPath(String fieldName) {
        String recordPath = path;
        if (fieldName != null && recordPath.endsWith((new StringBuilder()).append("/").append(fieldName).toString()))
            recordPath = recordPath.substring(0, recordPath.lastIndexOf("/"));
        return recordPath;
    }

    /**
     * set by {@link org.solmix.fmk.datasource.ValidationContext#addPath(String)} or
     * {@link org.solmix.fmk.datasource.ValidationContext#removePathSegment()}
     * 
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @param currentDataSource the currentDataSource to set
     */
    public void setCurrentDataSource(DataSource currentDataSource) {
        this.currentDataSource = currentDataSource;
    }

    /**
     * @return the dSCall
     */
    public DSCall getRpcManager() {
        return dSCall;
    }

    /**
     * @param dSCall the dSCall to set
     */
    public void setRpcManager(DSCall rpcManager) {
        this.dSCall = rpcManager;
    }

    /**
     * @return the requestContext
     */
    public Context getRequestContext() {
        return requestContext;
    }

    /**
     * @param requestContext the requestContext to set
     */
    public void setRequestContext(Context requestContext) {
        this.requestContext = requestContext;
    }

    /**
     * @return the dSRequstContext
     */
    public DSRequestData getDSRequstContext() {
        return DSRequstContext;
    }

    /**
     * @param dSRequstContext the dSRequstContext to set
     */
    public void setDSRequstContext(DSRequestData dSRequstContext) {
        DSRequstContext = dSRequstContext;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the vtype
     */
    public Vtype getVtype() {
        return vtype;
    }

    /**
     * Indicate the validation type.
     * <P>
     * <b>NOTE:</b> This is not the indispensability value,for example,use <code>DSRequestContext!=null</code> can
     * indicate this is a datasource-request validation context
     * 
     * @param vtype the vtype to set
     */
    public void setVtype(Vtype vtype) {
        this.vtype = vtype;
    }

    /**
     * @return the propertiesOnly
     */
    public boolean isPropertiesOnly() {
        return propertiesOnly;
    }

    public boolean validate(DataSource datasource) throws SlxException {

        // ValidationEventHandler _validator = datasource.getValidationEventHandler();
        // if ( _validator == null )
        // _validator = new DefaultValidationHandler();
        // List< IValidationEvent > _events = datasource.getContext().getValidationEvents();
        // if ( _events != null )
        // for ( IValidationEvent event : _events )
        // {
        // if ( event.getStuts() == Status.NO_HANDLED )
        // if ( !_validator.handleEvent( event ) )
        // {
        // nofValidateDS.incrementAndGet();
        // return false;
        // }
        // }
        // nosValidateDS.incrementAndGet();
        return true;

    }

    /**
     * <b><li>Manager Method:</b> the number of the successes validation.
     * 
     * @return
     */
    public static long getSucessValidatDS() {

        return nosValidateDS.longValue();
    }

    /**
     * <b><li>Manager Method:</b> the number of the failed validation.
     * 
     * @return
     */
    public static long getFailedValidatDS() {
        return nofValidateDS.longValue();

    }

    /**
     * @param b
     */
    public void setPropertiesOnly(boolean b) {
        this.propertiesOnly = b;

    }

    public void setPropertiesOnly() {
        this.propertiesOnly = true;

    }

    /**
     * @param string
     * @param basicDataSource
     */
    public void addToTemplateContext(String key, Object value) {
        if (templateContext == null)
            initTemplateContext();
        templateContext.put(key, value);

    }

    public void addToTemplateContext(Map<String, Object> keyValues) {
        if (templateContext == null)
            initTemplateContext();
        templateContext = DataUtil.mapMerge(keyValues, templateContext);

    }

    /**
    * 
    */
    private void initTemplateContext() {
        templateContext = new HashMap<String, Object>();
        templateContext.put("dataSources", new DataSourcesHandler());
        templateContext.put("util", new Util());

    }

    public Map<String, Object> getTemplateContext() {
        return templateContext;
    }

    public void setTemplateContext(Map<String, Object> templateContext) {
        this.templateContext = templateContext;
    }
}
