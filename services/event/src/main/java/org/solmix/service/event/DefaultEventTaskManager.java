/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.service.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月14日
 */

public class DefaultEventTaskManager implements EventTaskManager
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventTaskManager.class);

    private final Container container;
    private final EventHandlerBlackList blackList;

    private final TopicFilter topicFilter;
    
    private final Map<Filter, IEventHandler> cachedHandlers;
    /**
     * @param eventHandlerBlackListImpl
     * @param topicFilter
     */
    public DefaultEventTaskManager( Container container,EventHandlerBlackList backlist, TopicFilter topicFilter)
    {
        this.container=container;
        this.blackList=backlist;
        this.topicFilter=topicFilter;
        cachedHandlers = Collections.synchronizedMap(new HashMap<Filter, IEventHandler>());
    }
    @SuppressWarnings("unchecked")
    public List<EventTask> createEventTasks(IEvent event) {
        if (cachedHandlers.isEmpty()) {
            getHandlersFromContainer();
        }
        if (event.getTopic() == null || event.getTopic().trim().equals("")) {
            return null;
        }
        final List<EventTask> result = new ArrayList<EventTask>();
        @SuppressWarnings("rawtypes")
        Hashtable topicFilter = new Hashtable();
        topicFilter.put("event.topics", event.getTopic());
        for (Filter filter : cachedHandlers.keySet()) {
            IEventHandler handler = cachedHandlers.get(filter);
            if (filter.match(topicFilter)) {
                if (!blackList.contains(handler)) {
                    //XXX No support event filter,maybe support by next version.
                    result.add(new DefaultEventTask(this, event, handler));
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("Topic :" + event.getTopic() + " is blacklisting.");
                }
            }
        }
        return result;
    }

    /**
     * 
     */
    private void getHandlersFromContainer() {
        if(container!=null){
            ConfiguredBeanProvider provider=   container.getExtension(ConfiguredBeanProvider.class);
            Collection< ? extends IEventHandler> handlers=  provider.getBeansOfType(IEventHandler.class);
            for(IEventHandler handler:handlers){
                EventTopic t = handler.getClass().getAnnotation(EventTopic.class);
                String topic = t.value();
                String formatTopic = topicFilter.createFilter(topic);
                try {
                    Filter filter = FrameworkUtil.createFilter(formatTopic);
                    cachedHandlers.put(filter, handler);
                } catch (InvalidSyntaxException e) {
                    logger.error("Filter expression syntax error", e);
                }
            }
        }
    }

    /**
     * @param handler
     */
    public void addToBlackList(IEventHandler handler) {
        blackList.add(handler);

    }

}
