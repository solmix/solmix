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

package com.solmix.fmk.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.solmix.api.exception.SlxException;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.logs.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.util.MapperUtil;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public class JSONExport extends AbstractExport
{

private static final Logger log = new Logger(JSONExport.class);

/**
 * {@inheritDoc}
 * 
 * @see com.solmix.api.export.IExport#exportResultSet(java.util.List, java.util.Map, java.io.Writer)
 */
@Override
public void exportResultSet(List<Map<Object, Object>> rows, Map<String, String> columnMap, OutputStream outStream)
    throws SlxException {
    if (rows == null || rows.size() <= 0) {
        log.debug("Empty or null result set");
        return;
    }
    
    if(DataUtil.isNotNullAndEmpty(columnMap)){
    rows = DataUtil.remapRows(rows, columnMap, false);
    }
    if (rows.size() <= 0) {
        log.debug("Empty result set after column remap");
        return;
    }
    Writer out=null;
    try {
        out = new BufferedWriter( new OutputStreamWriter(outStream,"gbk"));
    } catch (UnsupportedEncodingException e) {
       throw new  SlxException(Tmodule.BASIC,Texception.UN_SUPPORTEDEN_CODING,e);
    }
    MapperUtil.toJSON(rows, out);
    try {
        out.flush();
    } catch (IOException e) {
      new SlxException(Tmodule.DATASOURCE,Texception.IO_EXCEPTION,e);
    }
}

}
