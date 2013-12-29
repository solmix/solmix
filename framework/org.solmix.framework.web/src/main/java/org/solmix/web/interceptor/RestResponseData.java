/*
 * SOLMIX PROJECT
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

package org.solmix.web.interceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月29日
 */

public class RestResponseData
{

    private int status;

    private Boolean invalidateCache;

    private Boolean isDSResponse;

    private Integer startRow;

    private Integer endRow;

    private Integer totalRows;

    private Object[] errors;

    private Object data;

    
    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    
    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
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
     * @return the errors
     */
    public Object[] getErrors() {
        return errors;
    }

    
    /**
     * @param errors the errors to set
     */
    public void setErrors(Object[] errors) {
        this.errors = errors;
    }

    
    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    
    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }
    
}
