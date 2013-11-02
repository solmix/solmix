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

package org.solmix.eventservice.deliver;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.solmix.eventservice.EventDeliver;
import org.solmix.eventservice.EventTask;
import org.solmix.eventservice.util.EventThreadPool;
import org.solmix.eventservice.util.SyncEventThread;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-1
 */

public class SyncDeliver implements EventDeliver
{

    private final EventThreadPool threadPool;

    private int timeout;

    /** The matchers for ignore timeout handling. */
    private Matcher[] ignoreTimeoutMatcher;

    public SyncDeliver(final EventThreadPool threadPool, int timeout, String[] ignoreTimeout)
    {
        this.threadPool = threadPool;
        update(timeout, ignoreTimeout);
    }

    public void update(final int timeout, final String[] ignoreTimeout) {
        this.timeout = timeout;
        if (ignoreTimeout == null || ignoreTimeout.length == 0) {
            ignoreTimeoutMatcher = null;
        } else {
            Matcher[] ignoreTimeoutMatcher = new Matcher[ignoreTimeout.length];
            for (int i = 0; i < ignoreTimeout.length; i++) {
                String value = ignoreTimeout[i];
                if (value != null) {
                    value = value.trim();
                }
                if (value != null && value.length() > 0) {
                    if (value.endsWith(".")) {
                        ignoreTimeoutMatcher[i] = new PackageMatcher(value.substring(0, value.length() - 1));
                    } else if (value.endsWith("*")) {
                        ignoreTimeoutMatcher[i] = new SubPackageMatcher(value.substring(0, value.length() - 1));
                    } else {
                        ignoreTimeoutMatcher[i] = new ClassMatcher(value);
                    }
                }
            }
            this.ignoreTimeoutMatcher = ignoreTimeoutMatcher;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.eventservice.EventDeliver#execute(java.util.List)
     */
    @Override
    public void execute(List<EventTask> tasks) {

        final Thread sleepingThread = Thread.currentThread();
        final SyncEventThread syncThread = sleepingThread instanceof SyncEventThread ? (SyncEventThread) sleepingThread
            : null;
        final Iterator<EventTask> i = tasks.iterator();
        while (i.hasNext()) {
            final EventTask task = i.next();

            if (!useTimeout(task)) {
                // no timeout, we can directly execute
                task.execute();
            } else if (syncThread != null) {
                // if this is a cascaded event, we directly use this thread
                // otherwise we could end up in a starvation
                final long startTime = System.currentTimeMillis();
                task.execute();
                if (System.currentTimeMillis() - startTime > timeout) {
                    task.blackListHandler();
                }
            } else {
                final Rendezvous startBarrier = new Rendezvous();
                final Rendezvous timerBarrier = new Rendezvous();
                threadPool.executeTask(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            // notify the outer thread to start the timer
                            startBarrier.waitForRendezvous();
                            // execute the task
                            task.execute();
                            // stop the timer
                            timerBarrier.waitForRendezvous();
                        } catch (final IllegalStateException ise) {
                            // this can happen on shutdown, so we ignore it
                        }
                    }
                });
                // we wait for the inner thread to start
                startBarrier.waitForRendezvous();

                // timeout handling
                // we sleep for the sleep time
                // if someone wakes us up it's the finished inner task
                try {
                    timerBarrier.waitAttemptForRendezvous(timeout);
                } catch (final TimeoutException ie) {
                    // if we timed out, we have to blacklist the handler
                    task.blackListHandler();
                }

            }
        }

    }

  
    private boolean useTimeout(EventTask task) {
        // we only check the classname if a timeout is configured
        if (timeout > 0) {
            final Matcher[] ignoreTimeoutMatcher = this.ignoreTimeoutMatcher;
            if (ignoreTimeoutMatcher != null) {
                final String className = task.getHandlerClassName();
                for (int i = 0; i < ignoreTimeoutMatcher.length; i++) {
                    if (ignoreTimeoutMatcher[i] != null) {
                        if (ignoreTimeoutMatcher[i].match(className)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * The matcher interface for checking if timeout handling is disabled for the handler. Matching is based on the
     * class name of the event handler.
     */
    private static interface Matcher
    {

        boolean match(String className);
    }

    /** Match a package. */
    private static final class PackageMatcher implements Matcher
    {

        private final String packageName;

        public PackageMatcher(final String name)
        {
            packageName = name;
        }

        @Override
        public boolean match(String className) {
            final int pos = className.lastIndexOf('.');
            return pos > -1 && className.substring(0, pos).equals(packageName);
        }
    }

    /** Match a package or sub package. */
    private static final class SubPackageMatcher implements Matcher
    {

        private final String packageName;

        public SubPackageMatcher(final String name)
        {
            packageName = name + '.';
        }

        @Override
        public boolean match(String className) {
            final int pos = className.lastIndexOf('.');
            return pos > -1 && className.substring(0, pos + 1).startsWith(packageName);
        }
    }

    /** Match a class name. */
    private static final class ClassMatcher implements Matcher
    {

        private final String className;

        public ClassMatcher(final String name)
        {
            className = name;
        }

        @Override
        public boolean match(String className) {
            return className.equals(this.className);
        }
    }

}
