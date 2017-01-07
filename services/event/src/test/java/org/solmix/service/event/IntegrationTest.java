package org.solmix.service.event;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.event.EventService;

//@RunWith(PaxExam.class)
public class IntegrationTest {

	protected static final Logger logger = LoggerFactory
			.getLogger(IntegrationTest.class);
	private static final int RUNS = 5;
	public static final int BATCH_SIZE = 500000;

	@Test
	public void test() {
	}
//	 @Inject
	    protected BundleContext bundleContext;

	    /** Event admin reference. */
	    private ServiceReference eventAdminReference;

	    private EventService service;

	    final AtomicLong counter = new AtomicLong();

	    Collection<Listener> listeners = new ArrayList<Listener>();
	    
	    
	    
	    private static abstract class Listener implements EventHandler {
	        private ServiceRegistration registration;

	        protected Listener() {
	        }

	        public void register(BundleContext bundleContext, String...topics) {
	            final Dictionary<String, Object> props = new Hashtable<String, Object>();
	            if ( topics != null ) {
	                props.put("event.topics", topics);
	            } else {
	                props.put("event.topics", "*");
	            }
	            this.registration = bundleContext.registerService(EventHandler.class.getName(), this, props);
	        }

	        public void unregister() {
	            registration.unregister();
	        }
	    }
}
