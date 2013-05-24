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

import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.MenuBar;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-6-1
 */

public class ApplicationMenuBar extends HLayout
{

    private static final int HEIGHT = 27;
private MenuBar bar;

    private int menuPosition;

    public ApplicationMenuBar()
    {
        super();
        this.setStyleName("slx_menu_control_bar");
        this.setHeight(HEIGHT);

         bar = new MenuBar();
//        Menu menu = new Menu();
//        menu.setTitle("No Defined Menu22");
//        menu.addItem(new MenuItem("Item2"));
//        Menu menu1 = new Menu();
//        menu1.setTitle("caidan");
//        menu1.setID("csm");
//        menu1.setShowShadow(true);
//        menu1.setWidth(Solmix.getConstants().default_menu_width());
//        menu1.setShadowDepth(10);
//        MenuItem item = new MenuItem();
//        menu1.addItem(item);
//        Menu[] menus = new Menu[2];
//        menus[0] = menu;
//        menus[1]=menu1;
//        menuBar.addMenus(menus, 0);
        this.addMember(bar);
    }
public MenuBar getMenuBar(){
    return bar;
}


}
