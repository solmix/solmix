package com.solmix.sgt.client.im;

import com.solmix.sgt.client.im.event.IMEvent;


public interface IMProxy {
	public void start();
	public void stop();
	public void broadcast(IMEvent event);
}
