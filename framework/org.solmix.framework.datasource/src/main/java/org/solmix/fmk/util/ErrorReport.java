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

package org.solmix.fmk.util;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.LoggerFactory;

import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.event.IValidationEvent;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.ValidationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 0.0.2 2010-12-18 solmix-ds
 * 
 */
public class ErrorReport extends LinkedMap implements Serializable
{

   /**
    * 
    */
   private static final long serialVersionUID = -5476167834389051318L;

   public ErrorReport()
   {

   }

   private void addError( String fieldName, IValidationEvent event )
   {

      DataUtil.putMultiple( this, fieldName, event );

   }

   public void addError( String fieldName, ErrorMessage error )
   {

      DataUtil.putMultiple( this, fieldName, error );

   }

   public void addError(String fieldName, String errorMsg)
   {
      addError(fieldName, new ErrorMessage(errorMsg));
   }

   /**
    * {@inheritDoc}
    * 
    * @throws SlxException
    * 
    * @see org.solmix.api.datasource.IErrorReport#addError(java.lang.String, java.lang.String, java.lang.String)
    */
   public void addError( String fieldName, String message, String suggestedValue, ValidationContext vcontext ) throws SlxException
   {
      IValidationEvent vevent = null;
      if ( vcontext.getVfactory() != null )
      {
         vevent = vcontext.getVfactory().create( fieldName, message, suggestedValue );
      } else
      {
          LoggerFactory.getLogger(SlxLog.VALIDATION_LOGNAME).warn( "there is no validation event factory set,check you configuration" );
      }
      if ( vevent != null )
      {
         Object event = get( fieldName );
         if ( event == null )
            put( fieldName, vevent );
         else
            DataUtil.putCombinedList( this, fieldName, vevent );
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.datasource.IErrorReport#getErrors(java.lang.String)
    */
   public List getErrors( String fieldName )
   {
      return DataUtil.makeListIfSingle( get( fieldName ) );
   }

}
