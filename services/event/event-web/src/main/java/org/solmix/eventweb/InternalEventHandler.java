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
package org.solmix.eventweb;

import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.DefaultBroadcasterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.event.AbstractEventHandler;
import org.solmix.api.event.EventTopic;
import org.solmix.api.event.IEvent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-17
 */
@EventTopic("org/solmix/*" )
public class InternalEventHandler extends AbstractEventHandler
{
    static final Logger logger = LoggerFactory.getLogger(WebEventHandler.class);
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.AbstractEventHandler#handleIEvent(org.solmix.api.event.IEvent)
     */
    @Override
    protected void handleIEvent(IEvent event) {
        // TODO Auto-generated method stub
       
          BroadcasterFactory factory = DefaultBroadcasterFactory.getDefault();
        Broadcaster b=null;
          b=  factory.lookup("/data/event/*");
        if(b==null){
            logger.info("Can't find broadcaster");
        }else{
            Map<String,Object> res= new HashMap<String,Object>();
            res.put("topic", event.getTopic());
            res.put("data", event.getProperties());
                b.broadcast(res);
        }
         

    }

}
