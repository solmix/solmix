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
package org.solmix.service.event.filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.solmix.runtime.event.IEventHandler;
import org.solmix.service.event.EventHandlerBlackList;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public class EventHandlerBlackListImpl implements EventHandlerBlackList
{

    private final Set<IEventHandler> blankList = Collections.synchronizedSet(new HashSet<IEventHandler>() {

        /**
         * 
         */
        private static final long serialVersionUID = 7675960799383204788L;

        @Override
        public boolean contains(final Object object) {
            for (Iterator<IEventHandler> iter = super.iterator(); iter.hasNext();) {
                final IEventHandler handler =  iter.next();
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
     * @see org.solmix.eventservice.IEventHandlerBlackList#add(org.osgi.service.event.IEventHandler)
     */
    @Override
    public void add(IEventHandler handler) {
        blankList.add(handler);
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.IEventHandlerBlackList#contains(org.osgi.service.event.IEventHandler)
     */
    @Override
    public boolean contains(IEventHandler handler) {
        return  blankList.contains(handler);
    }

   
}
