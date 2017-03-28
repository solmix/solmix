package org.solmix.runtime.event;

public abstract class EventServiceAdapter implements EventService {
	@Override
	public void postEvent(IEvent event) {
	}

	@Override
	public void sendEvent(IEvent event) {
	}
	
	@Override
	public void addEventHandler(String topic, IEventHandler handler){
		throw new UnsupportedOperationException("Not support");
	}

	@Override
	public void addEventHandler(IEventHandler handler){
		throw new UnsupportedOperationException("Not support");
	}

}
