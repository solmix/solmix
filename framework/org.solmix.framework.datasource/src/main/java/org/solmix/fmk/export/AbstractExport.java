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

package org.solmix.fmk.export;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.solmix.api.exception.SlxException;
import org.solmix.api.export.IExport;
import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.DateUtil;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public abstract class AbstractExport implements IExport
{

public static final int MAC_LINEBREAK = 1;

public static final int UNIX_LINEBREAK = 2;

public static final int DOS_LINEBREAK = 3;

public static final int DEFAULT_LINEBREAK = 4;



protected Map<String,Object> context= new HashMap<String,Object>() ;



protected int _getLineBreakStyle(){
   return context.get(IExport.LINE_BREAK_STYLE)==null? 0:(Integer) context.get(IExport.LINE_BREAK_STYLE);
}

protected String _getExportHeaderString(){
    return (String)context.get(IExport.EXPORT_HEADER_STRING);
}



/**
 * @return the order
 */
@SuppressWarnings("unchecked")
protected List<String> _getOrder() {
    return(List<String>)context.get(IExport.ORDER);
}

/**
 * @return the exportDelimiter
 */
protected String _getExportDelimiter() {
    return context.get(IExport.EXPORT_DELIMITER)==null?",":(String)context.get(IExport.EXPORT_DELIMITER);
}
/**
 * @return the context
 */
public Map<String, Object> getContext() {
    return context;
}


/**
 * @param context the context to set
 */
public void setContext(Map<String, Object> context) {
    this.context = context;
}

/**
 * {@inheritDoc}
 * 
 * @see org.solmix.api.export.IExport#printLine(java.io.PrintWriter, java.lang.String)
 */
@Override
public void printLine(PrintWriter out, String printText) {
    if (!printText.equals(""))
        out.print(printText);
    if (_getLineBreakStyle() == 1)
        out.print("\n");
    else if (_getLineBreakStyle() == 2)
        out.print("\r");
    else if (_getLineBreakStyle() == 3)
        out.print("\r\n");
    else
        out.println();

}

/**
 * {@inheritDoc}
 * 
 * @see org.solmix.api.export.IExport#exportResultSet(java.util.List, java.io.Writer)
 */
public void exportResultSet(List<Map<Object, Object>> list,  OutputStream  outStream) throws SlxException {
    exportResultSet(list, (Map<String, String>) null, outStream);

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

public String getDelimitedValues(Collection<Map<Object, Object>> rows, Collection<String> columns, String delimiter,
    String quoteChar) {
    return getDelimitedValues(rows, DataUtil.listToStringArray(columns), delimiter, quoteChar);
}

protected String getDelimitedValues(Collection<Map<Object, Object>> rows, String columns[], String delimiter,
    String quoteChar) {
    StringWriter output = new StringWriter();
    PrintWriter out = new PrintWriter(output);
    for (Map<Object, Object> row : rows) {
        for (String column : columns) {
            Object value = row.get(column);
            String _stringValue;
            if (value == null)
                _stringValue = "";
            else if (value instanceof Date)
                _stringValue = DateUtil.simpleDateFormat((Date) value);
            else
                _stringValue = value.toString();
            if (quoteChar != null) {
                Perl5Util perl = new Perl5Util();
                String pattern;
                if (quoteChar.equals("/"))
                    pattern = (new StringBuilder()).append("s#").append(quoteChar).append("#").append(quoteChar).append(
                        quoteChar).append("#g").toString();
                else
                    pattern = (new StringBuilder()).append("s/").append(quoteChar).append("/").append(quoteChar).append(
                        quoteChar).append("/g").toString();
                _stringValue = perl.substitute(pattern, _stringValue);
                _stringValue = (new StringBuilder()).append(quoteChar).append(_stringValue).append(quoteChar).toString();
            }
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

/**
 * @param headers
 * @param delimiter
 * @param quoteChar
 * @return
 */
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
