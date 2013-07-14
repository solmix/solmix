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

package org.solmix.api.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.event.IValidationEvent;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.rpc.RPCManagerCompletionCallback;
import org.solmix.commons.util.DataUtil;

/**
 * Context for DataSource request.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-api
 */
@SuppressWarnings("unchecked")
public class DSRequestData implements java.io.Serializable
{

    public static final String BUILTIN_APPLICATION = "builtinApplication";

    private static final Logger log =LoggerFactory.getLogger(DSRequestData.class.getName());

    private boolean _allowMultiUpdate;

    private Object constraints;

    private List<String> outputs;

    private boolean forceInvalidateCache;

    protected String userId;

    // 验证失败返回的信息
    protected String securityFailureMessage;

    private boolean freeOnExecute;

    private List<Object> uploadedFiles;

    private String validationMode;

    private Roperation Roperation;

    private String appID;

    private Boolean isClientRequest;

    private String exportAs;

    protected Object beforeValidatedValues;

    public void init() {
        isClientRequest = false;
        batchSize = null;
        // if ( toperation.getStartRow() == null )
        // toperation.setStartRow( 0L );
        // if ( toperation.getEndRow() == null )
        // toperation.setEndRow( -1L );
        _allowMultiUpdate = false;
        forceInvalidateCache = false;
        freeOnExecute = true;
        beforeValidatedValues = null;
        if (getAppID() == null)
            setAppID(BUILTIN_APPLICATION);
        if (getRepo() == null)
            repo = "default";
    }

    /**
     * @return the beforeValidatedValues
     */
    public Object getBeforeValidatedValues() {
        return beforeValidatedValues;
    }

    /**
     * @param beforeValidatedValues the beforeValidatedValues to set
     */
    public void setBeforeValidatedValues(Object beforeValidatedValues) {
        this.beforeValidatedValues = beforeValidatedValues;
    }

    /**
     * @return the appId
     */
    public String getAppID() {
        if (appID == null && this.Roperation != null && Roperation.getAppID() != null)
            appID = Roperation.getAppID();
        return appID;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppID(String appID) {
        this.appID = appID;
    }

    /**
     * @return the Roperation
     */
    public Roperation getRoperation() {
        return Roperation;
    }

    /**
     * @param Roperation the Roperation to set
     */
    public void setRoperation(Roperation Roperation) {
        this.Roperation = Roperation;
    }

    /**
     * @return the exportHeader
     */
    public String getExportHeader() {
        if (exportHeader == null)
            exportHeader = Roperation.getExportHeader();
        return exportHeader;
    }

    /**
     * @param exportHeader the exportHeader to set
     */
    public void setExportHeader(String exportHeader) {
        this.exportHeader = exportHeader;
    }

    /**
     * @return the exportFooter
     */
    public String getExportFooter() {
        if (exportFooter == null)
            exportFooter = Roperation.getExportFooter();
        return exportFooter;
    }

    /**
     * @param exportFooter the exportFooter to set
     */
    public void setExportFooter(String exportFooter) {
        this.exportFooter = exportFooter;
    }

    /**
     * @return the exportTitleSeparatorChar
     */
    public String getExportTitleSeparatorChar() {
        if (exportTitleSeparatorChar == null)
            exportTitleSeparatorChar = Roperation.getExportTitleSeparatorChar();
        return exportTitleSeparatorChar;
    }

    public Object getFieldValue(Object fieldName) {
        Map valueSet = getValues();
        if (valueSet != null && valueSet.get(fieldName) != null)
            return valueSet.get(fieldName);
        Map criteria = getCriteria();
        if (criteria != null && criteria.get(fieldName) != null)
            return criteria.get(fieldName);
        else
            return null;
    }

    /**
     * @param exportTitleSeparatorChar the exportTitleSeparatorChar to set
     */
    public void setExportTitleSeparatorChar(String exportTitleSeparatorChar) {
        this.exportTitleSeparatorChar = exportTitleSeparatorChar;
    }

    private String exportHeader;

    private String exportFooter;

    private String exportTitleSeparatorChar;

    private String exportFilename;

    private String exportDelimiter;

    private String exportDisplay;

    private String lineBreakStyle;

    private List<String> exportFields;

    /**
     * @return the exportAs
     */
    public String getExportAs() {
        if (exportAs == null)
            exportAs = Roperation.getExportAs();
        return exportAs;
    }

    /**
     * @param exportAs the exportAs to set
     */
    public void setExportAs(String exportAs) {
        this.exportAs = exportAs;
    }

    /**
     * @return the exportFilename
     */
    public String getExportFilename() {
        if (exportFilename == null && Roperation != null)
            exportFilename = Roperation.getExportFilename();
        return exportFilename;
    }

    /**
     * @param exportFilename the exportFilename to set
     */
    public void setExportFilename(String exportFilename) {
        this.exportFilename = exportFilename;
    }

    /**
     * @return the exportDelimiter
     */
    public String getExportDelimiter() {
        if (exportDelimiter == null && Roperation != null)
            exportDelimiter = Roperation.getExportDelimiter();
        return exportDelimiter;
    }

    /**
     * @param exportDelimiter the exportDelimiter to set
     */
    public void setExportDelimiter(String exportDelimiter) {
        this.exportDelimiter = exportDelimiter;
    }

    /**
     * @return the exportDisplay
     */
    public String getExportDisplay() {
        if (exportDisplay == null && Roperation != null)
            exportDisplay = Roperation.getExportDisplay();
        return exportDisplay;
    }

    /**
     * @param exportDisplay the exportDisplay to set
     */
    public void setExportDisplay(String exportDisplay) {
        this.exportDisplay = exportDisplay;
    }

    /**
     * @return the lineBreakStyle
     */
    public String getLineBreakStyle() {
        if (lineBreakStyle == null && Roperation != null)
            lineBreakStyle = Roperation.getLineBreakStyle();
        return lineBreakStyle;
    }

    /**
     * @param lineBreakStyle the lineBreakStyle to set
     */
    public void setLineBreakStyle(String lineBreakStyle) {
        this.lineBreakStyle = lineBreakStyle;
    }

    /**
     * @return the exportFields
     */
    public List<String> getExportFields() {
        if (exportFields == null) {

            String l = Roperation == null ? null : Roperation.getExportFields();
            if (l != null) {
                List<String> fields = new ArrayList<String>();

                fields.addAll(DataUtil.simpleSplit(l, ","));
                exportFields = fields;
            }
        }
        return exportFields;
    }

    /**
     * @param exportFields the exportFields to set
     */
    public void setExportFields(List<String> exportFields) {
        this.exportFields = exportFields;
    }

    /**
     * @return the isExport
     */
    public Boolean getIsExport() {
        if (Roperation == null)
            return null;
        return new Boolean(Roperation.isExportResults());
    }

    private Object rawCriteria;

    private Object rawValues;

    private Object rawOldValues;

    /**
     * {@link org.solmix.api.data.DSRequestData#criteria criteria} maybe <code>Map</code> or List &lt Map &gt
     * 
     * @return the criteria
     */
    public Object getRawCriteria() {
        return rawCriteria;
    }

    /**
     * Add a criteria to DataSourceRequest.<br>
     * if previous value is a <code>Map</code> Object,just use <code>put(key,value)</code>.if not,use a
     * <code>List</code> to wrapped.
     * 
     * @param key
     * @param value
     */
    public void addToCriteria(String key, Object value) {

        if (getRawCriteria() == null) {
            rawCriteria = new HashMap<String, Object>();
            ((Map) rawCriteria).put(key, value);
        } else {
            if (rawCriteria instanceof Map) {
                ((Map) rawCriteria).put(key, value);
            } else {
                rawCriteria = DataUtil.makeListIfSingle(rawCriteria);
                Map _tmp = new HashMap();
                _tmp.put(key, value);
                ((List) rawCriteria).add(_tmp);
            }
        }
    }

    public void addToUploadedFiles(Object fileItem) {
        if (getUploadedFiles() == null)
            uploadedFiles = new ArrayList<Object>();
        uploadedFiles.add(fileItem);
    }

    /**
     * @param criteria the criteria to set
     */
    public void setCriteria(Object criteria) {
        this.rawCriteria = criteria;
    }

    /**
     * @return the values
     */
    public Object getRawValues() {
        return rawValues;
    }

    // public void addToValues(String key, Object value)
    // {
    // if (getRawValues() == null)
    // values = new HashMap<String, Object>();
    // values.put(key, value);
    // }

    /**
     * The values always be Map or List of Map.
     * 
     * @param values
     */
    public void setValues(Object values) {
        this.rawValues = values;
    }

    private Eoperation operationType;

    String downloadFieldName;

    String downloadFileName;

    private Object rawSortBy;

    public static final long FETCH_ALL = -1L;

    private Integer batchSize;

    public static final long ENDROW_UNSET = -1L;

    private String repo;

    private List<IValidationEvent> validationList;

    Map<String, Object> templateContext;

    private Object operation;

    private Integer endRow;

    private Integer startRow;

    /**
     * Return this request Operation ID.This used for indicate unique of operation.
     * 
     * @return the operation
     */
    public Object getOperation() {
        if (operation == null && getRoperation() != null) {
            operation = getRoperation().getOperationId();
        }

        return operation;
    }

    /**
     * Return this request Operation ID.This used for indicate unique of operation.
     * 
     * @return the operation
     */
    public String getOperationId() {
        if (operation == null && getRoperation() != null)
            operation = getRoperation().getOperationId();
        if (operation != null) {
            if (operation instanceof String)
                return operation.toString();
            else if (operation instanceof Map<?, ?>) {
                Map<String, String> tmp = (Map<String, String>) operation;
                return tmp.get("dataSource") + "_" + tmp.get("type");
            } else {
                if (log.isWarnEnabled())
                    log.warn("operation is validte object:" + operation.toString());
            }
        }
        return null;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void addToTemplateContext(String name, Object value) {
        if (templateContext == null)
            templateContext = new HashMap<String, Object>();
        templateContext.put(name, value);
    }

    public void addValidationEvent(IValidationEvent event) {
        if (validationList == null)
            validationList = new ArrayList<IValidationEvent>();
        validationList.add(event);
    }

    /**
     * Indicate this is a request from client,not from internal,for usually,the request along with a
     * {@link org.solmix.api.context.Context}
     * 
     * @return the isClientRequest
     */
    public Boolean getIsClientRequest() {
        return isClientRequest;
    }

    /**
     * Indicate this is a request from client,not from internal,for usually,the request along with a
     * {@link org.solmix.api.context.Context}
     * 
     * @param isClientRequest the isClientRequest to set
     */
    public void setIsClientRequest(Boolean isClientRequest) {
        this.isClientRequest = isClientRequest;
    }

    /**
     * @return the isDownload
     */
    public Boolean getIsDownload() {
        if (getDownloadFieldName() != null) {
            Eoperation opType = getOperationType();
            if (opType == Eoperation.DOWNLOAD_FILE || opType == Eoperation.VIEW_FILE)
                return true;
        }
        return false;
    }

    /**
     * @return the operationType
     */
    public Eoperation getOperationType() {
        if (operationType == null && getOperation() != null) {
            if (getRoperation().getOperationType() != null)
                operationType = Eoperation.fromValue(getRoperation().getOperationType());

            if (operationType == null) {
                String operation = this.getOperationId();
                if (operation != null && operation.indexOf("_") != -1)
                    operationType = Eoperation.fromValue(operation.substring(operation.lastIndexOf("_")));

            }
        }
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(Eoperation operationType) {
        this.operationType = operationType;
    }

    /**
     * @return the downloadFieldName
     */
    public String getDownloadFieldName() {
        return downloadFieldName;
    }

    /**
     * @param downloadFieldName the downloadFieldName to set
     */
    public void setDownloadFieldName(String downloadFieldName) {
        this.downloadFieldName = downloadFieldName;
    }

    /**
     * @return the downloadFileName
     */
    public String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * @param downloadFileName the downloadFileName to set
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    /**
     * @return the sortBy
     */
    public Object getRawSortBy() {
        return rawSortBy;
    }

    /**
     * @param sortBy the sortBy to set
     */
    public void setRawSortBy(Object sortBy) {
        this.rawSortBy = sortBy;
    }

    /**
     * @return the batchSize
     */
    public Integer getBatchSize() {
        if (batchSize == null)
            batchSize = getEndRow() - getStartRow() > 0 ? getEndRow() - getStartRow() : 0;
        return batchSize;
    }

    /**
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Cache the validationEvent for current datasource request instance.
     * 
     * @return the validationList
     */
    public List<IValidationEvent> getValidationList() {
        return validationList;
    }

    /**
     * @param validationList the validationList to set
     */
    public void setValidationList(List<IValidationEvent> validationList) {
        this.validationList = validationList;
    }

    /**
     * no set method,because this method just for convenience.
     * 
     * @return the dataSourceName
     */
    public Object getDataSourceNames() {
        if (Roperation != null) {
            return Roperation.getDataSource();
        }
        return null;
    }

    /**
     * @return the repo
     */
    public String getRepo() {
        return repo;
    }

    /**
     * @param repo the repo to set
     */
    public void setRepo(String repo) {
        this.repo = repo;
    }

    /**
     * @return the templateContext
     */
    public Map<String, Object> getTemplateContext() {
        return templateContext;
    }

    /**
     * @param templateContext the templateContext to set
     */
    public void setTemplateContext(Map<String, Object> templateContext) {
        this.templateContext = templateContext;
    }

    /**
     * Indicate allow multiple update.
     * 
     * @return the _allowMultiUpdate
     */
    public boolean is_allowMultiUpdate() {
        return _allowMultiUpdate;
    }

    /**
     * @param allowMultiUpdate the _allowMultiUpdate to set
     */
    public void set_allowMultiUpdate(boolean allowMultiUpdate) {
        _allowMultiUpdate = allowMultiUpdate;
    }

    /**
     * Alway List
     * 
     * @return the constraints
     */
    public Object getConstraints() {
        return constraints;
    }

    public void addToConstraints(Object obj) {
        if (obj == null)
            return;
        if (constraints == null)
            constraints = new ArrayList();
        List newConstraints = DataUtil.makeListIfSingle(obj);
        if (newConstraints.contains("*")) {
            constraints = null;
            return;
        } else {
            DataUtil.addDisjunctionToSet((List) constraints, newConstraints);
            return;
        }
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(Object constraints) {
        this.constraints = constraints;
    }

    /**
     * @return the outputs
     */
    public List<String> getOutputs() {
        return outputs;
    }

    public void addOutputs(Object obj) {
        if (obj == null)
            return;
        if (outputs == null)
            outputs = new ArrayList();
        List addedOutputs = DataUtil.makeListIfSingle(obj);
        if (addedOutputs.contains("*")) {
            outputs = null;
            return;
        } else {
            DataUtil.addDisjunctionToSet(outputs, addedOutputs);
            return;
        }
    }

    /**
     * @param outputs the outputs to set
     */
    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    /**
     * @return the forceInvalidateCache
     */
    public boolean isForceInvalidateCache() {
        return forceInvalidateCache;
    }

    /**
     * @param forceInvalidateCache the forceInvalidateCache to set
     */
    public void setForceInvalidateCache(boolean forceInvalidateCache) {
        this.forceInvalidateCache = forceInvalidateCache;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the securityFailureMessage
     */
    public String getSecurityFailureMessage() {
        return securityFailureMessage;
    }

    /**
     * @param securityFailureMessage the securityFailureMessage to set
     */
    public void setSecurityFailureMessage(String securityFailureMessage) {
        this.securityFailureMessage = securityFailureMessage;
    }

    /**
     * Used to support RPC transaction.
     * <p>
     * If this value is true ,will not free datasource utile manual free it. if used sql or jpa datasource,must used rpc
     * with {@link RPCManagerCompletionCallback} to commit the transaction. if not,should commit it yourself.
     * 
     * @return the freeOnExecute
     */
    public boolean isFreeOnExecute() {
        return freeOnExecute;
    }

    /**
     * If <code>true<code>,this request will free datasource at the end of this request process.
     * <P>
     * <b>NOTE:</B> if set this value is <code>false<code>,you must free datasource manual.
     * 
     * @see #isFreeOnExecute()
     * @param freeOnExecute the freeOnExecute to set
     */
    public void setFreeOnExecute(boolean freeOnExecute) {
        this.freeOnExecute = freeOnExecute;
    }

    /**
     * @return the fetchAll
     */
    public static long getFetchAll() {
        return FETCH_ALL;
    }

    /**
     * @return the endrowUnset
     */
    public static long getEndrowUnset() {
        return ENDROW_UNSET;
    }

    /**
    * 
    */
    private static final long serialVersionUID = -8718372768588837582L;

    /**
     * @return
     */
    public List<Object> getUploadedFiles() {
        return uploadedFiles;
    }

    /**
     * @param uploadedFiles the uploadedFiles to set
     */
    public void setUploadedFiles(List<Object> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public String getValidationMode() {
        return validationMode;
    }

    /**
     * <code> old values</code> must be one of List&ltMap&gt with size 1,or Map.
     * 
     * @return
     */
    public Object getRawOldValues() {
        return rawOldValues;
    }

    /**
     * <code> old values</code> must be one of List&ltMap&gt with size 1,or Map.
     * 
     * @param oldValues
     */
    public void setRawOldValues(Object oldValues) {
        this.rawOldValues = oldValues;
    }

    /**
     * Convenience for get {@link org.solmix.api.data.DSRequestData#getRawOldValues()}.get <code>rawOldValues</code> as
     * a <code>Map</code>
     * 
     * @return
     */
    public Map getOldValues() {
        Object values = getRawOldValues();
        if (values instanceof List) {
            List l = (List) values;
            if (l.size() == 0)
                return null;
            if (l.size() == 1) {
                return (Map) l.get(0);
            } else {
                log.warn("getOldValues() called on dsRequest containing multiple sets of values, returning first in list.");
                return (Map) l.get(0);
            }
        } else {
            return (Map) values;
        }
    }

    /**
     * Convenience for get {@link org.solmix.api.data.DSRequestData#getRawOldValues()}.get <code>rawOldValues</code> as
     * a <code>List</code>
     * 
     * @return
     */
    public List getOldValueSets() {
        return DataUtil.makeListIfSingle(getRawOldValues());
    }

    /**
     * get {@link org.solmix.api.data.DSRequestData#getRawCriteria()} as a <code>Map</code>
     * <p>
     * if {@link org.solmix.api.data.DSRequestData#getOperationType() OperationType} is
     * {@link org.solmix.api.jaxb.Eoperation.ADD ADD} the criteria is from
     * {@link org.solmix.api.data.DSRequestData#getValues()} if the OperationType is
     * {@link org.solmix.api.jaxb.Eoperation.UPDATE UPDATE} and {@link #getRawCriteria()} is null ,return
     * {@link #getValues()}
     * 
     * @return
     */
    public Map<String, Object> getCriteria() {
        if (getOperationType() == Eoperation.ADD) {
            return getValues();
        }
        Object criteria = getRawCriteria();
        if (getOperationType() == Eoperation.UPDATE && criteria == null) {
            criteria = getRawValues();
        }
        if (criteria instanceof List) {
            List<Object> l = (List<Object>) criteria;
            if (l.size() == 0)
                return null;
            if (l.size() == 1) {
                return (Map<String, Object>) l.get(0);
            } else {
                log.warn("getCriteria() called on dsRequest containing multiple where clauses, returning first in list.");
                return (Map<String, Object>) l.get(0);
            }
        } else {
            return (Map<String, Object>) criteria;
        }
    }

    /**
     * get {@link org.solmix.api.data.DSRequestData#getRawCriteria()} as a <code>List</code>
     * 
     * @return
     */
    public List getCriteriaSets() {
        if (getOperationType().equals(Eoperation.ADD)) {
            return getValueSets();
        }
        return DataUtil.makeListIfSingle(getRawCriteria());
    }

    /**
     * get {@link org.solmix.api.data.DSRequestData#getValues()} as a <code>Map</code>
     * <p>
     * If {@link org.solmix.api.data.DSRequestData#getOperationType() OperationType} is
     * {@link org.solmix.api.jaxb.Eoperation.FETCH FETCH} or {@link org.solmix.api.jaxb.Eoperation.REMOVE REMOVE} return
     * {@link org.solmix.api.data.DSRequestData#getCriteria()} else return
     * {@link org.solmix.api.data.DSRequestData#getRawValues() RawValues}
     * 
     * @return
     */
    public Map getValues() {
        if (getOperationType() == Eoperation.FETCH || getOperationType() == Eoperation.REMOVE)
            return getCriteria();
        Object values = getRawValues();
        if (values instanceof List<?>) {
            List<?> l = (List<?>) values;
            if (l.size() == 0)
                return null;
            if (l.get(0) instanceof Map) {
                if (l.size() == 1) {
                    return (Map) l.get(0);
                } else {
                    log.warn("getValues() called on dsRequest containing multiple sets of values, returning first in list.");
                    return (Map) l.get(0);
                }
            } else {
                log.debug("getValues() called on dsRequest,and the values is not the List of map.ignore this value.");
                return null;
            }
        } else if (values instanceof Map<?, ?>) {
            return (Map) values;
        } else {
            return null;
        }
    }

    /**
     * get {@link org.solmix.api.data.DSRequestData#getValues()} as a <code>List</code>
     * 
     * @return
     */
    public List<Object> getValueSets() {
        if (getOperationType() == Eoperation.FETCH || getOperationType() == Eoperation.REMOVE) {
            return getCriteriaSets();
        } else {
            return DataUtil.makeListIfSingle(getRawValues());
        }
    }

    /**
     * Return sort field ,if there is a list for sort return the first one.
     * 
     * @return
     */
    public String getSortBy() {
        if (rawSortBy instanceof List) {
            List<?> l = (List<?>) rawSortBy;
            if (l.size() == 0)
                return null;
            if (l.size() == 1) {
                return (String) l.get(0);
            } else {
                log.warn("getSortBy() called on dsRequest containing multiple sortBy fields, returning first in list.");
                return (String) l.get(0);
            }
        } else {
            return (String) rawSortBy;
        }
    }

    public List getSortByFields() {
        return DataUtil.makeListIfSingle(rawSortBy);
    }

    /**
     * @return
     */
    public boolean isPaged() {
        if (getStartRow() != null && getStartRow() >= 0 && getEndRow() != null && getEndRow() > 0 && getBatchSize() != null && getBatchSize() > 0)
            return true;
        return false;
    }

    public Integer getEndRow() {
        if (endRow == null)
            endRow = Roperation == null ? null : Roperation.getEndRow();
        return endRow;
    }

    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    public Integer getStartRow() {
        if (startRow == null)
            startRow = Roperation == null ? null : Roperation.getStartRow();
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }
}
