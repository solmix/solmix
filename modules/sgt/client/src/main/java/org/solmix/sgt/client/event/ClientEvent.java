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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.core.JsObject;
import com.smartgwt.client.util.JSOHelper;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-10-18
 */

public class ClientEvent extends JsObject implements Serializable
{

    private static final long serialVersionUID = -1678651569931690467L;

    public ClientEvent(JavaScriptObject obj)
    {
        super(obj);
    }

    public ClientEvent()
    {
        super(JSOHelper.createObject());
    }

    public ClientEvent(String topic)
    {
        this();
        JSOHelper.setAttribute(jsObj, "topic", topic);
    }

    public String getTopic() {
        return JSOHelper.getAttribute(jsObj, "topic");
    }

    public void setTopic(String topic) {
        JSOHelper.setAttribute(jsObj, "topic", topic);
    }

    public void setProperties(Map<Object, Object> map) {
        JSOHelper.setAttribute(jsObj, "data", map);

    }

    @SuppressWarnings({ "rawtypes" })
    public Map getProperties() {
        return JSOHelper.getAttributeAsMap(jsObj, "data");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addAttribute(String key, Object value) {
        Map data = JSOHelper.getAttributeAsMap(jsObj, "data");
        if (data == null) {
            data = new HashMap();
        }
        data.put(key, value);
        setProperties(data);
    }

    public ClientEvent withAttribute(String key, Object value) {
        addAttribute(key, value);
        return this;

    }
}
