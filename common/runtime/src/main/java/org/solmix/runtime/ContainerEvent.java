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
package org.solmix.runtime;

import java.util.EventObject;

import org.solmix.runtime.event.Event;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public  class ContainerEvent extends EventObject implements Event
{

    private final int type;

    private final Container container;

    public final static int CREATED = 0x00000001;

    public final static int PRECLOSE = 0x00000002;

    public final static int POSTCLOSE = 0x00000003;

    /**
     * @param source
     */
    public ContainerEvent(int type,Object source,Container container)
    {
        super(source);
        this.type=type;
        this.container=container;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -6370021799067517907L;

    
    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    
    /**
     * @return the container
     */
    public Container getContainer() {
        return container;
    }

}
