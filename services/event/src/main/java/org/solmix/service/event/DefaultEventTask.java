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
package org.solmix.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public class DefaultEventTask implements EventTask
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultEventTask.class);
    private final DefaultEventTaskManager taskManager;
    private final IEvent event;
    private final IEventHandler handler;
    
    /**
     * @param defaultEventTaskManager
     * @param event
     * @param eventHandler
     */
    public DefaultEventTask(DefaultEventTaskManager taskManager, IEvent event, IEventHandler handler)
    {
        this.taskManager=taskManager;
        this.event=event;
        this.handler=handler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTask#execute()
     */
    @Override
    public void execute() {
        try {
            handler.handle(event);
        } catch (final Throwable e) {
            // The spec says that we must catch exceptions and log them:
            logger.error("Exception during event dispatch [" + event.getTopic() + "] | used handler["+handler.getClass().getName()+"]", e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTask#blackListHandler()
     */
    @Override
    public void blackListHandler() {
        taskManager.addToBlackList(handler);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventTask#getHandlerClassName()
     */
    @Override
    public String getHandlerClassName() {
        return handler.getClass().getName();
    }

}
