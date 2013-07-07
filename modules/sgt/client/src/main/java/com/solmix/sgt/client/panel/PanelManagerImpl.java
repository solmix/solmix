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
package com.solmix.sgt.client.panel;

import java.util.Map;

import com.gwtplatform.mvp.client.HandlerContainer;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-7
 */

public abstract class PanelManagerImpl implements PanelManager
{


    @Override
    public PanelFactory getPanel(String action) {
        return getPanel(action, null);
    }

    @Override
    public PanelFactory getPanel(String action, Map<String, String> params) {
        PanelFactory _return = getPanelByAction(action);
        if (_return != null &&params != null)
            _return.setParameters(params);
        if (_return != null && _return instanceof HandlerContainer) {
            if (!((HandlerContainer) _return).isBound()) {
                ((HandlerContainer) _return).bind();
            }
            ((HasDelayBind) _return).onDelayBind();
        }

        return _return;
    }

    /**
     * @param action
     * @return
     */
    protected abstract PanelFactory getPanelByAction(String action) ;



    @Override
    public void returnPanel(PanelFactory panel) {

        if (panel instanceof HandlerContainer) {
            ((HandlerContainer) panel).unbind();
        }
    }

}
