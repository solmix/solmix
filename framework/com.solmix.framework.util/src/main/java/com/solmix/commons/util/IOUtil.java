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
package com.solmix.commons.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import com.solmix.commons.logs.Logger;

/**
 * simply IO operation
 * 
 * @version 110035
 */
/**
 * 
 * @version 110035
 */
public class IOUtil
{
   public static final Logger log = new Logger(IOUtil.class);

   public static final int DEFAULT_BUFFER_SIZE = 4096;

   static class NonFlushingOutputStream extends FilterOutputStream
   {

      @Override
      public void flush()
      {
      }

      public NonFlushingOutputStream(OutputStream stream)
      {
         super(stream);
      }

   }

   /**
    * constructed function
    */
   public IOUtil()
   {
   }

   /**
    * @param in
    *           input Stream
    * @param out
    *           output stream
    * @throws IOException
    */
   public static void copyStreams(InputStream in, OutputStream out) throws IOException
   {
      copyStreams(in, out, 4096, true);
   }

   public static void copyStreams(InputStream in, OutputStream out, int bufSize) throws IOException
   {
      copyStreams(in, out, bufSize, true);
   }

   public static void copyStreams(InputStream in, OutputStream out, boolean buffered) throws IOException
   {
      copyStreams(in, out, DEFAULT_BUFFER_SIZE, buffered);
   }

   public static void copyStreams(InputStream in, OutputStream out, int bufSize, boolean buffered) throws IOException
   {
      byte buffer[] = new byte[bufSize];
      if (buffered) {
         in = new BufferedInputStream(in);
         out = new BufferedOutputStream(out);
      }
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1)
         out.write(buffer, 0, bytesRead);
      if (buffered)
         out.flush();
   }

   public static void copyCharacterStreams(Reader in, Writer out) throws IOException
   {
      copyCharacterStreams(in, out, 4096, true);
   }

   public static void copyCharacterStreams(Reader in, Writer out, int bufSize) throws IOException
   {
      copyCharacterStreams(in, out, bufSize, true);
   }

   public static void copyCharacterStreams(Reader in, Writer out, boolean buffered) throws IOException
   {
      copyCharacterStreams(in, out, 4096, buffered);
   }

   public static void copyCharacterStreams(Reader in, Writer out, int bufSize, boolean buffered) throws IOException
   {
      char buffer[] = new char[bufSize];
      if (buffered) {
         in = new BufferedReader(in);
         out = new BufferedWriter(out);
      }
      int bytesRead;
      while ((bytesRead = in.read(buffer)) != -1)
         out.write(buffer, 0, bytesRead);
      if (buffered)
         out.flush();
   }

   public static void addOutput(OutputStream out, List input) throws Exception
   {
      for (Object o : input) {
         if (o instanceof List)
            addOutput(out, (List) o);
         else if (o instanceof String)
            addOutput(out, (String) o);
         else if (o instanceof InputStream)
            addOutput(out, (InputStream) o);
      }
   }

   public static void addOutput(OutputStream out, String input) throws Exception
   {
      Writer writer = new OutputStreamWriter(new NonFlushingOutputStream(out));
      writer.write((String) input);
      writer.flush();
   }

   public static void addOutput(OutputStream out, InputStream input) throws Exception
   {
      copyStreams(input, out);
      try {
         input.close();
      } catch (Exception e) {
         log.error("IO error:close input stream" + e);
      }
   }

   public static void addOutput(OutputStream out, Reader input) throws Exception
   {
      Writer writer = new OutputStreamWriter(new NonFlushingOutputStream(out));
      copyCharacterStreams(input, writer);
      writer.flush();
      try {
         input.close();
      } catch (Exception e) {
         log.error("IO error:close input stream" + e);
      }
   }
   public static Reader makeReader(Object source)
   {
     if(source instanceof Reader){
        ValidateUtil.assertNotNull("parse null source", source);
         return (Reader)source;
     }
     if(source instanceof InputStream)
         return makeReader((InputStream)source);
     if((source instanceof String)) 
        return makeReader((String)source);
       if ((source instanceof StringBuffer))
          return makeReader((StringBuffer)source);
     if(source instanceof char[])
        return makeReader((char[])source);
     if(source instanceof byte[])
        return makeReader((byte[])source);
     else
         throw new IllegalArgumentException((new StringBuilder()).append("Don't know to make a Reader from a ").append(source.getClass().getName()).toString());
   }
   public static Reader makeReader(InputStream source)
   {
      ValidateUtil.assertNotNull("parse null source", source);
      return new InputStreamReader((InputStream) source);
   }

   public static Reader makeReader(String source)
   {
      ValidateUtil.assertNotNull("parse null source", source);
      return new StringReader(source);
   }

   public static Reader makeReader(StringBuffer source)
   {
      ValidateUtil.assertNotNull("parse null source", source);
      return new StringReader(source.toString());
   }

   public static Reader makeReader(char[] source)
   {
      ValidateUtil.assertNotNull("parse null source", source);
      return new CharArrayReader((char[]) source);
   }

   public static Reader makeReader(byte[] source)
   {
      ValidateUtil.assertNotNull("parse null source", source);
      return new InputStreamReader(new ByteArrayInputStream((byte[]) source));
   }

   public static String inputStreamToString(InputStream stream) throws IOException
   {
      return readerToString(new InputStreamReader(stream));
   }

   public static String readerToString(Reader reader) throws IOException
   {
      Writer writer = new StringWriter();
      copyCharacterStreams(reader, writer);
      return writer.toString();
   }

   public static void closeQuitely(InputStream is)
   {
      try {
         is.close();
      } catch (Exception ignored) {
      }
   }

   public static void closeQuitely(OutputStream os)
   {
      try {
         os.flush();
      } catch (Exception ignored) {
      }
      try {
         os.close();
      } catch (Exception ignored) {
      }
   }

}
