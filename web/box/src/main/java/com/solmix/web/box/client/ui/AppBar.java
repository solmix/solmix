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
import com.solmix.web.box.client.dnd.AppMenuDragController;
import com.solmix.web.box.client.dnd.AppMenuDropController;
import com.solmix.web.box.client.util.SizeUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class AppBar extends VerticalPanel implements HasRightClickHandler, RequiresResize
{

    private AppButtonsLayout buttonsLayout;

    private Widget mainButton;

    private Widget infoArea;

    private Image main;

    VerticalPanel buttons;

    private boolean isLeft = true;

    /**
     * @return the isLeft
     */
    public boolean isLeft() {
        return isLeft;
    }

    /**
     * @param isLeft the isLeft to set
     */
    public void setLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    private RightClickHandler rightClickHandler;

    public AppBar()
    {
        sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
        // setStyleName("slx-AppBar");
        int h = prepareSize();
        setHeight(h + "px");
        loadWidget(this);
        /**
         * This used deference load to fixed IE8 desplay problem。
         */
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            public void execute() {
                moveToLeft();

            }
        });

    }

    private void loadWidget(AppBar bar) {
        mainButton = loadMainButton();
        bar.add(mainButton);
        buttonsLayout = loadAppButtons();
        bar.add(buttonsLayout);
        infoArea = loadInfoArea();
        bar.add(infoArea);
    }

    protected Widget loadMainButton() {
        SimplePanel mainButton = new SimplePanel();
        main = new Image("/images/box/logo.png");
        main.setTitle("Solmix 主菜单");
        // main.setStyleName("slx-AppBar-mainImage");
        main.addMouseOverHandler(new MouseOverHandler() {

            public void onMouseOver(MouseOverEvent event) {
                main.addStyleName("slx-AppBar-mainImage-MouseOver");

            }
        });
        main.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                main.removeStyleName("slx-AppBar-mainImage-MouseOver");

            }
        });
        main.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.alert("click");
                AppBarShell.getInstance(0).moveToRight();

            }

        });
        // mainButton.setStyleName("slx-AppBar-mainButton");
        mainButton.add(main);
        return mainButton;

    }

    protected AppButtonsLayout loadAppButtons() {
        buttonsLayout = new AppButtonsLayout();
        AppMenuDragController dragc = new AppMenuDragController(RootPanel.get());
        buttons = new VerticalPanel();
        buttonsLayout.addRightClickHandler(new RightClickHandler() {

            @Override
            public void onRightClick(Widget sender, Event event) {

            }

        });
        AppButton b1 = AppButton.createButton(dragc, "images/box/10008.png");

        // b1.addClickHandler(new ClickHandler() {
        //
        // @Override
        // public void onClick(ClickEvent event) {
        // Window.alert("abc");
        //
        // }
        //
        // });
        b1.addRightClickHandler(new RightClickHandler() {

            @Override
            public void onRightClick(Widget sender, Event event) {
                // Window.alert("11111");

            }

        });
        AppButton b2 = AppButton.createButton(dragc, "images/box/10002.png");
        b2.addRightClickHandler(new RightClickHandler() {

            @Override
            public void onRightClick(Widget sender, Event event) {
                // Window.alert("22222222222");

            }

        });
        AppButton b3 = AppButton.createButton(dragc, "images/box/10010.png");
        buttons.add(b1);
        buttons.add(b2);
        buttons.add(b3);
        AppMenuDropController dropc = new AppMenuDropController(buttons);
        dragc.registerDropController(dropc);
        buttonsLayout.add(buttons);
        // buttonsLayout.setHorizontalAlignment(ALIGN_CENTER);
        // buttonsLayout.setStyleName("slx-AppBar-buttonsLayout");
        adaptSize(buttonsLayout);
        return buttonsLayout;

    }

    protected void adaptSize(Widget w) {
        int _h = prepareSize();
        _h = _h - Integer.parseInt(MainBox.getConstants().AppBar_TopSize()) * 2;
        w.setSize("76px", _h + "px");
    }

    private int prepareSize() {
        int _h = Window.getClientHeight();
        _h = _h - SizeUtil.getDeskSwitchHeight() - SizeUtil.getTaskBarHeight();
        return _h <= 400 ? 400 : _h;

    }

    protected Widget loadInfoArea() {
        SimplePanel infoArea = new SimplePanel();
        infoArea.setStyleName("slx-AppBar-infoArea");
        return infoArea;

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
     */
    @Override
    public void onResize() {
        int h = prepareSize();
        setHeight(h + "px");
        // adapt buttons Layout;
        adaptSize(buttonsLayout);
        if (!isLeft()) {
            toRightLocation();
        }

    }

    private void toRightLocation() {
        int _w = Window.getClientWidth();
        getElement().getStyle().setLeft(_w - 76, Unit.PX);
    }

    @Override
    public void onBrowserEvent(Event event) {

        // This will stop the event from being propagated to parent elements.
        event.stopPropagation();
        event.preventDefault();
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEUP:
                if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
                    rightClickHandler.onRightClick(this, event);
                }
                break;

            case Event.ONDBLCLICK:

                break;

            case Event.ONCONTEXTMENU:

                break;

            default:
                DomEvent.fireNativeEvent(event, this, this.getElement());
        }// end switch
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.web.box.client.ui.HasRightClickHandler#addRightClickHandler(com.solmix.web.box.client.ui.RightClickHandler)
     */
    @Override
    public void addRightClickHandler(RightClickHandler handler) {
        this.rightClickHandler = handler;

    }

    public void moveToRight() {
        toRightLocation();
        mainButton.setStyleName("slx-AppBar-mainButton-right");
        buttonsLayout.setStyleName("slx-AppBar-buttonsLayout-right");
        infoArea.setStyleName("slx-AppBar-infoArea-right");
        main.setStyleName("slx-AppBar-mainImage-right");
        int i = buttons.getWidgetCount();
        if (i > 0) {
            for (int j = 0; j < i; j++) {
                buttons.getWidget(j).setStyleName("slx-AppBar-buttonsImage-right");
            }
        }
        setHorizontalAlignment(ALIGN_RIGHT);
    }

    /**
     * Move the application menu bar to left.
     */
    public void moveToLeft() {
        getElement().getStyle().setLeft(0, Unit.PX);
        mainButton.setStyleName("slx-AppBar-mainButton");
        buttonsLayout.setStyleName("slx-AppBar-buttonsLayout");
        infoArea.setStyleName("slx-AppBar-infoArea");
        main.setStyleName("slx-AppBar-mainImage");
        int i = buttons.getWidgetCount();
        if (i > 0) {
            for (int j = 0; j < i; j++) {
                buttons.getWidget(j).setStyleName("slx-AppBar-buttonsImage");
            }
        }
        setHorizontalAlignment(ALIGN_LEFT);

    }

}

class AppButtonsLayout extends SimplePanel implements RequiresResize, HasRightClickHandler
{

    private RightClickHandler rightClickHandler;

    public AppButtonsLayout()
    {
        sinkEvents(Event.ONMOUSEUP | Event.ONDBLCLICK | Event.ONCONTEXTMENU);
    }

    @Override
    public void onBrowserEvent(Event event) {

        // This will stop the event from being propagated to parent elements.
        event.stopPropagation();
        event.preventDefault();
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEUP:
                if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
                    rightClickHandler.onRightClick(this, event);
                }
                break;

            case Event.ONDBLCLICK:

                break;

            case Event.ONCONTEXTMENU:

                break;

            default:
                DomEvent.fireNativeEvent(event, this, this.getElement());
        }// end switch
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.web.box.client.ui.HasRightClickHandler#addRightClickHandler(com.solmix.web.box.client.ui.RightClickHandler)
     */
    @Override
    public void addRightClickHandler(RightClickHandler handler) {
        this.rightClickHandler = handler;

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
     */
    @Override
    public void onResize() {
        // TODO Auto-generated method stub

    }
}