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

package org.solmix.eventservice.spring;

import java.io.IOException;
import java.util.Hashtable;

import javax.annotation.Resource;

import org.osgi.framework.Filter;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.eventservice.Cache;
import org.solmix.eventservice.EventAdminImpl;
import org.solmix.eventservice.EventFilter;
import org.solmix.eventservice.EventTaskManager;
import org.solmix.eventservice.TopicFilter;
import org.solmix.eventservice.filter.CachedEventFilter;
import org.solmix.eventservice.filter.CachedTopicFilter;
import org.solmix.eventservice.util.LeastRecentlyUsedCacheMap;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;
import org.solmix.runtime.event.EventManager;
import org.solmix.runtime.event.IEvent;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-9-22
 */

public class SpringEventManager implements EventManager
{

    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String SERVICE_PID = "org.solmix.services.event";

    private static final Logger logger = LoggerFactory.getLogger(SpringEventManager.class);

    private String fileEncoding;


    private EventAdminImpl eventAdmin;
    
    private SystemContext sc;
    
    public SpringEventManager(){
        this(null);
    }
    public SpringEventManager(final SystemContext sc){
        setSystemContext(sc);
    }

    /**
     * @param sc2
     */
    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc=sc;
        if(sc!=null){
            sc.setExtension(this, EventManager.class);
        }
        
    }
  

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.event.api.event.EventManager#postEvent(org.solmix.runtime.event.api.event.IEvent)
     */
    @Override
    public void postEvent(IEvent event) {
        eventAdmin.postEvent(toOsgiEvent(event));

    }

    synchronized void setUp() {
        if (eventAdmin == null) {
            eventAdmin = new EventAdminImpl() {

                @Override
                protected EventTaskManager createEventTaskManager() {
                    /**
                     * initial Topic filter.
                     */
                    Cache<String, String> topicCache = new LeastRecentlyUsedCacheMap<String, String>(getCacheSize());
                    final TopicFilter topicFilter = new CachedTopicFilter(topicCache, isRequireTopic());
                    /**
                     * Initial Event filter.
                     */
                    Cache<String, Filter> event_filter_cache = new LeastRecentlyUsedCacheMap<String, Filter>(getCacheSize());
                    final EventFilter eventFilter = new CachedEventFilter(event_filter_cache);

                    return new SpringEventTaskManager(sc, new EventHandlerBlackListImpl(), topicFilter, eventFilter);

                }
            };
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.event.api.event.EventManager#sendEvent(org.solmix.runtime.event.api.event.IEvent)
     */
    @Override
    public void sendEvent(IEvent event) {
        eventAdmin.sendEvent(toOsgiEvent(event));

    }

    private Event toOsgiEvent(IEvent event) {
        if(Event.class.isAssignableFrom(event.getClass())){
            return Event.class.cast(event);
        }else{
            return new Event(event.getTopic(), event.getProperties());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.event.api.event.EventManager#getProvider()
     */
    @Override
    public String getProvider() {
        return "Spring";
    }

   
    /**
     * @return the fileEncoding
     */
    public String getFileEncoding() {
        return fileEncoding;
    }

    /**
     * @param fileEncoding the fileEncoding to set
     */
    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

  

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void start() {
        setUp();
        if(sc!=null){
            ConfigureUnitManager cum=  sc.getExtension(ConfigureUnitManager.class);
        }
        DataTypeMap properties= getConfig();
        Hashtable config = new Hashtable();
        for(Object key:properties.keySet()){
            config.put(key.toString(), properties.get(key));
        }
        eventAdmin.start(config);

    }
    protected DataTypeMap getConfig() {
        ConfigureUnitManager cum = sc.getExtension(ConfigureUnitManager.class);
        ConfigureUnit cu=null;
        try {
            cu = cum.getConfigureUnit(SERVICE_PID);
        } catch (IOException e) {
        }
        if (cu != null)
            return cu.getProperties();
        else
            return new DataTypeMap();
    }
    public void stop() {
        if (eventAdmin != null) {
            eventAdmin.stop();
        }
    }

}
