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
package org.solmix.web.client.comet;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-23
 */

public class Info extends PopupPanel {

    public static void display(String title, String message) {

        final Info info = new Info(title, message);

        info.show();

        Timer t = new Timer() {
            @Override
            public void run() {
                info.hide();
            }
        };
        t.schedule(4000);
    }

    @Override
    public void show() {
        super.show();
        slots.add(level, this);
    }


    @Override
    public void hide() {
        super.hide();
        slots.set(level, null);
    }


    protected Info(String title, String message) {

        add(new InfoWidget(title, message));
        setWidth("300px");
        setHeight("50px");

        int root_width = Window.getClientWidth();
        int root_height = Window.getClientHeight();

        level = findAvailableLevel();

        int left = root_width - 320;
        int top = root_height - 80 - (level * 60);

        setPopupPosition(left, top);
    }

    private static ArrayList<Info> slots = new ArrayList<Info>();

    private int level;

    private static int findAvailableLevel() {
        int size = slots.size();
        for (int i = 0; i < size; i++) {
            if (slots.get(i) == null) {
                return i;
            }
        }
        return size;
    }

    public static class InfoWidget extends Composite {
        MyUiBinder binder = GWT.create(MyUiBinder.class);

        interface MyUiBinder extends UiBinder<Widget, InfoWidget> {
        }

        @UiField
        Label title;
        @UiField
        Label message;

        private InfoWidget(String title, String message) {
            initWidget(binder.createAndBindUi(this));
            this.title.setText(title);
            this.message.setText(message);
        }
    }


}
