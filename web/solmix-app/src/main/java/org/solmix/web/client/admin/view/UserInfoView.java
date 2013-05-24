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
package org.solmix.web.client.admin.view;

import org.solmix.web.client.widgets.AbstractFactory;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;


/**
 * 
 * @author Administrator
 * @version $Id$  2011-8-9
 */

public class UserInfoView extends VLayout
{
   public static class Factory extends AbstractFactory
   {

      /**
       * {@inheritDoc}
       * 
       * @see org.solmix.web.client.WidgetFactory#create()
       */
      @Override
      public Canvas create()
      {
         return  new UserInfoView();
      }
      
   }
   UserInfoView(){
       ToolStrip bar = new ToolStrip();
       bar.setWidth100();
       ToolStripButton btn = new ToolStripButton("new");
       bar.addButton(btn);
       this.addMember(bar);
//       this.setMargin(20);
      
     
      
       final DynamicForm form = new DynamicForm();  
       form.setGroupTitle("用户信息");  
       form.setIsGroup(true);  
       form.setWidth(300);  
       form.setMargin(10);
       form.setHeight(180);  
       form.setNumCols(2);  
       form.setColWidths(60, "*");  
       //form.setBorder("1px solid blue");  
       form.setPadding(5);  
       form.setCanDragResize(true);  
       form.setResizeFrom("R");  
         
       TextItem subjectItem = new TextItem();  
       subjectItem.setTitle("Subject");  
       subjectItem.setWidth("*");  
 
       TextAreaItem messageItem = new TextAreaItem();  
       messageItem.setShowTitle(false);  
       messageItem.setLength(5000);  
       messageItem.setColSpan(2);  
       messageItem.setWidth("*");  
       messageItem.setHeight("*");  
       form.setDataSource(DataSource.get("user_manager"));
//       form.setFields(subjectItem, messageItem); 
       this.addMember(form);
       IButton save = new IButton(" 用户注册");
       save.addClickHandler(new ClickHandler(){

        @Override
        public void onClick(ClickEvent event) {
            form.saveData();
            
        }
           
       });
       IButton clear = new IButton(" 清除信息");
       clear.addClickHandler(new ClickHandler(){

        @Override
        public void onClick(ClickEvent event) {
            form.clearValues();
            
        }
           
       });
       HLayout btns = new HLayout(20);
       btns.setMargin(10);
       btns.addMember(save);
//       btns.addMember(new Label(20));
       btns.addMember(clear);
       this.addMember(btns);
   }
}
