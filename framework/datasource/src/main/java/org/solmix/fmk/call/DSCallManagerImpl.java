/*
 *  Copyright 2012 The Solmix Project
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallInterceptor;
import org.solmix.api.call.DSCallManager;
import org.solmix.api.call.InterceptorOrder;
import org.solmix.api.call.InterceptorOrder.PRIORITY;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.Context;
import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.BasicDataSource;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月25日
 */

public class DSCallManagerImpl implements DSCallManager
{
    private static final Logger log = LoggerFactory.getLogger(DSCallManagerImpl.class.getName());

    private final SystemContext sc;
    private boolean executeFirstSet=false;
    private boolean inited;
    private DataTypeMap config;

    protected final LinkedList<DSCallInterceptor> interceptors = new LinkedList<DSCallInterceptor>();

    public DSCallManagerImpl(final SystemContext sc)
    {
        this.sc = sc;
    }

    @Override
    public void addInterceptors(DSCallInterceptor... interceptors) {
        if(inited)
            new IllegalStateException("Cannot add Interceptor After the manager have been initialed");
       for(DSCallInterceptor i:interceptors){
           positionInterceptor( InterceptorOrder.class.isAssignableFrom(i.getClass())?
            InterceptorOrder.class.cast(i).priority():InterceptorOrder.AFTER_DEFAULT,i);
       }

    }
    public  void init() throws SlxException{
        if(!inited){
            this.configInterceptor();
        }
    }

    /**
     * @param priority
     * @param i
     */
    protected void positionInterceptor(PRIORITY priority, DSCallInterceptor i) {
        switch(priority){
            case AFTER_DEFAULT:
                interceptors.addLast(i);
                break;
            case BEFORE_DEFAULT:
                int pos = executeFirstSet ? 1 : 0;
                interceptors.add(pos, i);
                break;
            case FIRST_BEFORE_DEFAULT:
                if (executeFirstSet)
                    throw new IllegalStateException("Cannot set more than one Interceptor to be executed first");
                log.info("DSCallInterceptor {} will always be executed first", i);
                interceptors.addFirst(i);
                executeFirstSet = true;
                break;
        }
    }
    @SuppressWarnings("unchecked")
    protected void configInterceptor() throws SlxException{
        ConfigureUnitManager cum = sc.getBean(org.solmix.api.cm.ConfigureUnitManager.class);
        ConfigureUnit cu = null;
        DataTypeMap frameworkConfig;
        try {
            cu = cum.getConfigureUnit(BasicDataSource.PID);
        } catch (IOException e) {
            throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
        }
        if (cu != null)
            frameworkConfig= cu.getProperties();
        else
            frameworkConfig= new DataTypeMap();
        Map<Object,Object> merged= new HashMap<Object,Object>();
        DataUtil.mapMerge(frameworkConfig, merged);
        DataUtil.mapMerge(config, merged);
        DataTypeMap interceptorConfig=new DataTypeMap(Collections.unmodifiableMap(merged));
        for(DSCallInterceptor i:interceptors){
            i.configure(interceptorConfig);
        }
        
    }
    
    DSCallInterceptor[] toArray(){
        DSCallInterceptor[] array= new DSCallInterceptor[interceptors.size()];
        return interceptors.toArray(array);
    }
    @Override
    public DSCall getDSCall() throws SlxException {
        init();
        return new DSCallImpl(toArray());
    }


    @Override
    public DSCall getDSCall(Context requestcontext) throws SlxException {
        DSCall call= getDSCall();
        call.setRequestContext(requestcontext);
        return call;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.call.DSCallManager#setConfig(org.solmix.commons.collections.DataTypeMap)
     */
    @Override
    public void setConfig(DataTypeMap config) {
        this.config=config;
    }

}
