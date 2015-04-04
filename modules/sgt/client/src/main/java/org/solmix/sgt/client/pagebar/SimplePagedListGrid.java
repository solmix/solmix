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

package org.solmix.sgt.client.pagebar;

import org.solmix.sgt.client.advanceds.JSCallBack;
import org.solmix.sgt.client.advanceds.JSCallBacks;
import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;
import org.solmix.sgt.client.widgets.Marsk;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.IsIntegerValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-17
 */

public class SimplePagedListGrid extends ListGrid
{

    public static final int DEFAULT_PAGE_SIZE = 75;




    // 上一页
    TopImgButton forward = new TopImgButton(16, 16, "[SKIN]actions/next.png", "[SKIN]actions/next-disabled.png");

    // 下一页
    TopImgButton backward = new TopImgButton(16, 16, "[SKIN]actions/prev.png", "[SKIN]actions/prev-disabled.png");

    // 末页

    IntegerRangeValidator v = new IntegerRangeValidator();

    // 输入框form
    DynamicForm pageForm;

    ToolStrip gridPageControls;

    // 页码输入框
    protected TextItem pageText;

    // 总页数
    protected Label totalLabel;

    // 总页数
    protected Label totalRow;

    int pageSize=DEFAULT_PAGE_SIZE;

    /**
     * 当前页
     */
    private int pageNum = -1;

    /**
     * 总页数
     */
    private int totalPage = -1;

    /**
     * 数据记录条数
     */
    private int totalRowNum = -1;

    

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }


    private final ListGrid owner;
    

    public SimplePagedListGrid()
    {
        this.owner = this;

        this.setWidth100();
        this.setHeight100();
        gridPageControls = new ToolStrip();

        gridPageControls.setWidth100();
        gridPageControls.setHeight(24);
        gridPageControls.setDefaultLayoutAlign(VerticalAlignment.CENTER);

        pageText = new TextItem();
        pageText.setWidth(50);
        pageText.setHeight(20);
        pageText.setShowTitle(false);
        pageText.setTextAlign(Alignment.RIGHT);
        // IntegerRangeValidator
        pageText.setValidators(v, new IsIntegerValidator());
        pageText.setValidateOnChange(true);
        pageText.addChangedHandler(new ChangedHandler() {

            @Override
            public void onChanged(ChangedEvent event) {
                try {
                    goToPage((Integer.valueOf(pageText.getValueAsString()).intValue()));
                } catch (Exception e) {
                    SC.say(pageText.getValueAsString() + "不是整数");
                }

            }

        });

        totalRow = new Label();
        totalRow.setWrap(false);
        totalRow.setWidth(40);
        totalRow.setContents("没有数据");

        totalLabel = new Label();
        totalLabel.setWrap(false);
        totalLabel.setWidth(50);

        pageForm = new DynamicForm();
        pageForm.setNumCols(1);
        pageForm.setWidth(45);
        pageForm.setItems(pageText);

        
        Label ye = new Label("第");
        ye.setWidth(4);


        forward.setTitle("下一页");
        forward.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPage(pageNum + 1);

            }

        });
       
        backward.setTitle("上一页");
        backward.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPage(pageNum - 1);

            }

        });
        gridPageControls.addSpacer(6);
        gridPageControls.addMember(backward);
        gridPageControls.addSpacer(6);

        // gridPageControls.addMember(go);
        gridPageControls.addMember(ye);
        gridPageControls.addMember(pageForm);
        gridPageControls.addMember(totalLabel);
        gridPageControls.addSpacer(6);
        gridPageControls.addMember(forward);
        
        gridPageControls.addFill();
        gridPageControls.addMember(totalRow);

        gridPageControls.setAlign(Alignment.LEFT);
        initPageBar(null);
        this.setGridComponents(new Object[] { ListGridComponent.HEADER, ListGridComponent.FILTER_EDITOR, ListGridComponent.BODY, gridPageControls });

    }

    public ToolStrip getGridPageControls() {
		return gridPageControls;
	}

    private Criteria _criteria;

    private String dataSource;

    private String operationId;
    public void loadData(Criteria criteria, String dataSource,String operationId){
    	loadData(criteria, dataSource, operationId,1);
    }
    public void loadData(Criteria criteria, String dataSource,String operationId, int pageNum) {
        final ListGrid grid = this;
        this.pageNum = pageNum;
        this.dataSource=dataSource;
        this.operationId=operationId;
        if (criteria == null)
            criteria = this.getCriteria();
        this._criteria =criteria!=null? new Criteria(criteria.getJsObj()):new Criteria();
        Roperation operation = new Roperation();
        operation.setDataSource(dataSource);
        operation.setOperationType(DSOperationType.FETCH);
        operation.setOperationId(operationId);
        operation.setCriteria(criteria);
        if(pageSize>0){
	        int startNum = (pageNum - 1) * pageSize;
	        operation.setStartRow(startNum);
	        operation.setEndRow(pageNum * pageSize);
        }else{
        	operation.setStartRow(-1);
        	operation.setEndRow(0);
        }
        Marsk.showLoadData();
        SlxRPC.send(operation, new JSCallBack() {
			
			@Override
			public void execute(RPCResponse response, JavaScriptObject rawData,
					RPCRequest request) {
				Marsk.hidden();
				 if (response.getStatus() == DSResponse.STATUS_SUCCESS) {
					 RecordList serdata = new RecordList(response.getDataAsObject());
                     grid.setData(serdata);
                         initPageBar(response);
                 } else if (response.getStatus() == DSResponse.STATUS_VALIDATION_ERROR) {

                 } else {
                     SC.warn(response.getDataAsString());
                 }
				
			}
		});
        
    }


    private void initPageBar(RPCResponse response) {
    	int startNum=0;
    	int endNum=0;
    	if(response==null){
    		totalRowNum=-1;
    	}else{
    		totalRowNum = response.getAttributeAsInt("totalRows");
    		startNum = response.getAttributeAsInt("startRow") + 1;
    		endNum = response.getAttributeAsInt("endRow");
    	}
        if (startNum > endNum)
            startNum = endNum;
        if (!gridPageControls.isVisible())
            gridPageControls.show();
        if(totalRowNum>=0){
        	totalRow.setContents("共" + totalRowNum + "条");
        }
        totalPage = ((totalRowNum % pageSize) == 0) ? totalRowNum / pageSize : totalRowNum / pageSize + 1;
        totalLabel.setContents("页,共" + totalPage + "页");
        pageNum = pageNum==-1?1:pageNum;
        pageText.setValue(pageNum);
        backward.disable();
        if (totalRowNum > pageSize) {
            forward.enable();
        } else {
            forward.disable();
        }
        v.setMax(totalPage);
        v.setMin(1);

    }

    /**
     * 转到指定页，获取分页数据并装载， 获取数据过程中，加模态遮罩
     * 
     * @param pageNum
     */
    public void goToPage(int pageNum) {
        if (pageNum > totalPage)
            pageNum = totalPage;
        if (pageNum < 1)
            pageNum = 1;
        if (pageNum == this.pageNum) {
            return;
        }
        loadData(this._criteria, dataSource, operationId, pageNum);
    }
    @Override
	public void removeSelectedData(){
    	removeSelectedData((JSCallBacks)null);
    }
    
	public void removeSelectedData(JSCallBacks callBack) {
		final ListGridRecord[] records = getSelectedRecords();
		String dsID = getDataSource().getID();
		Roperation[] opers;
		if (records != null) {
			opers = new Roperation[records.length];
			for (int i = 0; i < records.length; i++) {
				Roperation oper = new Roperation();
				oper.setDataSource(dsID);
				oper.setOperationType(DSOperationType.REMOVE);
				Criteria c = new Criteria();
				String[] atrrs = records[i].getAttributes();
				for (String atr : atrrs) {
					c.addCriteria(atr, records[i].getAttribute(atr));
				}
				oper.setCriteria(c);
				opers[i] = oper;
			}
			if (callBack != null) {
				SlxRPC.send(opers, callBack);
			} else {
				SlxRPC.send(opers, new JSCallBacks() {

					@Override
					public void execute(RPCResponse[] responses,
							JavaScriptObject rawData, RPCRequest request) {
						if(responses.length<=0){
							return;
						}
						if (responses[0].getStatus() == RPCResponse.STATUS_SUCCESS) {
							getRecordList().removeList(records);
						}else{
							SC.warn("删除记录错误,错误代码["+responses[0].getStatus()+"]!");
						}

					}
				});
			}
		}
	}
}
