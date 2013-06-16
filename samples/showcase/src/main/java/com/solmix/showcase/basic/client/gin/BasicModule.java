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
package com.solmix.showcase.basic.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.solmix.showcase.basic.client.presenter.BasicPresenter;
import com.solmix.showcase.basic.client.view.BasicView;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-16
 */

public class BasicModule extends AbstractPresenterModule
{

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.inject.client.AbstractGinModule#configure()
     */
    @Override
    protected void configure() {
        bindPresenter(BasicPresenter.class, BasicPresenter.MyView.class,  BasicView.class, BasicPresenter.MyProxy.class);
        
    }

}
