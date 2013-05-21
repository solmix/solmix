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
import com.solmix.web.box.client.util.SizeUtil;
import com.solmix.web.box.client.util.SizeUtil.Direction;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-13
 */

public class TaskBar extends HorizontalPanel implements RequiresResize, HasRightClickHandler
{

    private RightClickHandler rightClickHandler;

    public final static int HEIGHT;
    static {
        HEIGHT = Integer.parseInt(MainBox.getConstants().TaskBar_Height());
    }

    public final static int LIMITE_WIDTH = 600;

    public TaskBar()
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
                // GWT.log("Event.ONCONTEXTMENU", null);

                break;

            default:
                DomEvent.fireNativeEvent(event, this, this.getElement());
        }// end switch
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
     */
    @Override
    public void onResize() {
        SizeUtil.fullDirection(this, Direction.SOUTH, HEIGHT, LIMITE_WIDTH);

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

}
