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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-15
 */

public class TopHeader extends HLayout
{

   public static final int HEIGHT = 58;

   public static final String LOGO = "logo.png";

   public static final int LOGO_WIDTH = 61;

   public static final int LOGO_HEIGHT = 55;

   private static final int LOGO_NAME_WIDTH = 180;

   private static final String WEST_WIDTH_P = "50%";

   private static final String EAST_WIDTH_P = "50%";

   private ModuleContainer container;

 
   public TopHeader()
   {
      super();
      this.setStyleName("slx-MoudleContainer");
      this.setHeight(HEIGHT);
      // ImageButton m1= new ImageButton();
      Img logo = new Img(LOGO, LOGO_WIDTH, LOGO_HEIGHT);
      logo.setPrompt("This is the solmix project test page!");
      VLayout logoName = new VLayout();
      logoName.setStyleName("slx_logoName");
      logoName.setWidth(LOGO_NAME_WIDTH);

      HLayout westLayout = new HLayout();
      westLayout.setHeight(HEIGHT);
      westLayout.setWidth(WEST_WIDTH_P);
      westLayout.addMember(logo);
      westLayout.addMember(logoName);

      this.addMember(westLayout);

      HLayout eastLayout = new HLayout();
      eastLayout.setAlign(Alignment.RIGHT);
      eastLayout.setWidth(EAST_WIDTH_P);

      if(container!=null)
      eastLayout.addMember(container);

      this.addMember(eastLayout);
   }

   public ModuleContainer getModuleContainer()
   {
      return container;
   }
   public void setModuleContainer(ModuleContainer container){
      if(container!=null)
      this.addMember(container);
   }
 
}
