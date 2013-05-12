/*
 * ========THE SOLMIX PROJECT=====================================
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
package com.solmix.commons.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @version 110035
 */
public class NullOutputStream extends OutputStream
{

    public NullOutputStream()
    {
    }

    public void write(char ac[], int i, int j)
        throws IOException
    {
    }

    public void write(char ac[])
        throws IOException
    {
    }

    public void write(int i)
        throws IOException
    {
    }

    public void close()
        throws IOException
    {
    }

    public void flush()
        throws IOException
    {
    }
}