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
package com.solmix.fmk.serialize.jackson;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import com.solmix.api.jaxb.Tvalidator;
import com.solmix.api.jaxb.Tvalidators;


/**
 * 
 * @author solomon
 * @version 110035  2011-4-11
 */

public class TvalidatorsSerializer extends JsonSerializer<Tvalidators>
{

   /**
    * {@inheritDoc}
    * 
    * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator,
    *      org.codehaus.jackson.map.SerializerProvider)
    */
   @Override
   public void serialize(Tvalidators value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
   {

      List<Tvalidator> validators = value.getValidator();
      if (validators != null)
      {
         jgen.writeStartArray();
         for (Tvalidator validator : validators)
         {
            jgen.writeObject(validator);
         }
         jgen.writeEndArray();
      }

   }

}
