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
package org.solmix.cpt.client.widgets;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2011-6-1
 */

public class NavigatorSection extends SectionStackSection
{
    private final NavigatorSectionListGrid listGrid;
    public NavigatorSection(String sectionName,Record[] records){
       super(sectionName);
       
       listGrid= new NavigatorSectionListGrid(records);
       this.addItem(listGrid);
       this.setExpanded(true);
    }
    
    public ListGrid getListGrid(){
       return listGrid;
    }
    public void selectRecord(int record) {
       listGrid.selectRecord(record);
     }

     public int getRecord(String name) {
       int result = -1;

       // Log.debug("init getRecord() - " + name);

       ListGridRecord[] records = listGrid.getRecords();
       ListGridRecord record = null;
       String recordName = "";
       for (int i = 0; i < records.length; i++) {

         record = listGrid.getRecord(i);
         // as per NavigationPaneSectionListGrid
         recordName = record.getAttributeAsString("name");

         if (name.contentEquals(recordName)) {
//           Log.debug("name.contentEquals(recordName)");
           result = i;
           break;
         }
       }

       return result;
     }

     public ListGridRecord getSelectedRecord() {
        ListGridRecord record = null;

       ListGridRecord[] records = listGrid.getSelectedRecords();

       if (records.length != 0) {
         // get the name of the first selected record e.g. "Activities"
          record = records[0];
       } else {
//         Log.debug("getSelectedRecord() - No selected record in ListGrid");

          record = listGrid.getRecord(0);
         }

//       Log.debug("getSelectedRecord() - " + name);

       return record;
     }

     public void selectRecord(String name) {
//       Log.debug("selectRecord(name) - " + name);

       ListGridRecord[] records = listGrid.getRecords();
       ListGridRecord record = null;
       String recordName = "";

       for (int i = 0; i < records.length; i++) {

         record = listGrid.getRecord(i);
         recordName = record.getAttributeAsString("name");

         if (name.contentEquals(recordName)) {
//           Log.debug("name.contentEquals(recordName)");
           listGrid.deselectAllRecords();
           listGrid.selectRecord(i);
           break;
         }
       }
     }

     public void addRecordClickHandler(RecordClickHandler clickHandler) {
         listGrid.addRecordClickHandler(clickHandler);
     }
}
