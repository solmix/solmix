/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.rpc;

import javax.annotation.Resource;

import org.solmix.api.call.HttpServletRequestParser;
import org.solmix.api.call.DSCManager;
import org.solmix.api.call.DSCManagerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.context.WebContext;
import org.solmix.api.exception.SlxException;

/**
 * Implements {@link org.solmix.api.call.DSCManagerFactory}.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-31 solmix-ds
 */
public class DSCManagerFactoryImpl implements DSCManagerFactory
{
    
    private SystemContext sc;
    public DSCManagerFactoryImpl(){
    }
    public DSCManagerFactoryImpl(SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(SystemContext sc) {
        this.sc = sc;
        if(sc!=null){
            sc.setBean(this, DSCManagerFactory.class);
        }
    }
 
    @Override
    public DSCManager createRPCManager() throws SlxException {

        return new DSCManagerImpl();
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCManagerFactory#getRPCManager(java.lang.Object)
     */
    @Override
    public DSCManager createRPCManager(WebContext context) throws SlxException {

        return new DSCManagerImpl(context);
    }

    @Override
    public DSCManager createRPCManager(WebContext context, HttpServletRequestParser parser) throws SlxException {

        return new DSCManagerImpl(context, parser);
    }
}
