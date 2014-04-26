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
package org.solmix.fmk.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.event.EventManager;
import org.solmix.api.event.IEvent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年11月8日
 */

public class NullEventManager implements EventManager
{

    
    private static final Logger LOG = LoggerFactory.getLogger(NullEventManager.class);
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#postEvent(org.solmix.api.event.IEvent)
     */
    @Override
    public void postEvent(IEvent event) {
        LOG.trace("postEvent():\n{}", event);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#sendEvent(org.solmix.api.event.IEvent)
     */
    @Override
    public void sendEvent(IEvent event) {
        LOG.trace("sendEvent():\n{}", event);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#getProvider()
     */
    @Override
    public String getProvider() {
        return "null manager setting,used this";
    }

}
