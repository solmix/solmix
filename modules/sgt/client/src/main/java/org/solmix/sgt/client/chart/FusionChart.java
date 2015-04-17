package org.solmix.sgt.client.chart;

import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.smartgwt.client.core.Function;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.SC;
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
    private String chartType;
    public FusionChart(){
    	this(null,(String)null);
    }
    public FusionChart(FusionWidgetsType type){
    	this(null,type);
    }
	public FusionChart(final Roperation op, final FusionChartType chartType) {
		this(op,chartType.getValue());
	}
	public FusionChart(final Roperation op, final FusionWidgetsType chartType) {
		this(op,chartType.getValue());
	}
	protected FusionChart(final Roperation op, final String chartType) {
		swfId = "fcId_" + count;
        renderId="slx_fC_"+count;
        setID(renderId);
        ++count;
        setWidth100();
        setHeight100();
		this.op = op;
		this.chartType = chartType;
		if(op!=null){
			this.doOnRender(new Function() {

				@Override
				public void execute() {
					
					if (op != null) {
						SlxRPC.send(op, new RPCCallback() {
	
							@Override
							public void execute(RPCResponse response, Object rawData, RPCRequest request) {
								showChart(rawData.toString(), chartType);
							}
	
						});
					}

				}
			});
		}
	}
    public void setRedrawOnResize(boolean redrawOnResize){
    	super.setRedrawOnResize(redrawOnResize);
    	this.addResizedHandler(new ResizedHandler() {

            @Override
            public void onResized(ResizedEvent event) {
                resizeActiveChart();

            }
        });
    }
    /*private FusionChart() {
        setRedrawOnResize(false);
        
    }*/
    public String getChartId(){
        return swfId;
    }

    public String getRenderId(){
        return renderId;
    }
   
    @Override
	protected void onDraw(){
        super.onDraw();
    }
    
    public String getChartRoot(){
        if(chartRoot==null)
            chartRoot="xchart/";
      return chartRoot ;
    }
  
    public String getChartType() {
		return chartType;
	}
	/**
     * @return the canUpdate
     */
	public boolean isCanUpdate() {

		Element container = DOM.getElementById(renderId);
		boolean disposed = container == null ? false : true;
		if (!disposed)
			removeChart();
		return disposed;
	}
    
      @Override
	public String getInnerHTML() {
		getWidth();
		int width = getWidth();
		int height = getHeight();
		return "<div id='"
				+ renderId
				+ "'  eventproxy='"+swfId+"' style='margin-right:30px;float:left;vertical-align:center;' onscroll='return "+renderId+".$lh()' >"
				+ "<img src='images/loading_2.gif' style='margin-left:"
				+ (width - IMG_SIZE) / 2 + "px;margin-top:"
				+ (height - IMG_SIZE) / 2 + "px'>" + "</div>";
	}
      
   public void showChart(final String data, final String chartType,final int width,final int height,final ChartDataFormat dataFormat){
        	showChart(data,chartType,width+"",height+"",dataFormat);
   }
   
   public void showChart(final String data, final String chartType,final int width,final int height){
	   showChart(data,chartType,width,height,autoDiscoverFormat(data));
   }
   
   public void showChart(final String data, final String chartType,final String width,final String height,final ChartDataFormat dataFormat){
	   if(isCanUpdate()){
    	   if(dataFormat==ChartDataFormat.XML){
               _showXmlChart(data,chartType,width,height);
         	}else{
         		_showJsonChart(data,chartType,width,height);
         	}
       }else{
		   Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {
	
	                 @Override
	                 public boolean execute() {
	                       if(isCanUpdate()){
	                    	   if(dataFormat==ChartDataFormat.XML){
	                               _showXmlChart(data,chartType,width,height);
	                         	}else{
	                         		_showJsonChart(data,chartType,width,height);
	                         	}
	                             return false;
	                       }
	                       return true;
	                 }
	       }, 100);
       }
   }
   public void showChart(final String data, final String chartType,final String width,final String height){
	   showChart(data,chartType,width,height,autoDiscoverFormat(data));
   }
   private ChartDataFormat autoDiscoverFormat(String data){
	   data=data.trim();
	   if(data.startsWith("{")){
		   return ChartDataFormat.JSON;
	   }else if(data.startsWith("<")){
		   return ChartDataFormat.XML;
	   }else{
		   SC.warn("Can't Auto disCovery chart data format,Data:"+data);
		   throw new IllegalArgumentException();
	   }
   }

   public void showChart(String data, String chartType) {
	   showChart(data, chartType, getInnerWidth(), getInnerHeight());
   }

	public void resizeContainerAndActiveChart(String chartId, int width,int height) {
		setWidth(width);
		setHeight(height);
		resizeActiveChart(width, height);
	}

	public void resizeActiveChart() {
		resizeActiveChart(this.getInnerWidth(), this.getInnerHeight());
	}

 
  
     
     
     protected void onInitialize()
     {
           super.onInit();
     }
    
    private native void _showXmlChart(String data, String chartType, String width, String height) /*-{
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
    
    private native void renderChart(String chartType, String width, String height) /*-{
    var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
    var renderId=this.@org.solmix.sgt.client.chart.FusionChart::getRenderId()();
    var chartRoot=this.@org.solmix.sgt.client.chart.FusionChart::getChartRoot()();
    var chartPath=chartRoot+chartType+".swf";
    try {
        var chart = new $wnd.FusionCharts(chartPath, chartId, width, height,"0","1");
        chart.render(renderId);
      } catch (e) {
       alert(e);
    }  
	}-*/;
    private native void setJSONData(final String data) /*-{
    var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
    try {
        //if (chart.setJSONData != null ) chart.setJSONData(data);
        //else chart.setJSONData(data);
        chart.render(renderId);
        chart.addEventListener ("Rendered" , function(DOMId){
        var did = $wnd.FusionCharts(chartId);
        did.setJSONData(data);
        } );
      } catch (e) {
       alert(e);
    }  
	}-*/;

    private native void _showJsonChart(final String data, String chartType, String width, String height) /*-{
    var chartId=this.@org.solmix.sgt.client.chart.FusionChart::getChartId()();
    var renderId=this.@org.solmix.sgt.client.chart.FusionChart::getRenderId()();
    var chartRoot=this.@org.solmix.sgt.client.chart.FusionChart::getChartRoot()();
    var chartPath=chartRoot+chartType+".swf";
    try {
        var chart = new $wnd.FusionCharts(chartPath, chartId, width, height,"0","1");
        //if (chart.setJSONData != null ) chart.setJSONData(data);
        //else chart.setJSONData(data);
        chart.render(renderId);
        chart.addEventListener ("Rendered" , function(DOMId){
        var did = $wnd.FusionCharts(chartId);
        did.setJSONData(data);
        } );
      } catch (e) {
       alert(e);
    }  
	}-*/;
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
	@Override
	protected native void onInit()/*-{
	    this.@org.solmix.sgt.client.chart.FusionChart::onInitialize()();
	    
	    // Handle redraw case
	    var self = this.@com.smartgwt.client.widgets.BaseWidget::getOrCreateJsObj()();
	    self.redraw = function() {
	    }
	    
	    }-*/;

	public void showChart(String dataAsString, FusionChartType type) {
		showChart(dataAsString, type.getValue());
	}
	public void showChart(String dataAsString, FusionWidgetsType type) {
		showChart(dataAsString, type.getValue());
		
	}
	
}
