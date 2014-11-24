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

public interface DataReader {

    /**
     * Read boolean.
     * 
     * @return boolean.
     * @throws IOException.
     */
    boolean readBool() throws IOException;

    /**
     * Read byte.
     * 
     * @return byte value.
     * @throws IOException.
     */
    byte readByte() throws IOException;

    /**
     * Read short integer.
     * 
     * @return short.
     * @throws IOException.
     */
    short readShort() throws IOException;

    /**
     * Read integer.
     * 
     * @return integer.
     * @throws IOException.
     */
    int readInt() throws IOException;

    /**
     * Read long.
     * 
     * @return long.
     * @throws IOException.
     */
    long readLong() throws IOException;

    /**
     * Read float.
     * 
     * @return float.
     * @throws IOException.
     */
    float readFloat() throws IOException;

    /**
     * Read double.
     * 
     * @return double.
     * @throws IOException.
     */
    double readDouble() throws IOException;

    /**
     * Read UTF-8 string.
     * 
     * @return string.
     * @throws IOException.
     */
    String readUTF() throws IOException;

    /**
     * Read byte array.
     * 
     * @return byte array.
     * @throws IOException.
     */
    byte[] readBytes() throws IOException;
}
