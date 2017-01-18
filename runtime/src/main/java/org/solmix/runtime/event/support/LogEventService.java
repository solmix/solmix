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
package org.solmix.runtime.event.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Extension;
import org.solmix.runtime.event.EventServiceAdapter;
import org.solmix.runtime.event.IEvent;


/**
 * 只是将Event 作为日志输出，post和send都为同步日志输出.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月3日
 */
@Extension("log")
public class LogEventService extends  EventServiceAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(LogEventService.class);

    
    @Override
    public void postEvent(IEvent event) {
        if(LOG.isDebugEnabled()){
            LOG.debug(event.toString());
        }
    }

    @Override
    public void sendEvent(IEvent event) {
        if(LOG.isDebugEnabled()){
            LOG.debug(event.toString());
        }
    }

}
