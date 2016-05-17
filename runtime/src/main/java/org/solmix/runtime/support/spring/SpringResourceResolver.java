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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.solmix.commons.util.ArrayUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.support.ResourceResolverAdaptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月29日
 */

public class SpringResourceResolver extends ResourceResolverAdaptor implements ResourceResolver, ApplicationContextAware
{

    ApplicationContext context;

    /**
     * @param applicationContext
     */
    public SpringResourceResolver(ApplicationContext applicationContext)
    {
        this.context = applicationContext;
    }

    @Override
    public InputStreamResource getAsStream(String name) {
        return getAsStream(name, context);
    }

    protected InputStreamResource getAsStream(String name, ApplicationContext context) {
        InputStreamResource result = null;
        Resource r = context.getResource(name);
        if (r != null && r.exists()) {
            result = new SpringInputStream(r);
        }
        r = context.getResource("/" + name);
        if (r != null && r.exists()) {
            result = new SpringInputStream(r);
        }
        if (result == null && context.getParent() != null) {
            result = getAsStream(name, context.getParent());
        }
        return result;
    }

    @Override
    public InputStreamResource[] getAsStreams(String locationPattern) throws IOException {

        return getAsStreams(locationPattern, context);
    }

    public InputStreamResource[] getAsStreams(String locationPattern, ApplicationContext context) throws IOException {
        InputStreamResource[] ires = null;
        Resource[] resources = context.getResources(locationPattern);

        if (!ArrayUtils.isEmptyArray(resources)) {
            ires = new InputStreamResource[resources.length];
            for (int i = 0; i < resources.length; i++) {
                ires[i] = new SpringInputStream(resources[i]);
            }
        }
        if (ires == null && context.getParent() != null) {
            ires = getAsStreams(locationPattern, context.getParent());
        }
        return ires;
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
            // ignore
        }
        try {
            if (ClassLoader.class.isAssignableFrom(resourceType)) {
                return resourceType.cast(context.getClassLoader());
            } else if (URL.class.isAssignableFrom(resourceType)) {
                Resource r = context.getResource(resourceName);
                if (r != null && r.exists()) {
                    r.getInputStream().close(); // checks to see if the URL really can resolve
                    return resourceType.cast(r.getURL());
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return null;
    }

    public <T> T resolve(String resourceName, Class<T> resourceType, ApplicationContext context) {
        T resource = null;
        try {

            if (resourceName == null) {
                String names[] = context.getBeanNamesForType(resourceType);
                if (names != null && names.length > 0) {
                    resource = resourceType.cast(context.getBean(names[0], resourceType));
                }
            } else {
                resource = resourceType.cast(context.getBean(resourceName, resourceType));
            }
        } catch (NoSuchBeanDefinitionException def) {
            // ignore
        }
        if (resource == null) {
            try {
                if (ClassLoader.class.isAssignableFrom(resourceType)) {
                    resource = resourceType.cast(context.getClassLoader());
                } else if (URL.class.isAssignableFrom(resourceType)) {
                    Resource r = context.getResource(resourceName);
                    if (r != null && r.exists()) {
                        r.getInputStream().close(); // checks to see if the URL really can resolve
                        resource = resourceType.cast(r.getURL());
                    }
                }
            } catch (IOException e) {
                // ignore
            }
        }
        if (resource == null && context.getParent() != null) {
            resource = resolve(resourceName, resourceType, context.getParent());
        }

        return resource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    class SpringInputStream implements InputStreamResource
    {

        private Resource resource;

        SpringInputStream(Resource resource)
        {
            this.resource = resource;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return resource.getInputStream();
        }

        @Override
        public boolean exists() {
            return resource.exists();
        }

        @Override
        public boolean isReadable() {
            return resource.isReadable();
        }

        @Override
        public File getFile() throws IOException {
            return resource.getFile();
        }

        @Override
        public URL getURL() throws IOException {
            return resource.getURL();
        }

        @Override
        public URI getURI() throws IOException {
            return resource.getURI();
        }

        @Override
        public long lastModified() throws IOException {
            return resource.lastModified();
        }

        @Override
        public String getFilename() {
            return resource.getFilename();
        }

        @Override
        public String getDescription() {
            return resource.getDescription();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.solmix.runtime.resource.InputStreamResource#createRelative(java.lang.String)
         */
        @Override
        public InputStreamResource createRelative(String relativePath) throws IOException {
            return new SpringInputStream(resource.createRelative(relativePath));
        }

    }

}
