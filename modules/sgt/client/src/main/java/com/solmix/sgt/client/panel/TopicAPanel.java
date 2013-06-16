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

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author Administrator
 * @version 110035 2013-2-9
 */

public abstract class TopicAPanel extends DataSourcePanel
{

    protected IButton button;

    /**
     * {@inheritDoc}
     * 
     * @see com.ieslab.eimpro.client.panel.IPanel#create()
     */
    @Override
    public Canvas create() {
        VLayout main = new VLayout();
        main.setWidth100();
        main.setHeight100();

        final HLayout resultLayout = new HLayout();
        resultLayout.setLayoutMargin(5);
        resultLayout.setMembersMargin(5);

        HLayout condition = new HLayout();
        condition.setMargin(5);
        final DynamicForm form = new DynamicForm();
        form.setWidth("80%");
        form.setHeight(26);
        buildCondition(form);
        condition.addMember(form);
        button = new IButton("查          询");
        condition.addMember(button);
        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Canvas[] canvas = resultLayout.getMembers();
                for (Canvas c : canvas) {
                    resultLayout.removeMember(c);
                }
                search(form.getValuesAsCriteria(), resultLayout);
            }

        });
        main.addMember(condition);
        SectionStack sectionStack = new SectionStack();

        sectionStack.setWidth100();
        sectionStack.setHeight100();
        String title = Canvas.imgHTML("silk/world.png") + " 查询结果";
        SectionStackSection section = new SectionStackSection(title);

        section.setCanCollapse(false);
        section.setExpanded(true);
        section.setItems(resultLayout);
        sectionStack.setSections(section);
        main.addMember(sectionStack);
        return main;
    }

    /**
     * @param form
     */
    protected abstract void buildCondition(DynamicForm form);

    /**
     * @param resultLayout
     * @param valuesAsCriteria
     */
    protected abstract void search(Criteria criteria, HLayout resultLayout);

}
