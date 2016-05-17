/*
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
package org.solmix.exchange.interceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月22日
 */

public class SuspendedException extends RuntimeException
{
    private static final long serialVersionUID = -4389995627948442133L;


    public SuspendedException(Throwable cause) {
        super(cause);
    }
    
    public SuspendedException() {
    }
    
   
    /**
     * Returns a transporter-specific runtime exception
     * @return RuntimeException the transporter-specific runtime exception, 
     *         can be null for asynchronous transports
     */
    public RuntimeException getRuntimeException() {
        Throwable ex = getCause();
        return ex instanceof RuntimeException ? (RuntimeException)ex : null;
    }
}
