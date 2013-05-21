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

package com.solmix.cms.server;

import java.util.concurrent.locks.Lock;

import javax.jcr.Repository;

/**
 * 
 * @author Administrator
 * @version $Id$ 2012-8-19
 */

public class RepositoryMonitor implements Runnable
{

    SolmixJcrRepository repository;

    /**
     * @param solmixJcrRepository
     */
    public RepositoryMonitor(SolmixJcrRepository solmixJcrRepository)
    {
        this.repository = solmixJcrRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("unused")
    @Override
    public void run() {
        long pollTime = 100L;
        Lock lock = new java.util.concurrent.locks.ReentrantLock();
        boolean running = repository.isRunning();
        while (running) {
            synchronized (this) {
                try {
                    this.wait(pollTime);
                } catch (InterruptedException e) {
                }
            }
            if (running) {
                Repository repo = repository;
                boolean ok = false;
            }

        }

    }

}
