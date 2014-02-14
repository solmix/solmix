/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.api.serialize;

import java.io.Reader;
import java.io.Writer;

import org.solmix.api.exception.SlxException;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-7 solmix-api
 */
public interface JSParser
{

    public enum JSType
    {
        ISC(0) , UN_SET(1);

        private final int value;

        JSType(int value)
        {
            this.value = value;
        }

        public int value() {
            return value;
        }

        public static JSType fromValue(int v) {
            for (JSType c : JSType.values()) {
                if (c.value == v) {
                    return c;
                }
            }
            throw new IllegalArgumentException("illegal dsresponse status");
        }
    }

    /**
     * @return
     */
    boolean isPrettyPrint();

    /**
     * @param prettyPrint
     */
    void setPrettyPrint(boolean prettyPrint);

    /**
     * 
     * @return
     */
    boolean isOmitNullValues();

    /**
     * @param omitNullValues
     */
    void setOmitNullValues(boolean omitNullValues);

    /**
     * @return
     */
    String getImplName();

    /**
     * @param out
     * @param obj
     * @throws SlxException
     */
    void toJavaScript(Writer out, Object obj) throws SlxException;

    /**
     * @param obj
     * @return
     * @throws SlxException
     */
    String toJavaScript(Object obj) throws SlxException;

    /**
     * Serialize java object to JSON text.
     * @param out
     * @param value
     */
    void toJSON(Writer out, Object value) throws SlxException;

    /**
     * Convert input  JavaScript string to java object. 
     * @param inputString
     * @param Type
     * @return
     * @throws SlxException
     */
    <T> T toJavaObject(String inputString, Class<T> Type) throws SlxException;
    /**
     * Convert input  JavaScript reader to java object. 
     * @param inputString
     * @param Type
     * @return
     * @throws SlxException
     */
    <T> T toJavaObject(Reader reader, Class<T> Type) throws SlxException;

}
