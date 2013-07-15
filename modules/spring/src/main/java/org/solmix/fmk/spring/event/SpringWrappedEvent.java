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

package org.solmix.fmk.spring.event;

import org.springframework.context.ApplicationEvent;

import org.solmix.api.event.IEvent;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */

public class SpringWrappedEvent extends ApplicationEvent
{

    /**
     * @param source
     */
    public SpringWrappedEvent(IEvent source)
    {
        super(source);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -6454686351120721685L;

}