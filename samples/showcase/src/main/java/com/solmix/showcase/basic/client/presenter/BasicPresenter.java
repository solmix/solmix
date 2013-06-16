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

package com.solmix.showcase.basic.client.presenter;

import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.tab.TabSet;
import com.solmix.sgt.client.event.RevealViewEvent;
import com.solmix.sgt.client.event.RevealViewHandler;
import com.solmix.sgt.client.event.UrlClickEvent;
import com.solmix.sgt.client.event.UrlClickHandler;
import com.solmix.showcase.basic.client.view.handler.BasicUiHandlers;
import com.solmix.showcase.client.presenter.MainPresenter;
import com.solmix.showcase.client.tokens.NameTokens;
import com.solmix.showcase.client.widgets.ModuleButton;
import com.solmix.showcase.client.widgets.ModuleContainer;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-16
 */

public class BasicPresenter extends Presenter<BasicPresenter.MyView, BasicPresenter.MyProxy> implements BasicUiHandlers, RevealViewHandler, UrlClickHandler
{

    @ProxyStandard
    @NameToken(NameTokens.Basic)
    public interface MyProxy extends Proxy<BasicPresenter>, Place
    {

    }

    public interface MyView extends View, HasUiHandlers<BasicUiHandlers>
    {

        void init(RecordList result);

        TabSet getMainTabSet();
    }

    private static TabSet mainTabSet;

    private static MyView view;

    private final PlaceManager placeManager;

    @Inject
    public BasicPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager)
    {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
        this.placeManager = placeManager;
        BasicPresenter.mainTabSet = getView().getMainTabSet();
        BasicPresenter.view = view;
    }
    @Override
    public void onBind() {
        super.onBind();
        this.addRegisteredHandler(RevealViewEvent.getType(), this);
        this.addRegisteredHandler(UrlClickEvent.getType(), this);
//        initServerData();
    }
    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.showcase.basic.client.view.handler.BasicUiHandlers#onItemClick(java.lang.String)
     */
    @Override
    public void onItemClick(String token) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.TYPE_SetModuleContent, this);

    }

    @Override
    protected void onReveal() {
        super.onReveal();
        ModuleContainer container = MainPresenter.getModuleContainer();
        Map<String, ModuleButton> buttons = container.getModuleButtons();
        ModuleButton curBt = null;
        for (String key : buttons.keySet()) {
            if (key.equals(NameTokens.Basic)) {
                curBt = buttons.get(key);
            } else {
                buttons.get(key).setSelected(false);
            }
        }
        if (curBt != null)
            curBt.setSelected(true);
        // TO MAIN PAGE.

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sgt.client.event.UrlClickHandler#onUrlClick(com.solmix.sgt.client.event.UrlClickEvent)
     */
    @Override
    public void onUrlClick(UrlClickEvent event) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.sgt.client.event.RevealViewHandler#onRevealView(com.solmix.sgt.client.event.RevealViewEvent)
     */
    @Override
    public void onRevealView(RevealViewEvent event) {
        // TODO Auto-generated method stub
        
    }
}
