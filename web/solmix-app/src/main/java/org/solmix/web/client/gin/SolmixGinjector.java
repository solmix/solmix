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

import org.solmix.web.client.admin.presenter.AdminPresenter;
import org.solmix.web.client.basic.presenter.BasicPresenter;
import org.solmix.web.client.presenter.LockScreenPresenter;
import org.solmix.web.client.presenter.LoginPagePresenter;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.client.sandbox.presenter.SandBoxPresenter;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */
@GinModules({DispatchAsyncModule.class,ClientModule.class})
public interface SolmixGinjector extends Ginjector 
{
//   UrlContext getUrlContext();
   EventBus getEventBus();
   PlaceManager getPlaceManager();
//   
   Provider<LoginPagePresenter> getLoginPagePresenter();
   AsyncProvider<MainPagePresenter> getMainPageSmartGwtPresenter();
//   ProxyFailureHandler getProxyFailureHandler();
   AsyncProvider<SandBoxPresenter> getSandBoxPresenter();
   AsyncProvider<AdminPresenter> getAdminPresenter();
   AsyncProvider<BasicPresenter> getBasicPresenter();
  AsyncProvider<LockScreenPresenter> getLockScreenPresenter();
}
