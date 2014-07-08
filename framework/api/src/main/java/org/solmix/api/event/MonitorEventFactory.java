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
package org.solmix.api.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.solmix.commons.util.DataUtils;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-10-4
 */

public class MonitorEventFactory 
{

    static MonitorEventFactory instance;
    BundleContext bundleContext;
    private MonitorEventFactory( BundleContext context){
        bundleContext= context;
    }
    private MonitorEventFactory(){
        
    }

	public synchronized static MonitorEventFactory getInstance(BundleContext bundleContext) {
        return new MonitorEventFactory(  bundleContext);
    }
    public static MonitorEventFactory getDefault(){
        if(instance==null)
            instance = new MonitorEventFactory();
        return instance;
    }

    public IMonitorEvent createMonitorEvent(Map<String, Object> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    public TimeMonitorEvent createTimeMonitorEvent(long time,TimeUnit unit,String msg) {
        Map<String, Object> properties = new HashMap<String, Object>();
       
        properties.put(TimeMonitorEvent.TOTAL_TIME, time);
        properties.put(TimeMonitorEvent.TIME_UNIT, unit);
        properties.put(TimeMonitorEvent.MESSAGE, msg);
        return createTimeMonitorEvent(properties);
    }
    public TimeMonitorEvent createTimeMonitorEvent(long time,String msg) {
        return createTimeMonitorEvent(time,TimeUnit.MILLISECONDS,msg);
    }

    public TimeMonitorEvent createTimeMonitorEvent(Map<String, Object> properties) {
        TimeMonitorEvent event;
        if(properties!=null){
        DataUtils.mapMerge(defaultProperties(), properties);
        event = new TimeMonitorEvent(properties);
        }else{
            event = new TimeMonitorEvent(defaultProperties());
        }
        return event;
    }
    private Map<String, Object> defaultProperties(){
        Map<String, Object> properties = new HashMap<String, Object>();
        if(this.bundleContext!=null){
            properties.put(TimeMonitorEvent.BUNDLE_ID, bundleContext.getBundle().getBundleId());
            properties.put(TimeMonitorEvent.BUNDLE_SYMBOLICNAME, bundleContext.getBundle().getSymbolicName());
        }
        properties.put(TimeMonitorEvent.TIMESTAMP, System.currentTimeMillis());
        properties.put(TimeMonitorEvent.TIME_UNIT, TimeUnit.MILLISECONDS);
        return properties;
    }

}
