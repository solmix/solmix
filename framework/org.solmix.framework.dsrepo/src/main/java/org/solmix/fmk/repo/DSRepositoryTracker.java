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

package org.solmix.fmk.repo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.repo.DSRepository;

/**
 * 
 * @version 110035
 */
public class DSRepositoryTracker
{

    private final Logger log = LoggerFactory.getLogger(DSRepositoryTracker.class.getName());

    private final List<DSRepository> repos = new CopyOnWriteArrayList<DSRepository>();

    /**
     * @return the repos
     */
    public List<DSRepository> getRepos() {
        return repos;
    }

    public void init() {
        log.info("Initial DataSource repository service tracker.");
    }

    public void close() {
        repos.clear();
    }

    public void register(DSRepository repo) {

        repos.add(repo);
    }

    public void unregister(DSRepository repo) {
        repos.remove(repo);
    }
}
