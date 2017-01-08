
package org.solmix.service.event;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.AbstractDelegateProvisionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.event.BaseEvent;
import org.solmix.runtime.event.EventService;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;

import static org.ops4j.pax.exam.Constants.START_LEVEL_SYSTEM_BUNDLES;
import static org.ops4j.pax.exam.CoreOptions.*;

//@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerMethod.class)
public class IntegrationTest
{

    protected static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);

    private static final int RUNS = 5;

    public static final int BATCH_SIZE = 500000;

    private static final String BUNDLE_JAR_SYS_PROP = "project.bundle.file";

  
//    @Inject
    protected BundleContext bundleContext;

    /** Event admin reference. */
    private ServiceReference eventAdminReference;

    private EventService service;

    final AtomicLong counter = new AtomicLong();

    Collection<Listener> listeners = new ArrayList<Listener>();

//    @Configuration
    public static Option[] configuration() {
//        Properties p=  System.getProperties();
//       for(Object o: p.keySet()){
//           System.out.println(o);
//       }
       System.out.println(System.getProperty("user.dir"));
        final String bundleFileName = System.getProperty(BUNDLE_JAR_SYS_PROP);
        logger.info("Bundle jar at :" + bundleFileName);
//        final File bundleFile = new File(bundleFileName);
//        if (!bundleFile.canRead()) {
//            throw new IllegalArgumentException("Cannot read from bundle file " + bundleFileName + " specified in the " + BUNDLE_JAR_SYS_PROP
//                + " system property");
//        }
        return options(
            vmOption("-Xms1024m"),
            // vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
            provision(mavenBundle("org.ops4j.pax.tinybundles", "tinybundles", "1.0.0"),
                mavenBundle("org.solmix.services", "solmix-service-event", "0.6.1-SNAPSHOT"),
                mavenBundle("org.apache.sling", "org.apache.sling.commons.log", "2.1.2"),
                mavenBundle("org.apache.felix", "org.apache.felix.configadmin", "1.2.8"),
                mavenBundle("org.apache.felix", "org.apache.felix.metatype", "1.0.4"),
//                CoreOptions.bundle(bundleFile.toURI().toString()),
                mavenBundle("org.ops4j.pax.url", "pax-url-mvn", "1.3.5"))
            // below is instead of normal Pax Exam junitBundles() to deal
            // with build server issue
//            new DirectURLJUnitBundlesOption(), systemProperty("pax.exam.invoker").value("junit"),
//            bundle("link:classpath:META-INF/links/org.ops4j.pax.exam.invoker.junit.link")
            );
    }
    
//    @After
    public void tearDown() {
        for (Listener listener : listeners) {
            removeListener(listener);
        }
    }
    
    @Test
    public void test(){
        
    }
    
    protected EventService loadEventService() {
        if ( eventAdminReference == null || eventAdminReference.getBundle() == null ) {
            service = null;
            eventAdminReference = bundleContext.getServiceReference(EventService.class.getName());
        }
        if ( service == null && eventAdminReference != null ) {
            service = (EventService) bundleContext.getService(eventAdminReference);
        }
        return service;
    }
    protected void send(String topic, Dictionary<String, Object> properties, boolean sync) {
        final IEvent event = new BaseEvent(topic, properties);
        if ( sync ) {
            service.sendEvent(event);
        } else {
            service.postEvent(event);
        }
    }
    
    public void addListener(Listener listener, String... topics) {
        listener.register(bundleContext,topics);
        listeners.add(listener);
    }

    private void removeListener(Listener listener) {
        listener.unregister();
    }


    private static abstract class Listener implements IEventHandler
    {

        private ServiceRegistration registration;

        protected Listener()
        {
        }

        public void register(BundleContext bundleContext, String... topics) {
            final Dictionary<String, Object> props = new Hashtable<String, Object>();
            if (topics != null) {
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

    private static class DirectURLJUnitBundlesOption extends AbstractDelegateProvisionOption<DirectURLJUnitBundlesOption>
    {

        /**
         * Constructor.
         */
        public DirectURLJUnitBundlesOption()
        {
            super(
                bundle("http://repository.springsource.com/ivy/bundles/external/org.junit/com.springsource.org.junit/4.9.0/com.springsource.org.junit-4.9.0.jar"));
            noUpdate();
            startLevel(START_LEVEL_SYSTEM_BUNDLES);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return String.format("DirectURLJUnitBundlesOption{url=%s}", getURL());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DirectURLJUnitBundlesOption itself() {
            return this;
        }

    }
}
