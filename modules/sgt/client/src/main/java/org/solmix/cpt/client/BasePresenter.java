/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.cpt.client;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-3
 */

public  class BasePresenter<V extends View, Proxy_ extends Proxy<?>> extends Presenter< V, Proxy_>
{
    /**
     * Child presenters can fire a RevealContentEvent with TYPE_MODULE to set themselves as children of this
     * presenter.
     */
    @ContentSlot
    public static final Type<RevealContentHandler<?>> TYPE_MODULE = new Type<RevealContentHandler<?>>();


   
    public BasePresenter(EventBus eventBus, V view, Proxy_ proxy)
    {
        super( eventBus, view, proxy);
    }
 
  

    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
    }
}
