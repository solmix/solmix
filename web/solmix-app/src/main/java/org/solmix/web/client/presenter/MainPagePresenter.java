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

package org.solmix.web.client.presenter;

import java.util.Map;

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.Solmix;
import org.solmix.web.client.cache.ClientCache;
import org.solmix.web.client.cache.ClientCacheManager;
import org.solmix.web.client.data.DSRepo;
import org.solmix.web.client.veiw.handlers.MainPageUiHandlers;
import org.solmix.web.client.widgets.ModuleButton;
import org.solmix.web.client.widgets.ModuleContainer;
import org.solmix.web.shared.action.LogoutAction;
import org.solmix.web.shared.action.LogoutResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.smartgwt.client.widgets.Window;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-15
 */

public class MainPagePresenter extends Presenter<MainPagePresenter.MainView, MainPagePresenter.MainProxy> implements
    MainPageUiHandlers
{

    private final PlaceManager placeManager;

    private static ModuleContainer container;

    private final LogoutPresenterWidget logoutWidget;

    private final DispatchAsync dispatcher;

    private static ClientCache cache;

    /**
     * Child presenters can fire a RevealContentEvent with TYPE_SetModuleContent to set themselves as children of this
     * presenter.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetModuleContent = new Type<RevealContentHandler<?>>();

    @Inject
    public MainPagePresenter(EventBus eventBus, MainView view, MainProxy proxy, PlaceManager placeManager,
        DispatchAsync dispatcher, LogoutPresenterWidget logoutWidget)
    {
        super(eventBus, view, proxy);
        getView().setUiHandlers(this);
        this.placeManager = placeManager;
        MainPagePresenter.container = getView().getModuleContainer();
        this.logoutWidget = logoutWidget;
        this.dispatcher = dispatcher;
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.mainPage)
    public interface MainProxy extends Proxy<MainPagePresenter>, Place
    {

    }

    public interface MainView extends View, HasUiHandlers<MainPageUiHandlers>
    {

        ModuleContainer getModuleContainer();

    }

    public static ClientCache getCache() {
        return cache;
    }
    public static String getCurrentUser(){
        if(cache==null)
            return "";
        return cache.getCurrentUserData()==null?"":cache.getCurrentUserData().getAttributeAsString(Solmix.USER_NAME_KEY);
            
    }
    @Override
    protected void onReveal(){
        super.onReveal();
        cache=ClientCacheManager.getDefault();
    }
    @Override
    protected void onBind()
    {
       super.onBind();
       DSRepo.loadByModuleName(getModuleName());
       
    }
    /**
     * @return
     */
    private String getModuleName() {
        return NameTokens.SYSTEM;
    }
    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);

    }

    @Override
    public void prepareFromRequest(PlaceRequest placeRequest) {
        String action = placeRequest.getNameToken();
        if (NameTokens.mainPage.equals(action)) {
            PlaceRequest req = new PlaceRequest(NameTokens.SandBox).with(NameTokens.ACTION, "main");
            placeManager.revealPlace(req);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.veiw.handlers.MainPageUiHandlers#onModuleButtonClicked(java.lang.String)
     */
    public void onModuleButtonClicked(String place) {
        if (place.length() != 0) {
            PlaceRequest placeRequest = new PlaceRequest(place).with(NameTokens.ACTION, "main");
            placeManager.revealPlace(placeRequest);
        }

    }

    public static ModuleContainer getModuleContainer() {
        return container;
    }

    public static ModuleButton getModuleButtonByName(String name) {
        Map<String, ModuleButton> buttons = container.getModuleButtons();
        ModuleButton curBt = null;
        for (String key : buttons.keySet()) {
            if (key.equals(name)) {
                curBt = buttons.get(key);
            }
        }
        return curBt;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.veiw.handlers.MainPageUiHandlers#onLogoutClicked()
     */
    @Override
    public void onLogoutClicked() {
        final Window s = (Window) logoutWidget.getView().asWidget();
        s.draw();
        dispatcher.execute(new LogoutAction(1), new AsyncCallback<LogoutResult>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("Log failed" + caught.getMessage());

            }

            @Override
            public void onSuccess(LogoutResult result) {
                if (result.isSuccess()) {
                    s.clear();
                    com.google.gwt.user.client.Window.open("/", "_self", "");
                    RootPanel.get().clear();
                }
            }

        });

    }

}
