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
package org.solmix.sgt.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-18
 */

public class GWTServerEvent extends GwtEvent<GWTServerEventHandler>
{
    private static Type<GWTServerEventHandler> TYPE;
    
    private ServerEvent serverEvent;
    
    public GWTServerEvent(ServerEvent serverEvent){
        this.serverEvent=serverEvent;
    }
    
    public static Type<GWTServerEventHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<GWTServerEventHandler>();
        }
        return TYPE;
    }

    
    /**
     * @return the serverEvent
     */
    public ServerEvent getServerEvent() {
        return serverEvent;
    }

    
    /**
     * @param serverEvent the serverEvent to set
     */
    public void setServerEvent(ServerEvent serverEvent) {
        this.serverEvent = serverEvent;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GWTServerEventHandler> getAssociatedType() {
        return getType();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(GWTServerEventHandler handler) {
       handler.onHandle(this);
        
    }

}
