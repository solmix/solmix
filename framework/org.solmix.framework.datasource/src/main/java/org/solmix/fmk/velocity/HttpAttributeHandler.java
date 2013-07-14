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

package org.solmix.fmk.velocity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-21 solmix-ds
 */
public class HttpAttributeHandler
{

   public HttpAttributeHandler( HttpServletRequest request )
   {
      this.request = request;
   }

   public HttpAttributeHandler( HttpSession session )
   {
      this.session = session;
   }

   public Object get( String attrName ) throws Exception
   {
      Object value;
      if ( request != null )
         value = request.getAttribute( attrName );
      else if ( session != null )
      {
         value = session.getAttribute( attrName );
      } else
      {
         log.warn( "In HttpAttributeHandler.get() both request and session were null" );
         throw new Exception( "Both request and session were null" );
      }
      return value;
   }

   private HttpServletRequest request;

   private HttpSession session;

   public static Logger log = LoggerFactory.getLogger( HttpAttributeHandler.class.getName() );

}
