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
package org.solmix.cpt.client.data;

import java.util.List;

import org.solmix.cpt.client.menu.MenuRecord;


/**
 * 获取菜单数据回调函数，只用于动态从服务端获取数据后回调。
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-19
 */

public interface MenuDataCallBack
{

    /**
     * @param _return
     */
    void execute(List<MenuRecord> menuData);

}
