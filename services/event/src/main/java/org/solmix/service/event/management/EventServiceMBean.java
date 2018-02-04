package org.solmix.service.event.management;

import javax.management.JMException;
import javax.management.ObjectName;

import org.solmix.runtime.management.ManagedComponent;
import org.solmix.service.event.DefaultEventService;


public class EventServiceMBean implements ManagedComponent
{

    private DefaultEventService service;
    
    public EventServiceMBean(DefaultEventService service){
        this.service=service;
    }
    @Override
    public ObjectName getObjectName() throws JMException {
        // TODO Auto-generated method stub
        return null;
    }

}
