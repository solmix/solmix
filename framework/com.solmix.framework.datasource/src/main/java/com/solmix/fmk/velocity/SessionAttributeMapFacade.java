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

package com.solmix.fmk.velocity;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper HttpSession attributes names to context map.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-ds
 */
public class SessionAttributeMapFacade extends AbstractMap< String ,Object > implements HttpSession
{

   HttpSession session;

   public SessionAttributeMapFacade( HttpSession session )
   {
      this.session = session;
   }

   @Override
public Object getAttribute( String attrName )
   {
      return get( attrName );
   }

@Override
public Enumeration getAttributeNames()
   {
      return session.getAttributeNames();
   }

   @Override
public long getCreationTime()
   {
      return session.getCreationTime();
   }

   @Override
public String getId()
   {
      return session.getId();
   }

   @Override
public long getLastAccessedTime()
   {
      return session.getLastAccessedTime();
   }

   @Override
public int getMaxInactiveInterval()
   {
      return session.getMaxInactiveInterval();
   }

   @Override
public ServletContext getServletContext()
   {
      return session.getServletContext();
   }

   @Override
@Deprecated
   public HttpSessionContext getSessionContext()
   {
      return session.getSessionContext();
   }

   @Override
public Object getValue( String arg0 )
   {
      return getAttribute( arg0 );
   }

   /**
    * this method is replaced by {@link #getAttributeNames()}
    */
   @Override
@Deprecated
   public String[] getValueNames()
   {
      return session.getValueNames();
   }

   @Override
public void invalidate()
   {
      session.invalidate();
   }

   @Override
public boolean isNew()
   {
      return session.isNew();
   }

   /**
    *this method is replaced by {@link #setAttribute(String, Object)}
    */
   @Override
@Deprecated
   public void putValue( String arg0, Object arg1 )
   {
      session.putValue( arg0, arg1 );
   }

   @Override
public void removeAttribute( String arg0 )
   {
      session.removeAttribute( arg0 );
   }

   @Override
public void removeValue( String arg0 )
   {
      session.removeValue( arg0 );
   }

   @Override
public void setAttribute( String arg0, Object arg1 )
   {
      session.setAttribute( arg0, arg1 );
   }

   @Override
public void setMaxInactiveInterval( int arg0 )
   {
      session.setMaxInactiveInterval( arg0 );
   }

   @Override
   public Set< Map.Entry< String ,Object >> entrySet()
   {
      Map< String ,Object > map = new HashMap< String ,Object >();
      String key;
      Object value;
      for ( Enumeration attrs = session.getAttributeNames(); attrs.hasMoreElements(); )
      {
         key = (String) attrs.nextElement();
         value = session.getAttribute( key );
         map.put( key, value );
      }

      return map.entrySet();
   }

   @Override
   public Set< String > keySet()
   {
      Set< String > set = new HashSet< String >();
      for ( Enumeration< ? > attrs = session.getAttributeNames(); attrs.hasMoreElements(); )
         set.add( (String) attrs.nextElement() );
      return set;
   }

   private static Logger log = LoggerFactory.getLogger( SessionAttributeMapFacade.class.getName() );

}
