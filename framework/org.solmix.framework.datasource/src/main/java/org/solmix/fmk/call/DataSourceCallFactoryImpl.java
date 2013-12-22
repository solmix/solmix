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

package org.solmix.fmk.call;

import javax.annotation.Resource;

import org.solmix.api.call.HttpServletRequestParser;
import org.solmix.api.call.DataSourceCall;
import org.solmix.api.call.DataSourceCallFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.context.WebContext;
import org.solmix.api.exception.SlxException;

/**
 * Implements {@link org.solmix.api.call.DataSourceCallFactory}.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-31 solmix-ds
 */
public class DataSourceCallFactoryImpl implements DataSourceCallFactory
{
    
    private SystemContext sc;
    public DataSourceCallFactoryImpl(){
    }
    public DataSourceCallFactoryImpl(SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(SystemContext sc) {
        this.sc = sc;
        if(sc!=null){
            sc.setBean(this, DataSourceCallFactory.class);
        }
    }
 
    @Override
    public DataSourceCall createRPCManager() throws SlxException {

        return new DataSourceCallImpl();
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DataSourceCallFactory#getRPCManager(java.lang.Object)
     */
    @Override
    public DataSourceCall createRPCManager(WebContext context) throws SlxException {

        return new DataSourceCallImpl(context);
    }

    @Override
    public DataSourceCall createRPCManager(WebContext context, HttpServletRequestParser parser) throws SlxException {

        return new DataSourceCallImpl(context, parser);
    }
}
