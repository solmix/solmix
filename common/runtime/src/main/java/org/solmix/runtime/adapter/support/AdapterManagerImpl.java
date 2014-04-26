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
package org.solmix.runtime.adapter.support;

import org.solmix.runtime.adapter.AdapterFactory;
import org.solmix.runtime.adapter.AdapterManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年4月24日
 */

public class AdapterManagerImpl implements AdapterManager
{

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#getAdapter(java.lang.Object, java.lang.Class)
     */
    @Override
    public <AdapterType> AdapterType getAdapter(Object adaptable,
        Class<AdapterType> type) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#hasAdapter(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean hasAdapter(Object adaptable, String adapterTypeName) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#registerAdapters(org.solmix.runtime.adapter.AdapterFactory, java.lang.Class)
     */
    @Override
    public void registerAdapters(AdapterFactory factory, Class<?> adaptable) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#unregisterAdapters(org.solmix.runtime.adapter.AdapterFactory)
     */
    @Override
    public void unregisterAdapters(AdapterFactory factory) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.adapter.AdapterManager#unregisterAdapters(org.solmix.runtime.adapter.AdapterFactory, java.lang.Class)
     */
    @Override
    public void unregisterAdapters(AdapterFactory factory, Class<?> adaptable) {
        // TODO Auto-generated method stub
        
    }

}
