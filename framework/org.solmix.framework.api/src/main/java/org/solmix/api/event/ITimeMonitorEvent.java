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
package org.solmix.api.event;

import org.osgi.service.event.EventConstants;
import org.solmix.SlxConstants;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-10-4
 */

public interface ITimeMonitorEvent extends IMonitorEvent
{
    public static final String TIME_MONITOER_TOPIC=SlxConstants.MONITOR_TOPIC_PREFIX+"time";

    public static final String BUNDLE_ID = EventConstants.BUNDLE_ID;
    public static final String BUNDLE_SYMBOLICNAME =EventConstants.BUNDLE_SYMBOLICNAME;
    public static final String TIMESTAMP =EventConstants.TIMESTAMP;
    public static final String MESSAGE =EventConstants.MESSAGE;
    public static final String TOTAL_TIME ="totalTime";
    public static final String TIME_UNIT ="timeUnit";
    public static final String UNIT_DAYS ="days";
    public static final String UNIT_HOURS ="hours";
    public static final String UNIT_MICROSECONDS ="microseconds";
    public static final String UNIT_MILLISECONDS ="milliseconds";
    public static final String UNIT_MINUTES ="minutes";
    public static final String UNIT_NANOSECONDS ="nanoseconds";
    public static final String UNIT_SECONDS ="seconds";
}
