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

package org.solmix.web.client.basic.presenter;

import java.util.Map;

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.WidgetFactory;
import org.solmix.web.client.basic.view.handlers.BasicUiHandlers;
import org.solmix.web.client.data.ControlBox;
import org.solmix.web.client.data.DSRepo;
import org.solmix.web.client.data.ExplorTreeNode;
import org.solmix.web.client.data.MenuBox;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.client.widgets.ModuleButton;
import org.solmix.web.client.widgets.ModuleContainer;
import org.solmix.web.shared.action.MenuConfigAction;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.solmix.sgt.client.event.RevealViewHandler;
import com.solmix.sgt.client.event.UrlClickHandler;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-6-1
 */

public class BasicPresenter extends Presenter<BasicPresenter.MyView, BasicPresenter.MyProxy> implements BasicUiHandlers, RevealViewHandler, UrlClickHandler
{

    private final PlaceManager placeManager;

    private final DispatchAsync dispatcher;
    private static boolean initMain = false;
    private String MAIN = "main";
    private Menu contextMenu;
    private static MenuConfigResult result = null;
    private static final String HISTORY_TOKEN = "historyToken";
    private static MyView view;

    private static TabSet mainTabSet;

    /**
     * @param eventBus
     * @param view
     * @param proxy
     */
    @Inject
    public BasicPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,
        DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
        this.placeManager = placeManager;
        this.dispatcher = dispatcher;
        BasicPresenter.mainTabSet = getView().getMainTabSet();
        BasicPresenter.view = view;
    }

    /**
     * 
     * @author solomon
     * @version $Id$ 2011-6-1
     */
    @ProxyCodeSplit
    @NameToken(NameTokens.Basic)
    public interface MyProxy extends Proxy<BasicPresenter>, Place
    {

    }

    /**
     * 
     * @author solomon
     * @version $Id$ 2011-6-1
     */

    public interface MyView extends View, HasUiHandlers<BasicUiHandlers>
    {

        void init(MenuConfigResult result);
        TabSet getMainTabSet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetModuleContent, this);

    }

    @Override
    protected void onBind() {
        super.onBind();
        DSRepo.loadByModuleName(getModuleName());
        initMenuAddSelectTab(MAIN, false);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        ModuleContainer container = MainPagePresenter.getModuleContainer();
        Map<String, ModuleButton> buttons = container.getModuleButtons();
        ModuleButton curBt = null;
        for (String key : buttons.keySet()) {
            if (key.equals(NameTokens.Basic)) {
                curBt = buttons.get(key);
            } else {
                buttons.get(key).setSelected(false);
            }
        }
        if (curBt != null)
            curBt.setSelected(true);
        // TO MAIN PAGE.

    }
    @Override
    public void prepareFromRequest(PlaceRequest placeRequest)
    {
       String action = placeRequest.getParameter("action", MAIN);
       if (action == null || action.length() == 0)
          action = MAIN;
       initMenuAddSelectTab(action, true);
    }
    protected String getModuleName() {
        return NameTokens.Basic;
    }

    protected void initMenuAddSelectTab(final String name, boolean iniData)
    {
       if (result == null && iniData)
          dispatcher.execute(new MenuConfigAction(NameTokens.Basic), new AsyncCallback<MenuConfigResult>() {
             public void onFailure(Throwable caught)
             {
//                System.out.println(caught.getMessage());
             }
             public void onSuccess(MenuConfigResult result)
             {
                 BasicPresenter.result = result;
                view.init(result);
                addOrSelectTab(name);
             }
          });
       else
       {
          addOrSelectTab(name);
       }
    }

    /**
     * @param name
     */
    protected void addOrSelectTab(String name) {
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
            WidgetFactory factory = MenuBox.getFactory(NameTokens.Basic, name);
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
                        PlaceRequest req = new PlaceRequest(NameTokens.Basic).with(NameTokens.ACTION, token);
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

    /**
     * @param mainTabSet2
     * @return
     */
    protected Menu createContextMenu(final TabSet mainTabSet)
    {
       Menu menu =(Menu)ControlBox.getControl("panel_context_menu", mainTabSet);
       return menu;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.basic.view.handlers.BasicUiHandlers#onItemClick(com.smartgwt.client.widgets.menu.Menu)
     */
    @Override
    public void onItemClick(String place) {
      
        if (place != null && place.length() != 0)
        {
           PlaceRequest req = new PlaceRequest(NameTokens.Basic).with(NameTokens.ACTION, place);
           placeManager.revealPlace(req);

        }
    }
}
