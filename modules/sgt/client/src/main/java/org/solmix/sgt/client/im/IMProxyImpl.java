
package org.solmix.sgt.client.im;

import org.atmosphere.gwt.client.AtmosphereClient;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.smartgwt.client.util.JSON;
import org.solmix.sgt.client.annotation.DefaultIMPlace;
import org.solmix.sgt.client.im.event.IMEvent;
import org.solmix.sgt.client.im.event.IMEventSerializer;

public class IMProxyImpl implements IMProxy
{

    private final String url;

    private AtmosphereClient proxy;

    private final EventBus eventBus;

    @Inject
    public IMProxyImpl(@DefaultIMPlace String url, EventBus eventBus)
    {
        this.url = url;
        this.eventBus = eventBus;
    }

    @Override
    public void start() {
        if (proxy == null) {
            IMEventSerializer serializer = GWT.create(IMEventSerializer.class);
            String url = GWT.getHostPageBaseURL() + this.url;
            proxy = new AtmosphereClient(url, serializer, new IMListener(eventBus));
            proxy.start();
        }
    }

    @Override
    public void stop() {
        if (proxy != null)
            proxy.stop();
    }
    @Override
    public void broadcast(IMEvent event){
    	proxy.broadcast(JSON.encode(event.getJsObj()));
    }
}
