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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.smartgwt.client.widgets.Window;


/**
 * 
 * @author Administrator
 * @version $Id$  2011-8-8
 */

public class LogoutPopupView extends PopupViewImpl implements org.solmix.web.client.presenter.LogoutPresenterWidget.MyView
{
   /**
    * @param eventBus
    */
   @Inject
   public LogoutPopupView(EventBus eventBus)
   {
      super(eventBus);
      window=new Window();
      window.setWidth(360);
      window.setHeight(120);
      window.setIsModal(true);
      window.centerInPage();
      window.setShowCloseButton(false);
      window.setTitle("logout ...");
   }
   private final Window window;
  
   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.View#asWidget()
    */
   @Override
   public Widget asWidget()
   {
      return window;
   }

}
