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

package org.solmix.eventservice.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.eventservice.BlackList;
import org.solmix.eventservice.EventFilter;
import org.solmix.eventservice.EventTask;
import org.solmix.eventservice.EventTaskManager;
import org.solmix.eventservice.TopicFilter;
import org.solmix.eventservice.security.PermissionsUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-1
 */

public class EventTaskManagerImpl implements EventTaskManager
{

    private static final Logger logger = LoggerFactory.getLogger(EventTaskManagerImpl.class);

    private final BundleContext bundleContext;

    private final BlackList blackList;

    private final TopicFilter topicFilter;

    private final EventFilter filter;

    public EventTaskManagerImpl(final BundleContext context, final BlackList blackList, final TopicFilter topic_filter, final EventFilter filter)
    {
        assertNotNull(context, "bundleContext");
        assertNotNull(blackList, "BlankList");
        assertNotNull(topic_filter, "TopicFilter");
        assertNotNull(filter, "EventFilter");
        bundleContext = context;
        this.blackList = blackList;
        this.topicFilter = topic_filter;
        this.filter = filter;
    }

    @Override
    public List<EventTask> createEventTasks(Event event) {
        Collection<ServiceReference<EventHandler>> handlerRefs = null;

        try {
            handlerRefs = bundleContext.getServiceReferences(EventHandler.class, topicFilter.createFilter(event.getTopic()));
        } catch (InvalidSyntaxException e) {
            logger.warn("Invalid EVENT_TOPIC [" + event.getTopic() + "]", e);
        }

        if (null == handlerRefs || handlerRefs.size() == 0) {
            return null;
        }

        final List<EventTask> result = new ArrayList<EventTask>();
        Iterator<ServiceReference<EventHandler>> it = handlerRefs.iterator();
        while (it.hasNext()) {
            ServiceReference<EventHandler> ref = it.next();
            final Bundle serviceBundle = ref.getBundle();
            if (serviceBundle != null) {
                if (!blackList.contains(ref)) {
                    if (serviceBundle.hasPermission(PermissionsUtil.createSubscribePermission(event.getTopic()))) {
                        try {
                            if (event.matches(filter.createFilter((String) ref.getProperty(EventConstants.EVENT_FILTER)))) {
                                result.add(new EventTaskImpl(this, event, ref));
                            }
                        } catch (InvalidSyntaxException e) {
                            logger.warn("Invalid EVENT_FILTER - Blacklisting ServiceReference [" + ref + " | Bundle(" + serviceBundle + ")]", e);
                            blackList.add(ref);
                        }
                    } else {
                        if (logger.isDebugEnabled())
                            logger.debug("Topic :" + event.getTopic() + " have no permission.");
                    }
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("Topic :" + event.getTopic() + " is blacklisting.");
                }
            }
        }
        return result;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTaskManager#getEventHandler(org.osgi.framework.ServiceReference)
     */
    public EventHandler getEventHandler(ServiceReference<EventHandler> eventHandlerRef) {
        final EventHandler result = blackList.contains(eventHandlerRef) ? null : bundleContext.getService(eventHandlerRef);
        return result == null ? this.nullEventHandler : result;
    }

    public void ungetEventHandler(EventHandler handler, ServiceReference<?> eventHandlerRef) {
        if (nullEventHandler != handler) {
            // Is the handler not unregistered or blacklisted?
            if (!blackList.contains(eventHandlerRef) && (null != eventHandlerRef.getBundle())) {
                bundleContext.ungetService(eventHandlerRef);
            }
        }

    }

    public void addToBlackList(ServiceReference<EventHandler> eventHandlerRef) {
        blackList.add(eventHandlerRef);

        logger.warn("Blacklisting ServiceReference [" + eventHandlerRef + " | Bundle(" + eventHandlerRef.getBundle() + ")] due to timeout!");

    }

    private void assertNotNull(final Object object, final String name) {
        if (null == object) {
            throw new NullPointerException(name + " may not be null");
        }
    }

    private final EventHandler nullEventHandler = new EventHandler() {

        /**
         * This is a null object that is supposed to do nothing at this point.
         * 
         * @param event an event that is not used
         */
        @Override
        public void handleEvent(final Event event) {
            // This is a null object that is supposed to do nothing at this
            // point. This is used once a EventHandler is requested for a
            // servicereference that is either stale (i.e., unregistered) or
            // blacklisted.
        }
    };
}
