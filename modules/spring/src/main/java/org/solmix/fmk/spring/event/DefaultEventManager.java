/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.spring.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.Filter;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.event.EventManager;
import org.solmix.api.event.IEvent;
import org.solmix.eventservice.Cache;
import org.solmix.eventservice.EventAdminImpl;
import org.solmix.eventservice.EventFilter;
import org.solmix.eventservice.EventTaskManager;
import org.solmix.eventservice.TopicFilter;
import org.solmix.eventservice.filter.CachedEventFilter;
import org.solmix.eventservice.filter.CachedTopicFilter;
import org.solmix.eventservice.util.LeastRecentlyUsedCacheMap;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-9-22
 */

public class DefaultEventManager implements EventManager, ApplicationContextAware
{

    public static final String XML_FILE_EXTENSION = ".xml";

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventManager.class);

    private final PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private Resource config;

    private String fileEncoding;

    private ApplicationContext applicationContext;

    private EventAdminImpl eventAdmin;

    /**
     * @return the config
     */
    public Resource getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Resource config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#postEvent(org.solmix.api.event.IEvent)
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

                    return new DefaultEventTaskManager(applicationContext, new EventHandlerBlackListImpl(), topicFilter, eventFilter);

                }
            };
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#sendEvent(org.solmix.api.event.IEvent)
     */
    @Override
    public void sendEvent(IEvent event) {
        eventAdmin.sendEvent(toOsgiEvent(event));

    }

    private Event toOsgiEvent(IEvent event) {
        return new Event(event.getTopic(), event.getProperties());

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#getProvider()
     */
    @Override
    public String getProvider() {
        return "Internal default spring powered event manager";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

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

    protected Properties loadProperties(Resource location) {
        Properties props = new Properties();
        if (logger.isInfoEnabled()) {
            logger.info("Loading properties file from " + location);
        }
        InputStream is = null;
        try {
            is = location.getInputStream();
            String filename = location.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                this.propertiesPersister.loadFromXml(props, is);
            } else {
                if (this.fileEncoding != null) {
                    this.propertiesPersister.load(props, new InputStreamReader(is, this.fileEncoding));
                } else {
                    this.propertiesPersister.load(props, is);
                }
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return props;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void start() {
        setUp();
        Properties prop = loadProperties(config);
        Set<Entry<Object, Object>> entries = prop.entrySet();
        Iterator<Entry<Object, Object>> it = entries.iterator();
        Hashtable config = new Hashtable();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            config.put(entry.getKey().toString(), entry.getValue());
        }
        eventAdmin.start(config);

    }

    public void stop() {
        if (eventAdmin != null) {
            eventAdmin.stop();
        }
    }

}
