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

package org.solmix.eventservice.tasks;

import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import org.solmix.eventservice.Activator;
import org.solmix.eventservice.EventTask;

/**
 * 
 * @author solomon
 * @version 110035 2011-10-1
 */

public class EventTaskImpl implements EventTask
{

    // The service reference of the handler
    private final ServiceReference/* <EventHandler> */eventHandlerRef;

    // The event to deliver to the handler
    private final Event event;

    private final EventTaskManagerImpl taskManager;

    EventTaskImpl(EventTaskManagerImpl manager, Event event, ServiceReference/* <EventHandler> */ref)
    {
        eventHandlerRef = ref;
        this.event = event;
        taskManager = manager;
    }

    public void execute() {
        // Get the service object
        final EventHandler handler = taskManager.getEventHandler(eventHandlerRef);

        try {
            handler.handleEvent(event);
        } catch (final Throwable e) {
            // The spec says that we must catch exceptions and log them:
            Activator.getLogService().log(eventHandlerRef, LogService.LOG_WARNING,
                "Exception during event dispatch [" + event + " | " + eventHandlerRef + " | Bundle(" + eventHandlerRef.getBundle() + ")]", e);
        } finally {
            taskManager.ungetEventHandler(handler, eventHandlerRef);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTask#blackListHandler()
     */
    @Override
    public void blackListHandler() {
        taskManager.addToBlackList(eventHandlerRef);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTask#getHandlerClassName()
     */
    @Override
    public String getHandlerClassName() {
        final EventHandler handler = taskManager.getEventHandler(eventHandlerRef);
        try {
            return handler.getClass().getName();
        } finally {
            taskManager.ungetEventHandler(handler, eventHandlerRef);
        }
    }

}
