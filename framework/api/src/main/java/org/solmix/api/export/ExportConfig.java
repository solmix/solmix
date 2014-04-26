/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.api.export;

import java.util.List;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-29
 */

public class ExportConfig
{

    private String exportHeader;

    private String exportFooter;

    private String exportTitleSeparatorChar;

    private String exportFilename;

    private String exportDelimiter;

    private String exportDisplay;

    private List<String> exportFields;

    private String exportAs;

    private Boolean exportDatesAsFormattedString;
    
    private String lineBreakStyle;
    
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
     * @return the exportDatesAsFormattedString
     */
    public Boolean getExportDatesAsFormattedString() {
        return exportDatesAsFormattedString;
    }

    
    /**
     * @param exportDatesAsFormattedString the exportDatesAsFormattedString to set
     */
    public void setExportDatesAsFormattedString(Boolean exportDatesAsFormattedString) {
        this.exportDatesAsFormattedString = exportDatesAsFormattedString;
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
}
