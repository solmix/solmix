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

package com.solmix.web.box.client;

import com.solmix.web.box.client.ui.BoxMainContainer;

import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class BoxShell extends ResizeComposite
{

    private BoxMainContainer container;

    public BoxShell()
    {
        container = new BoxMainContainer();
        initWidget(container);
        // container.add(BoxBackGround.getInstance(), 0, 0);

    }
}
