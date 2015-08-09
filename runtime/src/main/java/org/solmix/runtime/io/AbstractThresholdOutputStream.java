/**
 * Copyright (container) 2015 The Solmix Project
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
package org.solmix.runtime.io;

import java.io.IOException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月15日
 */

public abstract class AbstractThresholdOutputStream extends AbstractWrappedOutputStream {
    protected int threshold;
    protected LoadingByteArrayOutputStream buffer;
    
    public AbstractThresholdOutputStream(int threshold) {
        this.threshold = threshold;
        if (threshold >= 0) {
            buffer = new LoadingByteArrayOutputStream(threshold + 1);
        }
    }
    
    
    public abstract void thresholdReached() throws IOException;
    public abstract void thresholdNotReached() throws IOException;
    
    
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (buffer != null) {
            int space = threshold - buffer.size();
            if (space > len) {
                space = len;
            }
            buffer.write(b, off, space);
            len -= space;
            off += space;
            
            if (buffer.size() >= threshold) {
                thresholdReached();
                unBuffer();
            }
            if (len == 0) {
                return;
            }
        }
        super.write(b, off, len);
    }


    @Override
    public void write(int b) throws IOException {
        if (buffer != null) {
            buffer.write(b);
            if (buffer.size() >= threshold) {
                thresholdReached();
                unBuffer();
            }
            return;
        }
        super.write(b);
    }

    public void unBuffer() throws IOException {
        if (buffer != null) {
            if (buffer.size() > 0) {
                super.write(buffer.getRawBytes(), 0, buffer.size());
            }
            buffer = null;
        }  
    }


    @Override
    public void close() throws IOException {
        if (buffer != null) {
            thresholdNotReached();
            unBuffer();
        }
        super.close();
    }
}
