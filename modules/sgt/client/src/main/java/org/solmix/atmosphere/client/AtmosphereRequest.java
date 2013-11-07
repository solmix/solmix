
package org.solmix.atmosphere.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.SerializationException;

public final class AtmosphereRequest extends JavaScriptObject {
  
  static final Logger logger = Logger.getLogger("AtmosphereRequest");
  
  public void push(Object message) throws SerializationException {
      ClientSerializer serializer = getOutboundSerializer();
    this.pushImpl(serializer.serialize(message));
  }
  
  public native void pushImpl(String message) /*-{
    this.push(message);
  }-*/;
  
  public void pushLocal(Object message) throws SerializationException {
    this.pushLocalImpl(getOutboundSerializer().serialize(message));
  }
  
  public native void pushLocalImpl(String message) /*-{
    this.pushLocal(message);
  }-*/;
  
  protected AtmosphereRequest() {
    
  }
  
  native void setOutboundSerializer(ClientSerializer serializer) /*-{
    this.serializer = serializer;
  }-*/;

  native ClientSerializer getOutboundSerializer() /*-{
    return this.serializer;
  }-*/;

  public native String getUUID() /*-{
    return String(this.getUUID());
  }-*/;
}
