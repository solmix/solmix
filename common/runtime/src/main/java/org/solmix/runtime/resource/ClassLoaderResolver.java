/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.runtime.resource;

import java.io.InputStream;
import java.net.URL;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class ClassLoaderResolver extends ResourceResolverAdaptor
{

    private final ClassLoader loader; 

    public ClassLoaderResolver() { 
        this(ClassLoaderResolver.class.getClassLoader());
    }

    public ClassLoaderResolver(ClassLoader l) { 
        loader = l;
    }
 
    @Override
    public <T> T resolve(String resourceName, Class<T> resourceType) {
        if (resourceName == null || resourceType == null) {
            return null;
        }
        URL url = loader.getResource(resourceName);
        if (resourceType.isInstance(url)) {
            return resourceType.cast(url);
        }
        return null;
    } 

    @Override
    public InputStream getAsStream(String name) { 
        return loader.getResourceAsStream(name);
    } 

}
