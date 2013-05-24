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
import org.solmix.web.client.data.MenuGridRecord;

import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-31
 */

public class AdminNavigator extends VLayout
{

   private static final String WEST_WIDTH = "20%";

   private static final String SECTION_STACK_WIDTH = "100%";

   private SectionStack sectionStack;

   private static final int LABEL_HEIGHT = 25;

   public AdminNavigator()
   {

      this.setStyleName("slx_manager_navigator");
//      this.setWidth(WEST_WIDTH);

      Label navHead = new Label();
      navHead.setWidth100();
      navHead.setHeight(LABEL_HEIGHT);
      // navHead.setBackgroundColor("red");
      navHead.setStyleName("slx_SideNavigator-Header-Label");
      navHead.setContents(Solmix.getMessages().admin_workspace());

      this.addMember(navHead);

      sectionStack = new SectionStack();
      sectionStack.setWidth(SECTION_STACK_WIDTH);
      sectionStack.setVisibilityMode(VisibilityMode.MUTEX);
      sectionStack.setShowExpandControls(true);
      sectionStack.setAnimateSections(true);

      this.addMember(sectionStack);

   }

   public void addSection(String sectionName, MenuGridRecord[] records)
   {
      sectionStack.addSection(new NavigatorSection(sectionName, records));
   }
   public void addSection(String sectionName, MenuGridRecord[] records,int position)
   {
      sectionStack.addSection(new NavigatorSection(sectionName, records), position);
   }

   public void expandSection(int section)
   {
      sectionStack.expandSection(section);
   }

   public void expandSection(String name)
   {
      sectionStack.expandSection(name);
   }

   public void selectRecord(String name)
   {

      SectionStackSection[] sections = sectionStack.getSections();

      // Log.debug("Number of sections: " + sections.length);

      for (int i = 0; i < sections.length; i++)
      {
         SectionStackSection sectionStackSection = sections[i];

         if (((NavigatorSection) sectionStackSection).getRecord(name) != -1)
         {

            if (!sectionStack.sectionIsExpanded(i))
            {
               // Log.debug("sectionStack.expandSection(i)");
               sectionStack.expandSection(i);
            }

            ((NavigatorSection) sectionStackSection).selectRecord(name);
            break;
         }
      }
   }

   public void addSectionHeaderClickHandler(SectionHeaderClickHandler clickHandler)
   {
      sectionStack.addSectionHeaderClickHandler(clickHandler);
   }

   public void addRecordClickHandler(String sectionName, RecordClickHandler clickHandler)
   {

      // Log.debug("addRecordClickHandler(sectionName, clickHandler) - " + sectionName);

      SectionStackSection[] sections = sectionStack.getSections();

      for (int i = 0; i < sections.length; i++)
      {
         SectionStackSection sectionStackSection = sections[i];

         if (sectionName.contentEquals(sections[i].getTitle()))
         {
            ((NavigatorSection) sectionStackSection).addRecordClickHandler(clickHandler);
         }
      }
   }
}
