/*
 * SOLMIX PROJECT
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

package com.solmix.web.box.client.ui;

import com.solmix.web.box.client.MainBox;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class AppBarShell extends AbstractBoxShell
{

    private AppBar appBar;

    private static AppBarShell instance;

    /**
     * Indicate the application bar location.0 is left and 1 is right.
     */
    protected int d = 0;

    private AppBarShell()
    {
        appBar = new AppBar();
        initWidget(appBar);
    }

    public static AppBarShell getInstance(int i) {
        if (instance == null) {
            instance = new AppBarShell();
        }
        // TODO process direction,for example:contextmenu,background-image...
        return instance;

    }

    DecoratedPopupPanel popupPanel;

    Command toRight = new Command() {

        public void execute() {
            moveToRight();
            popupPanel.hide();
        }
    };

    Command toLeft = new Command() {

        public void execute() {
            moveToLeft();
            popupPanel.hide();
        }
    };

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.web.box.client.ui.HasRightClickHandler#addRightClickHandler(com.solmix.web.box.client.ui.RightClickHandler)
     */
    @Override
    public void addRightClickHandler(RightClickHandler handler) {
        appBar.addRightClickHandler(handler);
    }

    public void moveToRight() {
        appBar.moveToRight();
        appBar.setLeft(false);
        DeskSwitchShell.getInstance().moveToLeft();
        d = 1;
    }

    public void moveToLeft() {
        appBar.moveToLeft();
        appBar.setLeft(true);
        DeskSwitchShell.getInstance().moveToRight();
        d = 0;
    }

    public PopupPanel getContextMenu() {
        String selectedImage = "<img src='images/box/selected.png'/>";
        String kg = "&nbsp;&nbsp;";
        popupPanel = new DecoratedPopupPanel(true);
        MenuBar popupMenuBar = new MenuBar(true);
        popupMenuBar.setSize("120px", "100%");
        MenuItem toRightItem = new MenuItem((d == 1 ? selectedImage : kg) + MainBox.getMessages().appBar_context_right(), true, toRight);
        toRightItem.setSize("120px", "100%");
        MenuItem toLeftItem = new MenuItem((d == 0 ? selectedImage : kg) + MainBox.getMessages().appBar_context_left(), true, toLeft);
        toLeftItem.setSize("120px", "100%");
        popupMenuBar.addItem(toLeftItem);
        popupMenuBar.addItem(toRightItem);
        popupPanel.setWidget(popupMenuBar);
        popupPanel.setAnimationEnabled(true);
        return popupPanel;

    }

}
