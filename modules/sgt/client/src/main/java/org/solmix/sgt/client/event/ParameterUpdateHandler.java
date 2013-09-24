package org.solmix.sgt.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ParameterUpdateHandler extends EventHandler{

	void onParameterUpdate(ParameterUpdateEvent event);
}
