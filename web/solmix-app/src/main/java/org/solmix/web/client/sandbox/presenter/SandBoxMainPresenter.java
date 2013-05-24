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

import org.solmix.web.client.Solmix;
import org.solmix.web.client.sandbox.SandBoxTokens;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-18
 */

public class SandBoxMainPresenter extends Presenter<SandBoxMainPresenter.MyView, SandBoxMainPresenter.MyProxy>
{

   
   
   
   @Inject
   public SandBoxMainPresenter(EventBus eventBus, MyView view, MyProxy proxy)
   {
      super(eventBus, view, proxy);
      // TODO Auto-generated constructor stub
   }

   /**
    * 
    * @author solomon
    * @version $Id$  2011-5-18
    */
   @ProxyCodeSplit
   @NameToken(SandBoxTokens.MAIN)
   public interface MyProxy extends Proxy<SandBoxMainPresenter>, Place
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
      RevealContentEvent.fire(this, SandBoxPresenter.TYPE_show_sandbox,  this);
      
   }
   
   
   @Override
   protected void onReveal()
   {
      initTabPanel(SandBoxTokens.MAIN);
//      SandBoxPresenter.getMenuGrid().selectItem(SandBoxTokens.MAIN);
   }
   
   protected void  initTabPanel(String token){
      String name =SandBoxTokens.getNameByToken(token);
      TabSet set = SandBoxPresenter.getMainTabSet();
//      Tab tab = set.getTab(token+"_tab");
      String title = Solmix.getMessages().sandBox_main();
      Tab tab = new Tab();
      String icon =  "silk/sandbox_main.png";
      String imgHTML = Canvas.imgHTML(icon, 16, 16);
      tab.setTitle("<span>" + imgHTML + "&nbsp;" + title + "</span>");
//      if (url != SandBoxTokens.MAIN)
//         tab.setCanClose(true);
      
      if(tab!=null){
      VLayout _wapper = new VLayout();
      _wapper.addMember(getView().asWidget());
      tab.setPane(_wapper);
      set.addTab(tab);
      }
   }

}
