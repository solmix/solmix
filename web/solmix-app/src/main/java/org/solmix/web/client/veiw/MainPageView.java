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

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.Solmix;
import org.solmix.web.client.im.IMMainWindow;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.client.presenter.MainPagePresenter.MainView;
import org.solmix.web.client.veiw.handlers.MainPageUiHandlers;
import org.solmix.web.client.widgets.ModuleButton;
import org.solmix.web.client.widgets.ModuleContainer;
import org.solmix.web.client.widgets.TopControl;
import org.solmix.web.client.widgets.TopHeader;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.types.AnimationAcceleration;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-15
 */

public class MainPageView extends ViewWithUiHandlers<MainPageUiHandlers> implements MainView
{

    private static final String MAIN_MARGIN = "0px";
    public static int  DEFAULT_WIDTH=200;
    
    public static int  DEFAULT_HEIGHT=400;
    private static final int NORTH_HEIGHT = 78;

    private TopControl topControl;

    private TopHeader tophead;

    private ModuleContainer container;

    private VLayout main;

    private HLayout northLayout;

    private HLayout southMainContent;

    private LockScreenView lockscreen;
    private  IMMainWindow messageWin;

    @Inject
    public MainPageView(TopHeader tophead, TopControl topControl, LockScreenView lock)
    {
        this.topControl = topControl;
        lockscreen = lock;
        this.tophead = tophead;
        Window.enableScrolling(false);
        Window.setMargin(MAIN_MARGIN);
        container = new ModuleContainer();
        tophead.setModuleContainer(container);
        main = new VLayout();
        main.setWidth100();
        main.setHeight100();
        main.addResizedHandler(new ResizedHandler(){

            @Override
            public void onResized(ResizedEvent event) {
                if(messageWin!=null){
                int winLeft=com.google.gwt.user.client.Window.getClientWidth() - messageWin.getWidth()-10;
                messageWin.moveTo(winLeft, 78);
                }
                
            }
            
        });
//        messageWin.setTitle("Solmix IM");
        northLayout = new HLayout();
        northLayout.setHeight(NORTH_HEIGHT);

        southMainContent = new HLayout();
        VLayout top = new VLayout();
        bindCustomUiHandlers();
        top.addMember(tophead);
        bindControlUiHandlers();
        top.addMember(topControl);

        northLayout.addMember(top);
        main.addMember(northLayout);
        main.addMember(southMainContent);
    }
protected void buildMessageWin(){
    messageWin.setTitle("Solmix IM");
    //set width & height
    int clientWidth=Double.valueOf(com.google.gwt.user.client.Window.getClientWidth()*0.4).intValue();
    int winWidth = clientWidth>DEFAULT_WIDTH?DEFAULT_WIDTH:clientWidth;
    
    int winHeidht=com.google.gwt.user.client.Window.getClientHeight()-TopHeader.HEIGHT+TopControl.CONTROL_HEIGHT-60;
    
    messageWin.setWidth(winWidth);
    messageWin.setHeight(winHeidht>0?winHeidht:0);
    //set header controls
    ClickHandler clickHandler=new ClickHandler(){

        @Override
        public void onClick(ClickEvent event) {
           SC.say("click me");
            
        }   
        
    };
    ClickHandler hideClickHander=new ClickHandler(){

        @Override
        public void onClick(ClickEvent event) {
            messageWin.animateHide(AnimationEffect.SLIDE, null, 1000);
            
        }   
        
    };
    HeaderControl refresh = new HeaderControl(HeaderControl.REFRESH, clickHandler);
    HeaderControl doubleArrowUp = new HeaderControl(HeaderControl.DOUBLE_ARROW_UP, hideClickHander);
    HeaderControl help = new HeaderControl(HeaderControl.HELP, clickHandler); 
    help.setPrompt("check out help doc.");
    messageWin.setHeaderControls(HeaderControls.HEADER_LABEL,help,refresh,doubleArrowUp,HeaderControls.MINIMIZE_BUTTON);
    //set shadow
    messageWin.setShowShadow(true);
    messageWin.setShadowOffset(8);
    messageWin.setShadowSoftness(4);
    //set animate
    messageWin.setAnimateHideAcceleration(AnimationAcceleration.SMOOTH_END);
    messageWin.setAnimateShowAcceleration(AnimationAcceleration.SMOOTH_START);
    // set win location
    int winLeft=com.google.gwt.user.client.Window.getClientWidth() - messageWin.getWidth()  - messageWin.getPageLeft()-10;
    int winTop=TopHeader.HEIGHT+TopControl.CONTROL_HEIGHT;
    messageWin.setTop(winTop);
    messageWin.setLeft(winLeft);
    messageWin.setCanDragResize(true);
    messageWin.setMargin(0);
    //
    messageWin.setTitle("Solmix IM");
    messageWin.setAnimateMinimize(true);
    messageWin.setOpacity(70);
    messageWin.setCanDragReposition(false);
    messageWin.setCanDragResize(true);
    messageWin.setKeepInParentRect(true);
}
    /**
     * add top control
     */
    protected void bindControlUiHandlers() {
        ToolStripButton logout = new ToolStripButton(Solmix.getMessages().main_logout());
        logout.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                getUiHandlers().onLogoutClicked();
            }

        });
        logout.setHeight(20);
        topControl.addMember(logout);
        topControl.addSeparator();
        ToolStripButton lock = new ToolStripButton(Solmix.getMessages().main_lockScreen());
        lock.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                lockscreen.getWindow().show();
                lockscreen.setUser(MainPagePresenter.getCurrentUser());
                // to something to lock history.
            }

        });
        lock.setHeight(20);
        topControl.addMember(lock);
        topControl.addSeparator();
        ImgButton imgButton = new ImgButton();
        imgButton.setWidth(18);
        imgButton.setHeight(18);
        imgButton.setSrc("silk/emoticon.png");
        imgButton.setShowFocused(false);
        imgButton.setShowFocusedIcon(false);
        imgButton.setPrompt(Solmix.getMessages().main_self_prompt());
        imgButton.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                if(messageWin==null){
                    messageWin= new IMMainWindow();
                    buildMessageWin();
                }
                if(messageWin.isVisible()&&messageWin.isDrawn())
                    messageWin.animateHide(AnimationEffect.SLIDE, null, 1000);
                else
                messageWin.animateShow(AnimationEffect.SLIDE, null, 1000);
                
            }
            
        });
        topControl.addMember(imgButton);
        topControl.addSpacer(6);
    }

    protected void bindCustomUiHandlers() {
        /**
         * and sandbox module button
         */
        final ModuleButton module1 = new ModuleButton();
        module1.setModuleName(NameTokens.SandBox);
        module1.setTitle(Solmix.getMessages().moduleSandbox());
        module1.setSrc("modulebutton/sandbox/sandbox.png");
        module1.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (getUiHandlers() != null) {
                    getUiHandlers().onModuleButtonClicked(module1.getModuleName());
                    module1.setSelected(true);
                }
            }

        });
        container.addModuleButton(module1);
        /**
         * and admin manager module button
         */
        final ModuleButton module2 = new ModuleButton();
        module2.setModuleName(NameTokens.Admin);
        module2.setTitle(Solmix.getMessages().moduleAdmin());
        module2.setSrc("modulebutton/sandbox/sandbox.png");
        module2.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (getUiHandlers() != null) {
                    getUiHandlers().onModuleButtonClicked(module2.getModuleName());
                    module2.setSelected(true);
                }
            }

        });
        container.addModuleButton(module2);
        /**
         * and basic module button
         */
        final ModuleButton module3 = new ModuleButton();
        module3.setModuleName(NameTokens.Basic);
        module3.setTitle(Solmix.getMessages().moduleBasic());
        module3.setSrc("modulebutton/sandbox/sandbox.png");
        module3.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (getUiHandlers() != null) {
                    getUiHandlers().onModuleButtonClicked(module3.getModuleName());
                    module3.setSelected(true);
                }
            }

        });
        container.addModuleButton(module3);
        Label marg = new Label();
        marg.setStyleName("slx-MoudleContainer");
        marg.setWidth(30);
        container.addMember(marg);

        tophead.setModuleContainer(container);

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.View#asWidget()
     */
    public Widget asWidget() {
        return main;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.presenter.MainPagePresenter.MainView#getModuleContainer()
     */
    public ModuleContainer getModuleContainer() {
        return container;
    }

    @Override
    public void setInSlot(Object slot, Widget content) {
        // Log.debug("setInSlot()");
        if (slot == MainPagePresenter.TYPE_SetModuleContent) {
            setMainContent(content);
        } else {
            super.setInSlot(slot, content);
        }
    }

    private void setMainContent(Widget content) {
        // southMainContent.clear();

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
