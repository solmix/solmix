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
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.datasource.ResponseData;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.commons.util.DataUtil;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-api
 */
@ResponseData
@JsonIgnoreProperties({ "exportFields", "isExport", "dropExtraFields", "affectedRows", "requestConnectionClose" })
public class DSResponseData
{

    private static final Logger log = LoggerFactory.getLogger(DSResponseData.class.getName());

    public enum Type
    {
        REUTRN(0);

        private final int value;

        Type(int value)
        {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    @ResponseData
    private Boolean invalidateCache;

    @ResponseData
    private Boolean isDSResponse;

    @ResponseData
    private Integer startRow;

    @ResponseData
    private Integer endRow;

    @ResponseData
    private Integer totalRows;

    @ResponseData
    private List errors;

    @ResponseData
    private Status status;

    @ResponseData
    private Object data;

    @ResponseData
    private String exportAs;

    @ResponseData
    private String exportFilename;

    @ResponseData
    private String exportDelimiter;

    @ResponseData
    private String exportHeader;

    @ResponseData
    private String exportFooter;

    @ResponseData
    private String exportTitleSeparatorChar;

    @ResponseData
    private Boolean exportResults;

    @ResponseData
    private String exportDisplay;

    @ResponseData
    private String lineBreakStyle;

    @ResponseData
    private List<String> exportFields;

    private Boolean isExport;

    private Boolean dropExtraFields;

    Long affectedRows;
    
    private String operationType;

    private boolean requestConnectionClose;

    /**
    * 
    */
    private static final long serialVersionUID = -2991653788073500386L;

    public void init() {
        this.status = Status.UNSET;
        this.requestConnectionClose = false;
        this.invalidateCache = false;
        this.isDSResponse = true;
    }

    public void addToExportFields(String field) {
        if (exportFields == null)
            exportFields = new ArrayList<String>();
        exportFields.add(field);
    }

    /**
     * @return the invalidateCache
     */
    public Boolean getInvalidateCache() {
        return invalidateCache;
    }

    /**
     * @param invalidateCache the invalidateCache to set
     */
    public void setInvalidateCache(Boolean invalidateCache) {
        this.invalidateCache = invalidateCache;
    }

    /**
     * @return the isDSResponse
     */
    public Boolean getIsDSResponse() {
        return isDSResponse;
    }

    /**
     * @param isDSResponse the isDSResponse to set
     */
    public void setIsDSResponse(Boolean isDSResponse) {
        this.isDSResponse = isDSResponse;
    }

    /**
     * @return the startRow
     */
    public Integer getStartRow() {
        return startRow;
    }

    /**
     * @param startRow the startRow to set
     */
    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public boolean statusIsError() {
        return status.value() < 0;
    }

    /**
     * @return the endRow
     */
    public Integer getEndRow() {
        return endRow;
    }

    /**
     * @param endRow the endRow to set
     */
    public void setEndRow(Integer endRow) {
        this.endRow = endRow;
    }

    /**
     * @return the totalRows
     */
    public Integer getTotalRows() {
        return totalRows;
    }

    /**
     * @param totalRows the totalRows to set
     */
    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    /**
     * @return the exportResults
     */
    public Boolean getExportResults() {
        return exportResults;
    }

    /**
     * @param exportResults the exportResults to set
     */
    public void setExportResults(Boolean exportResults) {
        this.exportResults = exportResults;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Return the data as the type <code>T</code>.
     * 
     * @param type
     * @return
     */
    public <T> T getData(Class<T> type) {
        return _getData(type, getData());
    }

    @SuppressWarnings("unchecked")
    protected <T> T _getData(Class<T> type, Object data) {
        if (data == null)
            return null;
        if (type.isInstance(data))
            return (T) data;
        // First, assume that the type is Map.
        if (type.isAssignableFrom(Map.class)) {
            if (data instanceof List<?>) {
                if (((List<?>) data).size() == 0) {
                    return null;
                } else if (((List<?>) data).get(0) instanceof Map<?, ?>) {
                    return (T) ((List<?>) data).get(0);
                }
            } else if (data instanceof Map<?, ?>) {
                return (T) data;
            }
            // Then,assume that the type is List.
        } else if (type.isAssignableFrom(List.class)) {
            if (data instanceof List<?>) {
                return (T) data;
            } else {
                List<Object> re = new ArrayList<Object>();
                re.add(data);
                return (T) re;
            }
        } else if (!type.isPrimitive() && !type.isInterface() && !type.isArray()) {
            try {

                Object instance = type.newInstance();
                if (data instanceof Map<?, ?>) {
                    DataUtil.setProperties((Map) data, instance, false);
                    return (T) instance;
                } else if (data instanceof List<?>) {
                    List<Object> datas = (List<Object>) data;

                    if (datas.size() == 1 && datas.get(0) instanceof Map<?, ?>) {
                        DataUtil.setProperties((Map) datas.get(0), instance, false);
                    } else if (datas.size() > 1 && datas.get(0) instanceof Map<?, ?>) {
                        DataUtil.setProperties((Map) datas.get(0), instance, false);
                        log.warn("The data is more than one map or bean, used the first one and drop other " + (datas.size() - 1) + "(s)");
                    }
                    return (T) instance;
                } else {
                    return (T) DataUtil.castValue(data, type);
                }
            } catch (Exception ee) {
                log.debug((new StringBuilder()).append("Tried to convert inbound nested Map to: ").append(type.getName()).append(
                    " but DataTools.setProperties() on instantiated class failed").append(" with the following error: ").append(ee.getMessage()).toString());
            }
        }
        throw new IllegalArgumentException((new StringBuilder()).append("Can't convert value of type ").append(data.getClass().getName()).append(
            " to target type ").append(type.getName()).toString());
    }

    @JsonIgnore
    public <T> List<T> getDataList(Class<T> type) {
        Object data = getData();
        List<T> _return = new ArrayList<T>();
        if (data instanceof List<?>) {
            for (Object obj : (List<?>) data) {
                _return.add(_getData(type, obj));

            }
        }

        else {
            try {
                _return.add(DataUtil.convertType(type, data));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return _return;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return the exportHeader
     */
    public String getExportHeader() {
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
        return exportTitleSeparatorChar;
    }

    /**
     * @param exportTitleSeparatorChar the exportTitleSeparatorChar to set
     */
    public void setExportTitleSeparatorChar(String exportTitleSeparatorChar) {
        this.exportTitleSeparatorChar = exportTitleSeparatorChar;
    }

    /**
     * @return the exportAs
     */
    public String getExportAs() {
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
    @JsonIgnore
    public Boolean getIsExport() {
        return isExport;
    }

    /**
     * @param isExport the isExport to set
     */
    public void setIsExport(Boolean isExport) {
        this.isExport = isExport;
    }

    /**
     * @return the dropExtraFields
     */
    public Boolean getDropExtraFields() {
        return dropExtraFields;
    }

    /**
     * @param dropExtraFields the dropExtraFields to set
     */
    public void setDropExtraFields(Boolean dropExtraFields) {
        this.dropExtraFields = dropExtraFields;
    }

    /**
     * @return the affectedRows
     */
    public Long getAffectedRows() {
        return affectedRows;
    }

    /**
     * @param affectedRows the affectedRows to set
     */
    public void setAffectedRows(Long affectedRows) {
        this.affectedRows = affectedRows;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the errors
     */
    public List getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List errors) {
        this.errors = errors;
    }

    /**
     * @return the requestConnectionClose
     */
    public boolean isRequestConnectionClose() {
        return requestConnectionClose;
    }

    /**
     * @param requestConnectionClose the requestConnectionClose to set
     */
    public void setRequestConnectionClose(boolean requestConnectionClose) {
        this.requestConnectionClose = requestConnectionClose;
    }

    
    public String getOperationType() {
        return operationType;
    }

    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

}
