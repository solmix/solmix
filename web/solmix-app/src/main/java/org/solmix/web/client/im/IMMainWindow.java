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

package org.solmix.web.client.im;


import java.util.HashMap;
import java.util.Map;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.solmix.web.client.cache.ClientCache;
import org.solmix.web.client.presenter.MainPagePresenter;
import org.solmix.web.shared.comet.EventSerializer;
import org.solmix.web.shared.comet.IMEvent;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-11-6
 */

public class IMMainWindow extends Window implements EventArrivedHandler
{
    /**
     * Only cache for read,not change this value.
     */
    public static  String userName;
   public static long userid;
    private AtmosphereClient m_client;
    private IMWindowFactory m_factory;
    
    private Map<Long,BaseChartWindow> windowCache= new HashMap<Long,BaseChartWindow>();
    public IMMainWindow()
    {   
        init();
        setEdgeImage("edges/blue/4.png");
        setBackgroundColor("#4981ad");
       ClientCache cache=  MainPagePresenter.getCache();
       Record data =cache.getCurrentUserData();
       userName= data.getAttributeAsString("user_name");
       userid= data.getAttributeAsLong("personnel_id");
       Label nameLabel = new Label(  userName);
       nameLabel.setWidth100();
       nameLabel.setHeight(40);
       addItem(nameLabel);
       ToolStrip imControl = new ToolStrip();
       imControl.setWidth100();
       imControl.setHeight(20);
       ToolStripButton mailBtn = new ToolStripButton();
       mailBtn.setTitle("2");
       mailBtn.setIcon("silk/bell.png");
       imControl.addButton(mailBtn);
       addItem(imControl);
       VLayout main = new VLayout();
       main.setWidth100();
       main.setHeight100();
       ListGrid myFried = new ListGrid();
       myFried.setShowHeader(false);
       myFried.addCellDoubleClickHandler(new CellDoubleClickHandler(){

        @Override
        public void onCellDoubleClick(CellDoubleClickEvent event) {
            long userId=event.getRecord().getAttributeAsLong("personnel_id");
            String userName=event.getRecord().getAttributeAsString("user_name");
           BaseChartWindow window =windowCache.get(userId);
           if(window!=null&&window.isDrawn()){
               window.show();
               return;}
           window = m_factory.createUserWindow(userName,userId);
           windowCache.put(userId, window);
           window.draw();
        }
           
       });
       myFried.setWidth100();
       myFried.setHeight100();
       ListGridField  name = new ListGridField("user_name");
       myFried.setFields(name);
       myFried.setDataSource(DataSource.get("SYSTEM:user"));
       myFried.setAutoFetchData(true);
       main.addMember(myFried);
       this.addItem(main);
      
    }
private void init(){
    IMCometManager manager = new IMCometManager(m_client);
    manager.registerHander(this);
     EventSerializer serializer = GWT.create(EventSerializer.class);
     m_client = new AtmosphereClient(GWT.getModuleBaseURL() + "comet/im", serializer, manager);
     m_client.start();
     m_factory = new IMWindowFactory(m_client,manager);
 }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.im.EventArrivedHandler#handleEvent(org.solmix.web.shared.comet.IMEvent)
     */
    @Override
    public void handleEvent(IMEvent event) {
        // TODO Auto-generated method stub
        
    }
                  
}
