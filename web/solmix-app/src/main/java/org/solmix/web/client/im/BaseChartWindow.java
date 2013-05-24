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

import java.util.Date;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.solmix.web.shared.comet.IMEvent;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author solmix
 * @version $Id$  2011-11-17
 */

public class BaseChartWindow extends Window implements EventArrivedHandler
{
    public static final int WIDTH=600;
    public static final int HEIGHT=400;
    private final AtmosphereClient client;
    final Window infoWin ;
    public long targetUserId;
    public String targetUserName;
    public BaseChartWindow( final AtmosphereClient client){
        this.client=client;
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
  
        this.setHeaderIcon("icons/16/message.png");
        this.centerInPage();
        HLayout main = new HLayout();
        main.setWidth100();
        main.setHeight100();
        VLayout chart = new VLayout();
        chart.setShowResizeBar(true);
        chart.setResizeBarTarget("next");
//        chart.setBorder("1px solid #0083ff");
        chart.setWidth("75%");
        chart.setHeight100();
        
        final VLayout chartRes = new VLayout(); 
//        chartRes.setBorder("2px solid #0083ff");
        chartRes.setHeight("70%");
        infoWin = new Window(); 
        infoWin.setCanDrag(false);
        infoWin.setShowEdges(false);
        infoWin.setEdgeSize(0);
        infoWin.setWidth100();
        infoWin.setHeight100();
        infoWin.setCanSelectText(true);
        infoWin.setShowTitle(false);
        infoWin.setShowHeader(false);
        chartRes.addMember(infoWin);
        HLayout infoWinc = new HLayout();
        infoWinc.setBackgroundColor("#c8e3fd");
        infoWinc.setAlign(Alignment.RIGHT);
        Button infoWinbtn = new Button("clear");
        infoWinbtn.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                infoWin.scrollByPercent(100, 100);
                
            }
            
        });
        infoWinc.addMember(infoWinbtn);
        chartRes.addMember(infoWinc);
//        chartRes.setContents("abcd<br>aa");
        chartRes.setCanSelectText(true);
        
        VLayout chartSend = new VLayout();
//        chartSend.setBorder("1px solid #0083ff");
        chartSend.setHeight("25%");

        final RichTextEditor richTextEditor = new RichTextEditor();  
        richTextEditor.setHeight(155);  
        richTextEditor.setOverflow(Overflow.HIDDEN); 
        richTextEditor.setControlGroups(new String[]{"fontControls","styleControls","colorControls"});
        chartSend.addMember(richTextEditor);
        HLayout imControl = new HLayout();
        imControl.setBackgroundColor("#c8e3fd");
        imControl.setAlign(Alignment.RIGHT);
        Button sendBtn = new Button("send");
        sendBtn.addClickHandler(new ClickHandler(){

            @Override
            public void onClick(ClickEvent event) {
                String value =richTextEditor.getValue();

                String _info = "<font color='#0000ff' size='3'>"+IMMainWindow.userName+"</font>&nbsp;&nbsp;<font color='#0000ff' size='2'>"+getCurrentTime()+":</font><br>"+value;
                Canvas info  = new Canvas();
                info.setAutoHeight();
                info.setContents(_info);
                info.setCanSelectText(true);
                infoWin.addItem(info);
              
                IMEvent ime  = new IMEvent(IMMainWindow.userName,targetUserName,value);
                client.broadcast(ime);
                infoWin.scrollToBottom();
                richTextEditor.setValue("");
                richTextEditor.focus();
            }
            
        });
        imControl.addMember(sendBtn);
        imControl.setWidth100();
        imControl.setHeight(22);
        chartSend.addMember(imControl);
        
       
        chart.addMember(chartRes);
        chart.addMember(chartSend);
        VLayout info= new VLayout();
        main.addMember(chart);
        main.addMember(info);
        this.addItem(main);
       
        }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.im.EventArrivedHandler#handleEvent(org.solmix.web.shared.comet.IMEvent)
     */
    @Override
    public void handleEvent(IMEvent event) {
        String _info =event.getMessage();
        Canvas info  = new Canvas();
        info.setAutoHeight();
        info.setContents("<font color='#0000ff' size='3'>"+event.getSourceName()+"</font>&nbsp;&nbsp;<font color='#0000ff' size='2'>"+getCurrentTime()+":</font><br>"+_info);
        infoWin.addItem(info);
        
    }
    public String getCurrentTime(){
        DateTimeFormat fomate =  DateTimeFormat.getFormat("hh:mm:ss");
        String curTime = fomate.format(new Date());
        return curTime;
    }
  
}
