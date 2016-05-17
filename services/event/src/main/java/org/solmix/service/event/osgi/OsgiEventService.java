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
package org.solmix.service.event.osgi;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.solmix.runtime.event.EventService;
import org.solmix.runtime.event.IEvent;


/**
 * 通过OSGI 的EvnetAdmin提供服务.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月14日
 */

public class OsgiEventService implements EventService
{
    private volatile EventAdmin eventAdmin;
    
    public synchronized void unregister_eventAdmin(EventAdmin eventAdmin) {
        eventAdmin = null;
    }

    public synchronized void register_eventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }
    public boolean haveEventAdmin() {
        return eventAdmin != null;
    }

    /**
     * Initiate synchronous delivery of an event. This method does not return to the caller until delivery of the event
     * is completed.
     * 
     * @param event The event to send to all listeners which subscribe to the topic of the event.
     * 
     * @throws SecurityException If the caller does not have <code>TopicPermission[topic,PUBLISH]</code> for the topic
     *         specified in the event.
     */
    @Override
    public void sendEvent(IEvent event) {

        if (eventAdmin != null)
            eventAdmin.sendEvent(toOsgiEvent(event));
        else
            notSet();
    }

    private Event toOsgiEvent(IEvent event) {
        if(event.getClass().isAssignableFrom(Event.class)){
            return Event.class.cast(event);
        }else{
            return new Event(event.getTopic(), event.getProperties());
        }
    }

    protected  void notSet() {
        if (eventAdmin==null)
            throw new NullPointerException("EventAdmin Service not reserved!");
        else
            return;
    }

    /**
     * Initiate asynchronous delivery of an event. This method returns to the caller before delivery of the event is
     * completed.
     * 
     * @param event The event to send to all listeners which subscribe to the topic of the event.
     * 
     * @throws SecurityException If the caller does not have <code>TopicPermission[topic,PUBLISH]</code> for the topic
     *         specified in the event.
     */

    @Override
    public void postEvent(IEvent event) {
        if (eventAdmin != null)
            eventAdmin.postEvent(toOsgiEvent(event));
        else
            notSet();

    }

}
