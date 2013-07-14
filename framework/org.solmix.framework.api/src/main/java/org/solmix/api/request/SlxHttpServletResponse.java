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

package org.solmix.api.request;

import javax.servlet.http.HttpServletResponse;

import org.solmix.api.adapter.Adaptable;

/**
 * 
 * <code>SlxHttpServletResponse</code> inherate from {@link javax.servlet.http.HttpServletResponse HttpServletResponse}
 * used to extends HttpServletResponse function.
 * 
 * @version 0.0.4
 * @since 0.0.4
 */

public interface SlxHttpServletResponse extends HttpServletResponse, Adaptable
{

}
