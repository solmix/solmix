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

package com.solmix.showcase.client.js;

import com.google.web.bindery.event.shared.EventBus;
import com.solmix.sgt.client.event.UrlClickEvent;
import com.solmix.showcase.client.Showcase;

/**
 * 
 * @author ffz
 * @version 110035 2013-1-14
 */

public class ExportMethod
{

    private static EventBus eventBus;

    public static void onUrlClick(String params) {
        if (eventBus == null)
            eventBus = Showcase.getInjector().getEventBus();
        UrlClickEvent event = new UrlClickEvent(params);
        eventBus.fireEvent(event);

    }

    public static native void exportStaticMethod()/*-{
       $wnd.onUrlClick =
       $entry(@com.solmix.showcase.client.js.ExportMethod::onUrlClick(Ljava/lang/String;));
   }-*/;

}
