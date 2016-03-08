
package org.solmix.runtime.osgi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.threadpool.DefaultThreadPool;
import org.solmix.runtime.threadpool.ThreadPoolManager;

public class ManagedThreadPoolList implements ManagedServiceFactory, PropertyChangeListener
{

    public static final String FACTORY_PID = "org.solmix.runtime.threadpool";

    private static final Logger LOG = LoggerFactory.getLogger(ManagedThreadPoolList.class);

    private Map<String, DefaultThreadPool> queues = new ConcurrentHashMap<String, DefaultThreadPool>(4, 0.75f, 2);

    private ServiceTracker configAdminTracker;

    @Override
    public String getName() {
        return FACTORY_PID;
    }

    @Override
    public void updated(String pid, @SuppressWarnings("rawtypes") Dictionary props) throws ConfigurationException {
        if (pid == null) {
            return;
        }
        Dictionary<String, String> properties = props;
        String queueName = properties.get(DefaultThreadPool.PROPERTY_NAME);
        if (queues.containsKey(queueName)) {
            queues.get(queueName).update(properties);
        } else {
            DefaultThreadPool wq = new DefaultThreadPool(queueName);
            wq.setShared(true);
            wq.update(properties);
            wq.addChangeListener(this);
            queues.put(pid, wq);
        }
    }

    @Override
    public void deleted(String pid) {
        queues.remove(pid);
    }

    /*
     * On property changes of queue settings we update the config admin service pid of the queue
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            DefaultThreadPool queue = (DefaultThreadPool) evt.getSource();
            ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) configAdminTracker.getService();
            if (configurationAdmin != null) {
                Configuration selectedConfig = findConfigForQueueName(queue, configurationAdmin);
                if (selectedConfig != null) {
                    Dictionary<String, String> properties = queue.getProperties();
                    selectedConfig.update(properties);
                }
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    private Configuration findConfigForQueueName(DefaultThreadPool queue, ConfigurationAdmin configurationAdmin) throws Exception {
        Configuration selectedConfig = null;
        String filter = "(service.factoryPid=" + ManagedThreadPoolList.FACTORY_PID + ")";
        Configuration[] configs = configurationAdmin.listConfigurations(filter);
        for (Configuration configuration : configs) {
            @SuppressWarnings("rawtypes")
            Dictionary props = configuration.getProperties();
            String name = (String) props.get(DefaultThreadPool.PROPERTY_NAME);
            if (queue.getName().equals(name)) {
                selectedConfig = configuration;
            }
        }
        return selectedConfig;
    }

    public void addAllToWorkQueueManager(ThreadPoolManager manager) {
        if (manager != null) {
            for (DefaultThreadPool wq : queues.values()) {
                if (manager.getThreadPool(wq.getName()) == null) {
                    manager.addThreadPool(wq.getName(), wq);
                }
            }
        }
    }

    public void setConfigAdminTracker(ServiceTracker configAdminTracker) {
        this.configAdminTracker = configAdminTracker;
    }

    public void shutDown() {
        for (DefaultThreadPool wq : queues.values()) {
            wq.setShared(false);
            wq.shutdown(true);
        }
        queues.clear();
    }
}
