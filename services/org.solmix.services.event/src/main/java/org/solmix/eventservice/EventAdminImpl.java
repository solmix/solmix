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

import java.util.List;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.solmix.eventservice.deliver.AsyncDeliver;
import org.solmix.eventservice.deliver.SyncDeliver;
import org.solmix.eventservice.filter.DistributionFilter;
import org.solmix.eventservice.util.EventThreadPool;


/**
 * 
 * @author solomon
 * @version 110035  2011-9-27
 */

public class EventAdminImpl implements EventAdmin
{
    private SyncDeliver syn_deliver;
    private EventDeliver asy_deliver;
    private EventTaskManager taskManager;

    public EventAdminImpl(
        final EventTaskManager taskManager,
        EventThreadPool syncPool,
        EventThreadPool asyncPool ,
        final int timeout,
        final String[] ignoreTimeout)
    {
        assertNotNull(taskManager, "Managers");
        assertNotNull(syncPool, "syncPool");
        assertNotNull(asyncPool, "asyncPool");
        this.taskManager= taskManager;
        syn_deliver = new SyncDeliver(syncPool,timeout,ignoreTimeout);
        asy_deliver = new AsyncDeliver(asyncPool,syn_deliver);
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventAdmin#postEvent(org.osgi.service.event.Event)
     */
    @Override
    public void postEvent(Event event) {
        DistributionFilter.filte(event);
        List<EventTask> tasks= taskManager.createEventTasks(event);
        handleEvent(tasks,asy_deliver);

    }

    /**
     * @param event
     */
    protected void handleEvent(List<EventTask> tasks,final EventDeliver deliver) {
        if(tasks !=null && tasks.size()>0)
          deliver.execute(tasks);
        
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.osgi.service.event.EventAdmin#sendEvent(org.osgi.service.event.Event)
     */
    @Override
    public void sendEvent(Event event) {
        List<EventTask> tasks= taskManager.createEventTasks(event);
        handleEvent(tasks,syn_deliver);

    }
    private void assertNotNull(final Object object, final String name)
    {
        if(null == object)
        {
            throw new NullPointerException(name + " may not be null");
        }
    }
    /**
     * @param taskManager2
     * @param timeout
     * @param ignoreTimeout
     */
    public void update(EventTaskManager taskManager, int timeout, String[] ignoreTimeout) {
        this.taskManager=taskManager;
        syn_deliver.update(timeout, ignoreTimeout);
        
    }
    /**
     * 
     */
    public void shutdown() {
        taskManager = new EventTaskManager(){

            /**
             * This is a null object and this method will throw an
             * IllegalStateException due to the bundle being stopped.
             *
             * @param event An event that is not used.
             *
             * @return This method does not return normally
             *
             * @throws IllegalStateException - This is a null object and this method
             *          will always throw an IllegalStateException
             */
            public List<EventTask> createEventTasks(final Event event)
            {
                throw new IllegalStateException("The EventAdmin is stopped");
            }
        };
        
    }

}
