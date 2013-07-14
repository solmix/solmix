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
package org.solmix.commons.naming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @version 110035
 */
public class JNDISearchResult
{
   private static Logger log = LoggerFactory.getLogger(JNDISearchResult.class.getName());

   private final String prefix;

   private final String name;

   private final String path;

   private final String className;

   private final Object obj;

   public JNDISearchResult(String prefix, String name, String path, String className, Object obj)
   {
      this.prefix = prefix;
      this.name = name;
      this.path = path;
      this.className = className;
      this.obj = obj;
   }

   public String getPrefix()
   {
      return prefix;
   }

   public String getName()
   {
      return name;
   }

   public String getPath()
   {
      return path;
   }

   public String getClassName()
   {
      return className;
   }

   public Object getObject()
   {
      return obj;
   }

}
