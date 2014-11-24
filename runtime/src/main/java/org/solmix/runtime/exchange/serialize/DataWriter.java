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
package org.solmix.runtime.exchange.serialize;

import java.io.IOException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月24日
 */

public interface DataWriter {

    /**
     * Write boolean.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeBool(boolean v) throws IOException;

    /**
     * Write byte.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeByte(byte v) throws IOException;

    /**
     * Write short.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeShort(short v) throws IOException;

    /**
     * Write integer.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeInt(int v) throws IOException;

    /**
     * Write long.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeLong(long v) throws IOException;

    /**
     * Write float.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeFloat(float v) throws IOException;

    /**
     * Write double.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeDouble(double v) throws IOException;

    /**
     * Write string.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeUTF(String v) throws IOException;

    /**
     * Write byte array.
     * 
     * @param v value.
     * @throws IOException
     */
    void writeBytes(byte[] v) throws IOException;

    /**
     * Write byte array.
     * 
     * @param v value.
     * @param off offset.
     * @param len length.
     * @throws IOException
     */
    void writeBytes(byte[] v, int off, int len) throws IOException;

    /**
     * Flush buffer.
     * 
     * @throws IOException
     */
    void flushBuffer() throws IOException;
}
