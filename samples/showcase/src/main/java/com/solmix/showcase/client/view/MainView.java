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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.StretchImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.solmix.showcase.client.Showcase;
import com.solmix.showcase.client.presenter.MainPresenter;
import com.solmix.showcase.client.presenter.MainPresenter.MyView;
import com.solmix.showcase.client.tokens.NameTokens;
import com.solmix.showcase.client.view.handler.MainUiHandlers;
import com.solmix.showcase.client.widgets.ModuleButton;
import com.solmix.showcase.client.widgets.ModuleContainer;
import com.solmix.showcase.client.widgets.TopControl;
import com.solmix.showcase.client.widgets.TopHeader;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-15
 */

public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements MyView
{

    private static final String MAIN_MARGIN = "0px";

    public static int DEFAULT_WIDTH = 200;

    public static int DEFAULT_HEIGHT = 400;

    private static final int NORTH_HEIGHT = 78;

    private final VLayout main;

    private final TopControl topControl;

    private final TopHeader tophead;

    private final  ModuleContainer container;

    private final HLayout northLayout;

    private HLayout southMainContent;

    @Inject
    public MainView(TopHeader tophead, TopControl topControl/* , LockScreenView lock */)
    {
        this.topControl = topControl;
        this.tophead = tophead;
        Window.enableScrolling(false);
        Window.setMargin(MAIN_MARGIN);
        container = new ModuleContainer();
        tophead.setModuleContainer(container);
        main = new VLayout();
        main.setWidth100();
        main.setHeight100();

        northLayout = new HLayout();
        northLayout.setHeight(NORTH_HEIGHT);

        southMainContent = new HLayout();
        ToolStrip t= new   ToolStrip();
        StretchImgButton btn = new StretchImgButton();
        btn.setHeight(58);
        btn.setWidth(58);
        btn.setSrc("modulebutton/sandbox/sandbox.png");
        t.addChild(btn);
        t.setWidth100();
        VLayout top = new VLayout();
        bindCustomUiHandlers();
        top.addMember(tophead);
        bindControlUiHandlers();
        top.addMember(topControl);

        northLayout.addMember(top);
        main.addMember(northLayout);
        main.addMember(southMainContent);
    }

    /**
     * 
     */
    protected void bindControlUiHandlers() {
        ToolStripButton logout = new ToolStripButton(Showcase.getMessages().main_logout());
        logout.setHeight(20);
        topControl.addMember(logout);
        topControl.addSeparator();
        ToolStripButton lock = new ToolStripButton(Showcase.getMessages().main_lockScreen());
        lock.setHeight(20);
        topControl.addMember(lock);
        topControl.addSeparator();
        ImgButton imgButton = new ImgButton();
        imgButton.setWidth(18);
        imgButton.setHeight(18);
        imgButton.setSrc("icon/emoticon.png");
        imgButton.setShowFocused(false);
        imgButton.setShowFocusedIcon(false);
        imgButton.setPrompt(Showcase.getMessages().main_self_prompt());
        topControl.addMember(imgButton);
        topControl.addSpacer(6);
        
    }

    /**
     * 
     */
    protected void bindCustomUiHandlers() {
        /**
         * and sandbox module button
         */
        
        final ModuleButton module1 =createModuleButton(NameTokens.Basic,Showcase.getMessages().moduleBasic(),"modulebutton/sandbox/sandbox.png");
        container.addModuleButton(module1);
        /**
         * and admin manager module button
         */
        final ModuleButton module2 = createModuleButton(NameTokens.Admin,Showcase.getMessages().moduleAdmin(),"modulebutton/sandbox/sandbox.png");
        container.addModuleButton(module2);
        /**
         * and basic module button
         */
        final ModuleButton module3 =createModuleButton(NameTokens.SandBox,Showcase.getMessages().moduleSandbox(),"modulebutton/sandbox/sandbox.png");
        container.addModuleButton(module3);
        
        Label marg = new Label();
        marg.setStyleName("slx-MoudleContainer");
        marg.setWidth(30);
        container.addMember(marg);

        tophead.setModuleContainer(container);
        
    }
    private ModuleButton createModuleButton(String moduleName,String title,String src){
       final ModuleButton module = new ModuleButton();
        module.setModuleName(moduleName);
        module.setTitle(title);
        module.setSrc(src);
        module.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (getUiHandlers() != null) {
                    getUiHandlers().onModuleButtonClicked(module.getModuleName());
                    module.setSelected(true);
                }
            }

        }); 
        return module;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.View#asWidget()
     */
    @Override
    public Widget asWidget() {
        return main;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.showcase.client.presenter.MainPresenter.MyView#getModuleContainer()
     */
    @Override
    public ModuleContainer getModuleContainer() {
        return container;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        // Log.debug("setInSlot()");
        if (slot == MainPresenter.TYPE_SetModuleContent) {
            setMainContent(content);
        } else {
            super.setInSlot(slot, content);
        }
    }

    private void setMainContent(Widget content) {

        if (content != null) {
            // southMainContent= new HLayout();
            if (main.hasMember(southMainContent))
                main.removeMember(southMainContent);
            southMainContent = new HLayout();
            southMainContent.addMember(content);
            main.addMember(southMainContent);
        }
    }

}
