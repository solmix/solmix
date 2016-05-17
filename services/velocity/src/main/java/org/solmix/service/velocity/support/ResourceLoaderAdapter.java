/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.service.velocity.support;

import org.apache.commons.collections.ExtendedProperties;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月30日
 */

public class ResourceLoaderAdapter extends AbstractResourceLoader
{

    private ResourceManager resourceManager;
    private String         path;
    @Override
    protected InputStreamResource getResource(String templateName) {
        return resourceManager.getResourceAsStream(path+normalizeTemplateName(templateName));
    }

   
    @Override
    protected String getLogID() {
        return getClass().getSimpleName();
    }

    @Override
    protected String getDesc() {
        return path;
    }

    @Override
    public void init(ExtendedProperties configuration) {
        rsvc.getLog().info(getLogID() + " : initialization starting.");
        resourceManager=Assert.assertNotNull((ResourceManager)rsvc.getApplicationAttribute(ResourceManager.class.getName()));
        
        path = FileUtils.normalizeAbsolutePath(configuration.getString("path"), true);
        path += "/";
        Assert.assertTrue(!StringUtils.isEmpty(path), "path");

        rsvc.getLog().info(getLogID() + " : set path '" + path + "'");
        rsvc.getLog().info(getLogID() + " : initialization complete.");
    }

}
