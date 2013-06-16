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

package com.solmix.showcase.basic.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.MenuBar;
import com.smartgwt.client.widgets.tab.TabSet;
import com.solmix.showcase.basic.client.presenter.BasicPresenter.MyView;
import com.solmix.showcase.basic.client.view.handler.BasicUiHandlers;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-6-1
 */

public class BasicView extends ViewWithUiHandlers<BasicUiHandlers> implements MyView
{

    HLayout menuLayout;
    private final VLayout mainWidget;

    private final TabSet mainTabSet;

    private final String CONTENT_WIDTH = "100%";

    private static final int SHADOW_DEPTH = 10;

    private static final String ICON_PREFIX = "menu/16/";

    public static final String ICON_SUFFIX = ".png";

    @Inject
    BasicView()
    {
        this.mainWidget = new VLayout();
        mainWidget.setBackgroundColor("red");
        mainTabSet = new TabSet();
        initialTabSet();
    }

    /**
    * 
    */
    protected void initialTabSet() {
        mainTabSet.setWidth100();
        mainTabSet.setHeight100();
        Layout paneContainerProperties = new Layout();
        paneContainerProperties.setLayoutMargin(0);
        paneContainerProperties.setLayoutTopMargin(1);
        mainTabSet.setPaneContainerProperties(paneContainerProperties);
        mainTabSet.setBackgroundColor("#b3caec");
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.View#asWidget()
     */
    @Override
    public Widget asWidget() {
        return mainWidget;
    }

 
    @Override
    public void init(RecordList result) {
        menuLayout = new HLayout();
        menuLayout.setStyleName("slx_menu_control_bar");
        menuLayout.setHeight(27);
        menuLayout.setBackgroundColor("green");
        MenuBar menuBar = new MenuBar();
       /* if (result == null || result.getResults() == null)
            return;
        List<Menu> menus=new ArrayList<Menu>();
        for (MenuConfigBean bean : result.getResults()) {
            if ("0".equals(bean.getParentId())) {
                Menu menu = getMenu(bean, result.getResults());
               if(menu!=null)
                   menus.add(menu);
            }
        }
        Menu[] ms= new Menu[menus.size()];
        for(int i=0;i<ms.length;i++){
            ms[i]=menus.get(i);
        }
        menuBar.addMenus(ms, 0);*/
        menuLayout.addMember(menuBar);
        mainWidget.addMember(menuLayout);
        VLayout _southWapper = new VLayout();
        _southWapper.addMember(mainTabSet);
        _southWapper.setWidth(CONTENT_WIDTH);
        mainWidget.addMember(_southWapper);
    }

   /* *//**
     * @param bean
     * @param results
     *//*
    private Menu getMenu(MenuConfigBean bean, List<MenuConfigBean> results) {
        Menu menu = createMenu(bean);
        List<MenuItem> items = getSubMenu(bean, results);
        if (items != null) {
            for (MenuItem item : items) {
                menu.addItem(item);
            }
        }
        return menu;

    }

    public  String getIconString(String icon) {
        String name = icon.replace("\\W", "");
        return ICON_PREFIX + name + ICON_SUFFIX;
    }

    protected List<MenuItem> getSubMenu(MenuConfigBean mbean, List<MenuConfigBean> beans) {
        if (beans == null || mbean == null)
            return null;
        List<MenuItem> subMenus = null;
        for (MenuConfigBean m : beans) {
            if (m.getParentId() != null && m.getParentId().equals(mbean.getId())) {
                if (subMenus == null)
                    subMenus = new ArrayList<MenuItem>();
                List<MenuItem> sub = getSubMenu(m, beans);
                MenuItem item = createMenuItem(m);
                if (sub != null) {
                    Menu menu = createMenu(m);
                    for (MenuItem i : sub) {
                        menu.addItem(i);
                    }
                    item.setSubmenu(menu);
                }
                subMenus.add(item);
            }
        }
        return subMenus;
    }

    public Menu createMenu(MenuConfigBean bean) {
        Menu menu = new Menu();
        menu.setTitle(bean.getTitle());
//        menu.setID(bean.getId());
        menu.setShowShadow(true);
        menu.setWidth(80);
        menu.setShadowDepth(SHADOW_DEPTH);
//        menu.setHeight(27);
//        menu.setAnimateRectTime(50);
        return menu;
    }

    public MenuItem createMenuItem(MenuConfigBean m) {
        MenuItem item = new MenuItem();
        item.setTitle(m.getTitle() == null ? m.getUrl() : m.getTitle());
        if (m.getIcon() != null)
            item.setIcon(getIconString(m.getIcon()));
        final String place = m.getUrl();
        item.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                getUiHandlers().onItemClick(place);
            }

        });
        return item;

    }*/


   
    @Override
    public TabSet getMainTabSet() {
        return mainTabSet;
    }

  
}
