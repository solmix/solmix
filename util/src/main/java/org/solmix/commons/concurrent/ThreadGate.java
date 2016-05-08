/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.concurrent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年5月8日
 */

public class ThreadGate
{
    private boolean open = false;
    private Object msg = null;
    private boolean initialized = false;

    /**
     * Open the gate and release any waiting threads.
    **/
    public synchronized void open()
    {
        open = true;
        notifyAll();
    }

    /**
     * Returns the message object associated with the gate; the
     * message is just an arbitrary object used to pass information
     * to the waiting threads.
     * @return the message object associated with the gate.
    **/
    public synchronized Object getMessage()
    {
        return msg;
    }

    /**
     * Sets the message object associated with the gate. The message
     * object can only be set once, subsequent calls to this method
     * are ignored.
     * @param msg the message object to associate with this gate.
    **/
    public synchronized void setMessage(Object msg)
    {
        if (!initialized)
        {
            this.msg = msg;
            initialized = true;
        }
    }

    /**
     * Wait for the gate to open.
     * @return <tt>true</tt> if the gate was opened or <tt>false</tt> if the timeout expired.
     * @throws java.lang.InterruptedException If the calling thread is interrupted;
     *         the gate still remains closed until opened.
    **/
    public synchronized boolean await(long timeout) throws InterruptedException
    {
        long start = System.currentTimeMillis();
        long remaining = timeout;
        while (!open)
        {
            wait(remaining);
            if (timeout > 0)
            {
                remaining = timeout - (System.currentTimeMillis() - start);
                if (remaining <= 0)
                {
                    break;
                }
            }
        }
        return open;
    }
}
