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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-14
 */

public interface RightClickHandler extends EventHandler
{

    /**
     * Called when {@link com.google.gwt.dom.client.NativeEvent.BUTTON_RIGHT BUTTON_RIGHT} and
     * {@link com.google.gwt.user.client.Event.ONMOUSEUP ONMOUSEUP} is fired.
     * 
     * @param event the {@link ClickEvent} that was fired
     */
    void onRightClick(Widget sender, Event event);
}
