/*
 * ========THE SOLMIX PROJECT=====================================
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
package com.solmix.api.event;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.event.Event;
import com.solmix.SlxConstants;
import com.solmix.api.event.IValidationEvent.Level;


/**
 * 
 * @author solomon
 * @version 110035  2011-10-5
 */

public class ValidationEventWrapper
{

    public static final String ERROR_MESSAGE="errorMessage";
    public static final String OUT_TYPE="outType";
    public static final String EXCEPTION="exception";
    public static final String STATUS="status";
    public static final String NAME="name";
    public static final String LEVEL ="level";
    public static Event wrapper(IValidationEvent vevent){
        Dictionary <String,Object> properties = new Hashtable<String,Object>();
       String topic= SlxConstants.VALIDATION_TOPIC_SUFFIX;
       Level level =vevent.getLevel();
        switch(level){
            case DEBUG:
                topic=topic+"DEBUG";
                break;
            case WARNING:
                topic=topic+"WARNING";
                break;
            case ERROR:
                topic=topic+"ERROR";
                break;
                default:
                    topic=topic+"DEFAULT";
                    
        }
        if (vevent.getErrorMessage() != null)
        properties.put(ERROR_MESSAGE, vevent.getErrorMessage());
        if (vevent.getOutType() != null)
        properties.put(OUT_TYPE, vevent.getOutType().value());
        if (vevent.getException() != null)
        properties.put(EXCEPTION, vevent.getException());
        if (vevent.getStuts() != null)
        properties.put(STATUS, vevent.getStuts());
        if (vevent.getName() != null)
        properties.put(NAME, vevent.getName());
        if (vevent.getLevel() != null)
        properties.put(LEVEL, vevent.getLevel());
        return new Event(topic,properties);
    }
}
