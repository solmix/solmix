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
package org.solmix.service.event.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.solmix.runtime.event.IEventHandler;
import org.solmix.service.event.EventHandlerBlackList;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public class EventHandlerBlackListImpl implements EventHandlerBlackList
{

    private final Set<IEventHandler> blankList = Collections.synchronizedSet(new HashSet<IEventHandler>() {

        /**
         * 
         */
        private static final long serialVersionUID = 7675960799383204788L;

        @Override
        public boolean contains(final Object object) {
            for (Iterator<IEventHandler> iter = super.iterator(); iter.hasNext();) {
                final IEventHandler handler =  iter.next();
                if (null == handler) {
                    iter.remove();
                }
            }

            return super.contains(object);
        }
    });
    
    private final Map<IEventHandler,Long> times= Collections.synchronizedMap(new HashMap<IEventHandler,Long>());
    
	private long timeout;
    
    public EventHandlerBlackListImpl(long timeout){
    	this.timeout=timeout;
    }
 
    @Override
    public void add(IEventHandler handler) {
        blankList.add(handler);
        times.put(handler, System.currentTimeMillis());
    }

  
    @Override
    public boolean contains(IEventHandler handler) {
        boolean blocked=  blankList.contains(handler);
        if(blocked){
        	long mark= times.get(handler);
        	long current =System.currentTimeMillis();
        	//过一定时间后重新开启
        	if((current-mark)>(3*timeout)){
        		blankList.remove(handler);
        		times.remove(handler);
        		return false;
        	}
        	
        }
        return blocked;
        
    }

  
   
}
