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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.rmi.client.ClientAdapterFactory;
import org.apache.jackrabbit.rmi.client.ClientRepositoryFactory;
import org.apache.jackrabbit.rmi.client.LocalAdapterFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.commons.util.DataUtil;
import com.solmix.commons.util.IOUtil;

/**
 * 
 * @author Administrator
 * @version $Id$ 2012-8-18
 */

public class RepositoryBuilder
{

    private final static Logger LOG = LoggerFactory.getLogger(RepositoryBuilder.class);

    public static final String OSGI_SERVICE_PREFIX = "osgi:service/";

    private String jndi;

    private String repository_config;

    private volatile BundleContext context;

    private String repository_home;

    /**
     * @return the context
     */
    public BundleContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(BundleContext context) {
        this.context = context;
    }

    /**
     * @return the repository_home
     */
    public String getRepository_home() {
        return repository_home;
    }

    /**
     * @param repository_home the repository_home to set
     */
    public void setRepository_home(String repository_home) {
        this.repository_home = repository_home;
    }

    /**
     * @return the jndi
     */
    public String getJndi() {
        return jndi;
    }

    /**
     * @param jndi the jndi to set
     */
    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    /**
     * @return the rmi
     */
    public String getRmi() {
        return rmi;
    }

    /**
     * @param rmi the rmi to set
     */
    public void setRmi(String rmi) {
        this.rmi = rmi;
    }

    /**
     * @return the repository_config
     */
    public String getRepository_config() {
        return repository_config;
    }

    /**
     * @param repository_config the repository_config to set
     */
    public void setRepository_config(String repository_config) {
        this.repository_config = repository_config;
    }

    private String rmi;

    public Repository getRepository() {
        Repository tmp = null;
        String tried = "";
        // access repository thought jndi
        if (DataUtil.isNotNullAndEmpty(getJndi())) {
            tried += "[JNDI]";
            tmp = getOSGIJNDIService(getJndi());

        }
        if (tmp != null)
            return tmp;
        // access repository thought rmi
        if (DataUtil.isNotNullAndEmpty(getRmi())) {
            tried += " [RMI]";
            LOG.debug("Trying to acquire Repository '{0}' via RMI", getRmi());
            try {
                ClientRepositoryFactory crf = getClientRepositoryFactory();
                tmp = crf.getRepository(rmi);
                LOG.info("Acquired Repository '{0}' via RMI", getRmi());
            } catch (Throwable e) {
                LOG.info("Unable to acquire Repository'{0}' via RMI", getRmi());
            }
        }
        // try to self building.
        if (tmp == null) {
            tried += " [Embed]";
            tmp = tryToEmbedRepository();
        }
        if (tmp == null) {
            LOG.info("Unable to acquire Repository .Have tried {0}", tried);
        }
        return tmp;

    }

    /**
     * @return
     */
    protected Repository tryToEmbedRepository() {
        /*
         * String home = System.getProperty(ServerConstants.SOLMIX_HOME); if (home == null) { home =
         * System.getProperty(ServerConstants.SOLMIX_BASE); }
         */

        String repoHome = "";
        File repoHomeFile = new File(repository_home);
        if (!repoHomeFile.isAbsolute()) {
            repoHome = repoHomeFile.getAbsolutePath();
        }
        if (System.getProperty(ServerConstants.PROPERTY_DERBY_ERROR_FILE) == null) {
            System.setProperty(ServerConstants.PROPERTY_DERBY_ERROR_FILE, repoHome + "/derby.log");
        }
        InputStream ins = null;
        try {
            RepositoryConfig repoConf;
            if (DataUtil.isNotNullAndEmpty(repository_config)) {
                File configFile = new File(repository_config);
                if (configFile.canRead()) {
                    ins = new FileInputStream(configFile);
                    LOG.info("Using configuration file {0}", configFile.getAbsolutePath());
                } else {

                    try {
                        URL configURL = new URL(repository_config);
                        ins = configURL.openStream();
                        LOG.info("Using configuration file {0}", configFile.getAbsolutePath());
                    } catch (MalformedURLException e) {
                        LOG.info("Configuration file {0} has been lost,trying to recreate", configFile.getAbsolutePath());
                        copyFile("repository.xml", configFile);
                        ins = new FileInputStream(configFile);
                        LOG.info("Using configuration file {0}", configFile.getAbsolutePath());

                    }
                }
                repoConf = RepositoryConfig.create(ins, repoHome);
            } else {
                repoConf = RepositoryConfig.create(repoHomeFile);
            }
            return RepositoryImpl.create(repoConf);
        } catch (FileNotFoundException e) {
            LOG.error("Can not find the repository configuration file:{0}", repository_config);
        } catch (IOException ioe) {

        } catch (RepositoryException e) {
            LOG.error("Problem when embed repository from " + "", e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }
        return null;
    }

    private void copyFile(String absPath, File target) throws IOException {
        Bundle bundle = context.getBundle();
        URL entry = bundle.getEntry(absPath);
        if (entry == null) {
            throw new FileNotFoundException(absPath);
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = entry.openStream();
            target.getParentFile().mkdirs();
            out = new FileOutputStream(target);
            IOUtil.copyStreams(in, out);
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    /**
     * @return
     */
    protected ClientRepositoryFactory getClientRepositoryFactory() {
        return new ClientRepositoryFactory(getLocalAdapterFactory());
    }

    /**
     * @return
     */
    private LocalAdapterFactory getLocalAdapterFactory() {
        return new ClientAdapterFactory();
    }

    protected Repository getOSGIJNDIService(String jndi) {
        String name = OSGI_SERVICE_PREFIX + jndi;
        LOG.trace("getOSGIJNDIService(): service[{0}] ", name);
        try {
            InitialContext ic = new InitialContext();
            Object o = ic.lookup(name);
            if (o instanceof Repository) {
                return (Repository) o;
            }
        } catch (NamingException e) {
            LOG.error("getOSGIJNDIService() -- NamingException on OSGI service lookup" + name, e);
            e.printStackTrace();
            return null;
        }
        return null;

    }
}
