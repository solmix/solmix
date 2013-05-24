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

package org.solmix.web.client.widgets.controls;

import org.solmix.web.client.Solmix;
import org.solmix.web.client.widgets.AbstractFactory;

import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * 
 * @author Administrator
 * @version $Id$ 2011-8-7
 */

public class PanelContextMenu extends Menu
{

   public static class Factory extends AbstractFactory
   {

      /**
       * {@inheritDoc}
       * 
       * @see org.solmix.web.client.WidgetFactory#create()
       */
      @Override
      public Canvas create()
      {
         TabSet mainTabSet = null;
         if (this.parameters == null || !(this.parameters[0] instanceof TabSet))
         {
            this.desc = "create this widget need a tabset panel";
            return null;
         }
         mainTabSet = (TabSet) parameters[0];
         PanelContextMenu menu = new PanelContextMenu(mainTabSet);
         return menu;
      }

   }

   private  TabSet mainTabSet = null;

   private String[] args;

   /**
    * @param mainTabSet
    */
   public PanelContextMenu(final TabSet mainTabSet, String[] args)
   {
      this.mainTabSet = mainTabSet;
      this.args = args;
      this.setWidth(140);
      MenuItemIfFunction enableCondition = new MenuItemIfFunction() {

         public boolean execute(Canvas target, Menu menu, MenuItem item)
         {
            int selectedTab = mainTabSet.getSelectedTabNumber();
            return selectedTab != 0;
         }
      };

      MenuItem closeItem = new MenuItem(Solmix.getMessages().contextMenu_closeCur());
      closeItem.setEnableIfCondition(enableCondition);
      closeItem.setKeyTitle("Alt+C");
      KeyIdentifier closeKey = new KeyIdentifier();
      closeKey.setAltKey(true);
      closeKey.setKeyName("C");
      closeItem.setKeys(closeKey);
      closeItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

         public void onClick(MenuItemClickEvent event)
         {
            int selectedTab = mainTabSet.getSelectedTabNumber();
            mainTabSet.removeTab(selectedTab);
            mainTabSet.selectTab(selectedTab - 1);
         }
      });

      MenuItem closeAllButCurrent = new MenuItem(Solmix.getMessages().contextMenu_closeOther());
      closeAllButCurrent.setEnableIfCondition(enableCondition);
      closeAllButCurrent.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

         public void onClick(MenuItemClickEvent event)
         {
            int selected = mainTabSet.getSelectedTabNumber();
            Tab[] tabs = mainTabSet.getTabs();
            int[] tabsToRemove = new int[tabs.length - 2];
            int cnt = 0;
            for (int i = 1; i < tabs.length; i++)
            {
               if (i != selected)
               {
                  tabsToRemove[cnt] = i;
                  cnt++;
               }
            }
            mainTabSet.removeTabs(tabsToRemove);
         }
      });

      MenuItem closeAll = new MenuItem(Solmix.getMessages().contextMenu_closeAll());
      closeAll.setEnableIfCondition(enableCondition);
      closeAll.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

         public void onClick(MenuItemClickEvent event)
         {
            Tab[] tabs = mainTabSet.getTabs();
            int[] tabsToRemove = new int[tabs.length - 1];

            for (int i = 1; i < tabs.length; i++)
            {
               tabsToRemove[i - 1] = i;
            }
            mainTabSet.removeTabs(tabsToRemove);
            mainTabSet.selectTab(0);
         }
      });

      this.setItems(closeItem, closeAllButCurrent, closeAll);
   }

   public PanelContextMenu(TabSet mainTabSet)
   {
      this(mainTabSet, null);
   }

}
