/*
 * Copyright 2014 The Solmix Project
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

import java.util.Dictionary;
import java.util.Map;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月3日
 */

public class BaseEvent implements IEvent
{

    private final String topic;

    private final EventArguments arguments;

    public BaseEvent(String topic, Map<String, ?> properties)
    {
    	EventUtil.validateTopicName(topic);
        this.topic = topic;
        // safely publish the event properties
        this.arguments = (properties instanceof EventArguments) ? (EventArguments) properties : new EventArguments(properties);
    }

    public BaseEvent(String topic, Dictionary<String, ?> properties)
    {
    	EventUtil.validateTopicName(topic);
        this.topic = topic;
        // safely publish the event properties
        this.arguments = new EventArguments(properties);
    }

   

    @Override
    public Map<String, ?> getProperties() {
        return arguments;
    }

    @Override
    public Object getProperty(String name) {
        if (EVENT_TOPIC.equals(name)) {
            return topic;
        }
        return arguments.get(name);
    }

    @Override
    public String getTopic() {
        return topic;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Topic is [").append(getTopic()).append("] values:");
        if (arguments != null) {
            for (String key : arguments.keySet()) {
                sb.append(key).append(" = ").append(arguments.get(key)).append(" ");
            }
        }
        return sb.toString();
    }
}
