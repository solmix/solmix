package org.solmix.service.event;

import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

@EventTopic("org/solmix/monitor/time")
public class MonitorEventHandler implements IEventHandler {

	private AtomicInteger  count= new AtomicInteger();
	private long threadId;
	@Override
	public void handle(IEvent event) {
		count.getAndIncrement();
		threadId=Thread.currentThread().getId();
	}

	public int getCount(){
		return count.intValue();
	}

	public long getThreadId() {
		return threadId;
	}

	
}
