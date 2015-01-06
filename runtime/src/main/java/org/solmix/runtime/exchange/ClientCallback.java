/**
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

package org.solmix.runtime.exchange;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class ClientCallback implements Future<Object[]> {

    protected Map<String, Object> context;

    protected Object[] result;

    protected Throwable exception;

    protected volatile boolean done;

    protected boolean cancelled;

    protected boolean started;

    public ClientCallback() {
    }

    /**
     * Called when a message is first received prior to any actions being
     * applied to the message. The InterceptorChain is setup so modifications to
     * that can be done.
     */
    public void start(Message msg) {
        started = true;
    }

    /**
     * If the processing of the incoming message proceeds normally, this method
     * is called with the response context values and the resulting objects.
     * 
     * The default behavior just stores the objects and calls notifyAll to wake
     * up threads waiting for the response.
     * 
     * @param ctx
     * @param res
     */
    public void handleResponse(Map<String, Object> ctx, Object[] res) {
        context = ctx;
        result = res;
        done = true;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * If processing of the incoming message results in an exception, this
     * method is called with the resulting exception.
     * 
     * The default behavior just stores the objects and calls notifyAll to wake
     * up threads waiting for the response.
     * 
     * @param ctx
     * @param ex
     */
    public void handleException(Map<String, Object> ctx, Throwable ex) {
        context = ctx;
        exception = ex;
        done = true;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!started) {
            cancelled = true;
            synchronized (this) {
                notifyAll();
            }
            return true;
        }
        return false;
    }

    /**
     * return the map of items returned from an operation.
     * 
     * @return the response context
     * @throws InterruptedException if the operation was cancelled.
     * @throws ExecutionException if the operation resulted in a fault.
     */
    public Map<String, Object> getResponseContext()
        throws InterruptedException, ExecutionException {
        synchronized (this) {
            if (!done) {
                wait();
            }
        }
        if (cancelled) {
            throw new InterruptedException("Operation Cancelled");
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] get() throws InterruptedException, ExecutionException {
        synchronized (this) {
            if (!done) {
                wait();
            }
        }
        if (cancelled) {
            throw new InterruptedException("Operation Cancelled");
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this) {
            if (!done) {
                unit.timedWait(this, timeout);
            }
        }
        if (cancelled) {
            throw new InterruptedException("Operation Cancelled");
        }
        if (!done) {
            throw new TimeoutException("Timeout Exceeded");
        }
        if (exception != null) {
            throw new ExecutionException(exception);
        }
        return result;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    /*
     * If the operation completes with a fault, the resulting exception object
     * ends up here.
     */
    public Throwable getException() {
        return exception;
    }
}
