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

import org.osgi.framework.BundleContext;
import org.solmix.api.event.IValidationEvent;
import org.solmix.api.event.MonitorEventFactory;
import org.solmix.api.event.IValidationEvent.Level;
import org.solmix.api.event.IValidationEvent.OutType;
import org.solmix.api.exception.SlxException;
import org.solmix.event.EventManager;
import org.solmix.event.IEvent;
import org.solmix.fmk.datasource.ValidationEventFactory;
import org.solmix.runtime.SystemContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-9
 */

public class EventWorker
{

    private final SystemContext sc;

    public EventWorker()
    {
        this(null);
    }

    public EventWorker(final SystemContext sc)
    {
        this.sc = sc;
    }

    public EventManager getEventManager() {
        if (sc != null) {
            EventManager em = sc.getBean(EventManager.class);
            if (em != null)
                return em;
        }
        return new NullEventManager();

    }

    public final IEvent createTimeMonitorEvent(long time, String msg) {
        MonitorEventFactory factory;
        boolean destory = false;
        if (sc != null && sc.getBean(BundleContext.class) != null) {
            destory = true;
            factory = MonitorEventFactory.getInstance(sc.getBean(BundleContext.class));
        } else {
            factory = MonitorEventFactory.getDefault();
        }
        IEvent e = factory.createTimeMonitorEvent(time, msg);
        if (destory)
            factory = null;
        return e;
    }

    public final void createAndFireTimeEvent(long time, String msg) {
        getEventManager().postEvent(createTimeMonitorEvent(time, msg));
    }

    public final IEvent createFieldValidationEvent(Level level, String msg) throws SlxException {
        ValidationEventFactory veFactory = ValidationEventFactory.instance();
        veFactory.setType(ValidationEventFactory.TYPE_FIELD);
        IValidationEvent event = veFactory.create(level, msg);
        return event;
    }

    public final IEvent createDSValidationEvent(Level level, String msg, Throwable e) {

        ValidationEventFactory veFactory = ValidationEventFactory.instance();
        veFactory = ValidationEventFactory.instance();
        veFactory.setType(ValidationEventFactory.TYPE_DS);
        IValidationEvent event = null;
        try {
            event = veFactory.create(OutType.SERVER, level, "", msg, e);
        } catch (SlxException e1) {
        }
        return event;
    }

    public final void createAndFireFldValidateEvent(Level level, String msg) throws SlxException {
        getEventManager().postEvent(createFieldValidationEvent(level, msg));
    }

    public final void createAndFireDSValidateEvent(Level level, String msg, Throwable e) {
        getEventManager().postEvent(createDSValidationEvent(level, msg, e));
    }
}
