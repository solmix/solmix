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

import org.solmix.web.client.veiw.handlers.LockScreenUiHandler;
import org.solmix.web.shared.action.LockScreenAction;
import org.solmix.web.shared.action.LockScreenResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.smartgwt.client.widgets.Window;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-31
 */

public class LockScreenPresenter extends Presenter<LockScreenPresenter.MyView,LockScreenPresenter.MyProxy> implements LockScreenUiHandler
{

    
    /**
     * @param eventBus
     * @param view
     * @param proxy
     */
    @Inject
    public LockScreenPresenter(EventBus eventBus, MyView view, MyProxy proxy, DispatchAsync dispatcher)
    {
        super(eventBus, view, proxy);
        this.dispatcher=dispatcher;
        getView().setUiHandlers(this);
    }

    /**
     * 
     * @author solomon
     * @version $Id$  2011-10-31
     */
//    @NameToken(NameTokens.LOCK_SCREEN)
    @ProxyCodeSplit
    public interface MyProxy extends Proxy<LockScreenPresenter>
    {

    }

    private static MyView view;
    private final DispatchAsync dispatcher;

    /**
     * 
     * @author solomon
     * @version $Id$  2011-10-31
     */

    public interface MyView extends View, HasUiHandlers<LockScreenUiHandler>
    {
    String getUserName();
    String getPassword();
    Window getWindow();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.veiw.handlers.LockScreenUiHandler#releaseLock()
     */
    @Override
    public void releaseLock() {
        String name=getView().getUserName();
        String psd= getView().getPassword();
        dispatcher.execute(new LockScreenAction(name,psd), new AsyncCallback<LockScreenResult>() {

            @Override
            public void onFailure(Throwable caught)
            {
               GWT.log("Log failed"+caught.getMessage());

            }

            @Override
            public void onSuccess(LockScreenResult result)
            {
                if(result.isSuccess()){
                  getView().getWindow().destroy();
                }
            }

         });
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.gwtplatform.mvp.client.Presenter#revealInParent()
     */
    @Override
    protected void revealInParent() {
        // TODO Auto-generated method stub
        
    }



}
