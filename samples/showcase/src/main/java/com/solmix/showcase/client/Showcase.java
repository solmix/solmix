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

package com.solmix.showcase.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.solmix.showcase.client.gin.ShowcaseInjector;
import com.solmix.showcase.client.i18n.Messages;
import com.solmix.showcase.client.js.ExportMethod;
import com.solmix.showcase.client.resource.AppResource;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-6-15
 */

public class Showcase implements EntryPoint
{
    public static final String DEFAULT_LOCAL="zh_CN";
    private static final ShowcaseInjector ginjector = GWT.create(ShowcaseInjector.class);
    
    private static Messages messages;
    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    @Override
    public void onModuleLoad() {
        if (!GWT.isScript()) {
            // pass key Ctrl+Alt+F1 show console. 
            KeyIdentifier debugKey = new KeyIdentifier();
            debugKey.setCtrlKey(true);
            debugKey.setKeyName("1");
            debugKey.setAltKey(true);

            Page.registerKey(debugKey, new KeyCallback() {

                @Override
                public void execute(String keyName) {
                    SC.showConsole();
                }
            });
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                moduleLoad();
            }

        });
        RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());

    }
    private void moduleLoad() {
        GWT.<AppResource> create(AppResource.class).css().ensureInjected();

        messages = GWT.create(Messages.class);
//        constants = GWT.create(Constants.class);
        DelayedBindRegistry.bind(ginjector);
        // 跳转到主页
        // ginjector.getPlaceManager().revealDefaultPlace();
        // 显示当前地址栏的页面
        ginjector.getPlaceManager().revealCurrentPlace();
        ExportMethod.exportStaticMethod();
    }
    public static ShowcaseInjector getInjector() {
        return ginjector;
    }
    public static Messages getMessages()
    {
        return messages;
    }
}
