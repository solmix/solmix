package org.solmix.sgt.client.im;

import org.solmix.sgt.client.im.event.IMEvent;


public interface IMProxy {
	public void start();
	public void stop();
	public void broadcast(IMEvent event);
}
