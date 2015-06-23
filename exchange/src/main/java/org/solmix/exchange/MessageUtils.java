/**
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

package org.solmix.exchange;

import static org.solmix.commons.util.DataUtils.asBoolean;

import org.solmix.commons.util.DataUtils;

/**
 * 消息工具类
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月13日
 */

public final class MessageUtils {

    private MessageUtils() {

    }

    public static boolean isInbount(Message message) {
        Boolean request = (Boolean) message.get(Message.INBOUND_MESSAGE);
        return request != null && request.booleanValue();
    }

    public static boolean isRequest(Message message) {
        Boolean request = (Boolean) message.get(Message.REQUEST_MESSAGE);
        return request != null && request.booleanValue();
    }
    public static boolean isPartialResponse(Message message) {
        return Boolean.TRUE.equals(message.get(Message.PARTIAL_RESPONSE_MESSAGE));
    }
    public static boolean getBoolean(Message message, String key, boolean df) {
        if (message == null) {
            return df;
        }
        Object v = message.get(key);
        if (v == null) {
            return df;
        }
        return asBoolean(v);
    }

    public static boolean getBoolean(Message message, String key) {
        return getBoolean(message, key, false);
    }

    public static Long getLong(Message message, String key) {
        if (message == null) {
            return null;
        }
        return DataUtils.getLong(message, key);
    }


    public static boolean isTrue(Object property) {
        if (property == null) {
            return false;
        }
        if (Boolean.TRUE.equals(property)
            || "true".equalsIgnoreCase(property.toString())) {
            return true;
        }
        return false;
    }

    public static boolean isEmptyPartialResponse(Message message) {
        return Boolean.TRUE.equals(message.get(Message.EMPTY_PARTIAL_RESPONSE_MESSAGE));
    }

    public static byte getByte(Message outMsg, String key) {
        Object v = outMsg.get(key);
        if (v instanceof Byte) {
            return ((Byte) v).byteValue();
        } else if (v != null) {
            return Byte.valueOf(v.toString()).byteValue();
        } else {
            return -1;
        }
    }

   
    public static String getString(Message msg, String key) {
        Object v = msg.get(key);
        if (v instanceof String) {
            return (String)v;
        } else if (v != null) {
            return v.toString();
        } else {
            return null;
        }
    }
}
