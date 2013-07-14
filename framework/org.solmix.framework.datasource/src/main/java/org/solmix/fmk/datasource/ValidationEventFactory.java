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

package org.solmix.fmk.datasource;

import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.criterion.ValidationEventLocator;
import org.solmix.api.event.IValidationEvent;
import org.solmix.api.event.IValidationEvent.Level;
import org.solmix.api.event.IValidationEvent.OutType;
import org.solmix.api.event.IValidationEvent.Status;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.event.DSValidationEvent;
import org.solmix.fmk.event.FiledValidationEvent;

public class ValidationEventFactory
{

   private ValidationEventFactory()
   {

   }

   static ValidationEventFactory instance;
   public static ValidationEventFactory instance()
   {
       if(instance ==null)
           instance =  new ValidationEventFactory();
      return instance;
   }
public static final String TYPE_DS="datasource";
public static final String TYPE_FIELD="field";
   private Status status;

   private Level level;

   private String name;

   private String type;

   /**
    * @param type the type to set
    */
   public void setType( String type )
   {
      this.type = type;
   }

   private OutType outType;

   /**
    * @return the status
    */
   public Status getStatus()
   {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus( Status status )
   {
      this.status = status;
   }

   /**
    * @return the level
    */
   public Level getLevel()
   {
      return level;
   }

   /**
    * @param level the level to set
    */
   public void setLevel( Level level )
   {
      this.level = level;
   }

   /**
    * @return the outType
    */
   public OutType getOutType()
   {
      return outType;
   }

   /**
    * @param outType the outType to set
    */
   public void setOutType( OutType outType )
   {
      this.outType = outType;
   }

   public static ValidationEventFactory getDSValidator( String name )
   {
      ValidationEventFactory v = new ValidationEventFactory();
      v.setName( name );
      v.setType( "datasource" );
      v.setStatus( Status.NO_HANDLED );
      v.setOutType( OutType.CLIENT );
      v.setLevel( Level.DEBUG );
      return v;
   }

   public static ValidationEventFactory getFieldValidator()
   {
      ValidationEventFactory v = new ValidationEventFactory();
      v.setType( "dsrequest" );
      v.setStatus( Status.NO_HANDLED );
      v.setOutType( OutType.CLIENT );
      v.setLevel( Level.DEBUG );
      return v;
   }

   /**
    * @param name the name to set
    */
   public void setName( String name )
   {
      this.name = name;
   }

   /**
    * @param errorMssage validation error message
    * @return
    * @throws SlxException
    */
   public IValidationEvent create( String name, String errorMssage ) throws SlxException
   {
      return create( name, errorMssage, null );

   }

   public IValidationEvent create( Level level, String errorMssage ) throws SlxException
   {
      return create( outType, level, errorMssage );
   }

   public IValidationEvent create( OutType outType, Level level, String errorMssage ) throws SlxException
   {
      return create( outType, level, name, errorMssage, null, null, null );
   }

   public IValidationEvent create( String name, String errorMssage, String sugest ) throws SlxException
   {
      return create( outType, level, name, errorMssage, null, null, sugest );
   }

   public IValidationEvent create( OutType outType, Level level, String name, String errorMssage, String sugest ) throws SlxException
   {
      return create( outType, level, name, errorMssage, null, null, sugest );
   }

   public IValidationEvent create( OutType outType, Level level, String name, String errorMssage, Throwable e ) throws SlxException
   {
      return create( outType, level, name, errorMssage, e, null, null );
   }

   public IValidationEvent create( OutType outType, Level level, String name, String errorMssage, Throwable e, ValidationEventLocator locator,
      String sugest ) throws SlxException
   {
      return _create( outType, level, name, errorMssage, e, locator, sugest, null );
   }

   public IValidationEvent create( OutType outType, Level level, String name, ErrorMessage erorObject, Throwable e, ValidationEventLocator locator,
      String sugest ) throws SlxException
   {
      return _create( outType, level, name, null, e, locator, sugest, erorObject );
   }

   protected IValidationEvent _create( OutType outType, Level level, String name, String errorMssage, Throwable e, ValidationEventLocator locator,
      String sugest, ErrorMessage erorObject ) throws SlxException
   {
      IValidationEvent ve = null;
      if ( type.equals( TYPE_DS ) )
      {
         ve = new DSValidationEvent();
      } else if ( type.equals( TYPE_FIELD ) )
      {
         ve = new FiledValidationEvent();
      } else
      {
         throw new SlxException( Tmodule.DATASOURCE, Texception.V_NO_SUCH_VALIDATIONEVENT_IMP, "no such validationevent implementation" );
      }
      // output type
      if ( outType == null )
      {
         if ( this.outType == null )
            outType = OutType.UN_SET;
         else
            outType = this.outType;
      }
      ve.setOutType( outType );
      // level
      if ( level == null )
      {
         if ( this.level == null )
         {
            if ( e != null )
               level = Level.ERROR;
            else
               level = Level.DEBUG;
         } else
            level = this.level;
      }
      ve.setLevel( level );
      // error message
      if ( errorMssage != null && sugest != null )
      {
         ve.setErrorMessage( new ErrorMessage( errorMssage, sugest ) );
      } else if ( errorMssage != null )
      {
         ve.setErrorMessage( new ErrorMessage( errorMssage ) );
      }
      if ( e != null )
         ve.setException( e );
      if ( locator != null )
         ve.setLocator( locator );
      if ( erorObject != null )
         ve.setErrorMessage( erorObject );
      if ( this.status != null )
         ve.setStatus( status );
      if ( name == null || name.isEmpty() )
         name = this.name;
      ve.setName( name );
      return ve;
   }
}
