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
package org.solmix.web.client.sandbox.presenter;

import org.solmix.web.client.sandbox.SandBoxTokens;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-29
 */

public class MenuConfigPresenter extends SandBoxAbstractPresenter<MenuConfigPresenter.MyView, MenuConfigPresenter.MyProxy>
{

   
   
   /**
    * @param eventBus
    * @param view
    * @param proxy
    */
   @Inject
   public MenuConfigPresenter(EventBus eventBus, MyView view, MyProxy proxy,DispatchAsync dispatcher)
   {
      super(eventBus, view, proxy,dispatcher);
   }

   /**
    * 
    * @author solomon
    * @version $Id$  2011-5-29
    */

   @ProxyCodeSplit
   @NameToken(SandBoxTokens.MENU_CONFIG)
   public interface MyProxy extends Proxy<MenuConfigPresenter>, Place
   {

   }

   /**
    * 
    * @author solomon
    * @version $Id$  2011-5-18
    */

   public interface MyView extends View
   {

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
    */
   @Override
   protected void revealInParent()
   {
      
      RevealContentEvent.fire(this, SandBoxPresenter.TYPE_show_sandbox, this);
     
      
   }
   @Override
   protected void onReveal(){
      super.onReveal();
      addOrSelectTabPanel(SandBoxTokens.MENU_CONFIG);
   }

}
