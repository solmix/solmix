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

package org.solmix.api.types;


/**
 *  Used {@link org.solmix.api.jaxb.Eoperation}
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-18 solmix-api
 */
@Deprecated
public enum DSOperationType implements ValueEnum
{
   /**
    * Fetch one or more records that match a set of search criteria.
    */
   FETCH( "fetch" ) , LOADSCHEMA( "loadSchema" ) ,
   /**
    * Store new records
    */
   ADD( "add" ) ,
   /**
    * Update an existing record
    */
   UPDATE( "update" ) ,
   /**
    * Remove (delete) an existing record
    */
   REMOVE( "remove" ) ,

   /**
    * Run server-side validation for add or update without actually performing the operation
    */
   VALIDATE( "validate" ) ,

   /**
    * Perform some arbitrary custom logic
    */
   CUSTOM( "custom" );

   private String value;

   DSOperationType( String value )
   {
      this.value = value;
   }

   @Override
public String value()
   {
      return this.value;
   }

}
