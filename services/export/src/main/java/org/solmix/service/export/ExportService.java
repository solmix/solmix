/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.service.export;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.solmix.runtime.Extension;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月7日
 */
@Extension("xls")
public interface ExportService
{

    /**
     * mac:1,unix:2,dos:3,default:4.
     */
    public static final String LINE_BREAK_STYLE = "lineBreakStyle";

    public static final String ORDER = "order";

    public static final String EXPORT_DELIMITER = "exportDelimiter";

    public static final String EXPORT_HEADER_STRING = "exportHeaderString";

    public static final String EXPORT_FOOTER_STRING = "exportFooterString";

    public static final String XML_TAG_NAME = "XMLTagName";
    
    public static final String ENCODING = "encoding";

    void exportResultSet(List<Map<Object, Object>> list, OutputStream outStream) throws ExportException;

    /**
     * Export record .
     * @param rows
     * @param columnMap
     * @param outStream
     */
    void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream) throws ExportException;

    /**
     * Used the printWriter to print text to a new line.
     * @param out
     * @param printText text to print.
     * @throws SlxException
     */
    void printLine(PrintWriter out, String printText) throws ExportException;

    /**
     * Get export context.
     * @return
     */
    ExportContext getContext();

    /**
     * Set export context.
     * @param ctx
     */
    void setContext(ExportContext ctx);

   
}
