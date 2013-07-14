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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper HttpServletRequtest attributes names to context map.
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-ds
 */
public class ServletRequestAttributeMapFacade extends HttpServletRequestWrapper implements Map< String ,Object >
{

   public ServletRequestAttributeMapFacade( HttpServletRequest request )
   {
      super( request );
   }

   @Override
   public Object getAttribute( String attrName )
   {
      return get( attrName );
   }

   @Override
public void clear()
   {
   }

   @Override
public boolean containsKey( Object key )
   {
      Object obj = super.getAttribute( key.toString() );
      return obj != null;
   }

   @Override
public boolean containsValue( Object value )
   {
      return false;
   }

   @Override
public Set< Map.Entry< String ,Object >> entrySet()
   {
      Map< String ,Object > map = new HashMap< String ,Object >();
      String key;
      Object value;
      for ( Enumeration< ? > attrs = super.getAttributeNames(); attrs.hasMoreElements(); )
      {
         key = (String) attrs.nextElement();
         value = super.getAttribute( key );
         map.put( key, value );
      }

      return map.entrySet();
   }

   @Override
public Object get( Object key )
   {
      Set< Map.Entry< String ,Object >> entrySet = entrySet();
      for ( Map.Entry< String ,Object > entry : entrySet )
      {
         if ( entry.getKey().equals( key ) )
         {
            return entry.getValue();
         }
      }
      return null;
   }

   @Override
public boolean isEmpty()
   {
      Enumeration< ? > attrs = super.getAttributeNames();
      return !attrs.hasMoreElements();
   }

   @Override
public Set< String > keySet()
   {
      Set< String > set = new HashSet< String >();
      for ( Enumeration< ? > attrs = super.getAttributeNames(); attrs.hasMoreElements(); )
         set.add( (String) attrs.nextElement() );
      return set;
   }

   @Override
public Object remove( Object key )
   {
      super.removeAttribute( key.toString() );
      return null;
   }

   @Override
public int size()
   {
      int count = 0;
      for ( Enumeration attrs = super.getAttributeNames(); attrs.hasMoreElements(); )
      {
         attrs.nextElement();
         count++;
      }

      return count;
   }

   /**
    * return attribute values collection. {@inheritDoc}
    * 
    * @see java.util.Map#values()
    */
   @Override
public Collection< Object > values()
   {
      List< Object > values = new ArrayList< Object >();
      for ( Enumeration attrs = super.getAttributeNames(); attrs.hasMoreElements(); )
         values.add( attrs.nextElement() );
      return values;
   }

   private static Logger log = LoggerFactory.getLogger( ServletRequestAttributeMapFacade.class.getName() );

   /**
    * {@inheritDoc}
    * 
    * @see java.util.Map#put(java.lang.Object, java.lang.Object)
    */
   @Override
   public Object put( String key, Object value )
   {
      super.setAttribute( key, value );
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.Map#putAll(java.util.Map)
    */
   @Override
   public void putAll( Map< ? extends String , ? extends Object > m )
   {
      Object value;
      for ( String key : m.keySet() )
      {
         value = m.get( key );
         super.setAttribute( key.toString(), value );
      }

   }

}
