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

package org.solmix.web.client.comet;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;
import org.atmosphere.gwt.client.extra.Window;
import org.atmosphere.gwt.client.extra.WindowFeatures;
import org.atmosphere.gwt.client.extra.WindowSocket;
import org.solmix.web.client.widgets.AbstractFactory;
import org.solmix.web.shared.comet.Event;
import org.solmix.web.shared.comet.EventSerializer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-10-23
 */

public class AtmosphereCometView extends VLayout
{

    public static class Factory extends AbstractFactory
    {

        private String id;

        public Canvas create() {
            AtmosphereCometView pane = new AtmosphereCometView();
            id = pane.getID();
            return pane;
        }

        public String getDescription() {
            return "comet test";
        }

        public String getID() {
            return id;
        }
    }

    PollAsync polling = GWT.create(Poll.class);

    AtmosphereClient client;

    Logger logger = Logger.getLogger(getClass().getName());

    Window screen;

    AtmosphereCometView()
    {
        Button button = new Button("Broadcast");
        button.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                sendMessage();
            }
        });

        Button post = new Button("Post");
        post.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                client.post(new Event(count++, "This was send using the post mechanism"));
            }
        });

        Button pollButton = new Button("Poll");
        pollButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                polling.pollDelayed(3000, new AsyncCallback<Event>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log("Failed to poll", caught);
                    }

                    @Override
                    public void onSuccess(Event result) {
                        Info.display("Polling message received: " + result.getCode(), result.getData());
                    }
                });
            }
        });

        Button wnd = new Button("Open Window");
        wnd.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                    @Override
                    public void execute() {
                        screen = Window.current().open(Document.get().getURL(), "child",
                            new WindowFeatures().setStatus(true).setResizable(true));
                    }
                });
            }
        });

        Button sendWindow = new Button("Send to window");
        sendWindow.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (screen != null) {
                    WindowSocket.post(screen, "wsock", "Hello Child!");
                }
            }
        });

        WindowSocket socket = new WindowSocket();
        socket.addHandler(new WindowSocket.MessageHandler() {

            @Override
            public void onMessage(String message) {
                Info.display("Received through window socket", message);
            }
        });
        socket.bind("wsock");
        this.addMember(button);
        this.addMember(post);
        this.addMember(pollButton);
        this.addMember(wnd);
        this.addMember(sendWindow);

        initialize();

        Button killbutton = new Button("Stop");
        killbutton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                client.stop();
            }
        });
        this.addMember(killbutton);
    }

    private class MyCometListener implements AtmosphereListener
    {

        @Override
        public void onConnected(int heartbeat, int connectionID) {
            GWT.log("comet.connected [" + heartbeat + ", " + connectionID + "]");
        }

        @Override
        public void onBeforeDisconnected() {
            logger.log(Level.INFO, "comet.beforeDisconnected");
        }

        @Override
        public void onDisconnected() {
            GWT.log("comet.disconnected");
        }

        @Override
        public void onError(Throwable exception, boolean connected) {
            int statuscode = -1;
            if (exception instanceof StatusCodeException) {
                statuscode = ((StatusCodeException) exception).getStatusCode();
            }
            GWT.log("comet.error [connected=" + connected + "] (" + statuscode + ")", exception);
        }

        @Override
        public void onHeartbeat() {
            GWT.log("comet.heartbeat [" + client.getConnectionID() + "]");
        }

        @Override
        public void onRefresh() {
            GWT.log("comet.refresh [" + client.getConnectionID() + "]");
        }

        @Override
        public void onMessage(List<? extends Serializable> messages) {
            StringBuilder result = new StringBuilder();
            for (Serializable obj : messages) {
                result.append(obj.toString()).append("<br/>");
            }
            logger.log(Level.INFO, "comet.message [" + client.getConnectionID() + "] " + result.toString());
            Info.display("[" + client.getConnectionID() + "] Received " + messages.size() + " messages",
                result.toString());
        }
    }

    ;

    public void initialize() {

        MyCometListener cometListener = new MyCometListener();

        AtmosphereGWTSerializer serializer = GWT.create(EventSerializer.class);
        // set a small length parameter to force refreshes
        // normally you should remove the length parameter
        client = new AtmosphereClient(GWT.getModuleBaseURL() + "comet/test", serializer, cometListener);
        client.start();
    }

    static int count = 0;

    public void sendMessage() {
        client.broadcast(new Event(count++, "Button clicked!"));
    }
}
