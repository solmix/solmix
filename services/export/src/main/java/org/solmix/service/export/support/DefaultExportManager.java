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

import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.extension.ExtensionLoader;
import org.solmix.service.export.ExportAs;
import org.solmix.service.export.ExportContext;
import org.solmix.service.export.ExportManager;
import org.solmix.service.export.ExportService;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月21日
 */

public class DefaultExportManager implements ExportManager,ContainerAware
{
    ConcurrentHashMap<ExportAs, ExportService> exportServices = new ConcurrentHashMap<ExportAs, ExportService>();
    
    private ExtensionLoader<ExportService> extensionLoader;
    @Override
    public ExportService getExportService(String formatName) {
        Assert.assertNotNull(formatName,"export format name");
        ExportAs as=  ExportAs.fromValue(formatName);
        return getExportService(as);
    }

   
    @Override
    public ExportService getExportService(ExportAs exportAs) {
        ExportService service= exportServices.get(exportAs);
        if(service!=null){
            return service;
        }else{
            service=  extensionLoader.getExtension(exportAs.name());
            if(service!=null){
                exportServices.putIfAbsent(exportAs, service);
            }
            return exportServices.get(exportAs);
        }
    }
  
    @Override
    public ExportService getExportService(ExportAs exportAs, ExportContext exportContext) {
        ExportService service = getExportService(exportAs);
        service.setContext(exportContext);
        return service;
    }

    @Override
    public void setContainer(Container container) {
        if(container!=null){
            extensionLoader=  container.getExtensionLoader(ExportService.class);
        }
    }

}
