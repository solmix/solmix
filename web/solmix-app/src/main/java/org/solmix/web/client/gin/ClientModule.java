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

package org.solmix.web.client.gin;

import org.solmix.web.client.SolmixPlaceManager;
import org.solmix.web.client.admin.presenter.AdminPresenter;
import org.solmix.web.client.admin.view.AdminView;
import org.solmix.web.client.basic.presenter.BasicPresenter;
import org.solmix.web.client.basic.view.BasicView;
import org.solmix.web.client.presenter.ErrorDialogPresenterWidget;
import org.solmix.web.client.presenter.LockScreenPresenter;
import org.solmix.web.client.presenter.LoginPagePresenter;
import org.solmix.web.client.presenter.LogoutPresenterWidget;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.client.sandbox.presenter.SandBoxPresenter;
import org.solmix.web.client.sandbox.view.SandBoxView;
import org.solmix.web.client.veiw.ErrorDialogPopupView;
import org.solmix.web.client.veiw.LockScreenView;
import org.solmix.web.client.veiw.LoginPageView;
import org.solmix.web.client.veiw.LogoutPopupView;
import org.solmix.web.client.veiw.MainPageView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-15
 */

public class ClientModule extends AbstractPresenterModule
{

   /**
    * {@inheritDoc}
    * 
    * @see com.google.gwt.inject.client.AbstractGinModule#configure()
    */
   @Override
   protected void configure()
   {
//      bind(UrlContext.class).to(SimpleUrlContext.class).in(Singleton.class);
//      bindConstant().annotatedWith(UrlScan.class).to(Solmix.URL_SCAN_PKG);
      bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
      bind(PlaceManager.class).to(SolmixPlaceManager.class).in(Singleton.class);
      bind(RootPresenter.class).asEagerSingleton();
      /**
       * form gwtp-0.6 version No more FailureHandler.If you were using it, handle the new and more versatile AsyncCallFailEvent instead.
       */
//      bind(ProxyFailureHandler.class).to(DefaultProxyFailureHandler.class).in(
//          Singleton.class);
      bind(TokenFormatter.class).to(ParameterTokenFormatter.class).in(
         Singleton.class);
      bindConstant().annotatedWith(DefaultPlace.class).to(org.solmix.web.client.NameTokens.mainPage);
      bindPresenter(LoginPagePresenter.class,LoginPagePresenter.LoginView.class,LoginPageView.class,LoginPagePresenter.LoginProxy.class);
      bindSingletonPresenterWidget(ErrorDialogPresenterWidget.class,
         ErrorDialogPresenterWidget.MyView.class, ErrorDialogPopupView.class);
      bindPresenter(MainPagePresenter.class, MainPagePresenter.MainView.class,
         MainPageView.class, MainPagePresenter.MainProxy.class);
      
      bindPresenter(SandBoxPresenter.class, SandBoxPresenter.MyView.class,
         SandBoxView.class, SandBoxPresenter.MyProxy.class);
      
      bindPresenter(AdminPresenter.class, AdminPresenter.MyView.class,
         AdminView.class, AdminPresenter.MyProxy.class);
      bindPresenter(BasicPresenter.class, BasicPresenter.MyView.class,
         BasicView.class, BasicPresenter.MyProxy.class);
      bindSingletonPresenterWidget(LogoutPresenterWidget.class,LogoutPresenterWidget.MyView.class,LogoutPopupView.class);
      bindSingletonPresenterWidget(LockScreenPresenter.class,LockScreenPresenter.MyView.class,LockScreenView.class);
   }
}
