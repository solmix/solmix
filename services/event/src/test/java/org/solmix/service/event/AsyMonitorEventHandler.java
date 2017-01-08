package org.solmix.service.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.runtime.event.EventTopic;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

@EventTopic(value="org/solmix/monitor/time")
public class AsyMonitorEventHandler implements IEventHandler {
	CountDownLatch count;
	private int hanlded=0;
	public AsyMonitorEventHandler(CountDownLatch count) {
		this.count=count;
	}
	@Override
	public void handle(IEvent event) {
		count.countDown();
		hanlded++;
		System.out.println(Thread.currentThread().getName()+"-"+System.currentTimeMillis());
	}
	public long getHanlded() {
		return hanlded;
	}


	
}
