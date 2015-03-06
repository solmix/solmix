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

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月3日
 */

public class  ContentPresenter<V extends View, Proxy_ extends Proxy<?>> extends Presenter< View, Proxy<?>>
{

    /**
     * @param eventBus
     * @param view
     * @param proxy
     * @param slot
     */
    public ContentPresenter(EventBus eventBus, View view, Proxy<?> proxy)
    {
        super(eventBus, view, proxy);
    }
    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, BasePresenter.TYPE_MODULE, this);


    }
}
