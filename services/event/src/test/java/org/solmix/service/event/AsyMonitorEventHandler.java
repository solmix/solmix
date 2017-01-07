package org.solmix.service.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

@EventTopic(value="org/solmix/monitor/time")
public class AsyMonitorEventHandler implements IEventHandler {

	private AtomicInteger  count= new AtomicInteger();
	private long threadId;
	@Override
	public boolean handle(IEvent event) {
		count.getAndIncrement();
		threadId=Thread.currentThread().getId();
		return false;
	}

	public int getCount(){
		return count.intValue();
	}

	public long getThreadId() {
		return threadId;
	}

	
}
