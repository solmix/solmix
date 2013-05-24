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

import org.solmix.web.client.widgets.AbstractFactory;
import org.solmix.web.client.widgets.BridgeCanvas;

import ua.metallic.ofcchart.client.ChartWidget;
import ua.metallic.ofcchart.client.event.ChartClickEvent;
import ua.metallic.ofcchart.client.event.ChartClickHandler;
import ua.metallic.ofcchart.client.model.ChartData;
import ua.metallic.ofcchart.client.model.elements.PieChart;
import ua.metallic.ofcchart.client.model.elements.PieChart.PieBounceAnimation;
import ua.metallic.ofcchart.client.model.elements.PieChart.Slice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-28
 */

public class BarChartView 
{

        public static class Factory extends AbstractFactory
        {

            private String id;

            public Canvas create() {
                return BarChartView.getWidget();
            }

            public String getDescription() {
                return "comet test";
            }

            public String getID() {
                return id;
            }
        }
        Layout main;
        static ChartWidget chart;
        Button resetBut;
//        DynamicForm l;
        static BridgeCanvas canvas;
        public static Canvas getWidget(){
            canvas= new BridgeCanvas();
            canvas.setWidth100();
            canvas.setHeight100();
            chart = new ChartWidget();
            
            GWT.log(chart.isAttached()+"chart has parent");
            chart.removeFromParent();
            chart.setSize("400", "300");
           GWT.log(chart.isAttached()+"chart has parent");
            ChartData cd = getPieChartLayer1();
            chart.setChartData(cd);
            canvas.addChild(chart);
            return canvas;
        }
        BarChartView(){
             main = new Layout();
             ToolStripButton btn = new ToolStripButton("create chart");
             // add chart
//             main.addMember(chart);
             ToolStrip tool = new ToolStrip();
             btn.addClickHandler(new ClickHandler(){

                @Override
                public void onClick(ClickEvent event) {
                  Layout win = new Layout();
                  win.setWidth(500);
                  win.setHeight(600);
                  win.addMember(chart);
                  win.show();
                    
                }
                 
             });
             tool.addButton(btn);
//             main.addMember(tool);
//              l= new DynamicForm();
//              l.setWidth100();
//              l.setHeight100();
//             l.setBackgroundColor("blue");
//             l.addChild(chart);
             
//             main.addMember(l);
//             main.addMember(hp);
//             main.animateShow(AnimationEffect.FADE, null,3000);
        }
        private static String[] getColours() {
            return new String[] { "#ff0000", "#00ff00", "#0000ff", "#ff9900", "#ff00ff", "#FFFF00", "#6699FF", "#339933" };
      }

      private static ChartData getPieChartAULayer2() {
            ChartData cd = new ChartData("亚洲销售情况(饼图第二层)", "font-size: 14px; font-family: Verdana; text-align: center;");
            cd.setBackgroundColour("#FFEEEE");
            PieChart pie = new PieChart();
            pie.setTooltip("#label# $#val#<br>#percent#");
            pie.setStartAngle(33);
            pie.setColours(getColours());
            pie.addSlices(new Slice(6000, "QLD"));
            pie.addSlices(new Slice(8000, "NSW"));
            pie.addSlices(new Slice(9000, "VIC"));
            pie.addSlices(new Slice(3000, "SA"));
            pie.addSlices(new Slice(1400, "TAS"));
            pie.addSlices(new Slice(1000, "NT"));
            pie.addSlices(new Slice(5000, "WA"));
            cd.addElements(pie);
            return cd;
      }

      private static ChartData getPieChartEULayer2() {
            ChartData cd = new ChartData("欧洲销售情况(饼图第二层)", "font-size: 14px; font-family: Verdana; text-align: center;");
            cd.setDecimalSeparatorComma(true);
            cd.setBackgroundColour("#EEEEFF");
            PieChart pie = new PieChart();
            pie.setTooltip("#label# $#val#<br>#percent#");
            pie.setStartAngle(33);
            pie.setColours(getColours());
            pie.addSlices(new Slice(9000, "BE"));
            pie.addSlices(new Slice(9000, "DE"));
            pie.addSlices(new Slice(7500, "ES"));
            pie.addSlices(new Slice(13000, "UK"));
            pie.addSlices(new Slice(6000, "FR"));
            pie.addSlices(new Slice(5000, "IT"));
            pie.addSlices(new Slice(6000, "RU"));
            pie.addSlices(new Slice(8000, "LT"));
            cd.addElements(pie);
            return cd;
      }

      private static ChartData getPieChartLayer1() {
            ChartData cd = new ChartData("各大洲销售情况(饼图第一层)", "font-size: 14px; font-family: Verdana; text-align: center;");
            cd.setBackgroundColour("#ffffff");
            PieChart pie = new PieChart();
            pie.setTooltip("#label# $#val#<br>#percent#");
            pie.setStartAngle(33);
            pie.setRadius(130);
            pie.setColours(getColours());
            Slice s1 = new Slice(33400, "AU");
            s1.setAnimation(new PieBounceAnimation(3000));
            s1.addChartClickHandler(new ChartClickHandler() {
                  @Override
                  public void onClick(ChartClickEvent event) {
                        chart.setChartData(getPieChartAULayer2());
                  }
            });
            pie.addSlices(s1);
            Slice s2 = new Slice(75000, "USA");
            s2.addChartClickHandler(new ChartClickHandler() {
                  @Override
                  public void onClick(ChartClickEvent event) {
                        chart.setChartData(getPieChartUSLayer2());
                  }
            });
            pie.addSlices(s2);
            Slice s3 = new Slice(63500, "EU");
            s3.addChartClickHandler(new ChartClickHandler() {
                  @Override
                  public void onClick(ChartClickEvent event) {
                        chart.setChartData(getPieChartEULayer2());
                  }
            });
            pie.addSlices(s3);
            cd.addElements(pie);
            return cd;
      }

      private static ChartData getPieChartUSLayer2() {
            ChartData cd = new ChartData("美洲销售情况(饼图第二层)", "font-size: 14px; font-family: Verdana; text-align: center;");
            cd.setBackgroundColour("#DDFFDD");
            PieChart pie = new PieChart();
            pie.setTooltip("#label# $#val#<br>#percent#");
            pie.setStartAngle(33);
            pie.setColours(getColours());
            pie.addSlices(new Slice(9000, "CA"));
            pie.addSlices(new Slice(9000, "NY"));
            pie.addSlices(new Slice(3500, "KY"));
            pie.addSlices(new Slice(5000, "CO"));
            pie.addSlices(new Slice(6000, "WA"));
            pie.addSlices(new Slice(5000, "NV"));
            pie.addSlices(new Slice(6000, "MO"));
            pie.addSlices(new Slice(8000, "LA"));
            pie.addSlices(new Slice(9000, "UT"));
            pie.addSlices(new Slice(3000, "TN"));
            pie.addSlices(new Slice(5500, "TX"));
            pie.addSlices(new Slice(1000, "VA"));
            pie.addSlices(new Slice(5000, "AK"));
            cd.addElements(pie);
            return cd;
      }
}
