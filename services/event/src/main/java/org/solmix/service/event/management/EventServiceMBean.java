package org.solmix.service.event.management;

import java.util.Set;

import javax.management.JMException;
import javax.management.ObjectName;

import org.solmix.runtime.event.IEventHandler;
import org.solmix.runtime.management.ManagedComponent;
import org.solmix.runtime.management.ManagementConstants;
import org.solmix.runtime.management.annotation.ManagedAttribute;
import org.solmix.runtime.management.annotation.ManagedOperation;
import org.solmix.runtime.management.annotation.ManagedResource;
import org.solmix.service.event.DefaultEventService;

@ManagedResource(componentName = "EventService", description = "Responsible for managing EventService")
public class EventServiceMBean implements ManagedComponent
{
    private static final String TYPE_VALUE = "EventService";

    private DefaultEventService service;
    
    public EventServiceMBean(DefaultEventService service){
        this.service=service;
    }
    
    @ManagedAttribute(description = "The ThreadPool Core Size", persistPolicy = "OnUpdate")
    public long getThreadPoolSize() {
        return service.getThreadPoolSize();
    }
    
    @ManagedAttribute(description = "Synchronous event handle timeout")
    public int getTimeout() {
        return service.getTimeout();
    }
    
    @ManagedAttribute(description = "Synchronous event ignore handle timeout")
    public String[] getIgnoreTimeout() {
        return service.getIgnoreTimeout();
    }
   
    
    @ManagedAttribute(description = "Receive asynchronous event count", persistPolicy = "OnUpdate")
    public long getPostCount() {
        return service.getPostCount();
    }
    
    @ManagedAttribute(description = "Receive synchronous event count", persistPolicy = "OnUpdate")
    public long getSendCount() {
        return service.getSendCount();
    }
    @ManagedAttribute(description = "Timeout count", persistPolicy = "OnUpdate")
    public long getTimeoutCount() {
        return service.getTimeoutCount();
    }
    @ManagedAttribute(description = "Handled event task count", persistPolicy = "OnUpdate")
    public long getHandleCount(){
        return service.getHandleCount();
    }
    
    @ManagedOperation
    public void setTimeout(int timeout){
        service.setTimeout(timeout);
    }
    
    @ManagedOperation
    public String[] getEventHandlerString(){
        Set<String> str= service.getTaskManager().getEventHandlerString();
        return str.toArray(new String[str.size()]);
    }
    
    @ManagedOperation
    public void setThreadPoolSize(int threadPoolSize){
        service.setThreadPoolSize(threadPoolSize);
    }
    
    @ManagedOperation
    public String[] getBlackListHandlers(){
       Set<IEventHandler> handlers= service.getTaskManager().getBlackListHandlers();
       String[] strHandlers = new String[handlers.size()];
       int i=0;
       for(IEventHandler handler:handlers){
           strHandlers[i]=handler.getClass().getName();
           i++;
       }
       return strHandlers;
    }
    
    @Override
    public ObjectName getObjectName() throws JMException {
        String id = service.getContainer().getId();
        StringBuilder buffer = new StringBuilder(ManagementConstants.DEFAULT_DOMAIN_NAME).append(':');
        buffer.append(ManagementConstants.TYPE_PROP).append('=').append(TYPE_VALUE).append(',');
        buffer.append(ManagementConstants.CONTAINER_ID_PROP).append('=').append(id).append(',');
        String instanceId  = new StringBuilder().append(service.hashCode()).toString();
        buffer.append(ManagementConstants.INSTANCE_ID_PROP).append('=').append(instanceId);
        return new ObjectName(buffer.toString());
    }

}
