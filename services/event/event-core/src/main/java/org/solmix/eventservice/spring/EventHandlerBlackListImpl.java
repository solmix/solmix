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
package org.solmix.eventservice.spring;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.service.event.EventHandler;
import org.solmix.eventservice.EventHandlerBlackList;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public class EventHandlerBlackListImpl implements EventHandlerBlackList
{

    private final Set<EventHandler> blankList = Collections.synchronizedSet(new HashSet<EventHandler>() {

        /**
         * 
         */
        private static final long serialVersionUID = 7675960799383204788L;

        @Override
        public boolean contains(final Object object) {
            for (Iterator<EventHandler> iter = super.iterator(); iter.hasNext();) {
                final EventHandler handler =  iter.next();
                if (null == handler) {
                    iter.remove();
                }
            }

            return super.contains(object);
        }
    });
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventHandlerBlackList#add(org.osgi.service.event.EventHandler)
     */
    @Override
    public void add(EventHandler handler) {
        blankList.add(handler);
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventHandlerBlackList#contains(org.osgi.service.event.EventHandler)
     */
    @Override
    public boolean contains(EventHandler handler) {
        return  blankList.contains(handler);
    }

   
}
