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
package org.solmix.web.server.comet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.gwt.server.GwtAtmosphereResource;
import org.solmix.web.shared.comet.IMEvent;


/**
 * 
 * @author solomon
 * @version $Id$  2011-10-23
 */

public class AtmosphereHandler extends AtmosphereGwtHandler {

    private ConcurrentHashMap<Long, Long> connetionIdCache= new ConcurrentHashMap<Long, Long>();
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Logger.getLogger("").setLevel(Level.INFO);
        Logger.getLogger("gwtcomettest").setLevel(Level.ALL);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.ALL);
        logger.trace("Updated logging levels");
    }

    @Override
    public int doComet(GwtAtmosphereResource resource) throws ServletException, IOException {
        resource.getBroadcaster().setID("GWT_COMET");
        HttpSession session = resource.getAtmosphereResource().getRequest().getSession(false);
        if (session != null) {
            logger.debug("Got session with id: " + session.getId());
            logger.debug("Time attribute: " + session.getAttribute("time"));
        } else {
            logger.warn("No session");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Url: " + resource.getAtmosphereResource().getRequest().getRequestURL()
                    + "?" + resource.getAtmosphereResource().getRequest().getQueryString());
        }
        return NO_TIMEOUT;
    }

    @Override
    public void cometTerminated(GwtAtmosphereResource cometResponse, boolean serverInitiated) {
        super.cometTerminated(cometResponse, serverInitiated);
        logger.debug("Comet disconnected");
    }
    @Override
    protected void doServerMessage(BufferedReader data, int connectionID) {
        List<Serializable> postMessages = new ArrayList<Serializable>();
        GwtAtmosphereResource resource = lookupResource(connectionID);
        if (resource == null) {
            return;
        }
        try {
            while (true) {
                String event = data.readLine();
                if (event == null) {
                    break;
                }
                String messageData = data.readLine();
                if (messageData == null) {
                    break;
                }
                data.readLine();
                if (logger.isTraceEnabled()) {
                    logger.trace("["+connectionID+"] Server message received: " +event + ";" + messageData.charAt(0));
                }
                if (event.equals("o")) {
                    if (messageData.charAt(0) == 'p') {
                        Serializable message = deserialize(messageData.substring(1));
                        if (message != null) {
                            postMessages.add(message);
                        }
                    } else if (messageData.charAt(0) == 'b') {
                        Serializable message = deserialize(messageData.substring(1));
                        if(message instanceof IMEvent){
                            processIMEvent((IMEvent)message);
                        }else{
                        broadcast(message, resource);
                        }
                    }
                    
                } else if (event.equals("s")) {
                    
                    if (messageData.charAt(0) == 'p') {
                        String message = messageData.substring(1);
                        postMessages.add(message);
                    } else if (messageData.charAt(0) == 'b') {
                        Serializable message = messageData.substring(1);
                        broadcast(message, resource);
                    }
                    
                } else if (event.equals("c")) {
                    
                    if (messageData.equals("d")) {
                        disconnect(resource);
                    }
                }
            }
        } catch (IOException ex) {
            logger.error("["+connectionID+"] Failed to read", ex);
        }

        if (postMessages.size() > 0) {
            post(postMessages, resource);
        }
    }

    /**
     * @param message
     */
    private void processIMEvent(IMEvent message) {
        // TODO Auto-generated method stub
        
    }

    
}
