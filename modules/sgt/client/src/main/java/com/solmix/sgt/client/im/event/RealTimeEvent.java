package com.solmix.sgt.client.im.event;

import com.google.gwt.event.shared.GwtEvent;

public class RealTimeEvent extends GwtEvent<RealTimeEventHandler> {

	  private static Type<RealTimeEventHandler> TYPE;
	  
	  private IMEvent im;
	  public RealTimeEvent(IMEvent im){
		  this.im=im;
	  }
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RealTimeEventHandler> getAssociatedType() {
		 return getType();
	}

	@Override
	protected void dispatch(RealTimeEventHandler handler) {
		 handler.onHandle(this);
	}
	 public static Type<RealTimeEventHandler> getType() {
	        if (TYPE == null) {
	            TYPE = new Type<RealTimeEventHandler>();
	        }
	        return TYPE;
	    }
	public IMEvent getIMEvent() {
		return im;
	}
	public void setIMEvent(IMEvent im) {
		this.im = im;
	}
	 
}
