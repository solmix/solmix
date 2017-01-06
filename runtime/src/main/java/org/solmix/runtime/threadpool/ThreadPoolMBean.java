package org.solmix.runtime.threadpool;

import javax.management.JMException;
import javax.management.ObjectName;

import org.solmix.runtime.Container;
import org.solmix.runtime.management.ManagedComponent;
import org.solmix.runtime.management.ManagementConstants;
import org.solmix.runtime.management.annotation.ManagedAttribute;
import org.solmix.runtime.management.annotation.ManagedResource;

@ManagedResource(componentName = "ThreadPool", 
description = "The Solmix ThreadPool", 
currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200)
public class ThreadPoolMBean implements ManagedComponent
{

    private ThreadPoolManager manager;
    private DefaultThreadPool pool;
    private static final String TYPE_VALUE = "ThreadPool";
    public ThreadPoolMBean(DefaultThreadPool threadpool,ThreadPoolManager pmanager){
        manager=pmanager;
        pool=threadpool;
    }

    @ManagedAttribute(description = "The ThreadPool MaxSize",
                      persistPolicy = "OnUpdate")
    public long getMaxQueueSize() {
        return pool.getMaxQueueSize();
    }
   
    @ManagedAttribute(description = "The ThreadPool Current size",
                      persistPolicy = "OnUpdate")
    public long getQueueSize() {
        return pool.getQueueSize();
    }

    @ManagedAttribute(description = "The largest number of threads")
    public int getLargestPoolSize() { 
        return pool.getLargestPoolSize(); 
    }

    @ManagedAttribute(description = "The current number of threads")
    public int getPoolSize() { 
        return pool.getPoolSize(); 
    }

    @ManagedAttribute(description = "The number of threads currently busy")
    public int getActiveCount() { 
        return pool.getActiveCount(); 
    }
    
    @ManagedAttribute(description = "The Threadpool has nothing to do",
                      persistPolicy = "OnUpdate")
    public boolean isQueueEmpty() {
        return pool.isQueueEmpty();
    }

    @ManagedAttribute(description = "The ThreadPool is very busy")
    public boolean isQueueFull() {
        return pool.isQueueFull();
    }

    @ManagedAttribute(description = "The ThreadPool MaxSize",
                      persistPolicy = "OnUpdate")
    public int getMaxThreads() {
        return pool.getMaxThreads();
    }
    public void setMaxThreads(int hwm) {
        pool.setMaxThreads(hwm);
    }

    @ManagedAttribute(description = "The ThreadPool MinSize",
                      persistPolicy = "OnUpdate")
    public int getMinThreads() {
        return pool.getMinThreads();
    }

    public void setMinThreads(int lwm) {
        pool.setMinThreads(lwm);
    }

    @Override
    public ObjectName getObjectName() throws JMException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(ManagementConstants.DEFAULT_DOMAIN_NAME).append(':');
        if (!pool.isShared()) {
            String busId = Container.DEFAULT_CONTAINER_ID;
            if (manager instanceof DefaultThreadPoolManager) {
                busId = ((DefaultThreadPoolManager)manager).getContainer().getId();
            }
            buffer.append(ManagementConstants.CONTAINER_ID_PROP).append('=').append(busId).append(',');
            buffer.append(ThreadPoolManagerMBean.TYPE_VALUE).append('=');
            buffer.append(ThreadPoolManagerMBean.NAME_VALUE).append(',');
        } else {
            buffer.append(ManagementConstants.CONTAINER_ID_PROP).append("=Shared,");
        }
        buffer.append(ManagementConstants.TYPE_PROP).append('=').append(TYPE_VALUE).append(',');
        buffer.append(ManagementConstants.NAME_PROP).append('=').append(pool.getName()).append(',');
        buffer.append(ManagementConstants.INSTANCE_ID_PROP).append('=').append(pool.hashCode());
        return new ObjectName(buffer.toString());
    }


}
