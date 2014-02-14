/*
 * Copyright 2012 The Solmix Project
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.repo.DSRepository;
import org.solmix.api.repo.DSRepositoryManager;

/**
 * 
 * @version 110035
 */
public class DSRepositoryManagerImpl implements DSRepositoryManager
{

    private DSRepositoryTracker tracker;

    private DSRepository defaultRepo;

    private SystemContext sc;

    private final Map<String, DSRepository> internal;

    private boolean delayInit;

    public static final String PID = "org.solmix.framework.dsrepo";

    public DSRepositoryManagerImpl()
    {
        this(null);
    }

    public DSRepositoryManagerImpl(final SystemContext sc)
    {
        setSystemContext(sc);
        internal = new LinkedHashMap<String, DSRepository>();
    }

    @Resource
    public void setSystemContext(final SystemContext sc) {
        this.sc = sc;
        if (sc != null) {
            sc.setBean(this, DSRepositoryManager.class);
        }
    }

    /**
     * @return the defaultRepo
     */
    public DSRepository getDefaultRepo() {
        if (defaultRepo == null) {
            defaultRepo = new ExtXmlFileRepository(sc);
        }
        return defaultRepo;
    }

    /**
     * @param defaultRepo the defaultRepo to set
     */
    public void setDefaultRepo(DSRepository defaultRepo) {
        this.defaultRepo = defaultRepo;
    }

    /**
     * @return the tracker
     */
    public DSRepositoryTracker getTracker() throws SlxException {
        return tracker;
    }

    /**
     * @param tracker the tracker to set
     */
    public void setTracker(DSRepositoryTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.DSRepoService#loadDSRepo()
     */
    @Override
    public synchronized DSRepository getRepository(String ID) throws SlxException {
        delayInit();
        DSRepository frepo = internal.get(ID);
        if (frepo != null)
            return frepo;

        List<DSRepository> repos = this.tracker.getRepos();
        for (DSRepository repo : repos) {
            if (repo.getName().equals(ID)) {
                internal.put(repo.getName(), frepo);
                return repo;
            }
        }
        return null;
    }

    private synchronized void delayInit() {
        if (!delayInit) {
            internal.put(DSRepository.EXT_FILE, getDefaultRepo());
            internal.put(DSRepository.BUILDIN_FILE, getBuildInFileRepo());
            delayInit=true;
        }
    }

    /**
     * @return
     */
    private DSRepository getBuildInFileRepo() {
        return new ConfiguredFileRepository(sc);
    }

    @Override
    public DSRepository[] getRepositories() throws SlxException {
        delayInit();
        Collection<DSRepository> buildIns=internal.values();
        if (tracker != null) {
            List<DSRepository> repos = this.tracker.getRepos();
            if (repos != null && !repos.isEmpty())
                buildIns.addAll(repos);
        } 
        DSRepository valueArr[] = new DSRepository[buildIns.size()];
       return buildIns.toArray(valueArr);
    }

}
