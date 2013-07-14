/*
 * SOLMIX PROJECT
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

package com.smartgwt.extensions.pagebar.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.ExportDisplay;
import com.smartgwt.client.types.ExportFormat;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.form.validator.IsIntegerValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.menu.IconMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.extensions.advanceds.client.Roperation;
import com.smartgwt.extensions.advanceds.client.SlxRPCCallBack;
import com.smartgwt.extensions.advanceds.client.SlxRPCManager;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-17
 */

public class PagedListGrid extends ListGrid
{

    public static final int DEFAULT_PAGE_SIZE = 75;

    private boolean usePageBar;

    ModalWindow mask;

    TopImgButton first = new TopImgButton(16, 16, "[SKIN]actions/first.png", "[SKIN]actions/first-disabled.png");

    // 上一页
    TopImgButton forward = new TopImgButton(16, 16, "[SKIN]actions/next.png", "[SKIN]actions/next-disabled.png");

    // 下一页
    TopImgButton backward = new TopImgButton(16, 16, "[SKIN]actions/prev.png", "[SKIN]actions/prev-disabled.png");

    // 末页
    TopImgButton last = new TopImgButton(16, 16, "[SKIN]actions/last.png", "[SKIN]actions/last-disabled.png");

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

    int pageSize;

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
     * @return the usePageBar
     */
    public boolean isUsePageBar() {
        return usePageBar;
    }

    /**
     * @param usePageBar the usePageBar to set
     */
    public void setUsePageBar(boolean usePageBar) {
        this.usePageBar = usePageBar;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the maskMessage
     */
    public String getMaskMessage() {
        return maskMessage;
    }

    /**
     * @param maskMessage the maskMessage to set
     */
    public void setMaskMessage(String maskMessage) {
        this.maskMessage = maskMessage;
    }

    public PagedListGrid(int pageSize)
    {
        this(null, pageSize, true);
    }

    public PagedListGrid(boolean showPageBar)
    {
        this(null, DEFAULT_PAGE_SIZE, showPageBar);
    }

    public PagedListGrid(int pageSize, boolean showPageBar)
    {
        this(null, pageSize, showPageBar);
    }

    private String maskMessage;

    private Canvas maskCanvas;

    private ListGrid owner;

    public PagedListGrid(Canvas maskCanvas, int pageSize, boolean usePageBar)
    {
        this.maskCanvas = maskCanvas;
        this.usePageBar = usePageBar;
        this.pageSize = pageSize;
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

        // 转到按钮
        IButton go = new IButton("转到");
        go.setWidth(40);
        go.setHeight(20);
        Label ye = new Label("第");
        ye.setWidth(4);

        gridPageControls.setHeight(20);
        // gridPageControls.setStyleName("normal");
        first.setTitle("首页");
        first.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPage(1);

            }

        });

        forward.setTitle("下一页");
        forward.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPage(pageNum + 1);

            }

        });
        last.setTitle("最后一页");
        last.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                goToPage(totalPage);

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
        gridPageControls.addMember(first);
        gridPageControls.addSpacer(6);
        gridPageControls.addMember(backward);
        gridPageControls.addSpacer(6);

        // gridPageControls.addMember(go);
        gridPageControls.addMember(ye);
        gridPageControls.addMember(pageForm);
        gridPageControls.addMember(totalLabel);
        gridPageControls.addSpacer(6);
        gridPageControls.addMember(forward);
        gridPageControls.addSpacer(6);
        gridPageControls.addMember(last);
        gridPageControls.addSeparator();
        DynamicForm f = new DynamicForm();
        ComboBoxItem pagSize = new ComboBoxItem();
        pagSize.setValue(getPageSize());
        pagSize.setWidth(60);
        pagSize.setTitle("每页");
        pagSize.setType("comboBox");
        pagSize.setValueMap("75条", "150条", "250条", "500条", "全部");
        pagSize.addChangedHandler(new ChangedHandler() {

            @Override
            public void onChanged(ChangedEvent event) {
                Object v = event.getValue();
                if ("75条".equals(v)) {
                    setPageSize(75);
                } else if ("150条".equals(v)) {
                    setPageSize(150);
                } else if ("250条".equals(v)) {
                    setPageSize(250);
                } else if ("500条".equals(v)) {
                    setPageSize(500);
                } else if ("全部".equals(v)) {
                    setPageSize(-1);
                    setShowAllRecords(true);

                }
                firstCall = true;
                pagedFetchData(_criteria, _callback, _request);
            }

        });
        f.setItems(pagSize);
        gridPageControls.addMember(f);
        ToolStripButton print = new ToolStripButton();
        print.setIcon("[SKIN]actions/print.png");
        print.setTitle("打印");
        print.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Canvas.showPrintPreview(owner);

            }

        });
        gridPageControls.addSeparator();
        gridPageControls.addMember(print);
        gridPageControls.addSeparator();
        Menu menu = new Menu();
        MenuItem csv = new MenuItem("Excel导出(CSV)");
        // excel.setIcon("silk/check.png");
        csv.setChecked(true);
        csv.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                exportFunction(ExportFormat.CSV);
            }

        });
        menu.addItem(csv);
        MenuItem excel = new MenuItem("Excel导出");
        // excel.setIcon("silk/check.png");
        excel.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                exportFunction(ExportFormat.XLS);
            }

        });
        menu.addItem(excel);
        MenuItem xml = new MenuItem("XML导出");
        xml.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                exportFunction(ExportFormat.XML);
            }

        });
        menu.addItem(xml);
        MenuItem json = new MenuItem("JSON导出");
        json.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

            @Override
            public void onClick(MenuItemClickEvent event) {
                exportFunction(ExportFormat.JSON);
            }

        });
        menu.addItem(json);

        IconMenuButton moreExp = new IconMenuButton();
        moreExp.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                exportFunction(ExportFormat.CSV);

            }

        });
        moreExp.setTitle("导出");
        moreExp.setIcon("[SKIN]actions/export.png");
        moreExp.setShowMenuIcon(true);
        moreExp.setMenu(menu);
        gridPageControls.addMember(moreExp);
        gridPageControls.addFill();
        gridPageControls.addMember(totalRow);

        gridPageControls.setAlign(Alignment.LEFT);
        gridPageControls.hide();
        this.setGridComponents(new Object[] { ListGridComponent.HEADER, ListGridComponent.FILTER_EDITOR, ListGridComponent.BODY, gridPageControls });

    }

    public void exportFunction(ExportFormat format) {
        Roperation operation = new Roperation();
        SlxRPCManager.transform(_request, _criteria, operation);
        operation.setExportResults(true);
        switch (format) {
            case XLS:
                operation.setExportFilename("tmp1.xls");
                break;
            case XML:
                operation.setExportFilename("tmp1.XML");
                break;
            case JSON:
                operation.setExportFilename("tmp1.JS");
                break;
            case CSV:
                operation.setExportFilename("tmp1.CSV");
                break;
            default:
                operation.setExportFilename("tmp1.XML");
        }
        operation.setExportAs(format);
        operation.setExportDisplay(ExportDisplay.DOWNLOAD);
        SlxRPCManager.send(operation);
    }

    private Criteria _criteria;

    private DSRequest _request;

    private DSCallback _callback;

    private boolean firstCall;

    /**
     * 在smartgwt中 DSRequest中的 operationId 在提交了fetch,add 等操作后，就会从绑定组件上自动生成一个ID,用于区分不同的请求，这里由于是分页每次都应该提交同一个请求。
     */
    private String operationId;

    protected void pagedFetchData(Criteria criteria, DSCallback dsCallback, DSRequest request) {
        pagedFetchData(criteria, dsCallback, request, 1);
    }

    public void pagedFetchData(Criteria criteria, DSRequest request) {
        pagedFetchData(criteria, null, request, 1);
    }

    protected void pagedFetchData(Criteria criteria, DSCallback dsCallback) {
        pagedFetchData(criteria, dsCallback, null);
    }

    public void pagedFetchData(Criteria criteria) {
        pagedFetchData(criteria, null, null);
    }

    public void pagedFetchData() {
        pagedFetchData(null);
    }

    public void pagedFetchData(Criteria criteria, DSCallback dsCallback, DSRequest request, int pageNum) {
        final ListGrid grid = this;
        this.pageNum = pageNum;
        if (criteria == null)
            criteria = this.getCriteria();
        final String OperationID = request != null ? request.getOperationId() : null;
        this._criteria =criteria!=null? new Criteria(criteria.getJsObj()):new Criteria();
        if (dsCallback == null) {
            firstCall = true;
            dsCallback = new DSCallback() {

                @Override
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    // hold this request.and criteria.
                    _request = request;
                    _request.setCriteria(_criteria);
                    _request.setOperationId(OperationID);
                    if (response.getStatus() == DSResponse.STATUS_SUCCESS) {
                        grid.setData(response.getDataAsRecordList());
                        if (firstCall)
                            initPageBar(response);
                        else
                            updatePageBar(response);
                    } else if (response.getStatus() == DSResponse.STATUS_VALIDATION_ERROR) {

                    } else {
                        SC.warn(response.getDataAsString());
                    }
                    firstCall = false;
                }

            };
        }
        this._callback = dsCallback;
        if (request == null) {
            request = new DSRequest();
        }
        if(_request!=null)
        	_request.setCriteria(criteria);
       if(pageSize>0){
            int startNum = (pageNum - 1) * pageSize;
            request.setStartRow(startNum);
            request.setEndRow(pageNum * pageSize);
        }else{
            request.setStartRow(-1);
            request.setEndRow(0);
        }
        grid.fetchData(criteria, _callback, _request);
    }

    private void updatePageBar(DSResponse response) {
        int startNum = response.getStartRow() + 1;
        int endNum = response.getEndRow();
        totalRow.setContents("| " + startNum + "-" + endNum + "条  | 共" + totalRowNum + "条");
        pageText.setValue(pageNum);
        if (pageNum >= totalPage) {
            forward.disable();
            last.disable();
        }
        if (pageNum > 0 && pageNum < totalPage) {
            forward.enable();
            last.enable();
        }
        if (pageNum > 1) {
            first.enable();
            backward.enable();
        }
        if (pageNum == 1) {
            first.disable();
            backward.disable();
        }
    }

    private void initPageBar(DSResponse response) {
        totalRowNum = response.getTotalRows();
        int startNum = response.getStartRow() + 1;
        int endNum = response.getEndRow();
        if (startNum > endNum)
            startNum = endNum;
        if (!gridPageControls.isVisible())
            gridPageControls.show();
        totalRow.setContents("| " + startNum + "-" + endNum + "条  | 共" + totalRowNum + "条");
        totalPage = ((totalRowNum % pageSize) == 0) ? totalRowNum / pageSize : totalRowNum / pageSize + 1;
        totalLabel.setContents("页,共" + totalPage + "页");
        pageNum = 1;
        pageText.setValue(pageNum);
        first.disable();
        backward.disable();
        if (totalRowNum > pageSize) {
            forward.enable();
            last.enable();
        } else {
            forward.disable();
            last.disable();
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
        pagedFetchData(this._criteria, this._callback, this._request, pageNum);
    }
    
     public void removeSelectedData(SlxRPCCallBack callBack ){
		ListGridRecord[] records = getSelectedRecords();
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
			SlxRPCManager.send(opers, callBack);
		}
     }
}
