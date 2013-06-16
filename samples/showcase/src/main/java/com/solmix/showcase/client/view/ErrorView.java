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
package com.solmix.showcase.client.view;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.solmix.showcase.client.presenter.ErrorPresenter.MyView;
import com.solmix.showcase.client.widgets.TopHeader;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-15
 */

public class ErrorView extends ViewImpl implements MyView
{
    private  Window window;
    @Inject
    public ErrorView()
    {
      
    }



    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.View#asWidget()
     */
    @Override
    public Widget asWidget() {
        if(window==null){
            window = new Window();
            window.setCanDrag(false);
            window.setShowMinimizeButton(false);
            window.setShowCloseButton(false);
            window.setShowShadow(true);
            window.setShadowSoftness(3);
            window.setShadowOffset(5);
            window.setWidth(400);
            window.setHeight(300);
            window.centerInPage();
            window.setTitle("Not found page error ...");
            
            

            VLayout errorMain = new VLayout();
            HLayout title = new HLayout();
            title.setAlign(Alignment.CENTER);
            title.setWidth100();
            title.setHeight(60);
            title.setPadding(10);
            Img logo = new Img(TopHeader.LOGO, TopHeader.LOGO_WIDTH, TopHeader.LOGO_HEIGHT);
            Label msg = new Label();
            msg.setMargin(30);
            msg.setContents("<font style='font-size:18px;'>The page you are looking for is not here.</font>");
            msg.setHeight(30);
            title.addMember(logo);
            errorMain.addMember(title);
            errorMain.addMember(msg);
            
            window.addItem(errorMain);
            
        }
        return window;
    }

}
