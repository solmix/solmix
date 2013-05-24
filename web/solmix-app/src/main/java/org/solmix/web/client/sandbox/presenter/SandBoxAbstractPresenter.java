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
package org.solmix.web.client.sandbox.presenter;

import java.util.List;

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.data.ExplorTreeNode;
import org.solmix.web.client.sandbox.SandBoxTokens;
import org.solmix.web.shared.action.MenuConfigAction;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-19
 * @param <V>
 */

public abstract class SandBoxAbstractPresenter<V extends View,P extends Proxy<?>> extends Presenter<V,P>
{
private Menu contextMenu;
private final DispatchAsync dispatch;
   /**
    * @param eventBus
    * @param viewd
    * @param proxy
    */
   public SandBoxAbstractPresenter(EventBus eventBus, V view, P proxy,DispatchAsync dispatcher)
   {
      super(eventBus, view, proxy);
     this.dispatch=dispatcher;
   }
   protected void addOrSelectTabPanel(String token){
      Tab tab = null;
      final String  name =SandBoxTokens.getNameByToken(token);
      final TabSet mainTabSet = SandBoxPresenter.getMainTabSet();
      if(contextMenu==null)
         contextMenu = createContextMenu(mainTabSet);
      if(SandBoxPresenter.getMenuGrid()!=null&&!SandBoxPresenter.getMenuGrid().isInitialed()){
         dispatch.execute(new MenuConfigAction(NameTokens.SandBox), new AsyncCallback<MenuConfigResult>() {

            public void onFailure(Throwable caught)
            {
               // TODO Auto-generated method stub

            }

            public void onSuccess(MenuConfigResult result)
            {
               SandBoxPresenter.getSandBoxView().initMenuGrid(result);
               List<MenuConfigBean> menus = result.getResults();
               for(MenuConfigBean menu:menus){
                  if(name!=null&&name.equals(menu.getUrl())){
                     String url=menu.getUrl();
                     String tabID= url+"_tab";
                    Tab tab = mainTabSet.getTab(tabID);

                     if (tab == null)
                     {
                        tab = new Tab();
                        tab.setID(tabID);
                        tab.setContextMenu(contextMenu);
                        String title = menu.getTitle()==null?"":menu.getTitle().trim();
                        String icon = menu.getIcon();
                        if (icon == null)
                        {
                           icon = "silk/plugin.png";
                        }
                        String imgHTML = Canvas.imgHTML(icon, 16, 16);
                        tab.setTitle("<span>" + imgHTML + "&nbsp;" + title + "</span>");
                        if (url != SandBoxTokens.MAIN)
                           tab.setCanClose(true);
                        if(tab!=null){
                           VLayout _wapper = new VLayout();
                           _wapper.addMember(getView().asWidget());
                           tab.setPane(_wapper);
                           mainTabSet.addTab(tab);
                           }
                       
                        mainTabSet.selectTab(tab);
                     }else {
                        mainTabSet.selectTab(tab);
                     }
                  }
               }

            }

         });
      }//end check menugrid is initial.
      else{
      ExplorTreeNode node= SandBoxPresenter.getMenuGrid().getNode(name);
       String url = node.getUrlMark();
      
      String tabID = url + "_tab";
      if (url != null && !("").equals(url.trim()))
      {
         tab = mainTabSet.getTab(tabID);

         if (tab == null)
         {
            tab = new Tab();
            tab.setID(tabID);
            tab.setContextMenu(contextMenu);
           String  title = node.getName()==null?"":node.getName().trim();
            String icon = node.getIcon();
            if (icon == null)
            {
               icon = "silk/plugin.png";
            }
            String imgHTML = Canvas.imgHTML(icon, 16, 16);
            tab.setTitle("<span>" + imgHTML + "&nbsp;" + title + "</span>");
            if (url != SandBoxTokens.MAIN)
               tab.setCanClose(true);
            if(tab!=null){
               VLayout _wapper = new VLayout();
               _wapper.addMember(getView().asWidget());
               tab.setPane(_wapper);
               mainTabSet.addTab(tab);
               }
           
            mainTabSet.selectTab(tab);
         }else {
            mainTabSet.selectTab(tab);
         }
      }
      }
   }
   protected Menu createContextMenu( final TabSet mainTabSet)
   {
      Menu menu = new Menu();
      menu.setWidth(140);

      MenuItemIfFunction enableCondition = new MenuItemIfFunction() {

         public boolean execute(Canvas target, Menu menu, MenuItem item)
         {
            int selectedTab = mainTabSet.getSelectedTabNumber();
            return selectedTab != 0;
         }
      };

      MenuItem closeItem = new MenuItem("关闭当前窗口");
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

      MenuItem closeAllButCurrent = new MenuItem("关闭其它窗口");
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

      MenuItem closeAll = new MenuItem("关闭所有窗口");
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

      menu.setItems(closeItem, closeAllButCurrent, closeAll);
      return menu;
   }

}
