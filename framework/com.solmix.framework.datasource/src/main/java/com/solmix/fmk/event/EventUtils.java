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

package com.solmix.fmk.event;

import com.solmix.api.event.IEvent;
import com.solmix.api.event.IValidationEvent;
import com.solmix.api.event.IValidationEvent.Level;
import com.solmix.api.event.IValidationEvent.OutType;
import com.solmix.api.event.MonitorEventFactory;
import com.solmix.api.exception.SlxException;
import com.solmix.commons.logs.Logger;
import com.solmix.fmk.context.SlxContext;
import com.solmix.fmk.datasource.ValidationEventFactory;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-30
 */

public class EventUtils
{

    static ValidationEventFactory veFactory;

    public final static IEvent createTimeMonitorEvent(long time, String msg) {
        MonitorEventFactory factory;
        boolean destory = false;
        if (SlxContext.getBundleContext() != null) {
            destory = true;
            factory = MonitorEventFactory.getInstance(SlxContext.getBundleContext());
        } else {
            factory = MonitorEventFactory.getDefault();
        }
        IEvent e = factory.createTimeMonitorEvent(time, msg);
        if (destory)
            factory = null;
        return e;
    }

    public static void createAndFireTimeEvent(long time, String msg) {
        SlxContext.getEventManager().postEvent(createTimeMonitorEvent(time, msg));
    }

    public final static IEvent createFieldValidationEvent(Level level, String msg) throws SlxException {
        if (veFactory == null)
            veFactory = ValidationEventFactory.instance();
        veFactory.setType(ValidationEventFactory.TYPE_FIELD);
        IValidationEvent event = veFactory.create(level, msg);
        return event;
    }

    public final static IEvent createDSValidationEvent(Level level, String msg, Throwable e) {

        if (veFactory == null) {
            veFactory = ValidationEventFactory.instance();
        }
        veFactory.setType(ValidationEventFactory.TYPE_DS);
        IValidationEvent event = null;
        try {
            event = veFactory.create(OutType.SERVER, level, "", msg, e);
        } catch (SlxException e1) {
        }
        return event;
    }

    public final static void createAndFireFldValidateEvent(Level level, String msg) throws SlxException {
        SlxContext.getEventManager().postEvent(createFieldValidationEvent(level, msg));
        Logger.validation.debug(msg);
    }

    public final static void createAndFireDSValidateEvent(Level level, String msg, Throwable e) {
        SlxContext.getEventManager().postEvent(createDSValidationEvent(level, msg, e));
        Logger.validation.debug(msg);
    }
}
