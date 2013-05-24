/*
 * ========THE SOLMIX PROJECT=====================================
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
package org.solmix.web.client.im.events;


import org.solmix.web.shared.comet.IMEvent;

import com.google.gwt.event.shared.GwtEvent;


/**
 * 
 * @author solmix
 * @version $Id$  2011-11-18
 */

public class IMEventArrivedEvent extends GwtEvent<IMEventArrivedHandler> 
{
    private boolean cancel = false;
    private IMEvent imEvent;
    /**
     * Handler type.
     */
    private static Type<IMEventArrivedHandler> TYPE;
    /**
     * @param jsObj
     */
    public IMEventArrivedEvent(IMEvent event)
    {
       this.imEvent=event;
    }
 
    /**
     * @return the imEvent
     */
    public IMEvent getImEvent() {
        return imEvent;
    }

    /**
     * Gets the type associated with this event.
     *
     * @return returns the handler type
     */
    public static Type<IMEventArrivedHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<IMEventArrivedHandler>();
        }
        return TYPE;
    }
    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public Type<IMEventArrivedHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void dispatch(IMEventArrivedHandler handler) {
        handler.onIMEventArrived(this);
        
    }

    /**
     * Call this method to suppress the standard button click event
     */
    public void cancel() {
        cancel = true;
    }
    /**
     * @return true if cancelled
     */
    public boolean isCancelled() {
        return cancel;
    }
}
