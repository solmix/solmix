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
package org.solmix.fmk.serialize.jackson;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.ToperationBindings;
import org.solmix.commons.util.DataUtil;


/**
 * 
 * @author solomon
 * @version 110035  2011-4-16
 */

public class ToperationBindingsSerializer extends JsonSerializer<ToperationBindings>
{

//   private static final Logger log = LoggerFactory.getLogger(ToperationBindingsSerializer.class.getName());
   /**
    * {@inheritDoc}
    * 
    * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator,
    *      org.codehaus.jackson.map.SerializerProvider)
    */
   @Override
   public void serialize(ToperationBindings value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
   {
      List<ToperationBinding> binds = value.getOperationBinding();
      if (DataUtil.isNotNullAndEmpty(binds))
      {
         jgen.writeStartArray();
         for (ToperationBinding bind : binds)
         {
            /*
             * try { Map<String, PropertyDescriptor> props = DataUtil.getPropertyDescriptors(bind); for (String field :
             * props.keySet()) { jgen.writeStartObject(); if(filt(field)){ PropertyDescriptor pd = props.get(field);
             * Method method= pd.getReadMethod(); if ( method != null ){ Object fieldValue = method.invoke(bind); if
             * (fieldValue != null) { jgen.writeFieldName(field); jgen.writeObject(fieldValue); } } } } } catch
             * (Exception e) { log.warn("serialize failed", e); } finally { jgen.writeEndObject(); }
             */
            jgen.writeObject(bind);

         }
         jgen.writeEndArray();
      }
   }

   protected boolean filter(String key)
   {
      String[] keeped = { "callbackParam", "dataFormat", "dataProtocol", "", "dataTransport", "dataURL", "defaultParams", "operationId",
         "operationType", "recordName", "recordXPath", "useFlatFields", "spoofResponses", "responseDataSchema", "useHttpProxy", "wsOperation",
         "xmlNamespaces" };
      for (String value : keeped)
      {
         if (key.equals(value))
            return true;
      }
      return false;
   }

}
