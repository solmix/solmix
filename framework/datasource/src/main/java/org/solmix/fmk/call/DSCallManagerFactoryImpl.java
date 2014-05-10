/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.call;

import javax.annotation.Resource;

import org.solmix.api.call.DSCallManager;
import org.solmix.api.call.DSCallManagerFactory;
import org.solmix.runtime.SystemContext;

/**
 * Implements {@link org.solmix.api.call.DataSourceCallFactory}.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-31 solmix-ds
 */
public class DSCallManagerFactoryImpl implements DSCallManagerFactory
{
    
    private SystemContext sc;
    public DSCallManagerFactoryImpl(){
    }
    public DSCallManagerFactoryImpl(final SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
        if(sc!=null){
            sc.setBean(this, DSCallManagerFactoryImpl.class);
        }
    }
    
    @Override
    public DSCallManager createDSCallManager() {
        return new DSCallManagerImpl(sc);
    }
    
    @Override
    public DSCallManager createSimpleDSCallManager() {
        return new DSCallManagerImpl(sc);
    }
 
}
