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

package org.solmix.api.types;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-18 solmix-api
 */
public enum EnumTranslateStrategy implements ValueEnum
{
   /**
    * Translates to/from a String matching the constant name. This is the default if not set.
    */
   STRING( "string" ) ,

   /**
    * Translates to/from an integer matching the ordinal number of the constant within the enumeration
    */
   ORDINAL( "ordinal" ) ,

   /**
    * Translates to/from a Javascript object containing one property for each property defined within the enum. The
    * constant itself and the ordinal number are included in the JS object. By default they are called "_constant" and
    * "_ordinal", but this can be overridden with the DataSource.enumOrdinalProperty and DataSource.enumConstantProperty
    * properties
    */
   BEAN( "bean" );

   private String value;

   EnumTranslateStrategy( String value )
   {
      this.value = value;
   }

   public String value()
   {
      return this.value;
   }

}
