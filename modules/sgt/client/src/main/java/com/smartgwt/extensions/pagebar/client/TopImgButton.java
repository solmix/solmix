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

package com.smartgwt.extensions.pagebar.client;

import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-11
 */

public class TopImgButton extends HLayout
{

    Img enable = new Img();

    Img disable = new Img();

    private Boolean _enable;

    public TopImgButton(int buttonWidth, int buttonHeight, String enableImgSrc, String disableImgSrc)
    {
        setWidth(buttonWidth);
        setHeight(buttonHeight);
        enable.setSrc(enableImgSrc);
        enable.setWidth(buttonWidth);
        enable.setHeight(buttonHeight);
        enable.setCursor(Cursor.POINTER);
        disable.setSrc(disableImgSrc);
        disable.setWidth(buttonWidth);
        disable.setHeight(buttonHeight);
        disable.setHeight(buttonHeight);
    }

    @Override
    public void enable() {
        if (_enable != null && !_enable) {
            removeMember(disable);
        }
        addMember(enable);
        _enable = true;

    }

    @Override
    public void disable() {
        if (_enable != null && _enable) {
            removeMember(enable);
        }
        addMember(disable);
        _enable = false;
    }

    public void setButtonClickHandler(ClickHandler handler) {
        enable.addClickHandler(handler);
    }

    public void setButtonTip(String tipMessage) {
        enable.setTooltip(tipMessage);
    }

}
