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
package org.solmix.commons.util;

/**
 * 
 * @version 110035
 */
public final class ValidateUtils
{
   static public void assertTrue(String message, boolean condition) {
      if (!condition)
         fail(message);
   }
   static public void assertTrue(boolean condition) {
      assertTrue(null, condition);
   }
   static public void assertNotNull(String message, Object object) {
      if(object == null)
         throw new NullPointerException(message==null?"":message);
   }
   static public void assertNotNull(Object object) {
      assertNotNull(null, object);
   }
   static public void assertNull(String message, Object object) {
      assertTrue(message, object == null);
   }
   static public void assertNull(Object object) {
      assertNull(null, object);
   }
   static public void fail(String message) {
      throw new AssertionError(message == null ? "" : message);
   }

   /**
    * Fails a test with no message.
    */
   static public void fail() {
      fail(null);
   }
}
