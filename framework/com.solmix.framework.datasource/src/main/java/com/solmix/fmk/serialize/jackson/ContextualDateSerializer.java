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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ContextualSerializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;


/**
 * 
 * @author solomon
 * @version 110035  2011-4-11
 */

public class ContextualDateSerializer extends JsonSerializer<Date> implements ContextualSerializer<Date>
{

   protected final DateFormat format;

   protected final String prefix;

   protected final String suffix;

   private boolean k_slxdate;

   public ContextualDateSerializer(String format, String prefix, String suffix)
   {
      this.format = (format == null) ? null : new SimpleDateFormat(format);
      this.prefix = prefix == null ? "" : prefix;
      this.suffix = suffix == null ? "" : suffix;
   }
   public ContextualDateSerializer(String prefix, String suffix)
   {
      this(null, prefix, suffix);
   }

   public ContextualDateSerializer(String prefix, String suffix, boolean slxDate)
   {
      this(null, prefix, suffix);
      k_slxdate = slxDate;
   }
   /**
    * {@inheritDoc}
    * 
    * @see org.codehaus.jackson.map.JsonSerializer#serialize(java.lang.Object, org.codehaus.jackson.JsonGenerator,
    *      org.codehaus.jackson.map.SerializerProvider)
    */
   @Override
   public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException
   {
      if (format == null)
      {
         if (k_slxdate)
         {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            String dateArgs = (new StringBuilder()).append(calendar.get(1)).append(",").append(calendar.get(2)).append(",").append(calendar.get(5)).toString();
            jgen.writeRawValue((new StringBuilder()).append(prefix).append(dateArgs).append(suffix).toString());
         } else
         // provider.defaultSerializeDateValue(value, jgen);
         jgen.writeRawValue((new StringBuilder()).append(prefix).append(value.getTime()).append(suffix).toString());
      } else
      {
         jgen.writeRawValue((new StringBuilder()).append(prefix).append(format.format(value)).append(suffix).toString());
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.codehaus.jackson.map.ContextualSerializer#createContextual(org.codehaus.jackson.map.SerializationConfig,
    *      org.codehaus.jackson.map.BeanProperty)
    */
   @Override
   public JsonSerializer<Date> createContextual(SerializationConfig config, BeanProperty property) throws JsonMappingException
   {
      // CustomDateFormat ann = property.getAnnotation(CustomDateFormat.class);
      // if (ann == null)
      // { // but if missing, default one from class
      // ann = property.getContextAnnotation(CustomDateFormat.class);
      // }
      // // If no customization found, just return base instance (this); no need to construct new serializer
      // String format = (ann == null) ? null : ann.format();
      // String prefix = (ann == null) ? null : ann.prefix();
      // String suffix = (ann == null) ? null : ann.suffix();
      // if (ann == null || ann.format().length() == 0)
      // {
      // return this;
      // }
      // return new ContextualDateSerializer(format, prefix, suffix);
      return this;
   }

}
