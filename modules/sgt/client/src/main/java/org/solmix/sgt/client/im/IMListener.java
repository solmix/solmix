
package org.solmix.sgt.client.im;

import java.util.List;
import java.util.Map;

import org.atmosphere.gwt.client.AtmosphereListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import org.solmix.sgt.client.im.event.IMEvent;
import org.solmix.sgt.client.im.event.RealTimeEvent;

public class IMListener implements AtmosphereListener
{

    private final EventBus eventBus;

    private String connectionUUID;

    public IMListener(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }


    @Override
    public void onBeforeDisconnected() {
        GWT.log("onBeforeDisconnected");

    }

    @Override
    public void onDisconnected() {
        GWT.log("onDisconnected");

    }

    @Override
    public void onError(Throwable exception, boolean connected) {
        GWT.log("onError:" + exception.getMessage() + ",connected:" + connected);

    }

    @Override
    public void onHeartbeat() {
        GWT.log("onHeartbeat");

    }

    @Override
    public void onRefresh() {
        GWT.log("onRefresh");

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(List<?> messages) {
        for (Object o : messages) {
            JavaScriptObject jso = JSON.decode(o.toString());
            Map<Object,Object> event = JSOHelper.convertToMap(jso);
            IMEvent im = new IMEvent();
            im.setTopic(event.get("topic").toString());
            im.setProperties((Map<Object,Object>) event.get("data"));
            RealTimeEvent rte = new RealTimeEvent(im);
            this.eventBus.fireEventFromSource(rte, this);
            // Info.display("[" + connectionId + "] Received " + messages.size() + " messages",
            // event.get("data").toString());
        }
        GWT.log(messages.toString());

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onConnected(int, java.lang.String)
     */
    @Override
    public void onConnected(int heartbeat, String connectionUUID) {
        this.connectionUUID = connectionUUID;
        GWT.log("connectionUUID:" + connectionUUID);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.atmosphere.gwt.client.AtmosphereListener#onAfterRefresh(java.lang.String)
     */
    @Override
    public void onAfterRefresh(String connectionUUID) {
        GWT.log("onAfterRefresh"+connectionUUID);
        
    }

}
