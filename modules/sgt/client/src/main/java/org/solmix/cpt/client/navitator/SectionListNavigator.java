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

package org.solmix.cpt.client.navitator;

import java.util.ArrayList;
import java.util.List;

import org.solmix.cpt.client.data.MenuCache;
import org.solmix.cpt.client.data.MenuDataCallBack;
import org.solmix.cpt.client.menu.MenuItemClickCallback;
import org.solmix.cpt.client.menu.MenuRecord;
import org.solmix.cpt.client.widgets.NavigatorSection;

import com.google.inject.Inject;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-12-1
 */

public class SectionListNavigator
{

    private static final int LABEL_HEIGHT = 25;

    private String title;

    private SectionStack sectionStack;

    private static final String SECTION_STACK_WIDTH = "100%";


    private final MenuCache menuCache;

    @Inject
    public SectionListNavigator(MenuCache menuCache)
    {
        this.menuCache = menuCache;
    }

    public Canvas asWidget(String appModule, String title,final MenuItemClickCallback menuCallback) {
        final VLayout navigator = new VLayout();
        navigator.addMember(getTitle());
        navigator.addMember(getSectionStack());
        navigator.setID(appModule);
        MenuDataCallBack callback = new MenuDataCallBack() {

            @Override
            public void execute(List<MenuRecord> menuData) {
                build(navigator,menuData,menuCallback);

            }

        };
        menuCache.findModuleMenuData(appModule, callback);
        return navigator;
    }

    private void build(VLayout navigator,List<MenuRecord> menus,final MenuItemClickCallback menuCallback) {
       
        List<Object[]> source = process(menus);
        if (source == null)
            return;
        for (int i = 0; i < source.size(); i++) {
            Object[] data = source.get(i);
            if (data == null)
                continue;
            MenuRecord parent = (MenuRecord) data[0];
            List<MenuRecord> childrens = (List<MenuRecord>) data[1];
            String name = parent.getTitle();
            MenuRecord[] records = new MenuRecord[childrens.size()];
            for (int j = 0; j < childrens.size(); j++) {
                records[j] = childrens.get(j);
            }
            addSection(name, records);
        }
        addRecordClickHandler( new RecordClickHandler(){

            @Override
            public void onRecordClick(RecordClickEvent event) {
                Record r=  event.getRecord();
                menuCallback.menuItemClick(new MenuRecord(r));
            }
            
        });
    }

    public void addSection(String sectionName, Record[] records) {
        sectionStack.addSection(new NavigatorSection(sectionName, records));
    }

    public void addSection(String sectionName, Record[] records, int position) {
        sectionStack.addSection(new NavigatorSection(sectionName, records), position);
    }

    public void expandSection(int section) {
        sectionStack.expandSection(section);
    }

    public void expandSection(String name) {
        sectionStack.expandSection(name);
    }

    public void selectRecord(String name) {

        SectionStackSection[] sections = sectionStack.getSections();

        // Log.debug("Number of sections: " + sections.length);

        for (int i = 0; i < sections.length; i++) {
            SectionStackSection sectionStackSection = sections[i];

            if (((NavigatorSection) sectionStackSection).getRecord(name) != -1) {

                if (!sectionStack.sectionIsExpanded(i)) {
                    // Log.debug("sectionStack.expandSection(i)");
                    sectionStack.expandSection(i);
                }

                ((NavigatorSection) sectionStackSection).selectRecord(name);
                break;
            }
        }
    }

    public void addSectionHeaderClickHandler(SectionHeaderClickHandler clickHandler) {
        sectionStack.addSectionHeaderClickHandler(clickHandler);
    }
    public void addRecordClickHandler( RecordClickHandler clickHandler) {

        // Log.debug("addRecordClickHandler(sectionName, clickHandler) - " + sectionName);
        SectionStackSection[] sections = sectionStack.getSections();

        for (int i = 0; i < sections.length; i++) {
            SectionStackSection sectionStackSection = sections[i];

                ((NavigatorSection) sectionStackSection).addRecordClickHandler(clickHandler);
        }
    }
    public void addRecordClickHandler(String sectionName, RecordClickHandler clickHandler) {

        // Log.debug("addRecordClickHandler(sectionName, clickHandler) - " + sectionName);
        SectionStackSection[] sections = sectionStack.getSections();

        for (int i = 0; i < sections.length; i++) {
            SectionStackSection sectionStackSection = sections[i];

            if (sectionName.contentEquals(sections[i].getTitle())) {
                ((NavigatorSection) sectionStackSection).addRecordClickHandler(clickHandler);
            }
        }
    }

    /**
     * @return
     */
    private List<Object[]> process(List<MenuRecord> records) {
        List<Object[]> _return = new ArrayList<Object[]>();
        if (records == null)
            return null;
        long rootId = 0;
        for (MenuRecord bean : records) {
            if (bean.getParentId() == 0) {
                rootId = bean.getMenuId();
                records.remove(bean);
                break;
            }
        }
        for (MenuRecord bean : records) {
            if (bean.getParentId() == rootId) {
                Object[] config = new Object[2];
                config[0] = bean;
                config[1] = new ArrayList<MenuRecord>();
                _return.add(config);
            }
        }

        for (MenuRecord bean : records) {
            long parent_id = bean.getParentId();
            for (Object[] sec : _return) {
                MenuRecord parent = (MenuRecord) sec[0];
                long id = parent.getMenuId();
                if (parent_id == id) {
                    ((List) sec[1]).add(bean);
                }
            }
        }
        return _return;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected SectionStack getSectionStack() {
        sectionStack = new SectionStack();
        sectionStack.setSectionHeaderClass("TitleSectionHeader");
        sectionStack.setWidth(SECTION_STACK_WIDTH);
        sectionStack.setHeaderHeight(32);
        sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
        sectionStack.setShowExpandControls(true);
        sectionStack.setAnimateSections(true);
        return sectionStack;
    }

    protected Canvas getTitle() {
        Label navHead = new Label();
        navHead.setWidth100();
        navHead.setHeight(LABEL_HEIGHT);
        navHead.setStyleName("slx_SideNavigator-Header-Label");
        navHead.setContents(title);

        return navHead;
    }

}
