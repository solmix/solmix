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
package org.solmix.web.client.chart;

import org.solmix.web.client.comet.AtmosphereCometView;
import org.solmix.web.client.widgets.AbstractFactory;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.events.SelectHandler.SelectEvent;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-28
 */

public class BarChartView1 
{

        public static class Factory extends AbstractFactory
        {

            private String id;

            public Canvas create() {
                BarChartView1 pane = new BarChartView1();
                id = pane.getWidget().getID();
                return pane.getWidget();
            }

            public String getDescription() {
                return "comet test";
            }

            public String getID() {
                return id;
            }
        }
        VLayout main;
        
        public Canvas getWidget(){
            return main;
        }
        BarChartView1(){
             main = new VLayout();
            Runnable onLoadCallback = new Runnable() {
                public void run() {
           
                  // Create a pie chart visualization.
                  PieChart pie = new PieChart(createTable(), createOptions());

                  pie.addSelectHandler(createSelectHandler(pie));
                  main.addMember(pie);
                }
              };

              // Load the visualization api, passing the onLoadCallback to be called
              // when loading is done.
              VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);
        }
        private Options createOptions() {
            Options options = Options.create();
            options.setWidth(400);
            options.setHeight(240);
            options.set3D(true);
            options.setTitle("My Daily Activities");
            return options;
          }

          private SelectHandler createSelectHandler(final PieChart chart) {
            return new SelectHandler() {
              @Override
              public void onSelect(SelectEvent event) {
                String message = "";
                
                // May be multiple selections.
                JsArray<Selection> selections = chart.getSelections();

                for (int i = 0; i < selections.length(); i++) {
                  // add a new line for each selection
                  message += i == 0 ? "" : "\n";
                  
                  Selection selection = selections.get(i);

                  if (selection.isCell()) {
                    // isCell() returns true if a cell has been selected.
                    
                    // getRow() returns the row number of the selected cell.
                    int row = selection.getRow();
                    // getColumn() returns the column number of the selected cell.
                    int column = selection.getColumn();
                    message += "cell " + row + ":" + column + " selected";
                  } else if (selection.isRow()) {
                    // isRow() returns true if an entire row has been selected.
                    
                    // getRow() returns the row number of the selected row.
                    int row = selection.getRow();
                    message += "row " + row + " selected";
                  } else {
                    // unreachable
                    message += "Pie chart selections should be either row selections or cell selections.";
                    message += "  Other visualizations support column selections as well.";
                  }
                }
                
//                Window.alert(message);
              }
            };
          }

          private AbstractDataTable createTable() {
            DataTable data = DataTable.create();
            data.addColumn(ColumnType.STRING, "Task");
            data.addColumn(ColumnType.NUMBER, "Hours per Day");
            data.addRows(2);
            data.setValue(0, 0, "Work");
            data.setValue(0, 1, 14);
            data.setValue(1, 0, "Sleep");
            data.setValue(1, 1, 10);
            return data;
          }
}
