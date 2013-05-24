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

import java.util.Map;

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.WidgetFactory;
import org.solmix.web.client.data.ControlBox;
import org.solmix.web.client.data.DSRepo;
import org.solmix.web.client.data.ExplorTreeNode;
import org.solmix.web.client.data.MenuBox;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.client.sandbox.view.handlers.SandBoxUiHandlers;
import org.solmix.web.client.widgets.ModuleButton;
import org.solmix.web.client.widgets.ModuleContainer;
import org.solmix.web.client.widgets.NavigatorTreeGrid;
import org.solmix.web.shared.action.MenuConfigAction;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-16
 */

public class SandBoxPresenter extends Presenter<SandBoxPresenter.MyView, SandBoxPresenter.MyProxy> implements SandBoxUiHandlers
{

   /**
    * Child presenters can fire a RevealContentEvent with TYPE_SetModuleContent to set themselves as children of this
    * presenter.
    */
   @ContentSlot
   public static final Type<RevealContentHandler<?>> TYPE_show_sandbox = new Type<RevealContentHandler<?>>();

   private static PlaceManager placeManager;

   private final DispatchAsync dispatcher;

   private static NavigatorTreeGrid menuGrid;

   private static TabSet mainTabSet;

   private static MenuConfigResult result = null;

   private Menu contextMenu;

   private static MyView view;

   private static final String MAIN = "main";

   private static final String HISTORY_TOKEN = "historyToken";

   private static boolean initMain = false;

   /**
    * @param eventBus
    * @param view
    * @param proxy
    */
   @Inject
   public SandBoxPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager, DispatchAsync dispatcher)
   {
      super(eventBus, view, proxy);
      getView().setUiHandlers(this);
      SandBoxPresenter.placeManager = placeManager;
      this.dispatcher = dispatcher;
      SandBoxPresenter.menuGrid = getView().getMenuGrid();
      SandBoxPresenter.mainTabSet = getView().getMainTabSet();
      SandBoxPresenter.view = view;
   }

   public static MyView getSandBoxView()
   {
      return view;
   }

   /**
    * 
    * @author solomon
    * @version $Id$ 2011-5-16
    */

   public interface MyView extends View, HasUiHandlers<SandBoxUiHandlers>
   {

      NavigatorTreeGrid getMenuGrid();

      TabSet getMainTabSet();

      void initMenuGrid(MenuConfigResult result);

   }

   /**
    * 
    * @author solomon
    * @version $Id$ 2011-5-16
    */
   @ProxyCodeSplit
   @NameToken(NameTokens.SandBox)
   public interface MyProxy extends Proxy<SandBoxPresenter>, Place
   {

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
    */
   @Override
   protected void revealInParent()
   {
      RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetModuleContent, this);

   }

   @Override
   protected void onBind()
   {
      super.onBind();
      DSRepo.loadByModuleName(getModuleName());
      initMenuAddSelectTab(MAIN, false);
   }

   public String getModuleName(){
       return NameTokens.SandBox;
   }
   @Override
   public void prepareFromRequest(PlaceRequest placeRequest)
   {
      String action = placeRequest.getParameter("action", MAIN);
      if (action == null || action.length() == 0)
         action = MAIN;
      initMenuAddSelectTab(action, true);
   }

   @Override
   protected void onReveal()
   {
      super.onReveal();
      ModuleContainer container = MainPagePresenter.getModuleContainer();
      Map<String, ModuleButton> buttons = container.getModuleButtons();
      ModuleButton curBt = null;
      for (String key : buttons.keySet())
      {
         if (key.equals(NameTokens.SandBox))
         {
            curBt = buttons.get(key);
         }else{
            buttons.get(key).setSelected(false);
         }
      }
      if (curBt != null)
         curBt.setSelected(true);
      // TO MAIN PAGE.

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.sandbox.view.handlers.SandBoxUiHandlers#onItemClick()
    */
   public void onItemClick(ExplorTreeNode node)
   {
      String place = node.getUrlMark();
      if (place != null && place.length() != 0)
      {
         PlaceRequest req = new PlaceRequest(NameTokens.SandBox).with(NameTokens.ACTION, place);
         placeManager.revealPlace(req);

      }

   }

   // private boolean isCreated(ExplorTreeNode node){
   // getView().getMainTabSet();
   // }

   public static NavigatorTreeGrid getMenuGrid()
   {
      return menuGrid;
   }

   public static TabSet getMainTabSet()
   {
      return mainTabSet;
   }

   private void addOrSelectTab(String name)
   {
      if (result == null || result.getResults() == null || name == null)
         return;
      MenuConfigBean selectedMenu = null;
      for (MenuConfigBean menu : result.getResults())
      {
         if (name.equals(menu.getUrl()))
         {
            selectedMenu = menu;
            break;
         }
      }
      if (selectedMenu != null)
      {
         WidgetFactory factory = MenuBox.getFactory(NameTokens.SandBox, name);
         if (factory == null)
            return;
         if (contextMenu == null)
            contextMenu = createContextMenu(mainTabSet);

         String url = factory.getID();
         String tab_id = url + "_tab";
         Tab tab = mainTabSet.getTab(tab_id);
         if (tab == null)
         {
            Canvas panel = factory.create();
            tab = new Tab();
            tab.setID(factory.getID() + "_tab");
            tab.setAttribute(HISTORY_TOKEN, name);
            String title = selectedMenu.getTitle();
            if (title == null)
               title = "&nbsp;&nbsp;&nbsp;&nbsp;";
            String icon = selectedMenu.getIcon();
            if (icon == null)
               icon = "plugin";
            else
               icon = ExplorTreeNode.iconPrefix + icon + ExplorTreeNode.iconSuffix;
            String imgHTML = Canvas.imgHTML(icon, 16, 16);
            tab.setTitle("<span>" + imgHTML + "&nbsp;" + title + "</span>");
            if (!"main".equals(name))
            {
               tab.setContextMenu(contextMenu);
               tab.setCanClose(true);
            }
            tab.addTabSelectedHandler(new TabSelectedHandler() {

               @Override
               public void onTabSelected(TabSelectedEvent event)
               {
                  Tab tab = event.getTab();
                  String token = tab.getAttributeAsString(HISTORY_TOKEN);
                  String currentToken = placeManager.getCurrentPlaceRequest().getParameter(NameTokens.ACTION, null);
                  if (token != currentToken)
                  {
                     PlaceRequest req = new PlaceRequest(NameTokens.SandBox).with(NameTokens.ACTION, token);
                     placeManager.revealPlace(req);
                  }
               }

            });
            tab.setPane(panel);
            if (MAIN.equals(name))
            {
               mainTabSet.addTab(tab, 0);
               initMain = true;
            } else
            {
               if (!initMain)
                  initMenuAddSelectTab(MAIN, false);
               mainTabSet.addTab(tab);
            }
            mainTabSet.selectTab(tab);
         } else
         {
            mainTabSet.selectTab(tab);
         }
      }
   }

   protected void initMenuAddSelectTab(final String name, boolean iniData)
   {
      if (result == null && iniData)
         dispatcher.execute(new MenuConfigAction(NameTokens.SandBox), new AsyncCallback<MenuConfigResult>() {

            public void onFailure(Throwable caught)
            {
               System.out.println(caught.getMessage());
            }

            public void onSuccess(MenuConfigResult result)
            {
               SandBoxPresenter.result = result;
               view.initMenuGrid(result);
               addOrSelectTab(name);

            }

         });
      else
      {
         addOrSelectTab(name);
      }
   }

   protected Menu createContextMenu(final TabSet mainTabSet)
   {
      Menu menu1 =(Menu)ControlBox.getControl("panel_context_menu", mainTabSet);
      return menu1;
   }
}
