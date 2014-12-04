/**
 * Copyright (c) 2014 The Solmix Project
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
package org.solmix.runtime.exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.solmix.runtime.exchange.model.ArgumentInfo;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年12月4日
 */

public class MessageList extends ArrayList<Object> {
    public static final Object REMOVED_MARKER = new Object();
    private static final long serialVersionUID = -5780720048950696258L;

    public MessageList() {
        super(6);
    }
    public MessageList(Object ... values) {
        super(Arrays.asList(values));
    }
    public MessageList(List<?> values) {
        super(values);
    }
    
    public static MessageList getContentsList(Message msg) {
        @SuppressWarnings("unchecked")
        List<Object> o = (msg.getContent(List.class));
        if (o == null) {
            return null;
        }
        if (!(o instanceof MessageList)) {
            MessageList l2 = new MessageList(o);
            msg.setContent(List.class, l2);
            return l2;
        }
        return (MessageList)o;
    }
    
    @Override
    public Object set(int idx, Object value) {
        ensureSize(idx);
        return super.set(idx, value);
    }
    
    private void ensureSize(int idx) {
        while (idx >= size()) {
            add(REMOVED_MARKER);
        }
    }
    
    public Object put(ArgumentInfo key, Object value) {
        ensureSize(key.getIndex());
        return super.set(key.getIndex(), value);
    }
    
    public boolean hasValue(ArgumentInfo key) {
        if (key.getIndex() >= size()) {
            return false;
        }
        return super.get(key.getIndex()) != REMOVED_MARKER;
    }
    
    public Object get(ArgumentInfo key) {
        Object o = super.get(key.getIndex());
        return o == REMOVED_MARKER ? null : o;
    }
    public void remove(ArgumentInfo key) {
        put(key, REMOVED_MARKER);
    }
}
