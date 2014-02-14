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
package org.solmix.api.event;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.event.Event;


/**
 * Delegate Osgi {@link org.osgi.service.event.Event Event} to {@link org.solmix.api.event.IEvent IEvent}
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-14
 */

public class DelegateEvent implements IEvent
{

    private final Event event;
    
    DelegateEvent(Event event){
        this.event=event;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> _tmp = new HashMap<String,Object>();
       for(String str:event.getPropertyNames()){
           _tmp.put(str, event.getProperty(str));
       }
        return _tmp;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String name) {
        return event.getProperty(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getTopic()
     */
    @Override
    public String getTopic() {
        return event.getTopic();
    }

}
