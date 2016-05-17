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

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.ExtendedProperties;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.resource.InputStreamResource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月30日
 */

public class PreloadedResourceLoader extends AbstractResourceLoader
{
    public static final String PRELOADED_RESOURCES_KEY = "resources";
    
    private Map<String, InputStreamResource> preloadedResources;
   
    @Override
    protected InputStreamResource getResource(String templateName) {
        return preloadedResources.get(normalizeTemplateName(templateName));
    }
   
    @Override
    protected String getLogID() {
        return getClass().getSimpleName();
    }
  
    @Override
    protected String getDesc() {
        return preloadedResources.size() + " preloaded resources";
    }

    @Override
    public void init(ExtendedProperties configuration) {
        rsvc.getLog().info(getLogID() + " : initialization starting.");

        preloadedResources= new TreeMap<String, InputStreamResource>();
        
        @SuppressWarnings("unchecked")
        Map<String, InputStreamResource> resources = Assert.assertNotNull(
            (Map<String, InputStreamResource>) configuration.getProperty(PRELOADED_RESOURCES_KEY), PRELOADED_RESOURCES_KEY);

        for (Map.Entry<String, InputStreamResource> entry : resources.entrySet()) {
            String templateName = normalizeTemplateName(entry.getKey());
            InputStreamResource resource = entry.getValue();

            preloadedResources.put(templateName, resource);
        }

        rsvc.getLog().info(getLogID() + " : preloaded resources: " + preloadedResources);
        rsvc.getLog().info(getLogID() + " : initialization complete.");
    }

}
