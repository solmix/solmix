package org.solmix.service.event;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solmix.commons.util.DataUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.Containers;
import org.solmix.runtime.event.BaseEvent;
import org.solmix.runtime.event.EventArguments;
import org.solmix.runtime.event.EventService;
import org.solmix.runtime.event.TimeMonitorEvent;

public class EventServiceTest {
	
	private  DefaultEventService service;
	private  Container container =ContainerFactory.getDefaultContainer();
	@Before
	public  void setup(){
		service=Containers.injectResource(container, DefaultEventService.class);
		service.setThreadPoolSize(10);
		service.start();
	}
	
	@After
	public  void tearDown(){
		if(service!=null){
			service.shutdown();
		}
		container.close();
	}
	@Test
	public void test() {
		EventArguments args= new EventArguments(DataUtils.buildMap("key","value"));
		TimeMonitorEvent event = new TimeMonitorEvent(args);
		
		service.sendEvent(event);
		MonitorEventHandler handler=	container.getExtension(MonitorEventHandler.class);
		Assert.assertNotNull(handler);
		Assert.assertEquals(1, handler.getCount());
		
	}
	
	@Test
	public void testAsy() throws InterruptedException {
		 CountDownLatch  count= new CountDownLatch(100);
		AsyMonitorEventHandler handler  = new AsyMonitorEventHandler(count);
		service.addEventHandler("asyc", handler);
		for(int i=0;i<100;i++){
		BaseEvent e=new BaseEvent("asyc", DataUtils.buildMap("key","value"));
		service.postEvent(e);
		}
		count.await();
		Assert.assertEquals(100, handler.getHanlded());
		
	}
}
