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
package org.solmix.fmk.datasource;

import java.util.LinkedList;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035  2011-2-17 solmix-ds 
 */
public class LinkedListTT
{

   /**
    * @param args
    */
   public static void main(String[] args)
   {
      LinkedList list = new LinkedList();
      list.push("1");
      System.out.println(list.getLast().toString());
      list.push("2");
      System.out.println(list.getFirst().toString());
   }

}
