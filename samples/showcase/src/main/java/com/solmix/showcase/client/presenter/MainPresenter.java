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

package com.solmix.showcase.client.presenter;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.extensions.advanceds.client.Roperation;
import com.smartgwt.extensions.advanceds.client.SlxRPC;
import com.smartgwt.extensions.advanceds.client.SlxRPCCallBack;
import com.solmix.showcase.client.data.ClientCache;
import com.solmix.showcase.client.tokens.NameTokens;
import com.solmix.showcase.client.view.handler.MainUiHandlers;
import com.solmix.showcase.client.widgets.ModuleContainer;

/**
 * 
 * @author ffz
 * @version 110035 2013-1-4
 */

public class MainPresenter extends Presenter<MainPresenter.MyView, MainPresenter.MyProxy> implements MainUiHandlers
{

    private final PlaceManager placeManager;

    /**
     * Child presenters can fire a RevealContentEvent with TYPE_SetModuleContent to set themselves as children of this
     * presenter.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_SetModuleContent = new Type<RevealContentHandler<?>>();

    private final MyView view;

    private final TokenFormatter tokenFormatter;
    private static ModuleContainer container;


    public static String HISTORY_TOKEN = "_historyToken";

    public static String MENU_NAME = "_menu_name";

    public static String RIGHT_ID = "_right_id";

    public static String GWT_P = "g_";

    /**
     * @param eventBus
     * @param view
     * @param proxy
     */
    @Inject
    public MainPresenter(EventBus eventBus, MyView view, MyProxy proxy, PlaceManager placeManager,  TokenFormatter tokenFormatter)
    {
        super(eventBus, view, proxy);
        this.tokenFormatter = tokenFormatter;
        this.placeManager = placeManager;
        this.view = view;
        MainPresenter.container = getView().getModuleContainer();
        view.setUiHandlers(this);
    }

    /**
     * 
     * @version 110035 2013-1-4
     */
    @ProxyStandard
    @NameToken(NameTokens.MAIN_PAGE)
    public interface MyProxy extends ProxyPlace<MainPresenter>
    {

    }

    /**
     * 
     * @author ffz
     * @version 110035 2013-1-4
     */

    public interface MyView extends View, HasUiHandlers<MainUiHandlers>
    {

        ModuleContainer getModuleContainer();
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
    public void onBind() {
        super.onBind();
        initServerData();
    }

      
    

    /**
     * 
     */
    private void initServerData() {
        Roperation oper = new Roperation().ds("SYSINIT").type(DSOperationType.FETCH).id("getMenu");
        SlxRPC.send(oper, new SlxRPCCallBack() {

            @Override
            public void execute(RPCResponse response, JavaScriptObject rawData, RPCRequest request) {
                RecordList menu = new RecordList(  response.getDataAsObject());
                ClientCache.setMenuData(menu);
            }

        });
        
    }

    /**
     * 
     * @see com.gwtplatform.mvp.client.Presenter#prepareFromRequest(com.gwtplatform.mvp.client.proxy.PlaceRequest)
     */
    @Override
    public void prepareFromRequest(PlaceRequest placeRequest) {
        String action = placeRequest.getNameToken();
      /*  if (NameTokens.MAIN.equals(action)) {
            PlaceRequest req = new PlaceRequest(NameTokens.Basic).with(NameTokens.ACTION, "main");
            placeManager.revealPlace(req);
        }*/
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.showcase.client.view.handler.MainUiHandlers#onModuleButtonClicked(java.lang.String)
     */
    @Override
    public void onModuleButtonClicked(String place) {
        if (place.length() != 0) {
            PlaceRequest placeRequest = new PlaceRequest(place).with(NameTokens.ACTION, "main");
            placeManager.revealPlace(placeRequest);
        }
        
    }
    public static ModuleContainer getModuleContainer() {
        return container;
    }
}
