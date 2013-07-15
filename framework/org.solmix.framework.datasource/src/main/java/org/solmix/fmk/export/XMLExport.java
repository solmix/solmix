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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.exception.SlxException;
import org.solmix.api.export.IExport;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.util.MapperUtil;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public class XMLExport extends AbstractExport
{

private static Logger log = LoggerFactory.getLogger(XMLExport.class);

/**
 * {@inheritDoc}
 * 
 * @see org.solmix.api.export.IExport#exportResultSet(java.util.List, java.util.Map, java.io.Writer)
 */
@Override
public void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream)
    throws SlxException {
    if (rows == null || rows.size() <= 0) {
        log.debug("Empty or null result set");
        return;
    }
    Writer out=null;
    try {
        out = new BufferedWriter( new OutputStreamWriter(outStream,"utf-8"));
    } catch (UnsupportedEncodingException e) {
       throw new  SlxException(Tmodule.BASIC,Texception.UN_SUPPORTEDEN_CODING,e);
    }
    if (rows == null || rows.size() <= 0) {
        log.debug("Empty or null result set");
        return;
    }
    PrintWriter pw;
    if (out instanceof PrintWriter)
        pw = (PrintWriter) out;
    else
        pw = new PrintWriter(out);
    rows = DataUtil.remapRows(rows, columnMap, false);
    try {
       String tagName= context.get(IExport.XML_TAG_NAME)==null?null:context.get(IExport.XML_TAG_NAME).toString();
       out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
       out.write("<List>\n");
       MapperUtil.records2XML(tagName,rows, out);
       out.write("</List>\n");
        out.flush();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}