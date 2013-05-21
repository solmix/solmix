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

package com.solmix.web.box.client.util;

import com.solmix.web.box.client.MainBox;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class SizeUtil
{

    public enum Direction
    {
        NORTH , EAST , SOUTH , WEST , CENTER , LINE_START , LINE_END
    }

    /**
     * The suppose that style is {left:0px ; top:0px;}
     * 
     * @param widget
     */
    public static void fullWindow(Widget widget) {
        int _w = Window.getClientWidth();
        int _h = Window.getClientHeight();
        widget.setSize(_w + "px", _h + "px");
    }

    public static void fullDirection(Widget widget, Direction d, int size, int limite) {
        int _w = Window.getClientWidth();
        int _h = Window.getClientHeight();

        switch (d) {
            case NORTH:
                widget.setSize(_w + "px", size + "px");
                break;
            case SOUTH:
                setLocation(widget, 0, _h - size);
                _w = _w <= limite ? limite : _w;
                widget.setSize(_w + "px", size + "px");
                break;
        }
    }

    public static void setLocation(Widget widget, int left, int top) {
        widget.getElement().getStyle().setLeft(left, Unit.PX);
        widget.getElement().getStyle().setTop(top, Unit.PX);
    }

    /**
     * @param shell
     * @param south
     * @param height
     */
    public static void fullDirection(Widget widget, Direction south, int height) {
        fullDirection(widget, south, height, 0);

    }

    public static int getDeskSwitchHeight() {
        return Integer.parseInt(MainBox.getConstants().DeskSwitchBar_Height());
    }

    public static int getTaskBarHeight() {
        return Integer.parseInt(MainBox.getConstants().TaskBar_Height());
    }

    public static int getAppBarWidth() {
        return Integer.parseInt(MainBox.getConstants().AppBar_TopSize());
    }
}
