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
package org.solmix.sql;



/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-3-21
 */

public enum EInterfaceType
{
   DATASOURCE("datasource") , DRIVERMANAGER("drivermanager") , JNDI("jndi") , JNDIOSGI("jndiosgi") , OSGI("osgi");

   private String value;

   private EInterfaceType(String value)
   {
   this.value = value;
   }

   public String value()
   {
      return value;
   }

   public static EInterfaceType fromValue(String v)
   {
      for (EInterfaceType c : EInterfaceType.values())
      {
         if ( c.value.equals( v.trim().toLowerCase() ) )
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }
}
