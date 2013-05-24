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

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */

public class ErrorDialogPresenterWidget extends
PresenterWidget<ErrorDialogPresenterWidget.MyView> {

   /**
    * 
    * @author solomon
    * @version $Id$  2011-5-15
    */

   public interface MyView extends PopupView
   {
   }

   @Inject
   public ErrorDialogPresenterWidget(final EventBus eventBus, final MyView view)
   {
      super(eventBus, view);
   }


}
