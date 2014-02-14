/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.cms.server;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.management.DataStoreGarbageCollector;
import org.apache.jackrabbit.api.management.RepositoryManager;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.cms.api.SolmixRepository;

/**
 * 
 * @author Administrator
 * @version 0.1.1 2012-8-17
 */

public class SolmixJcrRepository extends AbstractSolmixRepository implements Runnable, Repository, RepositoryManager
{

    private static final Logger LOG = LoggerFactory.getLogger(SolmixJcrRepository.class);

    private RepositoryBuilder builder;

    private Thread monitorThread;

    private boolean running;

    /**
     * seconds.
     */
    private long pollTimeInActive = 30;

    private long pollTimeActive = 60;

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Repository Service start method.Used by blueprint configuration.
     */
    public void start() {
        try {
            if (startRepository()) {
                LOG.info("Repository started successfully");
            } else {
                LOG.warn("Repository started failed ,try again");
            }
        } catch (Throwable t) {
            LOG.warn("start repository throw unexpected problem", t);
        }
        startRepositoryMonitor();
    }

    /**
     * 
     */
    protected void startRepositoryMonitor() {
        if (monitorThread == null) {
            running = true;
            monitorThread = new Thread(this, "Repository Monitor");
            monitorThread.start();
        }
    }

    public void stop() {

        // First stop repository monitor.
        stopRepositoryMonitor();
        stopRepository();
        builder.setContext(null);
    }

    /**
     * 
     */
    protected void stopRepositoryMonitor() {
        running = false;
        Thread rpThread = monitorThread;
        if (rpThread == null) {
            return;
        }
        monitorThread = null;
        synchronized (rpThread) {
            rpThread.notifyAll();
        }
        try {
            rpThread.join(10000L);
        } catch (InterruptedException ie) {
        }
        if (rpThread.isAlive()) {
            LOG.error("imed waiting for thread {0} to terminate", rpThread);
        }

    }

    /**
     * @return the builder
     */
    public RepositoryBuilder getBuilder() {
        return builder;
    }

    /**
     * @param builder the builder to set
     */
    public void setBuilder(RepositoryBuilder builder) {
        this.builder = builder;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.cms.server.AbstractSolmixRepository#acquireRepository()
     */
    @Override
    protected Repository acquireRepository() {
        return builder.getRepository();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.cms.server.AbstractSolmixRepository#getContext()
     */
    @Override
    protected BundleContext getContext() {
        return builder.getContext();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        long pollTime = 100L;
        final long MSEC = 1000L;
        final int pollTimeFactor = 2;
        Object lock = monitorThread;
        try {
            while (running) {
                synchronized (lock) {
                    try {
                        lock.wait(pollTime);
                    } catch (InterruptedException e) {
                    }
                }
                long newPollTime = pollTime;
                if (running) {
                    Repository repo = this.getRepository();
                    boolean ok = false;
                    if (repo == null) {
                        if (startRepository()) {
                            ok = true;
                            newPollTime = pollTimeActive * MSEC;
                        } else {
                            newPollTime = Math.min(pollTime * pollTimeFactor, Math.max(pollTimeInActive, pollTimeActive * MSEC));
                        }
                    } else if (this.checkRepository()) {
                        ok = true;
                        newPollTime = pollTimeActive * MSEC;
                    } else {
                        stopRepository();
                        newPollTime = pollTimeInActive * MSEC;
                    }
                    if (newPollTime != pollTime) {
                        pollTime = newPollTime;
                        LOG.debug("Repository monitor internal poll time set to " + pollTime + " repository is "
                            + (ok ? "available" : "Not avaliable"));
                    }
                }
            }// END while(running)
            LOG.info("Repository monitor is stopping ");

        } catch (Exception e) {
            LOG.error("Repsitory monitor caught unexpected problem", e);
        } finally {
            LOG.info("Stoping repository on shutdown");
            stopRepository();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.jackrabbit.api.management.RepositoryManager#createDataStoreGarbageCollector()
     */
    @Override
    public DataStoreGarbageCollector createDataStoreGarbageCollector() throws RepositoryException {
        RepositoryImpl repository = (RepositoryImpl) getRepository();
        if (repository != null) {
            return repository.createDataStoreGarbageCollector();
        }

        throw new RepositoryException("Repository couldn't be acquired");
    }

    @SuppressWarnings("rawtypes")
    protected ServiceRegistration registerService() {
        BundleContext context = getContext();
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.vendor", "solmix");
        String interfaces[] = new String[] { SolmixRepository.class.getName(), Repository.class.getName(), RepositoryManager.class.getName() };
        return context.registerService(interfaces, this, props);

    }

    protected void disposeRepository(Repository repository) {
        super.disposeRepository(repository);
        if (repository instanceof RepositoryImpl) {
            try {
                ((RepositoryImpl) repository).shutdown();
            } catch (Throwable t) {
                // todo
            }
        }
    }

    // protected Credentials getAdminCredentials(String adminUser) {
    // return new AdminCredentials(adminUser);
    // }
    //
    // protected Credentials getAnonymousCredentials(String user) {
    // return new AnonCredentials(user);
    // }
}
