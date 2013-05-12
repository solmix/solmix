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

import javax.servlet.http.HttpServletRequest;

import com.solmix.api.adapter.Adaptable;

/**
 * 
 * <code>SlxHttpServletRequest</code> inherate from {@link javax.servlet.http.HttpServletRequest HttpServletRequest}
 * used to extends HttpServeltRequest function.
 * 
 * @version 0.0.4
 * @since 0.0.4
 */

public interface SlxHttpServletRequest extends HttpServletRequest, Adaptable
{

    /**
     * Returns the {@link RequestProgressTracker} of this request.
     */
    RequestProgressTracker getRequestProgressTracker();
}
