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

package org.solmix.fmk.pool.internel;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;

import org.solmix.api.pool.PoolServiceFactory;
import org.solmix.fmk.pool.PoolManagerFactory;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-8
 */

public class PoolActivator implements BundleActivator
{

    private BundleContext context;

    private ConfigAdminSupport cmSuport;

    private PoolManagerFactory factory;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        factory = new PoolManagerFactory();
        cmSuport = new ConfigAdminSupport(context, factory);
        context.registerService(PoolServiceFactory.class, factory, null);

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.context = null;
        if (this.factory != null) {
            factory.destroy();
            this.factory = null;
        }
        if (this.cmSuport != null) {
            cmSuport.run();
        }
    }

    private static class ConfigAdminSupport implements Runnable
    {

        private Tracker tracker;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private ConfigAdminSupport(BundleContext context, PoolManagerFactory factory)
        {
            tracker = new Tracker(context, factory);
            Hashtable props = new Hashtable();
            props.put(Constants.SERVICE_PID, tracker.getName());
            context.registerService(ManagedServiceFactory.class.getName(), tracker, props);
            tracker.open();
        }

        public void run() {
            tracker.close();
        }

        @SuppressWarnings("rawtypes")
        private class Tracker extends ServiceTracker implements ManagedServiceFactory
        {

            private PoolManagerFactory factory;

            /**
             * @param context
             * @param filter
             * @param customizer
             */
            @SuppressWarnings("unchecked")
            public Tracker(BundleContext context, PoolManagerFactory factory)
            {
                super(context, ConfigurationAdmin.class.getName(), null);
                this.factory = factory;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.osgi.service.cm.ManagedServiceFactory#getName()
             */
            @Override
            public String getName() {
                return PoolManagerFactory.SERVICE_PID;
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.osgi.service.cm.ManagedServiceFactory#updated(java.lang.String, java.util.Dictionary)
             */
            @Override
            public void updated(String pid, Dictionary properties) throws ConfigurationException {
//                factory.update(pid, properties);

            }

            /**
             * {@inheritDoc}
             * 
             * @see org.osgi.service.cm.ManagedServiceFactory#deleted(java.lang.String)
             */
            @Override
            public void deleted(String pid) {
                // Do nothing ,just removed the configuration.the pool instance in the facotry used previously
                // configuration.

            }
        }

    }

}
