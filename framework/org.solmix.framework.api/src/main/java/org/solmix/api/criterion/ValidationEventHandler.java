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

package org.solmix.api.criterion;

import org.solmix.api.event.IValidationEvent;
import org.solmix.api.exception.SlxException;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-26 solmix-api
 */
public interface ValidationEventHandler
{

   /**
    * For extention
    * 
    * @author solomon
    * @since 0.0.1
    * @version 110035 2010-12-27 solmix-api
    */
   public static abstract class Listener
   {

      public void validationCallback( Object callback )
      {
      }

   }

   void setListener( Listener listener );

   /**
    * If an unchecked runtime exception is thrown form this method,the provider will treat it as if the method returned
    * false and interrupt current operation.
    * 
    * @param event
    * @return true if the DataSource should attempt to continue current operation after handing this warning/error,false
    *         if current operation should be terminated.
    */
   boolean handleEvent( IValidationEvent event ) throws SlxException;

}
