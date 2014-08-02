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

package org.solmix.fusionchart.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-12-6
 */

public class FreeChart extends Canvas
{
    
   

    private static int count = 0;

    private  String swfId="fcId_0";
    private String renderId="slx_fC_0";
    private static String chartRoot;

    public FreeChart() {
        swfId = "fcId_" + count;
        renderId="slx_fC_"+count;
        setID(renderId);
        ++count;
        setRedrawOnResize(false);
        setWidth100();
        setHeight100();
    }
    public String getChartId(){
        return swfId;
    }

    public String getRenderId(){
        return renderId;
    }
    public String getChartRoot(){
        if(chartRoot==null)
            chartRoot=GWT.getModuleName()+"/fchart/";
      return chartRoot ;
    }
    
    
    /**
     * @return the canUpdate
     */
    public boolean isCanUpdate() {
        
        Element container=DOM.getElementById(renderId);
        boolean disposed=container==null?false:true;
        return disposed;
    }
    
  
   @Override
    public String getInnerHTML() {
       
	   return "<div id='"+renderId+"'  eventproxy='"+swfId+"' style='margin-right:30px;float:left;vertical-align:center;' onscroll='return "+renderId+".$lh()'>" +
       		"<img src=\"images/loading.gif\" width=\"16\" height=\"16\" style=\"margin-right:8px;float:left;vertical-align:top;\"/>正在加载图像...<br/></div>";
	   }

    public native void getFusionChart(String data, String chartType, int width, int height) /*-{
        var chartId=this.@org.solmix.fusionchart.client.FusionChart::getChartId()();
        var renderId=this.@org.solmix.fusionchart.client.FusionChart::getRenderId()();
        var chartRoot=this.@org.solmix.fusionchart.client.FusionChart::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        try {
           var chart = new $wnd.FusionCharts(chartPath, chartId, width, height);
           if (chart.setXMLData != null )
            chart.setXMLData(data);
           else chart.setDataXML(data);
           chart.render(renderId);
         } catch (e) {
        }  
    }-*/;
    public native void getFusionChart(String data, String chartType, String width, String height) /*-{
        var chartId=this.@org.solmix.fusionchart.client.FusionChart::getChartId()();
        var renderId=this.@org.solmix.fusionchart.client.FusionChart::getRenderId()();
        var chartRoot=this.@org.solmix.fusionchart.client.FusionChart::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        try {
            var chart = new $wnd.FusionCharts(chartPath, chartId, width, height,"0","1");
            if (chart.setXMLData != null ) chart.setXMLData(data);
            else chart.setDataXML(data);
            chart.render(renderId);
          } catch (e) {
        }  
    }-*/;
    
    public void getFusionChart(String data, String chartType) {
        getFusionChart(data, chartType, getInnerWidth(), getInnerHeight());
  }
  

}
