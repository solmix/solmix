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

package com.solmix.api.request;

import java.io.PrintWriter;
import java.util.Iterator;

public interface RequestProgressTracker
{

    /** Creates an entry with the given message */
    void log(String message);

    /**
     * Creates an entry with a message constructed from the given <code>MessageFormat</code> format evaluated using the
     * given formatting arguments.
     */
    void log(String format, Object... args);

    /**
     * Starts a named timer. If a timer of the same name already exists, it is reset to the current time.
     */
    void startTimer(String timerName);

    /**
     * Logs an entry with the message set to the name of the timer and the number of milliseconds elapsed since the
     * timer start.
     */
    void logTimer(String timerName);

    /**
     * Logs an entry with the message constructed from the given <code>MessageFormat</code> pattern evaluated using the
     * given arguments and the number of milliseconds elapsed since the timer start.
     */
    void logTimer(String timerName, String format, Object... args);

    /**
     * Returns an <code>Iterator</code> of tracking entries. If there are no messages <code>null</code> is returned.
     */
    Iterator<String> getMessages();

    /**
     * Dumps the process timer entries to the given writer, one entry per line.
     */
    void dump(PrintWriter writer);

    /**
     * Call this when done processing the request - all calls except the first one are ignored
     */
    void done();
}
