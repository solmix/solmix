package org.solmix.sgt.client.event;


import com.google.gwt.user.client.rpc.SerializationException;


public interface EventService {
	public void start();
	public void stop();
	public void push(Object event) throws SerializationException;
}
