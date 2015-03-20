package org.solmix.sgt.client.widgets;

import org.solmix.sgt.client.advanceds.JSCallBacks;
import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;
import org.solmix.sgt.client.chart.FusionChart;
import org.solmix.sgt.client.chart.FusionChartType;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

public class ListChartGrid extends VLayout {

	FusionChart chart;
	ListGrid grid ;
	private String dataSource;
	private String listOp;
	private String chartOp;
	public ListChartGrid(String dataSource,String chartOp){
		this(dataSource,null,chartOp);
	}
	public ListChartGrid(String dataSource,String listOp,String chartOp){
		this.dataSource=dataSource;
		this.listOp=listOp;
		this.chartOp=chartOp;
		setWidth100();
		setHeight100();
		SectionStack cdSect = new SectionStack();
		cdSect.setVisibilityMode(VisibilityMode.MULTIPLE);
		cdSect.setHeight100();
		SectionStackSection zoneRmr = new SectionStackSection();
		zoneRmr.setTitle("图形结果");
		zoneRmr.setExpanded(true);
		zoneRmr.setResizeable(false); 
		chart = new FusionChart( );
		chart.setWidth100();
		chart.setHeight("50%");
		zoneRmr.setItems(chart);
		
		
		SectionStackSection listSect = new SectionStackSection();
		listSect.setTitle("查询结果");
		listSect.setExpanded(true);
		grid = new ListGrid();
		grid.setDataSource(dataSource);
		listSect.setItems(grid);
		cdSect.setSections(zoneRmr,listSect);
		addMember(cdSect);
	}
	
	public void fetchData(Criteria c){
		Roperation oper = new Roperation();
		oper.setDataSource(dataSource);
		oper.setOperationType(DSOperationType.FETCH);
		if(listOp!=null){
			oper.setOperationId(listOp);
		}
		if(c!=null){
			oper.setCriteria(c);
		}
		Roperation cop = new Roperation();
		cop.setDataSource(dataSource);
		cop.setOperationType(DSOperationType.FETCH);
		if(chartOp!=null){
			cop.setOperationId(chartOp);
		}
		if(c!=null){
			cop.setCriteria(c);
		}
		SlxRPC.send(new Roperation[] { oper, cop }, new JSCallBacks() {

			@Override
			public void execute(RPCResponse[] responses,
					JavaScriptObject rawData, RPCRequest request) {
				if (responses.length == 2) {

					RPCResponse response = responses[0];
					RecordList list = new RecordList(response.getDataAsObject());
					grid.setData(list);
					RPCResponse chartData = responses[1];
					chart.showChart(chartData.getDataAsString(), FusionChartType.Line);
				}
			}
		});
	}
}
