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

import java.util.Map;

import com.solmix.api.exception.SlxException;
import com.solmix.api.export.ExportService;
import com.solmix.api.export.IExport;
import com.solmix.api.jaxb.EexportAs;
import com.solmix.commons.util.DataUtil;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public class ExportManager implements ExportService
{

private static ExportManager exportManager;

public static IExport get(EexportAs type,Map<String,Object> context) throws SlxException {
    if (exportManager == null)
        exportManager = new ExportManager();
    return exportManager.getExport(type,context);

}
public static IExport get(EexportAs type)throws SlxException {
    return get(type,null);
}

/**
 * {@inheritDoc}
 * 
 * @see com.solmix.fmk.export.ExportService#getExport(java.lang.String)
 */
@Override
public IExport getExport(String formatName) throws SlxException {
    return  getExport(EexportAs.fromValue(formatName));
}

/**
 * {@inheritDoc}
 * 
 * @see com.solmix.api.export.ExportService#getExport(com.solmix.api.types.ExportType)
 */
@Override
public IExport getExport(EexportAs type,Map<String,Object> context) throws SlxException {
    IExport export= getExport(type);
    if(export!=null&&DataUtil.isNotNullAndEmpty(context)){
        export.setContext(context);
    }
    return export;
}


/**
 * {@inheritDoc}
 * 
 * @see com.solmix.api.export.ExportService#getExport(com.solmix.api.jaxb.EexportAs)
 */
@Override
public IExport getExport(EexportAs type) throws SlxException {
    switch (type) {
        case CSV:
            return new CSVExport();
        case JSON:
             return new JSONExport();
        case XML:
             return new XMLExport();
        case XLS:
             return new ExcelExport();
        case OOXML:
             return null;
             default:
                 return null;
    }}

}
