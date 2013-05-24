/*
 * ========THE SOLMIX PROJECT=====================================
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
package org.solmix.web.client.veiw;

import org.solmix.web.client.Solmix;
import org.solmix.web.client.presenter.LockScreenPresenter.MyView;
import org.solmix.web.client.veiw.handlers.LockScreenUiHandler;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-31
 */

public class LockScreenView extends ViewWithUiHandlers<LockScreenUiHandler> implements MyView
{
    private final Window window;
    final PasswordItem passwordItem; 
    final TextItem nameItem;
    /**
     * @param eventBus
     */
    @Inject
    public LockScreenView()
    {
//        super(eventBus);
        window=new Window();
        window.setWidth(360);
        window.setHeight(200);
        window.setIsModal(true);
        window.setShowModalMask(true);
        window.centerInPage();
        window.setShowCloseButton(false);
        window.setTitle(Solmix.getMessages().main_lockScreen());
        VLayout main = new VLayout();
        main.setWidth100();
        main.setHeight100();
        final DynamicForm form = new DynamicForm(); 
        HeaderItem header = new HeaderItem();  
        header.setDefaultValue(Solmix.getMessages().main_login()); 
//        form.setIsGroup(true);
//        form.setGroupTitle(Solmix.getMessages().main_login());
         nameItem = new TextItem();
        nameItem.setName(Solmix.getMessages().main_username());
        nameItem.setDisabled(true);
//        nameItem.setValue(MainPagePresenter.getCurrentUser());
        passwordItem = new PasswordItem();  
        passwordItem.setName(Solmix.getMessages().main_lockin_psd());
        passwordItem.setHeight(24);
        Button button = new Button( Solmix.getMessages().main_login());
        button.setIcon("icons/24/login_24.png");
        button.setHeight(24);
        button.setWidth(80);
        button.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                window.animateHide(AnimationEffect.FADE, null, 700);
//                getUiHandlers().releaseLock();
                
            }
            
        });
        
        form.setMargin(16);
        form.setFields(header,nameItem,passwordItem);
        
        HLayout layout = new HLayout();
        layout.setWidth100();
        layout.setAlign(Alignment.CENTER);
        layout.addMember(button);
        
        main.addMember(form);
        main.setMargin(4);
        main.setBorder("3px solid #0083ff");
        main.addMember(layout);
        window.addItem(main);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.View#asWidget()
     */
    @Override
    public Widget asWidget() {
        return window;
    }
    public Window getWindow(){
        return window;
    }
    public void setUser(String user){
        nameItem.setValue(user);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.presenter.LockScreenPresenterWidget.MyView#getUserName()
     */
    @Override
    public String getUserName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.presenter.LockScreenPresenterWidget.MyView#getPassword()
     */
    @Override
    public String getPassword() {
        return passwordItem.getValueAsString();
    }

}
