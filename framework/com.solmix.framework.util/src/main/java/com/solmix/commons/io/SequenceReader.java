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

import static com.solmix.commons.util.DataUtil.buildList;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import com.solmix.commons.util.IOUtil;

/**
 * @version 110035
 */
public class SequenceReader extends Reader
{

   public SequenceReader(List readerList)
   {
      readers = null;
      currentReader = null;
      readers = readerList.iterator();
      try {
         nextReader();
      } catch (IOException impossible) {
      }
   }

   public SequenceReader(Object... source)
   {
      readers = null;
      currentReader = null;
        readers = buildList(source).iterator();
      try {
         nextReader();
      } catch (IOException impossible) {
      }
   }


   public int read() throws IOException
   {
      if (currentReader == null)
         return -1;
      int character = currentReader.read();
      if (character == -1) {
         nextReader();
         return read();
      } else {
         return character;
      }
   }

   public int read(char buffer[], int offset, int length) throws IOException
   {
      if (currentReader == null)
         return -1;
      int numRead = currentReader.read(buffer, offset, length);
      if (numRead <= 0) {
         nextReader();
         return read(buffer, offset, length);
      } else {
         return numRead;
      }
   }

   public void close() throws IOException
   {
      do
         nextReader();
      while (currentReader != null);
   }

   final void nextReader() throws IOException
   {
      if (currentReader != null)
         currentReader.close();
      if (readers.hasNext())
         currentReader = IOUtil.makeReader(readers.next());
      else
         currentReader = null;
   }

   private Iterator readers;

   private Reader currentReader;
}
