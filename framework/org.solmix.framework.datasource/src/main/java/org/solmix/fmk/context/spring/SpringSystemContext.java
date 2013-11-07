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

package org.solmix.fmk.context.spring;

import org.solmix.api.bean.BeanConfigurer;
import org.solmix.api.bean.ConfiguredBeanProvider;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.fmk.cm.spring.SpringConfigureUnitManager;
import org.solmix.fmk.context.ext.SolmixSystemContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-4
 */

public class SpringSystemContext extends SolmixSystemContext implements ApplicationContextAware
{

    private AbstractApplicationContext applicationContext;

    private boolean closeContext;

    public SpringSystemContext()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (AbstractApplicationContext) applicationContext;
        ApplicationListener listener = new ApplicationListener() {

            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                SpringSystemContext.this.onApplicationEvent(event);
            }
        };
        this.applicationContext.addApplicationListener(listener);
        ApplicationContext ac = applicationContext.getParent();
        while (ac != null) {
            if (ac instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) ac).addApplicationListener(listener);
            }
            ac = ac.getParent();
        }
        setBean(applicationContext.getClassLoader(), ClassLoader.class);
        setBean(new SpringConfigurer(applicationContext), BeanConfigurer.class);
        //
        setBean(applicationContext, ApplicationContext.class);
        ConfiguredBeanProvider provider = getBean(ConfiguredBeanProvider.class);
        if (!(provider instanceof SpringBeanProvider)) {
            setBean(new SpringBeanProvider(applicationContext, this), ConfiguredBeanProvider.class);
        }
        setBean(new SpringConfigureUnitManager(), ConfigureUnitManager.class);
        if (getStatus() != ContextStatus.OPENING) {
            initialize();
        }
    }

    /**
     * @param event
     */
    protected void onApplicationEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        boolean doIt = false;
        ApplicationContext ac = applicationContext;
        while (ac != null && !doIt) {
            if (event.getSource() == ac) {
                doIt = true;
                break;
            }
            ac = ac.getParent();
        }
        if (doIt) {
            if (event instanceof ContextRefreshedEvent) {
                if (getStatus() != ContextStatus.OPENING) {
                    initialize();
                }
            } else if (event instanceof ContextClosedEvent) {
                // getBean(ContextLifeCycleManager.class).postShutdown();
            }
        }

    }

    @Override
    public void destroyBeans() {
        if (closeContext) {
            applicationContext.close();
        }
        super.destroyBeans();
    }

    /**
     * @param b
     */
    public void setCloseContext(boolean b) {
        this.closeContext = b;

    }

}
