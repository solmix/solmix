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

package com.solmix.api.export;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.solmix.api.exception.SlxException;

/**
 * @author solomon
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

    void printLine(PrintWriter out, String printText) throws SlxException;

    Map<String, Object> getContext();

    void setContext(Map<String, Object> ctx);

    /**
     * @param rows
     * @param columnMap
     * @param outStream
     */
    void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream) throws SlxException;
}
