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

import com.solmix.web.box.client.util.SizeUtil;
import com.solmix.web.box.client.util.SizeUtil.Direction;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class BoxMainContainer extends AbsolutePanel implements RequiresResize, ProvidesResize
{

    TaskBarShell taskShell;

    AppBarShell appShell;

    Widget deskShell;

    public BoxMainContainer()
    {
        super();
        SizeUtil.fullWindow(this);
        loadDefault(this);
    }

    private void loadDefault(BoxMainContainer container) {
        container.add(_loadBackGround(), 0, 0);
        deskShell = _loadDeskShell();
        container.add(deskShell, SizeUtil.getAppBarWidth(), 0);
        taskShell = _loadTaskBar();
        container.add(taskShell, 0, 0);
        appShell = _loadAppBar();
        container.add(appShell, 0, DeskSwitchShell.TAB_HEIGHT);
        appShell.addRightClickHandler(new RightClickHandler() {

            @Override
            public void onRightClick(Widget sender, Event event) {
                PopupPanel w = appShell.getContextMenu();
                int x = DOM.eventGetClientX(event);
                int y = DOM.eventGetClientY(event);
                int wx = Window.getClientWidth();
                int wy = Window.getClientHeight();
                x = (x + 130) > wx ? x - 130 : x;
                y = (y + 60) > wy ? y - 60 : y;
                w.setPopupPosition(x, y);
                w.show();
            }

        });
        taskShell.addRightClickHandler(new RightClickHandler() {

            @Override
            public void onRightClick(Widget sender, Event event) {

                Window.alert("11");
            }

        });

        SizeUtil.fullDirection(taskShell, Direction.SOUTH, taskShell.HEIGHT, taskShell.LIMITE_WIDTH);

    }

    /**
     * @return
     */
    private Widget _loadDeskShell() {

//       return DeskSwitchShell.getInstance();
    	TabLayoutPanel tabLayoutPanel = new TabLayoutPanel(1.5, Unit.EM);
		tabLayoutPanel.setAnimationDuration(1000);
		
		HTML htmlHaldfsoefjadfesfse = new HTML("haldfsoefja;dfesfse", true);
		tabLayoutPanel.add(htmlHaldfsoefjadfesfse, "New Widget", false);
		
		TextArea textArea = new TextArea();
		tabLayoutPanel.add(textArea, "New Widget", false);
		
		SimpleCheckBox simpleCheckBox = new SimpleCheckBox();
		tabLayoutPanel.add(simpleCheckBox, "New Widget", false);
		
		HTML htmlAsfsfe = new HTML("asfsfe123123123123132123132123123", true);
		tabLayoutPanel.add(htmlAsfsfe, "New Widget", false);
		return tabLayoutPanel;
    }

    protected Widget _loadBackGround() {
        return BoxBackGround.getInstance();

    }

    protected TaskBarShell _loadTaskBar() {
        TaskBarShell shell = TaskBarShell.getInstance();

        return shell;
    }

    protected AppBarShell _loadAppBar() {
        AppBarShell shell = AppBarShell.getInstance(0);

        return shell;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
     */
    @Override
    public void onResize() {
        SizeUtil.fullWindow(this);
        for (Widget child : getChildren()) {
            if (child instanceof RequiresResize) {
                ((RequiresResize) child).onResize();
            }
        }

    }

}
