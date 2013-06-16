package com.solmix.sgt.client.im.event;

import com.google.gwt.event.shared.EventHandler;

public interface RealTimeEventHandler extends EventHandler{

	void onHandle(RealTimeEvent event);
}
