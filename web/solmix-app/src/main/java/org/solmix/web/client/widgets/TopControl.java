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

package org.solmix.web.client.widgets;

import org.solmix.web.client.Solmix;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-15
 */

public class TopControl extends ToolStrip
{

    public static int CONTROL_HEIGHT = 20;

    String currentLocale = "default";
    public static Label welcome;

    public TopControl()
    {
        setStyleName("slx_topControl");
        setHeight(CONTROL_HEIGHT);
        setWidth100();
        addSpacer(20);
        welcome =new Label("Welcome");
        welcome.setWidth(300);
        addMember(welcome);
        addFill();
//        this.setAlign(Alignment.RIGHT);
        // LOCALE
        currentLocale = Location.getParameter("locale");
        if (currentLocale == null ||currentLocale.equals("default") ||  currentLocale.trim().equals("")) {
            currentLocale = "zh_CN";
        }
        ToolStripMenuButton menuButton;
        Menu menu = new Menu();
        menu.setShowShadow(true);
        menu.setShadowDepth(3);

        MenuItem zh_CN = new MenuItem("简体中文", "flags/16/CH.png");
        MenuItem en = new MenuItem("English", "flags/16/UK.png");
        zh_CN.addClickHandler(new ClickHandler() {

            public void onClick(MenuItemClickEvent event) {
                if (isNeedRelocation("zh_CN")) {
                    UrlBuilder builder = Location.createUrlBuilder().setParameter("locale", "zh_CN");
                    Window.Location.replace(builder.buildString());
                }
            }

        });
        en.addClickHandler(new ClickHandler() {

            public void onClick(MenuItemClickEvent event) {
                if (isNeedRelocation("en")) {
                    UrlBuilder builder = Location.createUrlBuilder().setParameter("locale", "en");
                    Window.Location.replace(builder.buildString());
                }
            }

        });
        menu.setItems(zh_CN, en);
        String realName = "简体中文";
        if (getSupportLocale(currentLocale).equals("en"))
            realName = "English";
        menuButton = new ToolStripMenuButton(realName, menu);
        menuButton.setWidth(100);
        menuButton.setHeight(CONTROL_HEIGHT);
        addMember(menuButton);

        // ADD SEPARATOR
        addSeparator();
        ToolStripButton button = new ToolStripButton(Solmix.getMessages().main_login());
        // button.setBackgroundImage("logo.gif");
        button.setHeight(20);
        this.addMember(button);
        this.addSeparator();
        // ToolStrip bar = new ToolStrip();
        // bar.setStyleName("");
    }

    private boolean isNeedRelocation(String localeStr) {
        localeStr = getSupportLocale(localeStr);
        if (localeStr.equals(currentLocale))
            return false;
        return true;
    }

    private String getSupportLocale(String locale) {
        if (locale == null)
            return Solmix.getConstants().default_locale();
        if (locale.equals("zh_CN") || locale.equals("en")) {
            return locale;
        } else {
            return Solmix.getConstants().default_locale();
        }
    }
}
