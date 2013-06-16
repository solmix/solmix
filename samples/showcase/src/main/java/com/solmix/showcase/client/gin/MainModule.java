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
package com.solmix.showcase.client.gin;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.solmix.sgt.client.annotation.DefaultIMPlace;
import com.solmix.sgt.client.annotation.DefaultPlace;
import com.solmix.sgt.client.annotation.ErrorPlace;
import com.solmix.sgt.client.panel.PanelManager;
import com.solmix.showcase.client.presenter.AppRootPresenter;
import com.solmix.showcase.client.presenter.ErrorPresenter;
import com.solmix.showcase.client.presenter.MainPresenter;
import com.solmix.showcase.client.tokens.NameTokens;
import com.solmix.showcase.client.view.ErrorView;
import com.solmix.showcase.client.view.MainView;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-15
 */

public class MainModule extends AbstractPresenterModule
{

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.inject.client.AbstractGinModule#configure()
     */
    @Override
    protected void configure() {
        
        bind(AppRootPresenter.class).asEagerSingleton();
        bind(Controls.class).asEagerSingleton();
        
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(Singleton.class);
        bind(PlaceManager.class).to(AppPlaceManager.class).in(Singleton.class);

        bind(PanelManager.class).to(AppPanelManager.class).in(Singleton.class);
//        bind(IMProxy.class).to(IMProxyImpl.class).in(Singleton.class);
        
        //bind annotations.
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.MAIN_PAGE);
        bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.ERROR_PAGE);
        bindConstant().annotatedWith(DefaultIMPlace.class).to(NameTokens.IM_URL);
        // bind main MVP.
        bindPresenter(MainPresenter.class, MainPresenter.MyView.class, MainView.class, MainPresenter.MyProxy.class);
        bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class, ErrorView.class, ErrorPresenter.MyProxy.class);


    }

}
