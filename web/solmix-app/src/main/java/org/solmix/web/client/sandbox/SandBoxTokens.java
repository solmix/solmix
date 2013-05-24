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

package org.solmix.web.client.sandbox;

import org.solmix.web.client.NameTokens;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-17
 */

public class SandBoxTokens
{

   protected static final String SAND_BOX_PREFIX_2 ="!action=";
   public static final String PREFIX=NameTokens.SandBox+SAND_BOX_PREFIX_2;

   public static final String MENU_CONFIG = PREFIX+"menuconfig";

   public static final String MAIN = PREFIX+"main";
   
   public static String getTokenByName(String name)
   {
      return PREFIX + name;
   }
   public static String getNameByToken(String token){
      if(token.indexOf(SAND_BOX_PREFIX_2)!=-1)
         token = token.substring(token.indexOf(SAND_BOX_PREFIX_2)+SAND_BOX_PREFIX_2.length());
      return token;
   }

}
