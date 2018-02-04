/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.service.event;

import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.event.EventServiceAdapter;
import org.solmix.runtime.event.IEvent;
import org.solmix.runtime.event.IEventHandler;
import org.solmix.runtime.management.ComponentManager;
import org.solmix.runtime.threadpool.DefaultThreadPool;
import org.solmix.runtime.threadpool.ThreadPool;
import org.solmix.runtime.threadpool.ThreadPoolManager;
import org.solmix.service.event.deliver.AsyncDeliver;
import org.solmix.service.event.deliver.SyncDeliver;
import org.solmix.service.event.filter.CachedTopicFilter;
import org.solmix.service.event.filter.EventHandlerBlackListImpl;
import org.solmix.service.event.filter.NullEventHandlerBlackList;
import org.solmix.service.event.management.EventServiceMBean;
import org.solmix.service.event.util.LeastRecentlyUsedCacheMap;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月3日
 */

public class DefaultEventService extends EventServiceAdapter
{

    private static final Logger logger = LoggerFactory.getLogger(DefaultEventService.class);

    private volatile ThreadPool asyThreadPool;

    private volatile ThreadPool synThreadPool;

    private volatile SyncDeliver syncDeliver;

    private volatile AsyncDeliver asyncDeliver;

    private volatile EventTaskManager taskManager;

    private Integer cacheSize;

    private Integer threadPoolSize;

    private Integer timeout;

    private String[] ignoreTimeout;

    private Boolean requireTopic;

    private boolean blackListEnable;

    private Object startLock = new Object();
    
    private volatile boolean runnning=false;

    @Resource
    private Container container;

    @Override
    public void postEvent(IEvent event) {
        checkRunning();
        List<EventTask> tasks = taskManager.createEventTasks(event);
        handleEvent(tasks, asyncDeliver);
    }

    @Override
    public void sendEvent(IEvent event) {
        checkRunning();
        List<EventTask> tasks = taskManager.createEventTasks(event);
        handleEvent(tasks, syncDeliver);
    }

    protected void handleEvent(List<EventTask> tasks, final EventDeliver deliver) {
        if (tasks != null && tasks.size() > 0)
            deliver.execute(tasks);

    }

    /**
     * 初始化并启动服务
     */
    @PostConstruct
    public void start() {
        synchronized (startLock) {
            configureService();
            startOrUpdateService();
            runnning=true;
            ComponentManager cm  = container.getExtension(ComponentManager.class);
            if(cm!=null){
                EventServiceMBean mbean = new EventServiceMBean(this);
                try {
                    cm.register(mbean);
                } catch (JMException e) {
                    logger.warn(e.getMessage(),e);
                }
            }
        }

    }

    /**
     * 关闭服务并回收资源
     */
    @PreDestroy
    public void shutdown() {
        synchronized (startLock) {
            runnning=false;
            ComponentManager cm  = container.getExtension(ComponentManager.class);
            if(cm!=null){
                EventServiceMBean mbean = new EventServiceMBean(this);
                try {
                    cm.unregister(mbean);
                } catch (JMException e) {
                    logger.warn(e.getMessage(),e);
                }
            }
            if(this.synThreadPool!=null){
                this.synThreadPool.shutdown(true);
            }
            if(this.asyThreadPool!=null){
                this.asyThreadPool.shutdown(true);
            }
        }
    }
    
    private void checkRunning(){
        if(!runnning){
            throw new IllegalStateException("EventService is going to shutdown ,refuse event");
        }
    }

    public Integer getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public Boolean isRequireTopic() {
        return requireTopic;
    }

    public void setRequireTopic(Boolean requireTopic) {
        this.requireTopic = requireTopic;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String[] getIgnoreTimeout() {
        return ignoreTimeout;
    }

    public void setIgnoreTimeout(String value) {
        if (value == null) {
            ignoreTimeout = null;
        } else {
            final StringTokenizer st = new StringTokenizer(value, ",");
            ignoreTimeout = new String[st.countTokens()];
            for (int i = 0; i < ignoreTimeout.length; i++) {
                ignoreTimeout[i] = st.nextToken();
            }
        }
    }

    void configureService() {
        cacheSize = getIntProperty("cacheSize", getCacheSize(), 30, 10);
        threadPoolSize = getIntProperty("threadPoolSize", getThreadPoolSize(), 20, 2);
        timeout = getIntProperty("timeout", getTimeout(), 5000, Integer.MIN_VALUE);
        requireTopic = getBooleanProperty(isRequireTopic(), true);

        if (timeout <= 100) {
            timeout = 0;
        }

    }

    protected void startOrUpdateService() {
        if (synThreadPool == null) {
            ThreadPoolManager manager= container.getExtension(ThreadPoolManager.class);
            synThreadPool =manager.createPool( "SYN-EVENT", 0, threadPoolSize, 5, 1024, 2 * 60 * 1000l);
        } else {
            ((DefaultThreadPool)synThreadPool).setMaxThreads(threadPoolSize);
        }
        final int asyncThreadPoolSize = threadPoolSize > 5 ? threadPoolSize / 2 : 2;
        if (asyThreadPool == null) {
            ThreadPoolManager manager= container.getExtension(ThreadPoolManager.class);
            asyThreadPool = manager.createPool( "ASY-EVENT", 0, threadPoolSize, 5, 1024, 2 * 60 * 1000l);
        } else {
            ((DefaultThreadPool)synThreadPool).setMaxThreads(asyncThreadPoolSize);
        }
        taskManager = createTaskManager();
        if (syncDeliver == null) {
            syncDeliver = new SyncDeliver(synThreadPool, timeout, ignoreTimeout);
        } else {
            syncDeliver.update(timeout, ignoreTimeout);
        }

        asyncDeliver = new AsyncDeliver(asyThreadPool, syncDeliver);
    }

    /**
     * @return
     */
    protected EventTaskManager createTaskManager() {
        Cache<String, String> topicCache = new LeastRecentlyUsedCacheMap<String, String>(cacheSize);
        final TopicFilter topicFilter = new CachedTopicFilter(topicCache, requireTopic);
        EventHandlerBlackList bl;
        if (isBlackListEnable()) {
            bl = new EventHandlerBlackListImpl(timeout);
        } else {
            bl = new NullEventHandlerBlackList();
        }
        return new DefaultEventTaskManager(container, bl, topicFilter);
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

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    private DefaultEventTaskManager getDefaultEventTaskManager(String operation) {
        if (this.taskManager instanceof DefaultEventTaskManager) {
            return (DefaultEventTaskManager) this.taskManager;
        } else {
            throw new IllegalStateException(operation + " is not supported");
        }
    }

    @Override
    public void addEventHandler(String topic, IEventHandler handler) {
        getDefaultEventTaskManager("addEventHandler").addEventHandler(topic, handler);
    }

    @Override
    public void addEventHandler(IEventHandler handler) {
        getDefaultEventTaskManager("addEventHandler").addEventHandler(handler);
    }

    public void removeEventHandler(String topic) {
        getDefaultEventTaskManager("removeEventHandler").removeEventHandler(topic);
    }

    public ThreadPool getAsyThreadPool() {
        return asyThreadPool;
    }

    public void setAsyThreadPool(DefaultThreadPool asyThreadPool) {
        this.asyThreadPool = asyThreadPool;
    }

    public ThreadPool getSynThreadPool() {
        return synThreadPool;
    }

    public void setSynThreadPool(DefaultThreadPool synThreadPool) {
        this.synThreadPool = synThreadPool;
    }

    /**
     * 是否启用黑名单功能，黑名单功能可以将超时的任务加入黑名单，如果可能超时的任务，可以考虑使用异步事项
     * 
     * @return
     */
    public boolean isBlackListEnable() {
        return blackListEnable;
    }

    public void setBlackListEnable(boolean blackListEnable) {
        this.blackListEnable = blackListEnable;
    }

}
