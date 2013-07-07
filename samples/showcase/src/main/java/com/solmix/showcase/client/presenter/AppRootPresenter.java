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
import com.gwtplatform.mvp.client.RootPresenter;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2013-1-5
 */

public class AppRootPresenter extends RootPresenter
{

    /**
     * @param eventBus
     * @param view
     */
    @Inject
    public AppRootPresenter(EventBus eventBus, RootView view)
    {
        super(eventBus, view);
    }

    @Override
    protected void onBind() {
        super.onBind();
    }
}