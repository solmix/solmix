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
package org.solmix.web.client.basic.view;

import java.util.Date;
import java.util.LinkedHashMap;

import org.solmix.web.client.widgets.AbstractFactory;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.util.EnumUtil;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.FilterEditorSubmitEvent;
import com.smartgwt.client.widgets.grid.events.FilterEditorSubmitHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;


/**
 * 
 * @author Administrator
 * @version $Id$  2011-8-23
 */

public class CSMdyrmrStatView extends VLayout
{
    public static class Factory extends AbstractFactory
    {

       private String id;

       public Canvas create()
       {
           CSMdyrmrStatView pane = new CSMdyrmrStatView();
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
    public CSMdyrmrStatView(){
        this.setWidth100();
        this.setHeight100();
        final ListGrid listGrid = new ListGrid();
        listGrid.setWidth100();
        listGrid.setHeight100();
        listGrid.setAlternateRecordStyles( true );
        listGrid.setDataSource( DataSource.get( "csm:csm_stat" ) );
        listGrid.setAutoFetchData( true );
        listGrid.setCanSort(false);
        listGrid.getRecords();
        listGrid.setShowFilterEditor(true);
        String[] names=listGrid.getDataSource().getFieldNames();
        ListGridField statTime = new ListGridField("statTime","titm");  
        statTime.setWidth(144);  
        final DateTimeFormat dateFormatter = DateTimeFormat.getFormat("EEE MMMM d yyyy");  
        statTime.setCellFormatter(new CellFormatter() {  
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {  
                if (value == null) return (String) value;  
                if (value.getClass() == java.util.Date.class) {  
                    return dateFormatter.format((Date)value);  
                }  
                return value.toString();  
            }  
        });  
        ListGridField[] fields = new ListGridField[names.length];
        fields[names.length-1]=statTime;
       for(int i=0;i<names.length;i++){
           if(!names.equals("statTime")){
        ListGridField field = new ListGridField(names[i]);
        fields[i]=field;
           }
       }
        listGrid.setFields(fields);
        listGrid.addFilterEditorSubmitHandler(new FilterEditorSubmitHandler(){

            @Override
            public void onFilterEditorSubmit(FilterEditorSubmitEvent event) {
               Criteria criteria = listGrid.getCriteria();
               if(criteria==null)
                   criteria = new Criteria();
              
               criteria.addCriteria("dateTime",  DateTimeFormat.getFormat("yyyyMMdd").format(new Date()));
               listGrid.invalidateCache();
               listGrid.filterData(criteria);
                
            }
            
        });
//      listGrid.setShowFilterEditor( true );
//      listGrid.setCanEdit( true );
//      listGrid.setEditEvent( ListGridEditEvent.CLICK );
//      listGrid.setCanRemoveRecords( true );
        final DynamicForm exportForm = new DynamicForm();  
        exportForm.setWidth(300);  
        SelectItem exportTypeItem = new SelectItem("exportType", "导出类型");  
        exportTypeItem.setWidth("*");  
        exportTypeItem.setDefaultToFirstOption(true);  
  
        LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();  
        valueMap.put("csv", "CSV..");  
        valueMap.put("xml", "XML");  
        valueMap.put("json", "JSON");  
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
                    listGrid.exportData(dsRequestProperties);  
            }  
        });  
        this.setMargin(0);
        HLayout formLayout = new HLayout(15);  
        formLayout.setHeight(60);
        formLayout.addMember(exportForm);  
        formLayout.addMember(exportButton); 
        ToolStrip toolBar = new ToolStrip();
        toolBar.setWidth100();
        toolBar.setMargin(0);
//        ControlBox.addToTarget(toolBar, "export", null);
        this.addMember(toolBar);
        this.addMember(formLayout); 
        this.addMember( listGrid );
       
    }
}
