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

package org.solmix.cpt.client.data;

import java.util.ArrayList;
import java.util.List;

import org.solmix.cpt.client.menu.MenuRecord;
import org.solmix.sgt.client.advanceds.JSCallBack;
import org.solmix.sgt.client.advanceds.Roperation;
import org.solmix.sgt.client.advanceds.SlxRPC;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年11月21日
 */

public class MenuCache
{

    private RecordList menu;

    private Object lock=new Object();
    @Inject
    public MenuCache()
    {

    }

    public RecordList getAllMenuData() {
        return getMenu();
    }

    /**
     * @return
     */
    public RecordList getMenu() {
        return menu;
    }

    public void initMenuData() {
        initMenuData(false);
    }

    public void initMenuData(boolean reload) {
        if (menu == null || reload) {
            Roperation oper = new Roperation().ds("controller/MenuController").type(DSOperationType.FETCH).id("initMenu");
            SlxRPC.send(oper, new JSCallBack() {

                @Override
                public void execute(RPCResponse response, JavaScriptObject rawData, RPCRequest request) {
                    if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
                        RecordList serdata = new RecordList(response.getDataAsObject());
                        menu = serdata;
                    } else {
                        SC.warn("Can't get menu data.");
                    }

                }

            });
        }
    }
 

    /**
     * @param url 多个url ,分隔
     * @param callback
     */
    public void findMenu(String url, final MenuDataCallBack callback) {
    	
		if (getMenu() != null) {
			String[] urls = url.split(",");
			List<MenuRecord> _return = new ArrayList<MenuRecord>();
			for (String u : urls) {
				for (int i = 0; i < menu.getLength(); i++) {
					Record record = getMenu().get(i);
					if (u.equals(record.getAttribute("url"))) {
						_return.add(new MenuRecord(record));
						break;
					}
				}
			}
			if(_return.size()==urls.length){
				callback.execute(_return);
				return;
			}
		}
        //can't find
            Roperation oper = new Roperation()
                .ds("controller/MenuController")
                .type(DSOperationType.FETCH)
                .criteria(new Criteria("url", url))
                .id("getMenuByUrl");
            SlxRPC.send(oper, new JSCallBack() {

                @Override
                public void execute(RPCResponse response, JavaScriptObject rawData, RPCRequest request) {
                    if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
                    	JavaScriptObject[] arrays=JSOHelper.toArray(response.getDataAsObject());
                    	for(JavaScriptObject array:arrays){
	                        Record record = new Record(array);
	                        if(getMenu()!=null){
	                        	RecordList menus = getMenu();
	                        	menus.add(record);
	                        }
	                        List<MenuRecord> _return = new ArrayList<MenuRecord>();
	                        _return.add(new MenuRecord(record));
	                        callback.execute(_return);
                    	}
                        
                    } else {
                        SC.warn("Can't get menu data.");
                    }

                }

            });
    }

    public void findModuleMenuData(final String moduleName, final MenuDataCallBack callback) {
        if (getMenu() == null) {
            Roperation oper = new Roperation().ds("controller/MenuController").type(DSOperationType.FETCH).id("initMenu");
            SlxRPC.send(oper, new JSCallBack() {

                @Override
                public void execute(RPCResponse response, JavaScriptObject rawData, RPCRequest request) {
                    if (response.getStatus() == RPCResponse.STATUS_SUCCESS) {
                        RecordList data = new RecordList(response.getDataAsObject());
                        menu = data;
                        findMenuData(moduleName, menu, callback);
                    } else {
                        SC.warn("Can't get menu data.");
                    }

                }

            });
        } else {
            findMenuData(moduleName, getMenu(), callback);
        }

    }

    private  void findMenuData(String moduleName, RecordList data, MenuDataCallBack callback) {
        List<MenuRecord> _return = new ArrayList<MenuRecord>();
        for (int i = 0; i < getMenu().getLength(); i++) {
            Record record = getMenu().get(i);
            if (moduleName.equals(record.getAttribute("module"))) {
                _return.add(new MenuRecord(record));
            }
        }
        callback.execute(_return);
    }

}
