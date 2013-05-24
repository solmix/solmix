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

package org.solmix.web.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-16
 */

public class ModuleContainer extends ToolStrip
{

   private static final int HEIGHT = 58;
   private Map<String, ModuleButton> moduleButtons;
   public ModuleContainer()
   {
      super();
      this.setStyleName("slx-MoudleContainer");
      this.setWidth100();
      this.setHeight(HEIGHT);
      this.setAlign(Alignment.RIGHT);
      moduleButtons= new HashMap<String,ModuleButton> ();

   }
   public void addModuleButton(ModuleButton button){
      moduleButtons.put(button.getModuleName(), button);
      this.addButton(button);
   }
   public Map<String,ModuleButton> getModuleButtons(){
      return moduleButtons;
   }
}
