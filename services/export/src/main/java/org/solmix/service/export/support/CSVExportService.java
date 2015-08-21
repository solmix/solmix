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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.service.export.ExportException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月21日
 */

public class CSVExportService extends AbstractExportService
{

    private static final Logger LOG  = LoggerFactory.getLogger(CSVExportService.class);
    @Override
    public void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream) throws ExportException {
        Writer out=null;
        try {
            out = new BufferedWriter( new OutputStreamWriter(outStream,getEncoding()));
        } catch (UnsupportedEncodingException e) {
           throw new  ExportException(e);
        }
        if (rows == null || rows.size() <= 0) {
            LOG.debug("Empty or null result set");
            return;
        }
        PrintWriter pw;
        if (out instanceof PrintWriter)
            pw = (PrintWriter) out;
        else
            pw = new PrintWriter(out);
        Collection<String> columns;
        Collection<String> headers;
        if (getOrder() != null) {
            columns = getOrder();
            if (columnMap == null) {
                headers = columns;
            } else {
                headers = new ArrayList<String>();
                for (String str : getOrder()) {
                    String remaped = columnMap.get(str);
                    if (remaped != null)
                        headers.add(remaped);
                    else
                        headers.add(str);
                }
            }
        }// end order check
        else {
            if (columnMap == null)
                columnMap = getColumnNames(rows);
            columns = columnMap.keySet();
            headers = columnMap.values();
        }
        printLine(pw, getDelimitedHeaders(headers, getExportDelimiter(), "\""));
        printLine(pw, getDelimitedValues(rows, columns, getExportDelimiter(), "\""));
        try {
            pw.flush();
            out.flush();
        } catch (IOException e) {
          new ExportException("export io exception",e);
        }
    }

}
