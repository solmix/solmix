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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.solmix.api.criterion.ErrorMessage;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Tservice;
import com.solmix.api.jaxb.Tvalidator;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.velocity.Velocity;

/**
 * 
 * @author Administrator
 * @version 110035 2011-3-13
 * @param <V>
 * @param <K>
 */
@SuppressWarnings( "unchecked" )
public class Validator< K, V > extends HashMap< K ,V >
{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private Map< K ,V > errorVariables;

   Tvalidator tvalidator;

   /**
    * Validator contained two strategy .for standard with Tvalidator and for extends with Map.
    */
   boolean standard;

   /**
    * @return the Tvalidator
    */
   public Tvalidator getTvalidator()
   {
      return tvalidator;
   }

   public Validator()
   {
      errorVariables = new HashMap< K ,V >();
   }

   public Validator( Tvalidator validator )
   {
      errorVariables = new HashMap< K ,V >();
      standard = true;
      tvalidator = validator;
   }

   public Validator( Map< K ,V > fieldData )
   {
      this( fieldData, null );
   }

   public Validator( Map< K ,V > fieldData, BasicDataSource ds )
   {
      errorVariables = new HashMap< K ,V >();
      standard = false;
      DataUtil.mapMerge( fieldData, this );
   }

   public String getExpression()
   {
      String _return = getProperty( "expression" );
      if ( _return == null && standard )
         _return = tvalidator.getExpression();
      return _return;
   }

   public String getOperator()
   {
      String _return = getProperty( "operator" );
      if ( _return == null && standard )
         _return = tvalidator.getOperator();
      return _return;
   }
   public String getSubstring()
   {
      String _return = getProperty( "substring" );
      if ( _return == null && standard )
         _return = tvalidator.getSubstring();
      return _return;
   }
   public String getOtherField()
   {
      String _return = tvalidator.getOtherField();
      if ( _return == null && standard )
         _return = getProperty( "otherField" );
      return _return;
   }
   public boolean getBoolean( String key )
   {
      return DataUtil.getBoolean( this, key );
   }

   public Long getCount()
   {
      Long _return = DataUtil.getLong( this, "count" );
      if ( _return == null && standard )
         _return = tvalidator.getCount();
      return _return;
   }

   public String getMask()
   {
      String _return = getProperty( "operator" );
      if ( _return == null && standard )
         _return = tvalidator.getOperator();
      return _return;
   }
   public String getProperty( String property )
   {
      if ( get( property ) == null )
         return null;
      else
         return get( property ).toString();
   }

   public boolean isClientOnly()
   {
      return getBoolean( "clientOnly" );
   }

   public boolean isServerOnly()
   {
      return getBoolean( "serverOnly" );
   }

   public boolean isStopIfFalse()
   {
      return getBoolean( "stopIfFalse" );
   }

   public String getErrorMessage()
   {
      String _return = getProperty( "errorMessage" );
      if ( _return == null && standard )
         _return = tvalidator.getErrorMessage();
      return _return;
   }

   public String getRelatedField()
   {
      String _return = getProperty( "relatedField" );
      // if ( _return == null && standard )
      // _return = tvalidator.
      return _return;
   }

   public String getRelatedDataSource()
   {
      String _return = getProperty( "relatedDataSource" );
      // if ( _return == null && standard )
      // _return = tvalidator.
      return _return;
   }
   public Long getMaxAsLong()
   {

      return getMaxAsDouble() == null ? null : getMaxAsDouble().longValue();
   }

   public Long getMinAsLong()
   {
      return getMinAsDouble() == null ? null : getMinAsDouble().longValue();
   }

   public Double getMaxAsDouble()
   {
      Double _return = DataUtil.getDouble( this, "max" );
      if ( _return == null && standard )
         _return = tvalidator.getMax();
      return _return;
   }

   public Double getMinAsDouble()
   {
      Double _return = DataUtil.getDouble( this, "min" );
      if ( _return == null && standard )
         _return = tvalidator.getMin();
      return _return;
   }
   public String getServerCondition()
   {
      String _return =getProperty( "serverCondition" );
      if ( _return == null && standard )
         _return = tvalidator.getServerCondition();
      return _return;
   }

   public Tservice getService()
   {
      Tservice _return = (Tservice) get( "service" );
      if ( _return == null && standard )
         _return = tvalidator.getService();
      return _return;
   }
   public Double getPrecision()
   {
      Double _return = DataUtil.getDouble( this, "precision" );
      if ( _return == null && standard )
         _return = tvalidator.getPrecision();
      return _return;
   }
   public String getType()
   {
      String _return = getProperty( "type" );
      if ( _return == null && standard )
         _return = tvalidator.getType() == null ? null : tvalidator.getType().value();
      return _return;
   }


   public List getValueMapList()
   {
      Object _return = get( "valueMapList" );
      if ( _return == null || standard )
         return null;
      return (List) _return;
   }
   public void addErrorMessageVariable( K name, V value )
   {
      errorVariables.put( name, value );
   }

   public Map< K ,V > getErrorMessageVariables()
   {
      return errorVariables;
   }

   /**
    * @param error
    * @throws Exception
    */
   public ErrorMessage evaluateErrorMessage( ErrorMessage error ) throws SlxException
   {
      String evaluated = Velocity.evaluateAsString( error.getErrorString(), errorVariables );
      error.setErrorString( evaluated );
      return error;

   }
}
