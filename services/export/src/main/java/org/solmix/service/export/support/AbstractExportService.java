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

package org.solmix.service.export.support;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.DateUtils;
import org.solmix.service.export.ExportContext;
import org.solmix.service.export.ExportService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月21日
 */

public abstract class AbstractExportService implements ExportService
{

    public static final int MAC_LINEBREAK = 1;

    public static final int UNIX_LINEBREAK = 2;

    public static final int DOS_LINEBREAK = 3;

    public static final int DEFAULT_LINEBREAK = 4;

    protected ExportContext context;

    @Override
    public ExportContext getContext() {
        return context;
    }

    @Override
    public void setContext(ExportContext ctx) {
        this.context = ctx;
    }

    @Override
    public void printLine(PrintWriter out, String printText) {
        if (!printText.equals(""))
            out.print(printText);
        if (getLineBreakStyle() == 1)
            out.print("\n");
        else if (getLineBreakStyle() == 2)
            out.print("\r");
        else if (getLineBreakStyle() == 3)
            out.print("\r\n");
        else
            out.println();
    }

    @Override
    public void exportResultSet(List<Map<Object, Object>> list, OutputStream outStream) {
        exportResultSet(list, (Map<String, String>) null, outStream);

    }

    protected int getLineBreakStyle() {
        return context.get(LINE_BREAK_STYLE) == null ? 0 : (Integer) context.get(LINE_BREAK_STYLE);
    }

    protected String getExportHeaderString() {
        return (String) context.get(EXPORT_HEADER_STRING);
    }

    /**
     * @return the order
     */
    @SuppressWarnings("unchecked")
    protected List<String> getOrder() {
        return (List<String>) context.get(ORDER);
    }

    protected String getEncoding() {
        return context.get(ENCODING) == null ? "utf-8" : context.get(ENCODING).toString();
    }

    /**
     * @return the exportDelimiter
     */
    protected String getExportDelimiter() {
        return context.get(EXPORT_DELIMITER) == null ? "," : (String) context.get(EXPORT_DELIMITER);
    }

    protected Map<String, String> getColumnNames(List<Map<Object, Object>> rows) {
        Map<String, String> columns = new HashMap<String, String>();
        for (Map<Object, Object> row : rows) {
            for (Object column : row.keySet()) {
                columns.put(column.toString(), column.toString());
            }
        }
        return columns;
    }

    public String getDelimitedValues(Collection<Map<Object, Object>> rows, Collection<String> columns, String delimiter, String quoteChar) {
        return getDelimitedValues(rows, DataUtils.listToStringArray(columns), delimiter, quoteChar);
    }

    protected String getDelimitedValues(Collection<Map<Object, Object>> rows, String columns[], String delimiter, String quoteChar) {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        for (Map<Object, Object> row : rows) {
            for (String column : columns) {
                Object value = row.get(column);
                String _stringValue;
                if (value == null)
                    _stringValue = "";
                else if (value instanceof Date)
                    _stringValue = DateUtils.simpleDateFormat((Date) value);
                else
                    _stringValue = value.toString();

                out.print(_stringValue);
                out.print(delimiter);
            }// end loop columns
            printLine(out, "");
        }// end loop rows
        out.flush();
        return output.toString();

    }

    protected String getDelimitedHeaders(String headers[], String delimiter, String quoteChar) {
        return getDelimitedHeaders(((Arrays.asList(headers))), delimiter, quoteChar);
    }

    protected String getDelimitedHeaders(Collection<String> headers, String delimiter, String quoteChar) {
        StringBuffer out = new StringBuffer();
        for (String header : headers) {
            if (header == null)
                header = "";
            if (quoteChar != null)
                header = quoteChar + header + quoteChar;
            out.append(header);
            out.append(delimiter);
        }
        return out.toString();
    }
}
