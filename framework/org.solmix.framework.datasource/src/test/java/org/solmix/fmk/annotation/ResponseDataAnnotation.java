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
package org.solmix.fmk.annotation;

import java.lang.reflect.Field;
import java.util.Map;

import org.solmix.api.data.DSResponseData;
import org.solmix.api.datasource.annotation.ResponseData;
import org.solmix.commons.util.DataUtil;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035  2011-2-3 solmix-ds 
 */
public class ResponseDataAnnotation
{

   public static void main( String[] args ) throws Exception
   {
      try
      {
         test();
      } catch ( SecurityException e )
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( IllegalArgumentException e )
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch ( IllegalAccessException e )
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public static void test() throws Exception
   {
      DSResponseData data = new DSResponseData();
      data.setIsDSResponse(true);
      data.getClass().isAnnotation();
      Field[] _fields = data.getClass().getDeclaredFields();
      for ( Field f : _fields )
      {
         if ( f.getAnnotation( ResponseData.class ) != null )
         {

         }
      }
      Map map = DataUtil.annotationFilter( data, ResponseData.class );
      System.out.println(map);
   }
}
