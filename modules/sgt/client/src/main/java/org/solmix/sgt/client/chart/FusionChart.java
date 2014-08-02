package org.solmix.sgt.client.chart;

import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;
import org.solmix.sgt.client.advanceds.XMLCallBack;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;


public class FusionChart extends HTMLFlow
{
    private static int count = 0;

    private  String swfId;
    private String renderId;
    private static String chartRoot;
    private  Roperation op;
    private static int IMG_SIZE=42;
    private FusionChartType chartType;

    public FusionChart(final Roperation op, final FusionChartType chartType)
    {
        swfId = "fcxId_" + getID();
        renderId = "slx_chartC_" + getID();
        this.op = op;
        this.chartType = chartType;
        this.doOnRender(new Function() {

            @Override
            public void execute() {
                SlxRPC.send(op, new XMLCallBack() {

                    @Override
                    public void execute(RPCResponse response, String xmlString, RPCRequest request) {
                        _getFusionChart(xmlString, chartType.getValue());
                    }

                });

            }
        });
        this.addResizedHandler(new ResizedHandler() {

            @Override
            public void onResized(ResizedEvent event) {
                resizeActiveChart();

            }
        });
    }
    private FusionChart() {
        setRedrawOnResize(false);
        
    }
    public String getChartId(){
        return swfId;
    }
    protected void onDraw(){
        super.onDraw();
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
        getWidth();
        int width = getWidth();
        int height = getHeight();
        return "<div id='" + renderId + "'   style='margin-right:30px;float:left;vertical-align:center;' >"
            + "<img src='images/loading_2.gif' style='margin-left:" + (width - IMG_SIZE) / 2 + "px;margin-top:" + (height - IMG_SIZE) / 2 + "px'>"
            + "</div>";
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
        var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
        var renderId=this.@org.solmix.sgt.client.chart.FusionChart::getRenderId()();
        var chartRoot=this.@org.solmix.sgt.client.chart.FusionChart::getChartRoot()();
        var chartPath=chartRoot+chartType+".swf";
        try {
//            $wnd.FusionCharts.setCurrentRenderer("javascript");
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
        var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
        var renderId=this.@org.solmix.sgt.client.chart.FusionChart::getRenderId()();
        var chartRoot=this.@org.solmix.sgt.client.chart.FusionChart::getChartRoot()();
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
        var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
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
          var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
          var chart = $wnd.FusionCharts(chartId);
          // dispose() does not exist in FusionCharts free version
          if (chart.dispose != null) chart.dispose();
          } catch (e) {
                // ignore
          }
    }-*/;

    public void getFusionChart(final String data, final String chartType) {
        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

            @Override
            public boolean execute() {
                if (isCanUpdate()) {
                    _getFusionChart(data, chartType);
                    return false;
                }
                return true;
            }
        }, 100);
    }

    public void _getFusionChart(String data, String chartType) {
        getFusionChart(data, chartType, getInnerWidth(), getInnerHeight());
    }
   
      @Override
      protected native void onInit()/*-{
          this.@org.solmix.sgt.client.chart.FusionChart::onInitialize()();
          
          // Handle redraw case
          var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
          self.redraw = function() {
          }
          
          }-*/;
//      
      protected void onInitialize()
      {
            super.onInit();
      }
}
