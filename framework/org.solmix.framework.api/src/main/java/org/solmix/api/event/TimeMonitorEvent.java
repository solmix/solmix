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

package org.solmix.api.event;

import java.util.Map;

import org.osgi.service.event.Event;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-10-4
 */

public class TimeMonitorEvent extends Event implements ITimeMonitorEvent, java.io.Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Map<String, Object> map;

    /**
     * @param topic
     * @param properties
     */
    public TimeMonitorEvent(Map<String, Object> map)
    {
        super(TIME_MONITOER_TOPIC, map);
        this.map = map;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.IEvent#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        return map;
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Topic is [").append(getTopic()).append("] values\n");
        if(map!=null){
            for(String key:map.keySet()){
                sb.append(key).append(" = ").append(map.get(key));
            }
        }
        return sb.toString();
        
    }

}
