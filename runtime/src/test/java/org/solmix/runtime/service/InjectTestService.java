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
package org.solmix.runtime.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.solmix.runtime.Container;
import org.solmix.runtime.adapter.AdapterManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月3日
 */

public class InjectTestService
{
    @Resource
    private AdapterManager adm;
    private TimeService timeService;
    private  Container c;
    public  InjectTestService(Container c){
        this.c=c;
    }

    public void setContainer(Container c){
        this.c=c;
    }
    public Container getContainer(){
        return c;
    }
    
    public AdapterManager getAdapterManager(){
        return adm;
    }
    @PreDestroy
    public void close(){
        System.out.println("close!!!!!!");
    }
    @PostConstruct
    public void setup(){
        System.out.println("setUp!!!!!!");
    }
    
    /**
     * @return the timeService
     */
    public TimeService getTimeService() {
        return timeService;
    }

    
    /**
     * @param timeService the timeService to set
     */
    @Resource
    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
    
}
