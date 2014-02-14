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

package org.solmix.api.export;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.solmix.api.exception.SlxException;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public interface IExport
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

    void exportResultSet(List<Map<Object, Object>> list, OutputStream outStream) throws SlxException;

    /**
     * Used the printWriter to print text to a new line.
     * @param out
     * @param printText text to print.
     * @throws SlxException
     */
    void printLine(PrintWriter out, String printText) throws SlxException;

    /**
     * Get export context.
     * @return
     */
    Map<String, Object> getContext();

    /**
     * Set export context.
     * @param ctx
     */
    void setContext(Map<String, Object> ctx);

    /**
     * Export record .
     * @param rows
     * @param columnMap
     * @param outStream
     */
    void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream) throws SlxException;
}
