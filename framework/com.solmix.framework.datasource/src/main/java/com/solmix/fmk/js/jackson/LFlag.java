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
package com.solmix.fmk.js.jackson;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035  2011-2-16 solmix-ds 
 */
public class LFlag
{
   public LFlag(String name, boolean flag)
   {
      this.name = name;
      this.flag = flag;
   }
   private String name;

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name
    *           the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the flag
    */
   public boolean isFlag()
   {
      return flag;
   }

   /**
    * @param flag
    *           the flag to set
    */
   public void setFlag(boolean flag)
   {
      this.flag = flag;
   }

   private boolean flag;
}
