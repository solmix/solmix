package org.solmix.sgt.client.event;

import java.util.Collections;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
public class ParameterUpdateEvent extends GwtEvent<ParameterUpdateHandler> {
	private static Type<ParameterUpdateHandler> TYPE;
	
	private final Map<String,Object> parameters;
	private final int navigationType;
	
	public ParameterUpdateEvent(int navigationType,Map<String,Object> params){
		parameters=params;
		this.navigationType=navigationType;
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ParameterUpdateHandler> getAssociatedType() {
		return getType();
	}

	public static Type<ParameterUpdateHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ParameterUpdateHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(ParameterUpdateHandler handler) {
		 handler.onParameterUpdate(this);

	}
	  public Map<String,Object> getParameters() {
	        if (this.parameters == null) {
	            return Collections.emptyMap();
	        } else {
	            return this.parameters;
	        }
	    }
	  public int getNavigationType(){
		  return this.navigationType;
	  }
}
