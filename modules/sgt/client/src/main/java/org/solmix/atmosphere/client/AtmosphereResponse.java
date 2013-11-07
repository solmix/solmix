
package org.solmix.atmosphere.client;

import java.util.Collections;
import java.util.List;

import org.solmix.atmosphere.client.AtmosphereRequestConfig.Transport;

import com.google.gwt.core.client.JavaScriptObject;

public final class AtmosphereResponse extends JavaScriptObject {

    public enum State {
        MESSAGE_RECEIVED,
        MESSAGE_PUBLISHED,
        OPENING,
        RE_OPENING,
        CLOSED,
        ERROR;
        
        @Override
        public String toString() {
            switch(this) {
                case MESSAGE_RECEIVED: return "messageReceived";
                case MESSAGE_PUBLISHED: return "messagePublished";
                case OPENING: return "opening";
                case RE_OPENING: return "re-opening";
                case CLOSED: return "closed";
                default:
                case ERROR: return "error";
            }
        }
        public static State fromString(String s) {
            for (State st : State.values()) {
                if (st.toString().equals(s)) {
                    return st;
                }
            }
            return State.ERROR;
        }
    }
    /**
     * See com.google.gwt.http.client.Response for status codes
     * 
     * @return 
     */
    public native int getStatus() /*-{
       return this.status;
    }-*/;
    
    public native String getReasonPhrase() /*-{
        return this.reasonPhrase;
    }-*/;

    public <T> List<T> getMessages() {
       Object containedMessage = getMessageObject();
       if (containedMessage == null) {
          return Collections.emptyList();
       } else if (containedMessage instanceof List) {
          return (List)containedMessage;
       } else {
          return (List<T>) Collections.singletonList(containedMessage);
       }
    }
        
    public native String getResponseBody() /*-{
        return this.responseBody;
    }-*/;
    
    public native String getHeader(String name) /*-{
        return this.headers[name];
    }-*/;
    
    public State getState() {
        return State.fromString(getStateImpl());
    }
    
    public Transport getTransport() {
        return Transport.fromString(getTransportImpl());
    }
        
    protected AtmosphereResponse() {
    }
    
    native void setMessageObject(Object message) /*-{
        this.messageObject = message;
    }-*/;
    
    native Object getMessageObject() /*-{
        return this.messageObject;
    }-*/;
    
    private native String getStateImpl() /*-{
        return this.state;
    }-*/;

    private native String getTransportImpl() /*-{
        return this.transport;
    }-*/;
    
}
