package org.solmix.service.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

@EventTopic(value="org/solmix/monitor/time")
public class MonitorEventHandler implements IEventHandler {

	private CountDownLatch  count= new CountDownLatch(1);
	@Override
	public boolean handle(IEvent event) {
		count.countDown();
		return false;
	}
	public CountDownLatch getCount() {
		return count;
	}
	public void setCount(CountDownLatch count) {
		this.count = count;
	}



	
}
