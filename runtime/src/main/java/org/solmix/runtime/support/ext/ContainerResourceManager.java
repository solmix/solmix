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
package org.solmix.runtime.support.ext;

import javax.annotation.Resource;

import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.support.ObjectTypeResolver;
import org.solmix.runtime.resource.support.ResourceManagerImpl;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月7日
 */

public class ContainerResourceManager extends ResourceManagerImpl
{
    
    private Container container;

    
    /**
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

    @Override
    protected void onFirstResolve() {
        super.onFirstResolve();
        if (container != null) {
            ConfiguredBeanProvider locator = container.getExtension(ConfiguredBeanProvider.class);
            if (locator != null) {
                this.addResourceResolvers(locator.getBeansOfType(ResourceResolver.class));
            }
        }
    }
    /**
     * @param container the container to set
     */
    @Resource
    public void setContainer(Container container) {
        this.container = container;
        firstCalled = false;
        super.addResourceResolver(new ObjectTypeResolver(container));
        if (null != container) {
            container.setExtension(this, ResourceManager.class);
        }
    }
    
    
    

}
