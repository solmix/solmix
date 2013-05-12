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

package com.solmix.fmk.js;

import java.io.IOException;
import java.io.Writer;

import com.solmix.api.exception.SlxException;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;

/**
 * 
 * @author Administrator
 * @version 110035 2011-3-12
 */

public class JSExpression implements IToJSON
{

   private final String expression;

   /**
    * @param string
    */
   public JSExpression( String expression )
   {
      this.expression = expression;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.fmk.js.IToJSON#toJSON(java.io.Writer)
    */
   @Override
   public void toJSON( Writer writer ) throws SlxException
   {
      try
      {
         writer.write( expression );
      } catch ( IOException e )
      {
         throw new SlxException( Tmodule.BASIC, Texception.IO_EXCEPTION, e );
      }

   }

}
