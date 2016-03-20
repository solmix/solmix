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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月30日
 */

public abstract class AbstractResourceLoader extends ResourceLoader
{
                       
    protected abstract InputStreamResource getResource(String templateName);

    protected abstract String getLogID();

    protected abstract String getDesc();
    
    @Override
    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        InputStreamResource resource =getResource(source);
        Exception exception = null;
        if (resource != null && resource.exists()) {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                exception = e;
            }
        }
        throw new ResourceNotFoundException(getLogID() + " Error: could not find template: " + source, exception);
    }

   
    @Override
    public boolean isSourceModified(Resource resource) {
        InputStreamResource res =getResource(resource.getName());
        if (resource == null || !res.exists()) {
            return true;
        }

        long lastModified;

        try {
            lastModified = res.lastModified();
        } catch (IOException e) {
            lastModified = 0;
        }

        // 2. 假如资源找到了，但是不支持lastModified功能，则认为modified==false，模板不会重新装载。
        if (lastModified <= 0L) {
            return false;
        }

        // 3. 资源找到，并支持lastModified功能，则比较lastModified。
        return lastModified != resource.getLastModified();
    }
    @Override
    public boolean resourceExists(String resourceName) {
        InputStreamResource res =getResource(resourceName);
        return res != null && res.exists();
    }

    protected final String normalizeTemplateName(String templateName) {
        if (StringUtils.isEmpty(templateName)) {
            throw new ResourceNotFoundException("Need to specify a template name!");
        }

        if (templateName.startsWith("/")) {
            templateName = templateName.substring(1);
        }

        return templateName;
    }

    @Override
    public long getLastModified(Resource resource) {
        InputStreamResource res =getResource(resource.getName());
        if (resource != null && res.exists()) {
            try {
                return res.lastModified();
            } catch (IOException e) {
            }
        }
        return 0;
    }

}
