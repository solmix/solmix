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

package org.solmix.web.client.sandbox.view;

import java.util.ArrayList;
import java.util.List;

import org.solmix.web.client.data.ExplorTreeNode;
import org.solmix.web.client.sandbox.presenter.SandBoxPresenter.MyView;
import org.solmix.web.client.sandbox.view.handlers.SandBoxUiHandlers;
import org.solmix.web.client.widgets.NavigatorTreeGrid;
import org.solmix.web.client.widgets.SideNavigator;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.LeafClickEvent;
import com.smartgwt.client.widgets.tree.events.LeafClickHandler;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-16
 */

public class SandBoxView extends ViewWithUiHandlers<SandBoxUiHandlers> implements MyView
{

   private static final String NAV_WIDTH = "20%";

   private static final String CONTENT_WIDTH = "80%";

   private final HLayout mainWidget;

   private final SideNavigator westNav;

   private final NavigatorTreeGrid menuGrid;

   private final TabSet mainTabSet;


   @Inject
   public SandBoxView(SideNavigator nav)
   {
      mainWidget = new HLayout();
      mainTabSet = new TabSet();
      this.westNav = nav;
      menuGrid = new NavigatorTreeGrid();
      bindCustomUiHandlers();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.View#asWidget()
    */
   public Widget asWidget()
   {
      return mainWidget;
   }

   protected void bindCustomUiHandlers()
   {
      initialTabSet();

      initialSideNavigator();
      westNav.addMember(menuGrid);
      VLayout _westWapper = new VLayout();
      _westWapper.setWidth(NAV_WIDTH);
      _westWapper.addMember(westNav);
      _westWapper.setShowResizeBar(true);
      mainWidget.addMember(_westWapper);

      VLayout _eastWapper = new VLayout();
      _eastWapper.addMember(mainTabSet);
      _eastWapper.setWidth(CONTENT_WIDTH);

      mainWidget.addMember(_eastWapper);

   }

   protected void initialTabSet()
   {
      mainTabSet.setWidth100();
      mainTabSet.setHeight100();
      Layout paneContainerProperties = new Layout();
      paneContainerProperties.setLayoutMargin(0);
      paneContainerProperties.setLayoutTopMargin(1);
      mainTabSet.setPaneContainerProperties(paneContainerProperties);
      mainTabSet.setBackgroundColor("#b3caec");
//      Tab tab = new Tab();
//      tab.setTitle("new ");
//      VLayout t = new VLayout();
//      tab.setPane(t);
////      Tab main = new Tab();
////      main.setID(SandBoxTokens.MAIN+"_tab");
////      main.setTitle("main");
////      main.setPane(new Layout());
//      mainTabSet.addTab(tab);
   }

   protected void initialSideNavigator()
   {

   }


   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.sandbox.presenter.SandBoxPresenter.MyView#getMenuGrid()
    */
   public NavigatorTreeGrid getMenuGrid()
   {
      return menuGrid;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.sandbox.presenter.SandBoxPresenter.MyView#initMenuGrid(org.solmix.web.shared.result.MenuConfigResult)
    */
   public void initMenuGrid(MenuConfigResult result)
   {
      menuGrid.setInitialed(true);
      menuGrid.addLeafClickHandler(new LeafClickHandler() {

         public void onLeafClick(LeafClickEvent event)
         {
            TreeNode node = event.getLeaf();
            ExplorTreeNode menuNode = ((ExplorTreeNode) node);
            getUiHandlers().onItemClick(menuNode);

         }

      });
      Tree tree = new Tree();
      tree.setModelType(TreeModelType.PARENT);
      tree.setNameProperty("name");
      tree.setOpenProperty("isOpen");
      tree.setIdField("nodeID");
      tree.setParentIdField("parentNodeID");
      tree.setRootValue("root");
      List<MenuConfigBean> menus = result.getResults();
      if(menus==null||menus.size()<=0)
         return;
      List<MenuConfigBean> nodes=new ArrayList<MenuConfigBean>();
      for(MenuConfigBean menu:menus){
         if(!"MAIN".equals(menu.getViewType()))
            nodes.add(menu);
      }
      ExplorTreeNode[] treeNodes = new ExplorTreeNode[nodes.size()];
      int count = 0;
      for (MenuConfigBean i : nodes)
      {
        
         treeNodes[count] = new ExplorTreeNode(i.getTitle(), i.getId(), i.getParentId(), i.getIcon(), true, "",  i.getUrl());
         count++;
      }
      tree.setData(treeNodes);
      menuGrid.setData(tree);

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.sandbox.presenter.SandBoxPresenter.MyView#getMainTabSet()
    */
   public TabSet getMainTabSet()
   {
      return mainTabSet;
   }
}
