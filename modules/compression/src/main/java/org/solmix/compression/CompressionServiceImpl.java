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
package org.solmix.compression;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.WebContext;
import org.solmix.api.interfaces.CompressionService;
import org.solmix.fmk.servlet.ServletTools;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2011-6-7
 */

public class CompressionServiceImpl implements CompressionService
{
   private static final Logger log = LoggerFactory.getLogger(CompressionServiceImpl.class.getName());
   public ByteArrayOutputStream wrapBuf;
   public OutputStream servletOutputStream;
   public boolean compressing;
   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.SingletonInstance#staticInstance()
    */
   @Override
   public Object staticInstance() throws Exception
   {
      return new CompressionServiceImpl();
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.CompressionService#canCompress(java.lang.Object)
    */
   @Override
   public boolean canCompress(Object requestContext)
   {
     WebContext context = (WebContext)requestContext;
      return ServletTools.compressionEnabled() && ServletTools.browserClaimsGZSupport(context.getRequest())/* && !ServletTools.contextIsIncluded(context)*/;

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.CompressionService#compressIfPossible(java.lang.Object)
    */
   @Override
   public boolean compressIfPossible(Object requestContext) throws Exception
   {
      WebContext context = (WebContext)requestContext;
      if(!canCompress(context))
         return false;
     try
     {
         servletOutputStream = context.getResponse().getOutputStream();
     }
     catch(Exception exception)
     {
         String s = context.getRequestPath() + ": ";
         log.error(s + "Can't compress response because the servlet output stream is already in use: " + exception.toString());
         return false;
     }
     wrapBuf = new ByteArrayOutputStream();
     context.setOut(new PrintWriter(wrapBuf));
     compressing = true;
     return true;

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.CompressionService#completeResponse(java.lang.Object)
    */
   @Override
   public void completeResponse(Object requestContext) throws Exception
   {
      WebContext context = (WebContext)requestContext;
      if(!compressing)
         return;
     try
     {
        context.getOut().flush();
     }
     catch(Exception _ex) { }
     try
     {
        context.getOut().close();
     }
     catch(Exception _ex) { }
     compressAndSend(context, wrapBuf);

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.CompressionService#compressAndSend(java.lang.Object, java.io.ByteArrayOutputStream)
    */
   @Override
   public void compressAndSend(Object requestContext, ByteArrayOutputStream byteArrayOutputStream)throws Exception
   {
      compressAndSend(requestContext, byteArrayOutputStream, -1);

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.services.interfaces.CompressionService#compressAndSend(java.lang.Object, java.io.ByteArrayOutputStream, int)
    */
   @Override
   public void compressAndSend(Object requestContext, ByteArrayOutputStream byteArrayOutputStream, int size) throws Exception
   {
      WebContext context = (WebContext)requestContext;
      String s = context.getRequestPath() + ": ";
      if(servletOutputStream == null)
          servletOutputStream = context.getResponse().getOutputStream();
      if(size == -1)
         size =250;
//      if(i == -1)
//          i = config.getInt("compressionFilter.compressThreshold", 250);
      if(byteArrayOutputStream.size() < size && (!ServletTools.IENeedsToSeeACompressedPage(context) || context.getRequest().getContentType() == null || context.getRequest().getContentType().indexOf("text/html") == -1))
      {
          log.debug(s + "Result data size of " + byteArrayOutputStream.size() + " bytes is less than set threshold of " + size + " bytes - not compressing");
          byteArrayOutputStream.writeTo(servletOutputStream);
          try
          {
              servletOutputStream.flush();
          }
          catch(Exception _ex) { }
          try
          {
              context.getResponse().flushBuffer();
          }
          catch(Exception _ex) { }
          return;
      }
      ByteArrayOutputStream bytearrayoutputstream1 = (new CompressionImpl()).compressBuffer(byteArrayOutputStream, context.getRequest().getContentType());
      log.info(s + byteArrayOutputStream.size() + " -> " + bytearrayoutputstream1.size() + " bytes");
      context.getResponse().setHeader("Content-Encoding", "gzip");
      if(!ServletTools.compressionReadyCookieIsSet(context))
          ServletTools.setCompressionReadyCookie(context);
      context.getResponse().setContentLength(bytearrayoutputstream1.size());
      bytearrayoutputstream1.writeTo(servletOutputStream);
      try
      {
          servletOutputStream.flush();
      }
      catch(Exception _ex) { }
      try
      {
          context.getResponse().flushBuffer();
      }
      catch(Exception _ex) { }

   }

}
