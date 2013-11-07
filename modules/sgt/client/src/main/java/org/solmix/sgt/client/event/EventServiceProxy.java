
package org.solmix.sgt.client.event;

import java.util.List;
import java.util.logging.Logger;

import org.solmix.atmosphere.client.Atmosphere;
import org.solmix.atmosphere.client.AtmosphereCloseHandler;
import org.solmix.atmosphere.client.AtmosphereMessageHandler;
import org.solmix.atmosphere.client.AtmosphereOpenHandler;
import org.solmix.atmosphere.client.AtmosphereRequest;
import org.solmix.atmosphere.client.AtmosphereRequestConfig;
import org.solmix.atmosphere.client.AtmosphereResponse;
import org.solmix.sgt.client.annotation.DefaultEventTopic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

public class EventServiceProxy implements EventService
{
    static final Logger logger = Logger.getLogger(EventServiceProxy.class.getName());
    private final String url;
    private Atmosphere proxy;
    private AtmosphereRequest request;
    private final EventBus eventBus;

    @Inject
    public EventServiceProxy(@DefaultEventTopic String url, EventBus eventBus)
    {
        this.url = url;
        this.eventBus = eventBus;
    }

    @Override
    public void start() {
        if (proxy == null) {
            proxy = Atmosphere.create();
            request=   proxy.subscribe(getAtmosphereRequestConfig());
        }
    }
    protected AtmosphereRequestConfig getAtmosphereRequestConfig(){
        EventSerializer serializer= new EventSerializer();
        AtmosphereRequestConfig atmConfig = AtmosphereRequestConfig.create(serializer);
        atmConfig.setUrl(GWT.getHostPageBaseURL()+url);
        atmConfig.setContentType("application/json; charset=UTF-8");
        atmConfig.setTransport(AtmosphereRequestConfig.Transport.STREAMING);
        atmConfig.setFallbackTransport(AtmosphereRequestConfig.Transport.LONG_POLLING);
        atmConfig.setOpenHandler(new AtmosphereOpenHandler() {
            @Override
            public void onOpen(AtmosphereResponse response) {
//               SC.say("JSON Connection opened");
            }
        });
        atmConfig.setCloseHandler(new AtmosphereCloseHandler() {
            @Override
            public void onClose(AtmosphereResponse response) {
//                SC.say("JSON Connection closed");
            }
        });
        atmConfig.setTransportFailureHandler(new org.solmix.atmosphere.client.AtmosphereTransportFailureHandler(){

            @Override
            public void onTransportFailure(String errorMsg, AtmosphereRequest request) {
//                SC.say("onTransportFailure"+errorMsg);
                
            }
            
        });
        atmConfig.setErrorHandler(new org.solmix.atmosphere.client.AtmosphereErrorHandler(){

            @Override
            public void onError(AtmosphereResponse response) {
//                SC.say("onError");
                
            }
            
        });
        atmConfig.setReconnectHandler(new org.solmix.atmosphere.client.AtmosphereReconnectHandler(){

            @Override
            public void onReconnect(AtmosphereRequestConfig request, AtmosphereResponse response) {
//                SC.say("onReconnect");
                
            }
            
        });
        atmConfig.setMessageHandler(new AtmosphereMessageHandler() {
            @Override
            public void onMessage(AtmosphereResponse response) {
                List<ServerEvent> events = response.getMessages();
                for (ServerEvent event : events) {
                    GWTServerEvent e = new GWTServerEvent(event);
                    eventBus.fireEventFromSource(e, this);
                    logger.info("received message through JSON: " + event.getTopic());
                }
            }
        });
        return atmConfig;
    }

    @Override
    public void stop() {
    }
    @Override
    public void push(Object event) throws SerializationException{
        if(request==null)
            start();
        request.push(event);
    }
}
