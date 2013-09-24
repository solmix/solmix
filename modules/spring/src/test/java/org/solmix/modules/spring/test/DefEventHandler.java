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
package org.solmix.modules.spring.test;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.solmix.eventservice.EventTopic;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */
@EventTopic("org/solmix/monitor/time" )
public class DefEventHandler implements EventHandler
{

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    @Override
    public void handleEvent(Event event) {
       System.out.println(event.getTopic());
        
    }

}
