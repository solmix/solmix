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
package com.solmix.commons.naming;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * @version 110035
 */
public class JNDIHelpler
{
   public static <T> Object findService(Class<T> clz) throws IOException
   {
      try {
         InitialContext ic = new InitialContext();

         return ic.lookup("osgi:service/" + clz.getName());
      } catch (NamingException e) {
         e.printStackTrace();
         IOException ioe = new IOException(clz.getName() + " service resolution failed");
         ioe.initCause(e);
         throw ioe;
      }

   }

   public static void main(String args[])
   {
      try {
         JNDI a = (JNDI) findService(JNDI.class);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
