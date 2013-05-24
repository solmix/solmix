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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereListener;
import org.solmix.web.shared.comet.IMEvent;

import com.google.gwt.core.client.GWT;


/**
 * 
 * @author solmix
 * @version $Id$  2011-11-18
 */

public class IMCometManager implements AtmosphereListener,CometManager
{
private BaseChartWindow chartWindow;
private AtmosphereClient callbackClient;
private int connectionId;
public IMCometManager(){
    
}
public IMCometManager(AtmosphereClient client){
    this.callbackClient=client;
}

private List<EventArrivedHandler > handlers = new ArrayList<EventArrivedHandler>();
    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onConnected(int, int)
     */
    @Override
    public void onConnected(int heartbeat, int connectionID) {
        GWT.log("connected"+connectionID);
        connectionId=connectionID;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onBeforeDisconnected()
     */
    @Override
    public void onBeforeDisconnected() {
        GWT.log("befordisconnected");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onDisconnected()
     */
    @Override
    public void onDisconnected() {
        GWT.log("disconnected");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onError(java.lang.Throwable, boolean)
     */
    @Override
    public void onError(Throwable exception, boolean connected) {
        GWT.log("error");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onHeartbeat()
     */
    @Override
    public void onHeartbeat() {
       GWT.log("headbit");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onRefresh()
     */
    @Override
    public void onRefresh() {
        GWT.log("refresh");

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onMessage(java.util.List)
     */
    @Override
    public void onMessage(List<? extends Serializable> messages) {
        GWT.log(messages.toString());
      for(Serializable msg: messages){
          if(msg instanceof IMEvent){
              sendMessage((IMEvent)msg);
          }
      }

    }
    public void sendMessage(IMEvent event){
        if(handlers!=null&&handlers.size()>0){
            for(EventArrivedHandler handler:handlers){
                handler.handleEvent(event);
            }
        }
    }
  
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.web.client.im.CometManager#registerHander(org.solmix.web.client.im.EventArrivedHandler)
     */
    @Override
    public void registerHander(EventArrivedHandler handler) {
        handlers.add(handler);
        
    }
}
