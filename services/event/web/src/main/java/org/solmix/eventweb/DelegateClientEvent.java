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

package org.solmix.eventweb;

import java.util.Map;

import org.solmix.event.IEvent;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-10-11
 */

public class DelegateClientEvent implements IEvent, java.io.Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 3989769924376087762L;

    public static final String TOPIC_PREFIX = "client";

    public static final String DEFAULT_TOPIC = TOPIC_PREFIX + "/default";

    public static final String BROADCASTER_ID = "X-BROADCASTER_ID";

    private final Map<String, Object> map;

    private final String topic;

    public DelegateClientEvent(Map<String, Object> map)
    {
        if (map.get("topic") == null) {
            topic = DEFAULT_TOPIC;
        } else {
            String client = map.get("topic").toString();
            if (!client.startsWith("/")) {
                topic = TOPIC_PREFIX + "/" + client;
            } else {
                topic = TOPIC_PREFIX + client;
            }

        }
        this.map = map;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.event.IEvent#getProperties()
     */
    @Override
    public Map<String, Object> getProperties() {
        return map;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.event.IEvent#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String name) {
        return map.get(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.event.IEvent#getTopic()
     */
    @Override
    public String getTopic() {
        return topic;
    }

    /**
     * @return the broadcasterID
     */
    public String getBroadcasterID() {
        return map.get(BROADCASTER_ID) == null ? null : map.get(BROADCASTER_ID).toString();
    }

    /**
     * @param broadcasterID the broadcasterID to set
     */
    public void setBroadcasterID(String broadcasterID) {
        map.put(BROADCASTER_ID, broadcasterID);
    }

}
