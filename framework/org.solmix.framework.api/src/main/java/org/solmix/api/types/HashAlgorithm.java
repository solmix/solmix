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
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-18 solmix-api
 */
public enum HashAlgorithm implements ValueEnum
{
   MD5( "MD5" ) , SHA( "SHA" );

   private String value;

   HashAlgorithm( String str )
   {
      this.value = str;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.types.ValueEnum#getValue()
    */
   @Override
   public String value()
   {
      // TODO Auto-generated method stub
      return value;
   }

}
