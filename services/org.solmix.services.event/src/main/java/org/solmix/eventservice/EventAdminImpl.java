/*
 * ========THE SOLMIX PROJECT=====================================
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.eventservice;

import java.util.Dictionary;
import java.util.List;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.eventservice.deliver.AsyncDeliver;
import org.solmix.eventservice.deliver.SyncDeliver;
import org.solmix.eventservice.filter.BlackListImpl;
import org.solmix.eventservice.filter.CachedEventFilter;
import org.solmix.eventservice.filter.CachedTopicFilter;
import org.solmix.eventservice.filter.DistributionFilter;
import org.solmix.eventservice.tasks.EventTaskManagerImpl;
import org.solmix.eventservice.util.EventThreadPool;
import org.solmix.eventservice.util.LeastRecentlyUsedCacheMap;

/**
 * OSGI Enterprise 4,chapter 113 Event Admin service specification.
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-9-27
 */

public class EventAdminImpl implements EventAdmin, ManagedService
{

    static final String PROP_CACHE_SIZE = "CacheSize";

    static final String PROP_THREAD_POOL_SIZE = "ThreadPoolSize";

    static final String PROP_TIMEOUT = "Timeout";

    static final String PROP_REQUIRE_TOPIC = "RequireTopic";

    static final String PROP_IGNORE_TIMEOUT = "IgnoreTimeout";

    /**
     * Distributed white-list if the topic in this list.the event will be distributed post.
     */
    static final String PROP_DIS_EVENT_WHITELIST = "dis.WhiteList";

    static final String PROP_DIS_EVENT_TARGET_TYPE = "dis.TargetType";

    static final String PROP_DIS_EVENT_TARGET_ID = "dis.TargetID";

    private static final Logger logger = LoggerFactory.getLogger(EventAdminImpl.class);

    private volatile EventThreadPool syn_pool;

    private volatile EventThreadPool asy_pool;

    private volatile SyncDeliver syn_deliver;

    private volatile EventDeliver asy_deliver;

    private volatile EventTaskManager taskManager;

    private BundleContext bundleContext;

    private boolean requireTopic;

    private int cacheSize;

    private int threadPoolSize;

    private int timeout;

    private String[] ignoreTimeout;

    private String[] disWhiteList;

    public EventAdminImpl()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventAdmin#postEvent(org.osgi.service.event.Event)
     */
    @Override
    public void postEvent(Event event) {
        DistributionFilter.filte(event);
        List<EventTask> tasks = taskManager.createEventTasks(event);
        handleEvent(tasks, asy_deliver);

    }

    /**
     * @return the bundleContext
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }

    /**
     * @param bundleContext the bundleContext to set
     */
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * @return the cacheSize
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * @param cacheSize the cacheSize to set
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * @return the threadPoolSize
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * @param threadPoolSize the threadPoolSize to set
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the ignoreTimeout
     */
    public String[] getIgnoreTimeout() {
        return ignoreTimeout;
    }

    /**
     * @param ignoreTimeout the ignoreTimeout to set
     */
    public void setIgnoreTimeout(String[] ignoreTimeout) {
        this.ignoreTimeout = ignoreTimeout;
    }

    /**
     * @return the disWhiteList
     */
    public String[] getDisWhiteList() {
        return disWhiteList;
    }

    /**
     * @param disWhiteList the disWhiteList to set
     */
    public void setDisWhiteList(String[] disWhiteList) {
        this.disWhiteList = disWhiteList;
    }

    /**
     * @return the requireTopic
     */
    public boolean isRequireTopic() {
        return requireTopic;
    }

    /**
     * @param requireTopic the requireTopic to set
     */
    public void setRequireTopic(boolean requireTopic) {
        this.requireTopic = requireTopic;
    }

    /**
     * @param event
     */
    protected void handleEvent(List<EventTask> tasks, final EventDeliver deliver) {
        if (tasks != null && tasks.size() > 0)
            deliver.execute(tasks);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventAdmin#sendEvent(org.osgi.service.event.Event)
     */
    @Override
    public void sendEvent(Event event) {
        List<EventTask> tasks = taskManager.createEventTasks(event);
        handleEvent(tasks, syn_deliver);

    }

    /**
     * 
     */
    public void shutdown() {
        taskManager = new EventTaskManager() {

            /**
             * This is a null object and this method will throw an IllegalStateException due to the bundle being
             * stopped.
             * 
             * @param event An event that is not used.
             * 
             * @return This method does not return normally
             * 
             * @throws IllegalStateException - This is a null object and this method will always throw an
             *         IllegalStateException
             */
            @Override
            public List<EventTask> createEventTasks(final Event event) {
                throw new IllegalStateException("The EventAdmin is stopped");
            }
        };

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
     */
    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        updateServer(properties);

    }

    /**
     * @param properties
     */
    protected void updateServer(final Dictionary<String, ?> properties) {
        new Thread() {

            @Override
            public void run() {
                synchronized (EventAdminImpl.this) {
                    EventAdminImpl.this.configureServer(properties);
                    EventAdminImpl.this.startOrUpdateServer();
                }
            }

        }.start();

    }

    /**
     * 
     */
    protected void startOrUpdateServer() {
        
        if (syn_pool == null) {
            syn_pool = new EventThreadPool(threadPoolSize, true);
        } else {
            syn_pool.updatePoolSize(threadPoolSize);
        }
        final int asyncThreadPoolSize = threadPoolSize > 5 ? threadPoolSize / 2 : 2;
        if (asy_pool == null) {
            asy_pool = new EventThreadPool(asyncThreadPoolSize, false);
        } else {
            asy_pool.updatePoolSize(asyncThreadPoolSize);
        }
        taskManager = createEventTaskManager();
        if (syn_deliver == null)
            syn_deliver = new SyncDeliver(syn_pool, timeout, ignoreTimeout);
        else
            syn_deliver.update(timeout, ignoreTimeout);
        asy_deliver = new AsyncDeliver(asy_pool, syn_deliver);

    }
    
    /**
     * Create Task manager.
     * @return
     */
    protected EventTaskManager createEventTaskManager(){
        
        /**
         * initial Topic filter.
         */
        Cache<String, String> topicCache = new LeastRecentlyUsedCacheMap<String, String>(cacheSize);
        final TopicFilter topicFilter = new CachedTopicFilter(topicCache, requireTopic);
        /**
         * Initial Event filter.
         */
        Cache<String, Filter> event_filter_cache = new LeastRecentlyUsedCacheMap<String, Filter>(cacheSize);
        final EventFilter filter = new CachedEventFilter(event_filter_cache);

        return new EventTaskManagerImpl(bundleContext, new BlackListImpl(), topicFilter, filter);
    }
    
    private void configureServer() {
        configureServer(null);
    }

    /**
     * @param config config properties.
     */
    void configureServer(Dictionary<String, ?> config) {
        if (config == null) {

            // The size of various internal caches. At the moment there are 4
            // internal caches affected. Each will cache the determined amount of
            // small but frequently used objects (i.e., in case of the default value
            // we end-up with a total of 120 small objects being cached). A value of less
            // then 10 triggers the default value.
            cacheSize = getIntProperty(PROP_CACHE_SIZE, bundleContext.getProperty(PROP_CACHE_SIZE), 30, 10);

            // The size of the internal thread pool. Note that we must execute
            // each synchronous event dispatch that happens in the synchronous event
            // dispatching thread in a new thread, hence a small thread pool is o.k.
            // A value of less then 2 triggers the default value. A value of 2
            // effectively disables thread pooling. Furthermore, this will be used by
            // a lazy thread pool (i.e., new threads are created when needed). Ones the
            // the size is reached and no cached thread is available new threads will
            // be created.
            threadPoolSize = getIntProperty(PROP_THREAD_POOL_SIZE, bundleContext.getProperty(PROP_THREAD_POOL_SIZE), 20, 2);

            // The timeout in milliseconds - A value of less then 100 turns timeouts off.
            // Any other value is the time in milliseconds granted to each EventHandler
            // before it gets blacklisted.
            timeout = getIntProperty(PROP_TIMEOUT, bundleContext.getProperty(PROP_TIMEOUT), 5000, Integer.MIN_VALUE);

            // Are EventHandler required to be registered with a topic? - The default is
            // true. The specification says that EventHandler must register with a list
            // of topics they are interested in. Setting this value to false will enable
            // that handlers without a topic are receiving all events
            // (i.e., they are treated the same as with a topic=*).
            requireTopic = getBooleanProperty(bundleContext.getProperty(PROP_REQUIRE_TOPIC), true);
            final String value = bundleContext.getProperty(PROP_IGNORE_TIMEOUT);
            if (value == null) {
                ignoreTimeout = null;
            } else {
                final StringTokenizer st = new StringTokenizer(value, ",");
                ignoreTimeout = new String[st.countTokens()];
                for (int i = 0; i < ignoreTimeout.length; i++) {
                    ignoreTimeout[i] = st.nextToken();
                }
            }
            final String whiteStr = bundleContext.getProperty(PROP_DIS_EVENT_WHITELIST);
            if (whiteStr == null) {
                disWhiteList = null;
            } else {
                final StringTokenizer st = new StringTokenizer(value, ",");
                disWhiteList = new String[st.countTokens()];
                for (int i = 0; i < disWhiteList.length; i++) {
                    disWhiteList[i] = st.nextToken();
                }
            }
        } else {
            cacheSize = getIntProperty(PROP_CACHE_SIZE, config.get(PROP_CACHE_SIZE), 30, 10);
            threadPoolSize = getIntProperty(PROP_THREAD_POOL_SIZE, config.get(PROP_THREAD_POOL_SIZE), 20, 2);
            timeout = getIntProperty(PROP_TIMEOUT, config.get(PROP_TIMEOUT), 5000, Integer.MIN_VALUE);
            requireTopic = getBooleanProperty(config.get(PROP_REQUIRE_TOPIC), true);
            ignoreTimeout = null;
            final Object value = config.get(PROP_IGNORE_TIMEOUT);
            if (value instanceof String) {
                ignoreTimeout = new String[] { (String) value };
            } else if (value instanceof String[]) {
                ignoreTimeout = (String[]) value;
            } else {
                logger.warn("Value for property: " + PROP_IGNORE_TIMEOUT + " is neither a string nor a string array - Using default");
            }
            final Object whiteObj = config.get(PROP_DIS_EVENT_WHITELIST);
            if (whiteObj instanceof String) {
                final StringTokenizer st = new StringTokenizer((String) whiteObj, ",");
                disWhiteList = new String[st.countTokens()];
                for (int i = 0; i < disWhiteList.length; i++) {
                    disWhiteList[i] = st.nextToken();
                }
            } else if (whiteObj instanceof String[]) {
                disWhiteList = (String[]) value;
            } else {
                logger.warn("Value for property: " + PROP_DIS_EVENT_WHITELIST + " is neither a string nor a string array - Using default");
            }
        }
        // a timeout less or equals to 100 means : disable timeout
        if (timeout <= 100) {
            timeout = 0;
        }

    }

    /**
     * Returns either the parsed int from the value of the property if it is set and not less then the min value or the
     * default. Additionally, a warning is generated in case the value is erroneous (i.e., can not be parsed as an int
     * or is less then the min value).
     */
    private int getIntProperty(final String key, final Object value, final int defaultValue, final int min) {
        if (null != value) {
            final int result;
            if (value instanceof Integer) {
                result = ((Integer) value).intValue();
            } else {
                try {
                    result = Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    logger.warn(new StringBuilder().append("Unable to parse property: ").append(key).append(" - Using default").toString(), e);
                    return defaultValue;
                }
            }
            if (result >= min) {
                return result;
            }

            logger.warn("Value for property: {0} is to low - Using default", key);
        }

        return defaultValue;
    }

    /**
     * Returns true if the value of the property is set and is either 1, true, or yes Returns false if the value of the
     * property is set and is either 0, false, or no Returns the defaultValue otherwise
     */
    private boolean getBooleanProperty(final Object obj, final boolean defaultValue) {
        if (null != obj) {
            if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue();
            }
            String value = obj.toString().trim().toLowerCase();

            if (0 < value.length() && ("0".equals(value) || "false".equals(value) || "no".equals(value))) {
                return false;
            }

            if (0 < value.length() && ("1".equals(value) || "true".equals(value) || "yes".equals(value))) {
                return true;
            }
        }

        return defaultValue;
    }

    public void start() {
        // initial configuration.
        configureServer();
        startOrUpdateServer();

    }
    public void start(Dictionary<String, ?> configuration){
        configureServer(configuration);
        startOrUpdateServer();
    }

    public void stop() {
        shutdown();
        if (syn_pool != null) {
            syn_pool.shutdown();
            syn_pool = null;
        }
        if (asy_pool != null) {
            asy_pool.shutdown();
            asy_pool = null;
        }
    }

}
