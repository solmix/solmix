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

import org.solmix.runtime.event.IEventHandler;



/**
 * Topic Black list,at osgi-env used {@link BlackList}.
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public interface EventHandlerBlackList
{
    /**
     * Add a topic to this blacklist.
     * 
     * @param ref The reference of the service that is blacklisted
     */
    public void add(final IEventHandler handler);

    /**
     * Lookup whether a given topic is blacklisted.
     * 
     * @param ref The reference of the service
     * 
     * @return <tt>true</tt> in case that the service reference has been blacklisted, <tt>false</tt> otherwise.
     */
    public boolean contains(final IEventHandler handler);
}
