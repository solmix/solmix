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
package org.solmix.commons.io;

import java.io.IOException;
import java.io.Writer;

/**
 * @version 110035
 */
public class ForkedWriter extends Writer
{

   public ForkedWriter(Writer one, Writer two)
   {
      this.one = null;
      this.two = null;
      this.one = one;
      this.two = two;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.io.Writer#write(char[], int, int)
    */
   public void write(char buffer[], int offset, int length) throws IOException
   {
      one.write(buffer, offset, length);
      two.write(buffer, offset, length);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.io.Writer#write(java.lang.String, int, int)
    */
   public void write(String str, int offset, int length) throws IOException
   {
      one.write(str, offset, length);
      two.write(str, offset, length);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.io.Writer#write(int)
    */
   public void write(int character) throws IOException
   {
      one.write(character);
      two.write(character);
   }

   public void close() throws IOException
   {
      one.close();
      two.close();
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.io.Writer#flush()
    */
   public void flush() throws IOException
   {
      one.flush();
      two.flush();
   }

   private Writer one;

   private Writer two;
}
