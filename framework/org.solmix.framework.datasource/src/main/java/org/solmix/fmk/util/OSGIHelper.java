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

package org.solmix.fmk.util;

import java.util.LinkedList;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.SlxConstants;
import org.solmix.api.exception.SlxException;
import org.solmix.api.repo.DSRepositoryManager;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.repo.DSRepositoryManagerImpl;
import org.solmix.fmk.repo.FileSystemRepository;
import org.solmix.fmk.servlet.ServletTools;

/**
 * @version 110035
 */
public class OSGIHelper
{

    private static Logger log = LoggerFactory.getLogger(OSGIHelper.class.getName());

    private static BundleContext context;

    private static LinkedList<DefaultDataSourceManager> dsmanagers = new LinkedList<DefaultDataSourceManager>();

    private static LinkedList<DSRepositoryManager> dsrepos = new LinkedList<DSRepositoryManager>();

    public static volatile ServletContext servletContext = null;

    public static volatile DefaultDataSourceManager DSM;

    public static volatile DSRepositoryManager DSR;

    public static volatile EventAdmin eventAdmin;

    /**
     * @return the context
     */
    public static BundleContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(BundleContext context) {
        OSGIHelper.context = context;
    }

    public synchronized void register_eventAdmin(EventAdmin eventAdmin) {
        OSGIHelper.eventAdmin = eventAdmin;
    }

    public synchronized void unregister_eventAdmin(EventAdmin eventAdmin) {
        OSGIHelper.eventAdmin = null;
    }

    public synchronized void register_dsm(DefaultDataSourceManager dsmanager) {
        dsmanagers.push(dsmanager);
        refresh_dsm();

    }

    public synchronized void unregister_dsm(DefaultDataSourceManager dsmanager) {
        dsmanagers.pop();
        refresh_dsm();
    }

    public synchronized void register_dsr(DSRepositoryManager dsrepo) {
        dsrepos.push(dsrepo);
        refresh_dsr();

    }

    public synchronized void unregister_dsr(DSRepositoryManager dsrepo) {
        dsrepos.pop();
        refresh_dsr();
    }

    /**
     * @return the dSM
     */
    public static DefaultDataSourceManager getDSM() {
        if (DSM == null && SlxConstants.isOSGI()) {
            try {
                throw new SlxException(Tmodule.DATASOURCE, Texception.OSGI_SERVICE_UNAVAILABLE, "osgi DataSourceManagerService is unavalable");
            } catch (SlxException e) {
                log.error(e.getMessage());
            }
            log.debug("use the OSGI container to find out datasource manager service");
        } else {
            if (DSM == null) {
                log.debug("Not under OSGI-ENV ,use default datasource manager service implementation.");
                DSM = new DefaultDataSourceManager();
            }
        }
        return DSM;
    }

    public static EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    public static void fireEvent(Event event) {
        if (eventAdmin != null) {
            eventAdmin.postEvent(event);
        }
    }

    /**
     * @return the dSR
     */
    public static DSRepositoryManager getDSR() {
        if (DSR == null && SlxConstants.isOSGI()) {
            try {
                throw new SlxException(Tmodule.REPO, Texception.OSGI_SERVICE_UNAVAILABLE, "osgi DSRepoService is unavalable");
            } catch (SlxException e) {
                log.error(e.getMessage());
            }
            log.debug("use the OSGI container to find out datasource repository service");
        } else {
            if (DSR == null) {
                log.debug("Not under OSGI-ENV ,use default datasource repository  service implementation.");
                DSRepositoryManagerImpl repoManager = new DSRepositoryManagerImpl();
                String repoLoacation = "";
                try {
                    repoLoacation = ServletTools.getContainerPath(servletContext) + "datasource";
                } catch (SlxException e) {
                    log.error(e.getMessage());
                }
                FileSystemRepository repo = new FileSystemRepository();
                repo.setLocation(repoLoacation);
                repoManager.setDefaultRepo(repo);
                DSR = repoManager;
            }
        }
        return DSR;
    }

    private synchronized void refresh_dsm() {
        if (dsmanagers != null && dsmanagers.size() >= 1)
            DSM = dsmanagers.getFirst();
    }

    private synchronized void refresh_dsr() {
        if (dsrepos != null && dsrepos.size() >= 1)
            DSR = dsrepos.getFirst();
    }

}
