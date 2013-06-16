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
package com.solmix.showcase.client.widgets;

import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StretchImgButton;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-16
 */

public class ModuleButton extends StretchImgButton
{
   private static final int HEIGHT=58;
   public ModuleButton(){
      super();
      this.setHeight(HEIGHT);
      this.setWidth(88);
//      this.setBackgroundImage("modulebutton/background.png");
      this.setTitleStyle("moduleButtonTitle");
      this.setValign(VerticalAlignment.BOTTOM);
   }
   public void setModuleName(String moduleName){
      this.setAttribute("slx_module_name", moduleName,false);
   }
   
   public String getModuleName(){
      return this.getAttribute("slx_module_name");
   }

}
