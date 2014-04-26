/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.commons.util;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年4月4日
 */

public class Assert
{

    /**
     * @param namespace
     * @param string
     */
    public static void isNotNull(Object o, String msg) {
        if (o == null)
            throw new java.lang.IllegalArgumentException(msg);
    }

    /**
     * @param b
     * @param string
     */
    public static void isTrue(boolean b, String msg) {
        if(!b)
            throw new java.lang.IllegalArgumentException(msg);
        
    }

    /**
     * @param services
     */
    public static void isNotNull(Object o) {
        if (o == null)
            throw new java.lang.IllegalArgumentException("Object must be not null");
        
    }

    /**
     * @param b
     */
    public static void isTrue(boolean b) {
        if(!b)
            throw new java.lang.IllegalArgumentException(" Must be true");
    }

}
