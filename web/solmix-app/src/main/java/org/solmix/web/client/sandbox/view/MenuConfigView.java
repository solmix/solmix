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
package org.solmix.web.client.sandbox.view;

import org.solmix.web.client.widgets.AbstractFactory;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-29
 */
public class MenuConfigView extends  VLayout
{

   public static class Factory extends AbstractFactory
   {

      private String id;

      public Canvas create()
      {
         MenuConfigView pane = new MenuConfigView();
         id = pane.getID();
         return pane;
      }

      public String getDescription()
      {
         return "main";
      }

      public String getID()
      {
         return id;
      }
   }
   MenuConfigView(){
      final ListGrid listGrid = new ListGrid();
      listGrid.setWidth100();
      listGrid.setHeight( 350 );
      listGrid.setAlternateRecordStyles( true );
      listGrid.setDataSource( DataSource.get( "menuConfig" ) );
      listGrid.setAutoFetchData( true );
      listGrid.setShowFilterEditor( true );
      listGrid.setCanEdit( true );
      listGrid.setEditEvent( ListGridEditEvent.CLICK );
      listGrid.setCanRemoveRecords( true );
      String[] names=listGrid.getDataSource().getFieldNames();
        ListGridField iconField = new ListGridField("icon");  
        iconField.setAlign(Alignment.CENTER);  
        iconField.setType(ListGridFieldType.IMAGE);  
        iconField.setImageURLPrefix("menu/16/");  
        iconField.setImageURLSuffix(".png");  
//        iconField.setCanEdit(false); 
        iconField.setCanFilter(false);
        ListGridField[] fields = new ListGridField[names.length+1];
        fields[0]=iconField;
       for(int i=0;i<names.length;i++){
        ListGridField field = new ListGridField(names[i]);
        fields[i+1]=field;
       }
        listGrid.setFields(fields);
      IButton newButton = new IButton( "新建菜单" );
      newButton.addClickHandler( new ClickHandler() {

         public void onClick( ClickEvent event )
         {
            listGrid.startEditingNew();
         }
      } );
     final IButton editBt = new IButton( "可编辑" );
      editBt.addClickHandler( new ClickHandler() {

         public void onClick( ClickEvent event )
         {
            if(editBt.getTitle().equals("可编辑")){
            listGrid.setCanEdit(false);
            editBt.setTitle("不可编辑");
            }else{
               listGrid.setCanEdit(true);
               editBt.setTitle("可编辑");
            }
         }
      } );

      this.setMargin(10);
      this.addMember( listGrid );
      this.addMember(editBt);
      this.addMember( newButton );
   }

}
