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

package com.solmix.compression;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import com.solmix.api.interfaces.Compression;
import org.slf4j.Logger;
import com.solmix.commons.util.IOUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2011-6-7
 */

public class CompressionImpl implements Compression
{

   public static int whiteSpaceBufSize = 2049;

   static ByteArrayOutputStream whiteSpace = null;

   private final static Logger log = LoggerFactory.getLogger(CompressionImpl.class.getName());

   CompressionImpl()
   {

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.services.interfaces.Compression#compressBuffer(java.io.ByteArrayOutputStream, java.lang.String)
    */
   @Override
   public ByteArrayOutputStream compressBuffer(ByteArrayOutputStream bytearrayoutputstream, String s) throws Exception
   {
      return compressBuffer(bytearrayoutputstream, mimeTypeRequiresPadding(s));

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.services.interfaces.Compression#compressBuffer(java.io.ByteArrayOutputStream, boolean)
    */
   @Override
   public ByteArrayOutputStream compressBuffer(ByteArrayOutputStream bytearrayoutputstream, boolean flag) throws Exception
   {
      bytearrayoutputstream.flush();
      int size = bytearrayoutputstream.size();
      int j = size;
      if (flag)
         j += whiteSpaceBufSize;

      ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream(j / 4);
      GZIPOutputStream gzipoutputstream = j > 0 ? new GZIPOutputStream(bytearrayoutputstream1, j) : new GZIPOutputStream(bytearrayoutputstream1);
      if (flag)
         getWhiteSpaceBuffer().writeTo(gzipoutputstream);
      bytearrayoutputstream.writeTo(gzipoutputstream);
      gzipoutputstream.flush();
      gzipoutputstream.close();
      bytearrayoutputstream1.flush();
      log.debug("Compressed buffer: start -->" + (flag ? getWhiteSpaceBuffer().toString() : "") + bytearrayoutputstream.toString() + "<-- end");
      return bytearrayoutputstream1;

   }

   private static final ByteArrayOutputStream getWhiteSpaceBuffer()
   {
      if (whiteSpace == null)
      {
         whiteSpace = new ByteArrayOutputStream();
         for (int i = 0; i < whiteSpaceBufSize; i++)
            whiteSpace.write(32);

         whiteSpace.write(10);
      }
      return whiteSpace;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.services.interfaces.Compression#compressStream(java.io.InputStream, boolean)
    */
   @Override
   public ByteArrayOutputStream compressStream(InputStream inputstream, boolean flag) throws Exception
   {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      IOUtil.copyStreams(inputstream, bytearrayoutputstream);
      return compressBuffer(bytearrayoutputstream, flag);

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.services.interfaces.SingletonInstance#staticInstance()
    */
   @Override
   public Object staticInstance() throws Exception
   {
      return new CompressionImpl();
   }

   private static final boolean mimeTypeRequiresPadding(String s)
   {
      return s != null && (s.indexOf("javascript") != -1 || s.indexOf("ecmascript") != -1);
   }

}
