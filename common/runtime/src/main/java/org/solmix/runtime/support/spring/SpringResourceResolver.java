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
package org.solmix.runtime.support.spring;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.ResourceResolverAdaptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月29日
 */

public class SpringResourceResolver extends ResourceResolverAdaptor implements ResourceResolver,ApplicationContextAware
{
    ApplicationContext context;
    /**
     * @param applicationContext
     */
    public SpringResourceResolver(ApplicationContext applicationContext)
    {
       this.context=applicationContext;
    }
    @Override
    public InputStream getAsStream(String name) {
        Resource r = context.getResource(name);
        if (r != null && r.exists()) {
            try {
                return r.getInputStream();
            } catch (IOException e) {
                //ignore and return null
            }
        } 
        r = context.getResource("/" + name);
        if (r != null && r.exists()) {
            try {
                return r.getInputStream();
            } catch (IOException e) {
                //ignore and return null
            }
        } 
        return null;
    }

    @Override
    public <T> T resolve(String resourceName, Class<T> resourceType) {
           
        try {
            T resource = null;
            if (resourceName == null) {
                String names[] = context.getBeanNamesForType(resourceType);
                if (names != null && names.length > 0) {
                    resource = resourceType.cast(context.getBean(names[0], resourceType));
                }
            } else {
                resource = resourceType.cast(context.getBean(resourceName, resourceType));
            }
            return resource;
        } catch (NoSuchBeanDefinitionException def) {
            //ignore
        }
        try {
            if (ClassLoader.class.isAssignableFrom(resourceType)) {
                return resourceType.cast(context.getClassLoader());
            } else if (URL.class.isAssignableFrom(resourceType)) {
                Resource r = context.getResource(resourceName);
                if (r != null && r.exists()) {
                    r.getInputStream().close(); //checks to see if the URL really can resolve
                    return resourceType.cast(r.getURL());
                }
            }
        } catch (IOException e) {
            //ignore
        }
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;        
    }

}
