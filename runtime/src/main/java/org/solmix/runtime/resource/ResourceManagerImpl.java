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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class ResourceManagerImpl implements ResourceManager
{
    private static final Logger LOG = LoggerFactory.getLogger(ResourceManagerImpl.class);

    protected final List<ResourceResolver> registeredResolvers    = new CopyOnWriteArrayList<ResourceResolver>();

    protected boolean firstCalled;
    
    
    public ResourceManagerImpl(){
        addResourceResolver(new ClasspathResolver());
        addResourceResolver(new ClassLoaderResolver(getClass().getClassLoader()));
    }
    public ResourceManagerImpl(ResourceResolver...resolvers ){
        if(resolvers!=null){
            addResourceResolvers(Arrays.asList(resolvers));
        }
    }
    public final void addResourceResolvers(Collection<? extends ResourceResolver> resolvers) { 
        for (ResourceResolver r : resolvers) {
            addResourceResolver(r);
        }
    } 
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#resolveResource(java.lang.String, java.lang.Class, java.util.List)
     */
    @Override
    public <T> T resolveResource(String name, Class<T> type,
        List<ResourceResolver> resolvers) {
        return findResource(name, type, false, resolvers);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#resolveResource(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T resolveResource(String name, Class<T> type) {
        return findResource(name, type, false, registeredResolvers);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#getResourceAsStream(java.lang.String)
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        return findResource(name, InputStream.class, true, registeredResolvers);
    }

    
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#addResourceResolver(org.solmix.runtime.resource.ResourceResolver)
     */
    @Override
    public void addResourceResolver(ResourceResolver resolver) {
        if (!registeredResolvers.contains(resolver)) { 
            registeredResolvers.add(0, resolver);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#removeResourceResolver(org.solmix.runtime.resource.ResourceResolver)
     */
    @Override
    public void removeResourceResolver(ResourceResolver resolver) {
        if (registeredResolvers.contains(resolver)) { 
            registeredResolvers.remove(resolver);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.resource.ResourceManager#getResourceResolvers()
     */
    @Override
    public List<ResourceResolver> getResourceResolvers() {
        return Collections.unmodifiableList(registeredResolvers); 
    }

    
    private <T> T findResource(String name, Class<T> type, boolean asStream, 
                               List<ResourceResolver> resolvers) {
        if (!firstCalled) {
            onFirstResolve();
        }
        if (resolvers == null) {
            resolvers = registeredResolvers;
        }
        if (LOG.isTraceEnabled()) { 
            LOG.trace("resolving resource [" + name + "]" + (asStream ? " as stream "  
                                                            : " type [" + type + "]"));
        }

        T ret = null; 
        
        for (ResourceResolver rr : resolvers) { 
            if (asStream) { 
                ret = type.cast(rr.getAsStream(name));
            } else { 
                ret = rr.resolve(name, type);
            }
            if (ret != null) { 
                break;
            }
        } 
        return ret;
    }
    protected void onFirstResolve() {
        //nothing
        firstCalled = true;
    }

    @Override
    public <T> T resolveResource(String resourceName, Class<T> resourceType,
        String implementor) {
        return resolveResource(new StringBuilder().append(resourceName).append("@").append(implementor).toString(),resourceType);

    }
}
