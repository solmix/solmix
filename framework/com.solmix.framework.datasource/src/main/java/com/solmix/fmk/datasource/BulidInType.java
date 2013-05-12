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

package com.solmix.fmk.datasource;

import java.util.List;
import java.util.Map;

import com.solmix.api.datasource.IType;
import com.solmix.api.exception.SlxException;
import com.solmix.commons.logs.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.util.DefaultValidators;
import com.solmix.fmk.util.ErrorReport;

/**
 * Handle build-in type validation.
 * 
 * @author Administrator
 * @version 110035 2011-3-13
 */

public class BulidInType implements IType
{

   private static Logger log = new Logger( BulidInType.class.getName() );

   protected String name;

   /**
    * maybe string,tvalidator,map
    */
   List< Object > validators;

   public BulidInType( String name, Object validators )
   {
      this.name = name;
      this.validators = DataUtil.makeListIfSingle( validators );
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.datasource.IType#create(java.lang.Object, java.lang.Object)
    */
   @Override
   public Object create( Object value, Object validationcontext ) throws SlxException
   {
      ValidationContext context = (ValidationContext) validationcontext;
      if ( validators == null || value == null )
         return value;
      if ( value instanceof List< ? > )
      {
         List valueList = (List) value;
         for ( Object o : valueList )
         {
            validateValue( o, context );
         }
         return valueList;
      } else
         return validateValue( value, context );
   }

   /**
    * @param value
    * @param context
    * @return
    * @throws SlxException
    */
   protected synchronized Object validateValue( Object value, ValidationContext context ) throws SlxException
   {
      String fieldName = context.getFieldName();
      Map< Object ,Object > currentRecord = context.getCurrentRecord();
      ErrorReport errors = DefaultValidators.validateField( currentRecord, fieldName, validators, context, null, value );
      if ( errors != null )
      {
         Object error = errors.get( fieldName );
         if ( error != null )
            context.addError( error );
      }
      if ( context.resultingValueIsSet() )
      {
         value = context.getResultingValue();
         context.clearResultingValue();
      }
      return value;
   }

   /**
    * @return the validators
    */
   public List< Object > getValidators()
   {
      return validators;
   }

   /**
    * @param validators the validators to set
    */
   public void setValidators( List< Object > validators )
   {
      this.validators = validators;
   }

   /**
    * @param name the name to set
    */
   public void setName( String name )
   {
      this.name = name;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.datasource.IType#getName()
    */
   @Override
   public String getName()
   {

      return this.name;
   }

}
