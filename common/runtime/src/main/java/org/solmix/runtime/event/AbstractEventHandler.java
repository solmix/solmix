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
package org.solmix.runtime.event;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-14
 */

public abstract class AbstractEventHandler implements EventHandler
{

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventHandler#handleEvent(org.osgi.service.event.Event)
     */
    @Override
    public void handleEvent(Event event) {
      if(event instanceof IEvent){
          handleIEvent((IEvent)event);
      }else if(event!=null){
          handleIEvent(toIEvent(event));
      }else{
          handleIEvent(null);
      }

    }
    protected IEvent toIEvent(Event event){
        
        return new DelegateEvent(event);
    }

    /**
     * @param event
     */
   protected abstract  void handleIEvent(IEvent event) ;

}
