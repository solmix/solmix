/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.spring.event;

import org.solmix.api.event.EventManager;
import org.solmix.api.event.IEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-30
 */

public class SpringEventManager implements EventManager, ApplicationContextAware
{

    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#postEvent(org.solmix.api.event.IEvent)
     */
    @Override
    public void postEvent(IEvent event) {

        doPublishEvent(event);
    }

    protected void doPublishEvent(IEvent event) {
        applicationContext.publishEvent(new SpringWrappedEvent(event));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#sendEvent(org.solmix.api.event.IEvent)
     */
    @Override
    public void sendEvent(IEvent event) {
        doPublishEvent(event);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.event.EventManager#getProvider()
     */
    @Override
    public String getProvider() {
        return SPRING;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
