/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.api.call;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月24日
 */

public interface InterceptorOrder
{
    enum PRIORITY {
        /**
         * The Interceptor must be executed before the default set of Interceptor
         */
        AFTER_DEFAULT,
        /**
         * The Interceptor must be executed after the default set of Interceptor
         */
        BEFORE_DEFAULT,
        /**
         * The Interceptor must be executed at first, before any Interceptor.
         */
        FIRST_BEFORE_DEFAULT
    }

    static PRIORITY AFTER_DEFAULT = PRIORITY.AFTER_DEFAULT;
    static PRIORITY BEFORE_DEFAULT = PRIORITY.BEFORE_DEFAULT;
    static PRIORITY FIRST_BEFORE_DEFAULT = PRIORITY.FIRST_BEFORE_DEFAULT;

    /**
     *
     * @return PRIORITY
     */
    PRIORITY priority();
}
