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
package com.solmix.showcase.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.solmix.showcase.client.tokens.NameTokens;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-15
 */

public class ErrorPresenter extends Presenter<ErrorPresenter.MyView, ErrorPresenter.MyProxy>
{
    @ProxyStandard
    @NameToken(NameTokens.ERROR_PAGE)
    public interface MyProxy extends ProxyPlace<ErrorPresenter>
    {
    }
    

    public interface MyView extends View
    {

    }
    
    @Inject
    public ErrorPresenter(EventBus eventBus, MyView view, MyProxy proxy)
    {
        super(eventBus, view, proxy);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected void revealInParent() {
        RevealRootContentEvent.fire(this, this);
        
    }

}
