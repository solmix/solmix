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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.eventservice.EventFilter;
import org.solmix.eventservice.EventTask;
import org.solmix.eventservice.EventTaskManager;
import org.solmix.eventservice.EventTopic;
import org.solmix.eventservice.TopicFilter;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-9-22
 * @since 0.4
 */

public class DefaultEventTaskManager implements EventTaskManager
{

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventTaskManager.class);

    private final ApplicationContext applicationContext;

    private final EventHandlerBlackListImpl blackList;

    private final TopicFilter topicFilter;

    private final EventFilter eventFilter;

    private final Map<Filter, EventHandler> cachedHandlers;

    /**
     * @param applicationContext2
     * @param blackListImpl
     * @param topicFilter
     * @param eventFilter
     */
    public DefaultEventTaskManager(ApplicationContext applicationContext, EventHandlerBlackListImpl blackList, TopicFilter topicFilter,
        EventFilter eventFilter)
    {
        this.applicationContext = applicationContext;
        this.blackList = blackList;
        this.topicFilter = topicFilter;
        this.eventFilter = eventFilter;
        cachedHandlers = Collections.synchronizedMap(new HashMap<Filter, EventHandler>());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTaskManager#createEventTasks(org.osgi.service.event.Event)
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<EventTask> createEventTasks(Event event) {
        if (cachedHandlers.isEmpty()) {
            getHandlersFromSpring();
        }
        if (event.getTopic() == null || event.getTopic().trim().equals("")) {
            return null;
        }
        final List<EventTask> result = new ArrayList<EventTask>();
        Hashtable topicFilter = new Hashtable();
        topicFilter.put(EventConstants.EVENT_TOPIC, event.getTopic());
        for (Filter filter : cachedHandlers.keySet()) {
            EventHandler handler = cachedHandlers.get(filter);
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
    private void getHandlersFromSpring() {
        Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(EventTopic.class);
        if (handlers == null || handlers.size() == 0)
            return;
        for (Object handler : handlers.values()) {
            if (handler instanceof EventHandler) {
                EventTopic t = handler.getClass().getAnnotation(EventTopic.class);
                String topic = t.value();
                String formatTopic = topicFilter.createFilter(topic);
                try {
                    Filter filter = FrameworkUtil.createFilter(formatTopic);
                    cachedHandlers.put(filter, (EventHandler) handler);
                } catch (InvalidSyntaxException e) {
                    logger.error("Filter expression syntax error", e);
                }
            }
        }
    }

    /**
     * @param handler
     */
    public void addToBlackList(EventHandler handler) {
        blackList.add(handler);

    }

}
