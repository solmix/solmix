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
package com.solmix.api.jaxb;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.TdataSource;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035  2010-12-25 solmix-api 
 */
public class JAXBTest
{
   @Test
   public void test()
   {

   }
   public static void main(String args[]) throws SlxException
   {
     
   }

   public static String marshall(Object jaxbObject)
   {

      try {

         JAXBContext jc = JAXBContext.newInstance(TdataSource.class);
         Marshaller marshaller = jc.createMarshaller();

      Writer outputWriter = new StringWriter();
         marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         marshaller.marshal(jaxbObject, outputWriter);

         return outputWriter.toString();

      } catch (Exception e) {

         e.printStackTrace();

      }

      return null;

   }
}
