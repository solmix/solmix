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

package org.solmix.fmk.export;

import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.export.ExportManager;
import org.solmix.api.export.IExport;
import org.solmix.api.jaxb.EexportAs;
import org.solmix.commons.util.DataUtil;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-22 solmix-ds
 */
public class ExportManagerImpl implements ExportManager
{

private static ExportManager exportManager;

public static IExport get(EexportAs type,Map<String,Object> context) throws SlxException {
    if (exportManager == null)
        exportManager = new ExportManagerImpl();
    return exportManager.getExport(type,context);

}
public static IExport get(EexportAs type)throws SlxException {
    return get(type,null);
}

/**
 * {@inheritDoc}
 * 
 * @see org.solmix.fmk.export.ExportService#getExport(java.lang.String)
 */
@Override
public IExport getExport(String formatName) throws SlxException {
    return  getExport(EexportAs.fromValue(formatName));
}

/**
 * {@inheritDoc}
 * 
 * @see org.solmix.api.export.ExportService#getExport(org.solmix.api.types.ExportType)
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
 * @see org.solmix.api.export.ExportService#getExport(org.solmix.api.jaxb.EexportAs)
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
