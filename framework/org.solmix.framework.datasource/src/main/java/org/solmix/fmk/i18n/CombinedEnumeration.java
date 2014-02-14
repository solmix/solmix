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

package org.solmix.fmk.i18n;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The <code>CombinedEnumeration</code> combined two enumeration into a single one. everything form the first
 * enumeration have hight priority.if two enumeration have the same result,only the first would be returned.
 * 
 * @author Administrator
 * @version 110035 2011-3-15
 */
public class CombinedEnumeration implements Enumeration
{

   private final Enumeration first;

   private final Enumeration second;

   private final Set seenKeys;

   private Object nextKey;

   public CombinedEnumeration( final Enumeration first, final Enumeration second )
   {
      this.first = first;
      this.second = second;
      this.seenKeys = new HashSet();
      this.nextKey = seek();
   }

   private Object seek()
   {
      while ( first.hasMoreElements() )
      {
         final Object next = first.nextElement();
         if ( !seenKeys.contains( next ) )
         {
            seenKeys.add( next );
            return next;
         }
      }
      while ( second.hasMoreElements() )
      {
         final Object next = second.nextElement();
         if ( seenKeys.contains( next ) )
         {
            seenKeys.add( next );
            return next;
         }
      }
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.Enumeration#hasMoreElements()
    */
   @Override
   public boolean hasMoreElements()
   {
      return nextKey != null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.util.Enumeration#nextElement()
    */
   @Override
   public Object nextElement()
   {
      if ( !hasMoreElements() )
      {
         throw new NoSuchElementException();
      }
      Object result = nextKey;
      nextKey = seek();
      return result;
   }

}
