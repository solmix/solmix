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

import org.solmix.web.client.Solmix;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-17
 */

public class SideNavigator extends VLayout
{
private static final int LABEL_HEIGHT=25;
   public SideNavigator(){
      Label navHead= new Label();
      navHead.setWidth100();
      navHead.setHeight(LABEL_HEIGHT);
//      navHead.setBackgroundColor("red");
      navHead.setStyleName("slx_SideNavigator-Header-Label");
      navHead.setContents(Solmix.getMessages().sandBox_workspace());
      
      
      this.addMember(navHead);
   }
}
