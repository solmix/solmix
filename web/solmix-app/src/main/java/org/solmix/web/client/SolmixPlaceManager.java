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
package org.solmix.web.client;

import org.solmix.web.client.gin.DefaultPlace;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */

public class SolmixPlaceManager extends PlaceManagerImpl
{
   private final PlaceRequest defaultPlaceRequest;
   /**
    * @param eventBus
    * @param tokenFormatter
    */
   @Inject
   public SolmixPlaceManager(EventBus eventBus, TokenFormatter tokenFormatter,@DefaultPlace String defaultNameToken)
   {
      super(eventBus, tokenFormatter);
      this.defaultPlaceRequest= new PlaceRequest(defaultNameToken);
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.proxy.PlaceManager#revealDefaultPlace()
    */
   public void revealDefaultPlace()
   {
      revealPlace(defaultPlaceRequest);

   }
   public void revealErrorPlace(String invalidHistoryToken) {
      PlaceRequest myRequest = new PlaceRequest(NameTokens.errorPage);
      revealPlace(myRequest);     
    }
}
