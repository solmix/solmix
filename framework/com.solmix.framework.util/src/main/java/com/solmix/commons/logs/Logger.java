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

package com.solmix.commons.logs;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.slf4j.LoggerFactory;

/**
 * Wrapper Logger
 * 
 * @version 1.0
 */
public class Logger
{

    public static final Logger auth;

    public static final Logger timing;

    public static final Logger validation;

    public static final Logger download;

    public static final Logger global;

    static {
        auth = new Logger("com.solmix.auth.Auth");
        timing = new Logger("com.solmix.timing.Timing");
        validation = new Logger("com.solmix.validation.Validation");
        download = new Logger("com.solmix.download.Download");
        global = new Logger("GLOBAL");
    }

    public static Throwable getRealTargetException(Throwable ite) {
        if (ite instanceof InvocationTargetException)
            return getRealTargetException(((InvocationTargetException) ite).getTargetException());
        else
            return ite;
    }

    public static void ignoreThread() {
        if (ignoreThreads == null)
            ignoreThreads = new HashSet<Thread>();
        if (!threadIgnored())
            ignoreThreads.add(Thread.currentThread());
    }

    public static void observeThread() {
        if (ignoreThreads != null)
            ignoreThreads.remove(Thread.currentThread());
    }

    public static boolean threadIgnored() {
        return ignoreThreads != null && ignoreThreads.contains(Thread.currentThread());
    }

    private static String prependContext(Object message) {
        Stack<String> s = getStackForCurrentThread();
        if (s.empty())
            return message.toString();
        else
            return (new StringBuilder()).append(s.toString()).append(" ").append(message.toString()).toString();
    }

    private String prefixMessage(String message) {
        if (message != null) {
            message = prependContext(message);
            if (prefixWithPackage)
                message = (new StringBuilder()).append(packagePrefix).append(message.toString()).toString();
            if (messagePrefix != null)
                message = (new StringBuilder()).append(messagePrefix).append(message.toString()).toString();
        } else {
            message = "null";
        }
        return message.toString();
    }

    public Logger(Object obj)
    {
        this(obj.getClass().getName());
    }

    /**
     * Instance the logger by class name.
     * 
     * @param subsystem
     */
    public Logger(String subsystem)
    {
        this(subsystem, null);
    }

    public Logger(String subsystem, String messagePrefix)
    {
        int start = 0;
        int end = 0;
        if (subsystem == null)
            subsystem = DEFAULT_SUBSYSTEM;
        if (subsystem.indexOf(".") != -1) {
            end = subsystem.lastIndexOf(".");
            start = subsystem.lastIndexOf(".", end - 1);
        }
        if (start > 0 && end > start)
            packageName = subsystem.substring(start + 1, end).toUpperCase();
        else
            packageName = subsystem;
        packagePrefix = (new StringBuilder()).append(packageName).append(" - ").toString();
        nativeLog = LoggerFactory.getLogger(subsystem);
        if (messagePrefix != null)
            this.messagePrefix = (new StringBuilder()).append(messagePrefix).append(" - ").toString();
    }

    public Logger()
    {
        this(DEFAULT_SUBSYSTEM);
    }

    public void error(String message) {
        if (isErrorEnabled() && !threadIgnored())
            nativeLog.error(prefixMessage(message));
    }

    public void error(String message, Throwable t) {
        if (isErrorEnabled() && !threadIgnored())
            nativeLog.error(prefixMessage(message), getRealTargetException(t));
    }

    public void error(String message, Object o) {
        if (isErrorEnabled() && !threadIgnored()) {
            String ostring = "null";
            if (o != null)
                ostring = o.toString();
            error((new StringBuilder()).append(message.toString()).append(": ").append(ostring).toString());
        }
    }

    public void warning(String message) {
        if (isWarnEnabled() && !threadIgnored())
            nativeLog.warn(prefixMessage(message));
    }

    public void warning(String message, Throwable t) {
        if (isWarnEnabled() && !threadIgnored())
            nativeLog.warn(prefixMessage(message), getRealTargetException(t));
    }

    public void warning(String message, Object o) {
        if (isWarnEnabled() && !threadIgnored()) {
            String ostring = "null";
            if (o != null)
                ostring = o.toString();
            warning((new StringBuilder()).append(message.toString()).append(": ").append(ostring).toString());
        }
    }

    /**
     * When {@link com.solmix.fmk.logs.Logger#isWarnEnabled() isWarnEnabled()} = ture and
     * {@link com.solmix.fmk.logs.Logger#threadIgnored() threadIgnored()} =false print the input message
     * 
     * @param message Warning message
     */
    public void warn(String message) {
        if (isWarnEnabled() && !threadIgnored())
            nativeLog.warn(prefixMessage(message));
    }

    public void warn(String message, Throwable t) {
        if (isWarnEnabled() && !threadIgnored())
            nativeLog.warn(prefixMessage(message), getRealTargetException(t));
    }

    public void warn(String message, Object o) {
        if (isWarnEnabled() && !threadIgnored())
            warning(message, o);
    }

    public void info(String message, Throwable t) {
        if (isInfoEnabled() && !threadIgnored())
            nativeLog.info(prefixMessage(message), getRealTargetException(t));
    }

    public void info(String message) {
        if (isInfoEnabled() && !threadIgnored())
            nativeLog.info(prefixMessage(message));
    }

    public void info(String message, Object o) {
        if (isInfoEnabled() && !threadIgnored()) {
            String ostring = "null";
            if (o != null)
                ostring = o.toString();
            nativeLog.info((new StringBuilder()).append(prefixMessage(message.toString())).append(": ").append(ostring).toString());
        }
    }

    public void debug(String message) {
        if (isDebugEnabled() && !threadIgnored())
            nativeLog.debug(prefixMessage(message));
    }

    public void debug(String message, Throwable t) {
        if (isDebugEnabled() && !threadIgnored())
            nativeLog.debug(prefixMessage(message), getRealTargetException(t));

    }

    public void debug(String message, Object o) {
        if (isDebugEnabled() && !threadIgnored()) {
            String ostring = "null";
            if (o != null)
                ostring = o.toString();
            nativeLog.debug((new StringBuilder()).append(prefixMessage(message.toString())).append(": ").append(ostring).toString());
        }
    }

    public void trace(String message) {
        if (isTraceEnabled() && !threadIgnored())
            nativeLog.trace(prefixMessage(message));
    }

    public void trace(String message, Throwable o) {
        if (isTraceEnabled() && !threadIgnored()) {
            nativeLog.trace(prefixMessage(message), getRealTargetException(o));
        }
    }

    public void trace(String message, Object o) {
        if (isTraceEnabled() && !threadIgnored()) {
            String ostring = "null";
            if (o != null)
                ostring = o.toString();
            nativeLog.trace((new StringBuilder()).append(prefixMessage(message.toString())).append(": ").append(ostring).toString());
        }
    }

    /**
     * Determines whether show <b>debug</b> message or not
     * 
     * @return
     */
    public boolean isDebugEnabled() {
        return nativeLog.isDebugEnabled();
    }

    public boolean isTraceEnabled() {
        return nativeLog.isTraceEnabled();
    }

    /**
     * Determines whether show info message or not
     * 
     * @return
     */
    public boolean isInfoEnabled() {
        return nativeLog.isInfoEnabled();
    }

    /**
     * Determines whether show warning message or not
     * 
     * @return
     */
    public boolean isWarnEnabled() {
        return nativeLog.isWarnEnabled();
    }

    /**
     * Determines whether show error message or not
     * 
     * @return
     */
    public boolean isErrorEnabled() {
        return nativeLog.isErrorEnabled();
    }

    private static Stack<String> getStackForCurrentThread() {
        Stack<String> s = threadContext.get(Thread.currentThread().getName());
        if (s == null) {
            s = new Stack<String>();
            threadContext.put(Thread.currentThread().getName(), s);
        }
        return s;
    }

    public static void pushContext(String message) {
        Stack<String> s = getStackForCurrentThread();
        s.push(message);
    }

    public static void popContext() {
        Stack<String> s = getStackForCurrentThread();
        try {
            s.pop();
        } catch (Exception ignored) {
        }
    }

    public static void removeContext() {
        threadContext.remove(Thread.currentThread().getName());
    }

    protected static boolean logEnabled = true;

    private static boolean prefixWithPackage = false;

    private static Map<String, Stack<String>> threadContext = Collections.synchronizedMap(new HashMap<String, Stack<String>>());

    public static Set<Thread> ignoreThreads;

    private static final String DEFAULT_SUBSYSTEM = "Undefined Subsystem";

    private final org.slf4j.Logger nativeLog;

    private String packageName;

    private final String packagePrefix;

    private String messagePrefix;

}