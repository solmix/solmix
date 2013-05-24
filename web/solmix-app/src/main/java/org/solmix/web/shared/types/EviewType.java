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
package org.solmix.web.shared.types;



/**
 * 
 * @author solomon
 * @version $Id$  2011-6-1
 */

public enum EviewType
{
   SECTION_HEADER("sbar"),SECTION_ITEM("item"),TREE("tree"),MAIN("main"),MENUITEM("menuitem"),MENUITEM_SEP("menuitem_sep"),MENU("menu");

   private final String value;

   EviewType(String v) {
       value = v;
   }

   public String value() {
       return value;
   }

   public static EviewType fromValue(String v) {
       for (EviewType c: EviewType.values()) {
           if (c.value.equals(v)) {
               return c;
           }
       }
       throw new IllegalArgumentException(v);
   }
}
