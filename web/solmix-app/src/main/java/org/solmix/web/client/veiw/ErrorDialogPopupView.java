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

import org.solmix.web.client.presenter.ErrorDialogPresenterWidget.MyView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.PopupViewImpl;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */

public class ErrorDialogPopupView extends PopupViewImpl implements MyView
{

   interface ErrorDialogPopupViewUiBinder extends UiBinder<DialogBox, ErrorDialogPopupView>
   {
   }

   private static ErrorDialogPopupViewUiBinder uiBinder = GWT.create(ErrorDialogPopupViewUiBinder.class);

   @UiField
   Button okButton;

   private final DialogBox widget;

   @Inject
   public ErrorDialogPopupView(EventBus eventBus)
   {
      super(eventBus);
      widget = uiBinder.createAndBindUi(this);
      widget.setAnimationEnabled(true);
   }


   @UiHandler("okButton")
   void okButtonClicked(ClickEvent event)
   {
      widget.hide();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.View#asWidget()
    */
   public Widget asWidget()
   {
      return widget;
   }
}