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

package org.solmix.fusionchartxt.client;

import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;
import org.solmix.sgt.client.advanceds.XMLCallBack;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-12-6
 */

public class FusionChartX extends Canvas
{
    
   

    private static int count = 0;

    private  String swfId;
    private String renderId;
    private static String chartRoot;

    public FusionChartX(final String data, final String chartType,Canvas parentElement){
    	final FusionChartX _this = new FusionChartX();
    	parentElement.addChild(_this);
    	 SlxRPC.send( new Roperation(data,DSOperationType.FETCH), new XMLCallBack(){

             @Override
             public void execute(RPCResponse response, String xmlString, RPCRequest request) {
            	 _this.getFusionChart(xmlString, chartType);
                 
             }
             
         });
    	
    }
    public FusionChartX() {
        swfId = "fcxId_" + getID();
        renderId="slx_chartC_"+getID();
        setRedrawOnResize(false);
    }
    public String getChartId(){
        return swfId;
    }

    public String getRenderId(){
        return renderId;
    }
    public String getChartRoot(){
        if(chartRoot==null)
            chartRoot="xchart/";
      return chartRoot ;
    }
  
    /**
     * @return the canUpdate
     */
    public boolean isCanUpdate() {
        
        Element container=DOM.getElementById(renderId);
        boolean disposed=container==null?false:true;
        if(!disposed)
            removeChart();
        return disposed;
    }
    
  
   @Override
    public String getInnerHTML() {
//	   return "<div id='"+renderId+"'  eventproxy='"+swfId+"' style='margin-right:30px;float:left;vertical-align:center;' onscroll='return "+renderId+".$lh()'>" +
        return "<div id='"+renderId+"'   style='margin-right:30px;float:left;vertical-align:center;' >" +
        		"<img src=\"images/loading.gif\" width=\"16\" height=\"16\" style=\"margin-right:8px;float:left;vertical-align:top;\"/>正在加载图像...<br/></div>";
    }
   public void getFusionChart(final String data, final String chartType,final int width,final int height){
	    Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				if(isCanUpdate()){
					_getFusionChart(data,chartType,width,height);
					return false;
				}
				return true;
			}
       }, 100);
   }

    public native void _getFusionChart(String data, String chartType, int width, int height) /*-{
        var chartId=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartId()();
        var renderId=this.@org.solmix.fusionchartxt.client.FusionChartX::getRenderId()();
        var chartRoot=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        try {
           var chart = new $wnd.FusionCharts(chartPath, chartId, width, height);
           if (chart.setXMLData != null ) chart.setXMLData(data);
           else chart.setDataXML(data);
           chart.render(renderId);
         } catch (e) {
         alert(e);
        }  
    }-*/;
    public void getFusionChart(final String data, final String chartType,final String width,final String height){
    	 Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

 			@Override
 			public boolean execute() {
 				if(isCanUpdate()){
 					_getFusionChart(data,chartType,width,height);
 					return false;
 				}
 				return true;
 			}
        }, 100);
    }
    public native void _getFusionChart(String data, String chartType, String width, String height) /*-{
        var chartId=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartId()();
        var renderId=this.@org.solmix.fusionchartxt.client.FusionChartX::getRenderId()();
        var chartRoot=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        try {
            var chart = new $wnd.FusionCharts(chartPath, chartId, width, height,"0","1");
            if (chart.setXMLData != null ) chart.setXMLData(data);
            else chart.setDataXML(data);
            chart.render(renderId);
          } catch (e) {
           alert(e);
        }  
    }-*/;
    public void resizeContainerAndActiveChart(String chartId, int width, int height) {
        setWidth(width);
      setHeight(height);
      resizeActiveChart( width, height);
  }
    public void resizeActiveChart(){
        resizeActiveChart(this.getInnerWidth(),this.getInnerHeight());
    }

  public native void resizeActiveChart(int width, int height) /*-{
        try {
        var chartId=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartId()();
        var chart = $wnd.FusionCharts(chartId);
        // resizeTo() does not exist in FusionCharts free version
        if (chart.resizeTo != null) chart.resizeTo(width, height);
  } catch (e) {
              // ignore
        }     
  }-*/;
    
    /**
     * Remove a chart instance from page and memory
     * @param chartId
     */
    public native void removeChart() /*-{
          try {
          var chartId=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartId()();
          var chart = $wnd.FusionCharts(chartId);
          // dispose() does not exist in FusionCharts free version
          if (chart.dispose != null) chart.dispose();
          } catch (e) {
                // ignore
          }
    }-*/;
  public void getFusionChart(final String data,final String chartType) {
	  Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				if(isCanUpdate()){
					_getFusionChart(data,chartType);
					return false;
				}
				return true;
			}
      }, 100);
  }
    public void _getFusionChart(String data, String chartType) {
    	
        getFusionChart(data, chartType, getInnerWidth(), getInnerHeight());
  }
    public void getFusionMap(String data, String chartType) {
        getFusionMap(data, chartType, getInnerWidth(), getInnerHeight());
  }

public native void getFusionMap(String data, String chartType, int width, int height) /*-{
  if ($wnd.FusionCharts.setCurrentRenderer != null) {
        $wnd.FusionCharts.setCurrentRenderer('javascript');
        var chartId=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartId()();
        var renderId=this.@org.solmix.fusionchartxt.client.FusionChartX::getRenderId()();
        var chartRoot=this.@org.solmix.fusionchartxt.client.FusionChartX::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        var chart = new $wnd.FusionCharts(chartPath, chartId, width, height);
            chart.setXMLData(data);
            chart.render(renderId);
  }
  }-*/;
	@Override
	protected native void onInit()/*-{
	    this.@org.solmix.fusionchartxt.client.FusionChartX::onInitialize()();
	    
	    // Handle redraw case
	    var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
	    self.redraw = function() {
	    }
	    
	    }-*/;
	
	protected void onInitialize()
	{
		super.onInit();
	}
}
