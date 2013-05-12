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

package com.solmix.eventservice;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import com.solmix.eventservice.filter.BlackListImpl;
import com.solmix.eventservice.filter.CacheEventFilter;
import com.solmix.eventservice.filter.CachedTopicFilter;
import com.solmix.eventservice.security.SecureEventAdminFactory;
import com.solmix.eventservice.tasks.EventTaskManagerImpl;
import com.solmix.eventservice.util.EventThreadPool;
import com.solmix.eventservice.util.LeastRecentlyUsedCacheMap;

/**
 * 
 * @author solmix
 * @version 110035 2011-9-28
 */

public class EventServer
{

    static final String PROP_CACHE_SIZE = "com.solmix.eventadmin.CacheSize";

    static final String PROP_THREAD_POOL_SIZE = "com.solmix.eventadmin.ThreadPoolSize";

    static final String PROP_TIMEOUT = "com.solmix.eventadmin.Timeout";

    static final String PROP_REQUIRE_TOPIC = "com.solmix.eventadmin.RequireTopic";

    static final String PROP_IGNORE_TIMEOUT = "com.solmix.eventadmin.IgnoreTimeout";

    static final String PROP_LOG_LEVEL = "com.solmix.eventadmin.LogLevel";

    /**
     * Distributed white-list if the topic in this list.the event will be distributed post.
     */
    static final String PROP_DIS_EVENT_WHITELIST = "com.solmix.eventadmin.dis.WhiteList";

    static final String PROP_DIS_EVENT_TARGET_TYPE = "com.solmix.eventadmin.dis.TargetType";

    static final String PROP_DIS_EVENT_TARGET_ID = "com.solmix.eventadmin.dis.TargetID";

    final String DEFAULT_DIS_TARGET_TYPE = "";

    final String DEFAULT_DIS_TARGET_ID = "";

    private ServiceRegistration msRegistration;

    /**
     * EventAdmin Service registration.
     */
    private volatile ServiceRegistration eaRegistration;

    static final String PID = "com.solmix.eventadmin";

    int cacheSize;

    int threadPoolSize;

    int timeout;

    boolean requireTopic;

    String[] ignoreTimeout;

    String[] disWhiteList;

    int logLevel;

    String disTargetID;

    String disTargetType;

    BundleContext bundleContext;

    private volatile EventThreadPool syn_pool;

    private volatile EventThreadPool asy_pool;

    private volatile EventAdminImpl eventAdmin;

    public EventServer(BundleContext context)
    {
        this.bundleContext = context;
        configureServer();
        startOrUpdateServer();
        ManagedService service = tryToCreateManagerService();
        if (service != null) {
            Dictionary<String, String> filter = new Hashtable<String, String>();
            filter.put(Constants.SERVICE_PID, PID);
            msRegistration = bundleContext.registerService(ManagedService.class.getName(), service, filter);
        }
    }

    public void destory() {
        if (this.msRegistration != null) {
            msRegistration.unregister();
            msRegistration = null;
        }
        if (this.eaRegistration != null) {
            eaRegistration.unregister();
            eaRegistration = null;
        }
        if (eventAdmin != null) {
            eventAdmin.shutdown();
        }
        if (syn_pool != null) {
            syn_pool.shutdown();
            syn_pool = null;
        }
        if (asy_pool != null) {
            asy_pool.shutdown();
            asy_pool = null;
        }
    }

    /**
     * Try to create a ManagedService.
     */
    private ManagedService tryToCreateManagerService() {
        try {
            return new ManagedService() {

                @Override
                public void updated(Dictionary properties) throws ConfigurationException {

                    updateServer(properties);
                }
            };
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Update server
     * 
     * @param properties
     */
    void updateServer(final Dictionary properties) {
        new Thread() {

            public void run() {
                synchronized (EventServer.this) {
                    EventServer.this.configureServer(properties);
                    EventServer.this.startOrUpdateServer();
                }
            }

        }.start();

    }

    /**
     * 
     */
    void startOrUpdateServer() {
        /**
         * initial Topic filter.
         */
        Cache<String, String> topicCache = new LeastRecentlyUsedCacheMap<String, String>(cacheSize);
        final TopicFilter topicFilter = new CachedTopicFilter(topicCache, requireTopic);
        /**
         * Initial Event filter.
         */
        Cache<String, Filter> event_filter_cache = new LeastRecentlyUsedCacheMap<String, Filter>(cacheSize);
        final EventFilter filter = new CacheEventFilter(event_filter_cache, bundleContext);

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

        final EventTaskManager taskManager = new EventTaskManagerImpl(bundleContext, new BlackListImpl(), topicFilter, filter);
        if (eventAdmin == null) {
            eventAdmin = new EventAdminImpl(taskManager, syn_pool, asy_pool, timeout, ignoreTimeout);
            // Registered the eventAdmin service.
            eaRegistration = bundleContext.registerService(EventAdmin.class.getName(), new SecureEventAdminFactory(eventAdmin), null);
        } else {
            eventAdmin.update(taskManager, timeout, ignoreTimeout);
        }
    }

    private void configureServer() {
        configureServer(null);
    }

    /**
     * @param config config properties.
     */
    void configureServer(Dictionary config) {
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
            logLevel = getIntProperty(PROP_LOG_LEVEL, bundleContext.getProperty(PROP_LOG_LEVEL), LogService.LOG_WARNING, // default
                                                                                                                         // log
                                                                                                                         // level
                                                                                                                         // is
                                                                                                                         // WARNING
                LogService.LOG_ERROR);
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
                Activator.getLogService().log(LogService.LOG_WARNING,
                    "Value for property: " + PROP_IGNORE_TIMEOUT + " is neither a string nor a string array - Using default");
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
                Activator.getLogService().log(LogService.LOG_WARNING,
                    "Value for property: " + PROP_DIS_EVENT_WHITELIST + " is neither a string nor a string array - Using default");
            }
            logLevel = getIntProperty(PROP_LOG_LEVEL, config.get(PROP_LOG_LEVEL), LogService.LOG_WARNING, // default log
                                                                                                          // level is
                                                                                                          // WARNING
                LogService.LOG_ERROR);
        }
        // a timeout less or equals to 100 means : disable timeout
        if (timeout <= 100) {
            timeout = 0;
        }

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
                    Activator.getLogService().log(LogService.LOG_WARNING, "Unable to parse property: " + key + " - Using default", e);
                    return defaultValue;
                }
            }
            if (result >= min) {
                return result;
            }

            Activator.getLogService().log(LogService.LOG_WARNING, "Value for property: " + key + " is to low - Using default");
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

}
