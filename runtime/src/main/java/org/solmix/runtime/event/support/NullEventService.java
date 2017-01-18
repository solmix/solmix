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

import org.solmix.runtime.Extension;
import org.solmix.runtime.event.EventServiceAdapter;
import org.solmix.runtime.event.IEvent;


/**
 * 没有EventService实现时，为了不让其他程序报错，
 * 设置的不对Event做任何处理的服务，直接丢弃Event.
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月3日
 */
@Extension("null")
public class NullEventService extends EventServiceAdapter
{
    @Override
    public void postEvent(IEvent event) {
        event=null;
    }

    @Override
    public void sendEvent(IEvent event) {
        event=null;
    }

	

}
