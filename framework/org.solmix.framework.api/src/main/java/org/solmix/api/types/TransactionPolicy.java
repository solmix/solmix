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
 * framework transaction policy.
 * @author Administrator
 * @since 0.0.1
 * @version 110041  
 * 
 */

public enum TransactionPolicy implements ValueEnum
{
    /**
     * First change should join transaction.the change include add, remove, update operation 
     */
    FROM_FIRST_CHANGE( "1" ) ,
    /**
     * Any change should join transaction.
     */
    ANY_CHANGE( "2" ) ,
    /**
     * All change should join transaction,include retch operation.
     */
    ALL( "3" ) ,
    NONE( "4" );

   private String value;

   TransactionPolicy( String value )
   {
      this.value = value;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.types.ValueEnum#getValue()
    */
   @Override
   public String value()
   {
      return value;
   }
}
