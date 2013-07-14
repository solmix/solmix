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

package org.solmix.sgt.client.panel;

import java.util.Map;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.HandlerContainerImpl;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-6
 */

public abstract class AbstractPanel extends HandlerContainerImpl implements PanelFactory, HasDelayBind
{

    protected String desc = "";

    protected Map<String, String> parameters;

    protected Canvas container;

    @Override
    public String getDescription() {
        return desc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.ieslab.eimpro.client.panel.PanelFactory#getViewName()
     */
    @Override
    public String getViewName() {
        String clzName = this.getClass().getName();
        return clzName.substring(0, clzName.lastIndexOf("$"));
    }

    @Override
    public void setParameters(Map<String, String> paramaters) {
        this.parameters = paramaters;
        onParameterSet(parameters);

    }

    protected void onParameterSet(Map<String, String> param) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.ieslab.eimpro.client.panel.PanelFactory#setContainer(com.smartgwt.client.widgets.Canvas)
     */
    @Override
    public void setContainer(Canvas containerTarget) {
        container = containerTarget;
        onContainerSet(container);
    }

    protected void onContainerSet(Canvas containerTarget) {

    }

    @Override
    public String getID() {
        return "";
    }

    protected <H> void registerHandler(EventBus eventBus, Type<H> type, H handler) {
        if (eventBus != null) {
            HandlerRegistration registration = eventBus.addHandler(type, handler);
            this.registerHandler(registration);
        }
    }

    @Override
    public void onDelayBind() {

    }
}
