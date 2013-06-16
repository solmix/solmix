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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManagerImpl;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import com.solmix.sgt.client.annotation.DefaultPlace;
import com.solmix.sgt.client.annotation.ErrorPlace;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-15
 */

public class AppPlaceManager extends PlaceManagerImpl
{

    private final PlaceRequest defaultPlaceRequest;
    private final PlaceRequest errorPalce;

    /**
     * @param eventBus
     * @param placeBuilder
     */
    @Inject
    public AppPlaceManager(EventBus eventBus, TokenFormatter tokenFormatter, @DefaultPlace String defaultNameToken,@ErrorPlace String errorPlace)
    {
        super(eventBus, tokenFormatter);
        this.defaultPlaceRequest = new PlaceRequest(defaultNameToken);
        this.errorPalce= new PlaceRequest(errorPlace);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.proxy.PlaceManager#revealDefaultPlace()
     */
    @Override
    public void revealDefaultPlace() {
        revealPlace(defaultPlaceRequest);

    }

    @Override
    public void revealErrorPlace(String invalidHistoryToken) {
        revealPlace(errorPalce);
    }

}
