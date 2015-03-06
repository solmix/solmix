/*
 * Copyright 2012 The Solmix Project
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
package  org.solmix.cpt.client.widgets;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-31
 */

public class NavigatorSectionListGrid extends ListGrid
{
   public static final String URL_PREFIX = "menu/16/";
   public static final String URL_SUFFIX = ".png";   
   
   private static final int ICON_FIELD_WIDTH = 27;
   
   public NavigatorSectionListGrid(Record[] records){
      super();
      this.setBaseStyle("slx_navigatorGridCell");
      this.setData(records);
      this.setWidth100();
      this.setHeight100();
      this.setShowAllRecords(true);
      this.setShowHeader(false);
      
      ListGridField appIconField = new ListGridField("icon",  ICON_FIELD_WIDTH);  
     appIconField.setImageSize(16); 
     appIconField.setAlign(Alignment.RIGHT);
     appIconField.setType(ListGridFieldType.IMAGE);  
     appIconField.setImageURLPrefix(URL_PREFIX);  
     appIconField.setImageURLSuffix(URL_SUFFIX);  
     appIconField.setCanEdit(false); 
     
     ListGridField appDisplayNameField = new ListGridField("title");
     this.setFields(appIconField,appDisplayNameField);
     this.addDataArrivedHandler(new DataArrivedHandler() {
      @Override
      public void onDataArrived(DataArrivedEvent event) {
        selectRecord(0);
      }
    });  
     
//     this.setAutoFetchData(true);
   }
}
