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

package org.solmix.fmk.engine.internal.request;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.request.RequestProgressTracker;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class SlxRequestProgressTracker implements RequestProgressTracker
{

    /**
     * The <em>printf</em> format to dump a tracking line.
     * 
     * @see #dumpText(PrintWriter)
     */
    private static final String DUMP_FORMAT = "%1$7d (%2$tF %2$tT) %3$s%n";
    
    private static final Logger log = LoggerFactory.getLogger(SlxRequestProgressTracker.class);

    /**
     * The name of the timer tracking the processing time of the complete process.
     */
    private static final String REQUEST_PROCESSING_TIMER = "Request Processing";

    /** Prefix for log messages */
    private static final String LOG_PREFIX = "LOG ";

    /** Prefix for comment messages */
    private static final String COMMENT_PREFIX = "COMMENT ";

    /** TIMER_END format explanation */
    private static final String TIMER_END_FORMAT = "{<elapsed msec>,<timer name>} <optional message>";

    /**
     * The system time at creation of this instance or the last {@link #reset()}.
     */
    private long processingStart;

    /**
     * The list of tracking entries.
     */
    private final List<TrackingEntry> entries = new ArrayList<TrackingEntry>();

    /**
     * Map of named timers indexed by timer name storing the system time of start of the respective timer.
     */
    private final Map<String, Long> namedTimerEntries = new HashMap<String, Long>();

    private boolean done;

    /**
     * Creates a new request progress tracker.
     */
    public SlxRequestProgressTracker()
    {
        reset();
    }

    /**
     * Resets this timer by removing all current entries and timers and adds an initial timer entry
     */
    public void reset() {
        done = false;

        // remove all entries
        entries.clear();
        namedTimerEntries.clear();

        // enter initial messages
        processingStart = startTimerInternal(REQUEST_PROCESSING_TIMER);
        entries.add(new TrackingEntry(COMMENT_PREFIX + "timer_end format is " + TIMER_END_FORMAT));
    }

    /**
     * @see org.apache.sling.api.request.RequestProgressTracker#getMessages()
     */
    @Override
    public Iterator<String> getMessages() {
        return new Iterator<String>() {

            private final Iterator<TrackingEntry> entryIter = entries.iterator();

            @Override
            public boolean hasNext() {
                return entryIter.hasNext();
            }

            @Override
            public String next() {
                // throws NoSuchElementException if no entries any more
                TrackingEntry entry = entryIter.next();

                long offset = entry.getTimeStamp() - processingStart;
                return String.format(DUMP_FORMAT, offset, entry.getTimeStamp(), entry.getMessage());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    /**
     * Dumps the process timer entries to the given writer, one entry per line. See the class comments for the rough
     * format of each message line.
     */
    @Override
    public void dump(final PrintWriter writer) {
        logTimer(REQUEST_PROCESSING_TIMER, "Dumping SlingRequestProgressTracker Entries");

        final StringBuilder sb = new StringBuilder();
        final Iterator<String> messages = getMessages();
        while (messages.hasNext()) {
            sb.append(messages.next());
        }
        writer.print(sb.toString());
    }

    /** Creates an entry with the given message. */
    @Override
    public void log(String message) {
        entries.add(new TrackingEntry(LOG_PREFIX + message));
    }

    /** Creates an entry with the given entry tag and message */
    @Override
    public void log(String format, Object... args) {
        String message = MessageFormat.format(format, args);
        entries.add(new TrackingEntry(LOG_PREFIX + message));
    }

    /**
     * Starts a named timer. If a timer of the same name already exists, it is reset to the current time.
     */
    @Override
    public void startTimer(String name) {
        startTimerInternal(name);
    }

    /**
     * Start the named timer and returns the start time in milliseconds. Logs a message with format
     * 
     * <pre>
     * TIMER_START{<name>} <optional message>
     * </pre>
     */
    private long startTimerInternal(String name) {
        long timer = System.currentTimeMillis();
        namedTimerEntries.put(name, timer);
        entries.add(new TrackingEntry(timer, "TIMER_START{" + name + "}"));
        return timer;
    }

    /**
     * Log a timer entry, including start, end and elapsed time.
     */
    @Override
    public void logTimer(String name) {
        if (namedTimerEntries.containsKey(name)) {
            logTimerInternal(name, null, namedTimerEntries.get(name));
        }
    }

    /**
     * Log a timer entry, including start, end and elapsed time.
     */
    @Override
    public void logTimer(String name, String format, Object... args) {
        if (namedTimerEntries.containsKey(name)) {
            logTimerInternal(name, MessageFormat.format(format, args), namedTimerEntries.get(name));
        }
    }

    /**
     * Log a timer entry, including start, end and elapsed time using TIMER_END_FORMAT
     */
    private void logTimerInternal(String name, String msg, long startTime) {
        final StringBuilder sb = new StringBuilder();
        sb.append("TIMER_END{");
        sb.append(System.currentTimeMillis() - startTime);
        sb.append(',');
        sb.append(name);
        sb.append('}');
        if (msg != null) {
            sb.append(' ');
            sb.append(msg);
        }
        entries.add(new TrackingEntry(sb.toString()));
    }

    @Override
    public void done() {
        if (done)
            return;
        logTimer(REQUEST_PROCESSING_TIMER, REQUEST_PROCESSING_TIMER);
        done = true;
    }

    /** Process tracker entry keeping timestamp, tag and message */
    private static class TrackingEntry
    {

        // creation time stamp
        private final long timeStamp;

        // tracking message
        private final String message;

        TrackingEntry(String message)
        {
            this.timeStamp = System.currentTimeMillis();
            this.message = message;
        }

        TrackingEntry(long timeStamp, String message)
        {
            this.timeStamp = timeStamp;
            this.message = message;
        }

        long getTimeStamp() {
            return timeStamp;
        }

        String getMessage() {
            return message;
        }
    }

}
