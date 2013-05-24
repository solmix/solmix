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
package org.solmix.web.client.sandbox;

import java.util.LinkedHashMap;

import org.solmix.web.client.widgets.AbstractFactory;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author solomon
 * @version $Id$  2011-8-29
 */

public class SimpleExportView extends VLayout
{
    public static class Factory extends AbstractFactory
    {

       private String id;

       public Canvas create()
       {
           SimpleExportView pane = new SimpleExportView();
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
    SimpleExportView(){
       final ListGrid listGrid = new ListGrid();
       listGrid.setWidth100();
       listGrid.setHeight( 350 );
       listGrid.setAlternateRecordStyles( true );
       listGrid.setDataSource( DataSource.get( "menuConfig" ) );
//       listGrid.setAutoFetchData( true );
       listGrid.setShowFilterEditor( true );
       
       final DynamicForm exportForm = new DynamicForm();  
       exportForm.setWidth(300);  
 
       SelectItem exportTypeItem = new SelectItem("exportType", "导出类型");  
       exportTypeItem.setWidth("*");  
       exportTypeItem.setDefaultToFirstOption(true);  
 
       LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();  
       valueMap.put("csv", "CSV..");  
       valueMap.put("xml", "XML");  
       valueMap.put("xls", "XLS (Excel97)");  
 
       exportTypeItem.setValueMap(valueMap);  
 
       BooleanItem showInWindowItem = new BooleanItem();  
       showInWindowItem.setName("showInWindow");  
       showInWindowItem.setTitle("Show in Window");  
       showInWindowItem.setAlign(Alignment.LEFT);  
 
       exportForm.setItems(exportTypeItem);  
 
       IButton exportButton = new IButton("Export");  
       exportButton.addClickHandler(new ClickHandler() {  
           public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {  
               String exportAs = (String) exportForm.getField("exportType").getValue();  
                  // exportAs is either XML or CSV, which we can do with requestProperties  
                   DSRequest dsRequestProperties = new DSRequest();  
                   dsRequestProperties.setExportAs((ExportFormat)EnumUtil.getEnum(ExportFormat.values(), exportAs));  
                   dsRequestProperties.setExportDisplay( ExportDisplay.DOWNLOAD);  
                   dsRequestProperties.setExportFilename("");
                   listGrid.exportData(dsRequestProperties);  
           }  
       });  
       this.setMargin(10);
       HLayout formLayout = new HLayout(15);  
       formLayout.setHeight(60);
       formLayout.addMember(exportForm);  
       formLayout.addMember(exportButton);  
       this.addMember(formLayout); 
       this.addMember( listGrid );
    }
}
