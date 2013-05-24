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

import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-19
 */

public class SandBoxMainView extends HLayout
{

   public static class Factory extends AbstractFactory 
   {

      private String id;

      public Canvas create()
      {
         SandBoxMainView pane = new SandBoxMainView();
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
   private static final String EXAMPLE_TEXT1 = "基于OSGI框架,采用SOA设计.实现模块化、动态加载、异构环境运行、多语言开发。 ";

   private static final String EXAMPLE_TEXT2 = "用企业总线<ESB>和服务注册机制,集成DAO、事务处理、IOC、单点登录、安全控制、系统监控、事件追踪等企业和互联网典型应用环境 ";

   private static final String EXAMPLE_TEXT3 = "遵循UI 和后台分离,数据和绑定分离 原则。通过消息驱动,绑定机制解耦,服务通过绑定对RIA 提供一致的支持（AJAX-GWT、flex"
      + "、JAVAFX);UI提供公共接口或者经过NMR路由支持后台多语言,异构。";

   private Layout main;
//   private  ChartWidget chart;
   private Button restBtn;
   public SandBoxMainView()
   {

      this.setMargin(3);
      HLayout left = new HLayout();
      left.setWidth("40%");
      final Label label1 = createLabel(EXAMPLE_TEXT1);
      final Label label2 = createLabel(EXAMPLE_TEXT2);
      final Label label3 = createLabel(EXAMPLE_TEXT3);

      label1.setShowEdges(true);
      label1.setEdgeImage("edges/custom/sharpframe_10.png");
      label1.setEdgeSize(10);

      label2.setLeft(80);
      label2.setTop(80);
      label2.setEdgeImage("edges/custom/frame_10.png");
      label2.setEdgeSize(10);

      label3.setLeft(160);
      label3.setTop(160);
      label3.setEdgeImage("edges/custom/glow_15.png");
      label3.setEdgeSize(15);

      left.addChild(label1);
      left.addChild(label2);
      left.addChild(label3);
      this.addMember(left);
      Layout right = new Layout();
      right.setWidth("60%");
      restBtn=new Button("rest");
//      chart = new ChartWidget();
//      chart.setSize("400", "300");
//      ChartData cd = getPieChartLayer1();
//      chart.setChartData(cd);
//      
//      right.addMember(chart);
//      right.addMember(restBtn);
//      this.addMember(right);

   }
 private Label createLabel(String EXAMPLE_TEXT)
 {
    Label label = new Label(EXAMPLE_TEXT);
    label.setWidth(250);
    label.setPadding(8);
    label.setBackgroundColor("#b3caec");
    label.setCanDragReposition(true);
    label.setCanDragResize(true);
    label.setDragAppearance(DragAppearance.TARGET);
    label.setShowEdges(true);
    label.setKeepInParentRect(false);
    return label;
 }
   private String[] getColours() {
       return new String[] { "#ff0000", "#00ff00", "#0000ff", "#ff9900", "#ff00ff", "#FFFF00", "#6699FF", "#339933" };
 }
//   /**
// * @return
// */
//private ChartData getPieChartLayer1() {
//    ChartData cd = new ChartData("Sales by Region - Layer 1", "font-size: 14px; font-family: Verdana; text-align: center;");
//    cd.setBackgroundColour("#ffffff");
//    PieChart pie = new PieChart();
//    pie.setTooltip("#label# $#val#<br>#percent#");
//    pie.setStartAngle(33);
//    pie.setRadius(130);
//    pie.setColours(getColours());
//    Slice s1 = new Slice(33400, "AU");
//    s1.addChartClickHandler(new ChartClickHandler() {
//          @Override
//          public void onClick(ChartClickEvent event) {
//                chart.setChartData(getPieChartAULayer2());
//          }
//    });
//    pie.addSlices(s1);
//    Slice s2 = new Slice(75000, "USA");
//    s2.addChartClickHandler(new ChartClickHandler() {
//          @Override
//          public void onClick(ChartClickEvent event) {
//                chart.setChartData(getPieChartUSLayer2());
//          }
//    });
//    pie.addSlices(s2);
//    Slice s3 = new Slice(63500, "EU");
//    s3.addChartClickHandler(new ChartClickHandler() {
//          @Override
//          public void onClick(ChartClickEvent event) {
//                chart.setChartData(getPieChartEULayer2());
//          }
//    });
//    pie.addSlices(s3);
//    cd.addElements(pie);
//    restBtn.enable();
//    return cd;
//}
//
//
//private ChartData getPieChartUSLayer2() {
//    ChartData cd = new ChartData("Sales in USA - Layer 2", "font-size: 14px; font-family: Verdana; text-align: center;");
//    cd.setBackgroundColour("#DDFFDD");
//    PieChart pie = new PieChart();
//    pie.setTooltip("#label# $#val#<br>#percent#");
//    pie.setStartAngle(33);
//    pie.setColours(getColours());
//    pie.addSlices(new Slice(9000, "CA"));
//    pie.addSlices(new Slice(9000, "NY"));
//    pie.addSlices(new Slice(3500, "KY"));
//    pie.addSlices(new Slice(5000, "CO"));
//    pie.addSlices(new Slice(6000, "WA"));
//    pie.addSlices(new Slice(5000, "NV"));
//    pie.addSlices(new Slice(6000, "MO"));
//    pie.addSlices(new Slice(8000, "LA"));
//    pie.addSlices(new Slice(9000, "UT"));
//    pie.addSlices(new Slice(3000, "TN"));
//    pie.addSlices(new Slice(5500, "TX"));
//    pie.addSlices(new Slice(1000, "VA"));
//    pie.addSlices(new Slice(5000, "AK"));
//    cd.addElements(pie);
//    restBtn.enable();
//    return cd;
//}
//private ChartData getPieChartAULayer2() {
//    ChartData cd = new ChartData("Sales in Australia - Layer 2", "font-size: 14px; font-family: Verdana; text-align: center;");
//    cd.setBackgroundColour("#FFEEEE");
//    PieChart pie = new PieChart();
//    pie.setTooltip("#label# $#val#<br>#percent#");
//    pie.setStartAngle(33);
//    pie.setColours(getColours());
//    pie.addSlices(new Slice(6000, "QLD"));
//    pie.addSlices(new Slice(8000, "NSW"));
//    pie.addSlices(new Slice(9000, "VIC"));
//    pie.addSlices(new Slice(3000, "SA"));
//    pie.addSlices(new Slice(1400, "TAS"));
//    pie.addSlices(new Slice(1000, "NT"));
//    pie.addSlices(new Slice(5000, "WA"));
//    cd.addElements(pie);
//    restBtn.enable();
//    return cd;
//}
//
//private ChartData getPieChartEULayer2() {
//    ChartData cd = new ChartData("Sales in Europe - Layer 2", "font-size: 14px; font-family: Verdana; text-align: center;");
//    cd.setDecimalSeparatorComma(true);
//    cd.setBackgroundColour("#EEEEFF");
//    PieChart pie = new PieChart();
//    pie.setTooltip("#label# $#val#<br>#percent#");
//    pie.setStartAngle(33);
//    pie.setColours(getColours());
//    pie.addSlices(new Slice(9000, "BE"));
//    pie.addSlices(new Slice(9000, "DE"));
//    pie.addSlices(new Slice(7500, "ES"));
//    pie.addSlices(new Slice(13000, "UK"));
//    pie.addSlices(new Slice(6000, "FR"));
//    pie.addSlices(new Slice(5000, "IT"));
//    pie.addSlices(new Slice(6000, "RU"));
//    pie.addSlices(new Slice(8000, "LT"));
//    cd.addElements(pie);
//    restBtn.enable();
//    return cd;
//}
}
