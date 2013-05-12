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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @version 110035
 */
public class ByteCountingOutputStream extends FilterOutputStream
{
   IByteCounter byteCounter;

   /**
    * @param out
    */
   public ByteCountingOutputStream(OutputStream out, IByteCounter byteCounter)
   {
      super(out);
      this.byteCounter = byteCounter;
   }

   public void write(int b) throws IOException
   {
      super.write(b);
      byteCounter.incrementBy(1L);
   }
}
