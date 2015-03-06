/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.cpt.client.navitator;

import org.solmix.cpt.client.menu.MenuItemClickCallback;

import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月1日
 */

public interface NavigatorContainer
{
    public static final String URL_PREFIX = "menu/16/";
    public static final String URL_SUFFIX = ".png"; 
    /**
     * Return the Navigator Container Layout,used this as a parent layout to put into another layout.
     * 
     * @return
     */
    Canvas containerCanvas();
    
    /**
     * build and show the layout.
     * 
     * @param name
     * @param title
     */
    void showNavigator(String name, String title,final MenuItemClickCallback menuCallback);


}
