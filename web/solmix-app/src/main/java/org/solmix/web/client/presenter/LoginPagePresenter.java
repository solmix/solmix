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

import org.solmix.web.client.NameTokens;
import org.solmix.web.client.veiw.handlers.LoginPageUiHandlers;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */

public class LoginPagePresenter extends Presenter<LoginPagePresenter.LoginView, LoginPagePresenter.LoginProxy>
 implements LoginPageUiHandlers
{



   public interface LoginView extends View, HasUiHandlers<LoginPageUiHandlers>
   {

      String getUserName();

      String getPassword();

      void resetAndFocus();

   }

   @ProxyStandard
   @NameToken(NameTokens.logInPage)
   public interface LoginProxy extends Proxy<LoginPagePresenter>, Place
   {

   }

   private final PlaceManager placeManager;

   private final ErrorDialogPresenterWidget errorDialog;

   @Inject
   public LoginPagePresenter(EventBus eventBus, LoginView view, LoginProxy proxy, PlaceManager placeManager, ErrorDialogPresenterWidget errorDialog)
   {
      super(eventBus, view, proxy);
      getView().setUiHandlers(this);

      this.placeManager = placeManager;
      this.errorDialog = errorDialog;
   }

   @Override
   protected void onReset()
   {
      super.onReset();

      getView().resetAndFocus();
   }
   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
    */
   @Override
   protected void revealInParent()
   {
      RevealRootContentEvent.fire(this, this);

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.veiw.handlers.LoginPageUiHandlers#onOkButtonClicked()
    */
   public void onOkButtonClicked()
   {
      sendCredentialsToServer();

   }

   /**
    * 
    */
   private void sendCredentialsToServer()
   {
      String userName = getView().getUserName();
      String password = getView().getPassword();
      if (userName.equalsIgnoreCase("solomon"))
      {
         PlaceRequest placeRequest = new PlaceRequest(NameTokens.mainPage);
         placeManager.revealPlace(placeRequest);
      } else
      {
         showErrorDialog();
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.veiw.handlers.LoginPageUiHandlers#showErrorDialog()
    */
   public void showErrorDialog()
   {
      addToPopupSlot(errorDialog);

   }

}
