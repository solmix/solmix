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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * 
 * @author Administrator
 * @version 110035 2012-2-19
 */

public class BoxBackGround extends Image implements RequiresResize, ProvidesResize
{

    private static BoxBackGround instance;

    private BoxBackGround()
    {

    }

    /**
     * 
     * @return
     */
    public static BoxBackGround getInstance() {
        if (instance == null) {
            instance = new BoxBackGround();
            instance.setUrl(MainBox.getConstants().BoxBackGround_url());
            SizeUtil.fullWindow(instance);
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.RequiresResize#onResize()
     */
    @Override
    public void onResize() {
        SizeUtil.fullWindow(instance);

    }

}
