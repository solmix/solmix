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

import javax.annotation.Resource;

import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.repo.DSRepository;
import org.solmix.api.repo.DSRepositoryManager;
import org.solmix.commons.util.DataUtil;

/**
 * 
 * @version 110035
 */
public class DSRepositoryManagerImpl implements DSRepositoryManager
{

    private DSRepositoryTracker tracker;

    private DSRepository defaultRepo;
    private SystemContext sc;
    
    public static final String PID="org.solmix.framework.dsrepo";
    
    public DSRepositoryManagerImpl(){
        this(null);
    }
    public DSRepositoryManagerImpl(SystemContext sc){
        setSystemContext(sc);
    }
    @Resource
    public void setSystemContext(SystemContext sc) {
        this.sc = sc;
        if (sc != null) {
            sc.setBean(this, DSRepositoryManager.class);
        }
    }
    /**
     * @return the defaultRepo
     */
    public DSRepository getDefaultRepo() {
        if(defaultRepo==null){
            defaultRepo=new FileSystemRepository(sc);
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
    public DSRepository loadDSRepo(String ID) throws SlxException {
        if ("default".equals(ID) || ID == null) {
            return getDefaultRepo();
        } else {
            List<DSRepository> repos = this.tracker.getRepos();
            for (DSRepository repo : repos) {
                if (repo.getName().equals(ID)) {
                    return repo;
                }
            }
            return null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.DSRepoService#getRepos()
     */
    @Override
    public DSRepository[] getRepos() throws Exception {
        DSRepository[] rps;

        if (tracker != null) {
            List<DSRepository> repos = this.tracker.getRepos();
            if (repos != null && !repos.isEmpty())
                return (DSRepository[]) DataUtil.listToArray(tracker.getRepos());
            else {
                DSRepository res[] = { defaultRepo };
                return res;
            }
        } else {
            DSRepository res[] = { defaultRepo };
            return res;
        }
    }

}
