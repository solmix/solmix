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

package com.solmix.eventservice.ext;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author solomon
 * @version 110035 2011-9-27
 */

public class LogTracker extends ServiceTracker<LogService, LogService> implements LogService
{

    /** LogService interface class name */
    protected final static String clazz = "org.osgi.service.log.LogService"; //$NON-NLS-1$

    /** PrintStream to use if LogService is unavailable */
    private final PrintStream out;

    /**
     * @param context
     * @param out
     */
    public LogTracker(BundleContext context, PrintStream out)
    {
        super(context, LogService.class, null);
        this.out = out;
    }

    public void log(int level, String message) {
        log(null, level, message, null);
    }

    public void log(int level, String message, Throwable exception) {
        log(null, level, message, exception);
    }

    public void log(ServiceReference reference, int level, String message) {
        log(reference, level, message, null);
    }

    public synchronized void log(ServiceReference reference, int level, String message, Throwable exception) {
        ServiceReference[] references = getServiceReferences();

        if (references != null) {
            int size = references.length;

            for (int i = 0; i < size; i++) {
                LogService service = (LogService) getService(references[i]);
                if (service != null) {
                    try {
                        service.log(reference, level, message, exception);
                    } catch (Exception e) {
                        // TODO: consider printing to System Error
                    }
                }
            }

            return;
        }

        noLogService(level, message, exception, reference);
    }

    /**
     * The LogService is not available so we write the message to a PrintStream.
     * 
     * @param level Logging level
     * @param message Log message.
     * @param throwable Log exception or null if none.
     * @param reference ServiceReference associated with message or null if none.
     */
    protected void noLogService(int level, String message, Throwable throwable, ServiceReference reference) {
        if (out != null) {
            synchronized (out) {
                // Bug #113286. If no log service present and messages are being
                // printed to stdout, prepend message with a timestamp.
                String timestamp = getDate(new Date());
                out.print(timestamp + " "); //$NON-NLS-1$

                switch (level) {
                    case LOG_DEBUG: {
                        out.print("Log Debug");

                        break;
                    }
                    case LOG_INFO: {
                        out.print("Log Info");

                        break;
                    }
                    case LOG_WARNING: {
                        out.print("Log Warning");

                        break;
                    }
                    case LOG_ERROR: {
                        out.print("Log Error");

                        break;
                    }
                    default: {
                        out.print("["); //$NON-NLS-1$
                        out.print("Log Unknown Log Level");
                        out.print("]: "); //$NON-NLS-1$

                        break;
                    }
                }

                out.println(message);

                if (reference != null) {
                    out.println(reference);
                }

                if (throwable != null) {
                    throwable.printStackTrace(out);
                }
            }
        }
    }

    // from EclipseLog to avoid using DateFormat -- see bug 149892#c10
    private String getDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        StringBuffer sb = new StringBuffer();
        appendPaddedInt(c.get(Calendar.YEAR), 4, sb).append('-');
        appendPaddedInt(c.get(Calendar.MONTH) + 1, 2, sb).append('-');
        appendPaddedInt(c.get(Calendar.DAY_OF_MONTH), 2, sb).append(' ');
        appendPaddedInt(c.get(Calendar.HOUR_OF_DAY), 2, sb).append(':');
        appendPaddedInt(c.get(Calendar.MINUTE), 2, sb).append(':');
        appendPaddedInt(c.get(Calendar.SECOND), 2, sb).append('.');
        appendPaddedInt(c.get(Calendar.MILLISECOND), 3, sb);
        return sb.toString();
    }

    private StringBuffer appendPaddedInt(int value, int pad, StringBuffer buffer) {
        pad = pad - 1;
        if (pad == 0)
            return buffer.append(Integer.toString(value));
        int padding = (int) Math.pow(10, pad);
        if (value >= padding)
            return buffer.append(Integer.toString(value));
        while (padding > value && padding > 1) {
            buffer.append('0');
            padding = padding / 10;
        }
        buffer.append(value);
        return buffer;
    }

}
