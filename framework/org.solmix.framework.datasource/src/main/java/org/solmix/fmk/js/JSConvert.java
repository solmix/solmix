/*
 * Copyright 2012 The Solmix Project
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
package org.solmix.fmk.js;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.solmix.fmk.util.SLXDate;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-4-11
 */
@SuppressWarnings("unchecked")
public class JSConvert
{

   public static JSConvert instance()
   {
      return new JSConvert();
   }

   private boolean writeNativeDate = false;

   public Object formatData(Object data) throws Exception
   {
      if (data == null)
      {
         return null;
      } else if (data instanceof String)
      {
         return convert(data);
      } else if (data instanceof List<?>)
      {
         List<Object> _return = new ArrayList<Object>();
         for (Object value : (List<?>) data)
         {
            _return.add(convert(value));
         }
         return _return;
      } else if (data instanceof Map<?, ?>)
      {
         Map _return = (Map) data;
         for (Object key : _return.keySet())
         {
            _return.put(key, convert(_return.get(key)));
         }
         return _return;
      } else
      {
         return convert(data);
      }
   }


   public Object convert(Object data)
   {
      if (data instanceof Date)
      {
         return convertDate((Date) data);

      }else if(data instanceof Enum){
         return convertEnum(data);
      } else if (data instanceof List<?>)
      {
         List<Object> _return = new ArrayList<Object>();
         for (Object value : (List<?>) data)
         {
            _return.add(convert(value));
         }
         return _return;
      } else if (data instanceof Map<?, ?>)
      {
         Map _return = (Map) data;
         for (Object key : _return.keySet())
         {
            _return.put(key, convert(_return.get(key)));
         }
         return _return;
      } else
      {
         return data;
      }
   }

   public Object convertDate(Date date)
   {
      if (date instanceof SLXDate)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         String dateArgs = calendar.get(1) + "," + calendar.get(2) + "," + calendar.get(5);
         if (writeNativeDate)
         {
            return "new Date(" + dateArgs + ")";
         } else
         {
            return "Date.parseServerDate(" + dateArgs + ")";
         }
      } else
      {
         return "new Date(" + date.getTime() + ")";
      }
   }
   public static Object convertEnum(Object data){
      return null;
   }
}
