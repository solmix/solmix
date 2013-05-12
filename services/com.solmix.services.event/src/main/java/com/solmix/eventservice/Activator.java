
package com.solmix.eventservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import com.solmix.eventservice.ext.LogTracker;

public class Activator implements BundleActivator
{
  private BundleContext bundleContext;
  private static LogTracker logTracker;
  private EventServer server;
//    protected DistributedEventAdmin eventAdminImpl;
//
//    protected ServiceTracker containerManagerTracker;
//
//    protected ServiceRegistration eventAdminRegistration;
//
//    private final Object appLock = new Object();
//
//    protected IContainer container;
//
//    private String containerId = "ecftcp://localhost:1111/server";
//
//    private String containerType = "ecf.generic.server";
//
//    private String targetId;
//
//    private String topic="defaultTopic";
//
//    private boolean done = false;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        
        logTracker= new LogTracker(bundleContext,System.out);
        logTracker.open();
        //start Event Service
        server = new EventServer(bundleContext);
//        System.out.println("1");
//        // Create event admin impl
//        eventAdminImpl = new DistributedEventAdmin(bundleContext);
//        try {
//            createConfigureAndConnectContainer();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        eventAdminImpl.start();
//        // register as EventAdmin service instance
//        Dictionary<String, Object> props = new Hashtable<String, Object>();
//        props.put(EventConstants.EVENT_TOPIC, topic);
//        eventAdminRegistration = bundleContext.registerService("org.osgi.service.event.EventAdmin", eventAdminImpl,props);
    }
    public static LogService getLogService(long timeout){
       try {
        return logTracker.waitForService(timeout);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
    }
    }
    public static LogService getLogService(){
        return getLogService(10000);
    }
    
    @Override
    public void stop(BundleContext bundleContext) throws Exception {

        if(logTracker!=null){
            logTracker.close();
            logTracker=null;
        }
        bundleContext = null;
        server.destory();
//        if (eventAdminRegistration != null) {
//            eventAdminRegistration.unregister();
//            eventAdminRegistration = null;
//        }
//        if (container != null) {
//            container.dispose();
//            getContainerManager().removeAllContainers();
//            container = null;
//        }
//        if (containerManagerTracker != null) {
//            containerManagerTracker.close();
//            containerManagerTracker = null;
//        }
//        synchronized (appLock) {
//            done = true;
//            appLock.notifyAll();
//        }
      
    }

//    protected void createConfigureAndConnectContainer() throws ContainerCreateException, SharedObjectAddException,
//        ContainerConnectException {
//        // get container factory and create container
//        IContainerFactory containerFactory = getContainerManager().getContainerFactory();
//        container = (containerId == null) ? containerFactory.createContainer(containerType)
//            : containerFactory.createContainer(containerType, new Object[] { containerId });
//        // Get socontainer
//        ISharedObjectContainer soContainer = (ISharedObjectContainer) container.getAdapter(ISharedObjectContainer.class);
//        // Add to soContainer, with topic as name
//        soContainer.getSharedObjectManager().addSharedObject(IDFactory.getDefault().createStringID(topic),
//            eventAdminImpl, null);
//
//        // then connect to target Id
//        if (targetId != null)
//            container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), targetId), null);
//    }
//
//    protected IContainerManager getContainerManager() {
//        if (containerManagerTracker == null) {
//            containerManagerTracker = new ServiceTracker(bundleContext, IContainerManager.class.getName(), null);
//            containerManagerTracker.open();
//        }
//        return (IContainerManager) containerManagerTracker.getService();
//    }
}
