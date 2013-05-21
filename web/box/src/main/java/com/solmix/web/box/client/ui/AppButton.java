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

import com.solmix.web.box.client.dnd.AppMenuDragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-21
 */

public class AppButton extends Image implements HasRightClickHandler
{

    private AppButton()
    {
        sinkEvents(Event.ONMOUSEUP | Event.ONCONTEXTMENU);
    }

    private RightClickHandler rightClickHandler;

    public void onBrowserEvent(Event event) {
        if (rightClickHandler == null) {
            super.onBrowserEvent(event);
            return;
        }
        // This will stop the event from being propagated to parent elements.
        event.stopPropagation();
        event.preventDefault();
        super.onBrowserEvent(event);
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEUP:
                if (DOM.eventGetButton(event) == Event.BUTTON_RIGHT) {
                    rightClickHandler.onRightClick(this, event);
                }
                if (DOM.eventGetButton(event) == Event.BUTTON_LEFT) {
                    GWT.log("left click");
                }

                break;

            // case Event.ONDBLCLICK:
            //
            // break;

            case Event.ONCONTEXTMENU:
                // GWT.log("Event.ONCONTEXTMENU", null);

                break;

            default:
                // DomEvent.fireNativeEvent(event, this, this.getElement());
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

    public static AppButton createButton(AppMenuDragController dragc, String url) {
        final AppButton btn = new AppButton();
        btn.setUrl(url);
        btn.setStyleName("slx-AppBar-buttonsImage");
        btn.setSize("58px", "58px");
        btn.addDoubleClickHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                GWT.log("mouse double click");

            }

        });
        btn.addMouseOverHandler(new MouseOverHandler() {

            public void onMouseOver(MouseOverEvent event) {
                btn.addStyleName("slx-AppBar-mainImage-MouseOver");
            }
        });
        btn.addMouseOutHandler(new MouseOutHandler() {

            @Override
            public void onMouseOut(MouseOutEvent event) {
                btn.removeStyleName("slx-AppBar-mainImage-MouseOver");
            }
        });
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.alert("mouse up");
            }

        });
        dragc.makeDraggable(btn);
        return btn;
    }

}
