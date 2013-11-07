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
package org.solmix.sgt.client.event;

import org.solmix.atmosphere.client.ClientSerializer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.SerializationException;
import com.smartgwt.client.core.JsObject;
import com.smartgwt.client.util.JSON;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-18
 */

public class EventSerializer implements ClientSerializer
{

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.atmosphere.client.ClientSerializer#deserialize(java.lang.String)
     */
    @Override
    public Object deserialize(String message) throws SerializationException {
        return new ServerEvent(JSON.decode(message));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.atmosphere.client.ClientSerializer#serialize(java.lang.Object)
     */
    @Override
    public String serialize(Object message) throws SerializationException {
        if(message instanceof JsObject){
            return JSON.encode(((JsObject)message).getJsObj());
        }
       if(message instanceof JavaScriptObject){
           return JSON.encode((JavaScriptObject)message);
       }else{
           throw new SerializationException("The serialized object must be JavaScriptObject ,but this object is:"+message.getClass().getName());
       }
    }

}
