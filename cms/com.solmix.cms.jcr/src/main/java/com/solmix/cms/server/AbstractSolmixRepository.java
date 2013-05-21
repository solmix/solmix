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

import java.util.Dictionary;
import java.util.Hashtable;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.cms.api.SolmixRepository;

/**
 * 
 * @author Administrator
 * @version $Id$ 2012-8-17
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractSolmixRepository implements SolmixRepository
{

    private ServiceRegistration repositoryService;

    private Repository repository;

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#getDescriptorKeys()
     */
    @Override
    public String[] getDescriptorKeys() {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorKeys();
        }
        logError("getDescriptorKeys");
        return new String[0];
    }

    private void logError(String methodName) {
        LOG.error(methodName + ": Repository not available");
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#isStandardDescriptor(java.lang.String)
     */
    @Override
    public boolean isStandardDescriptor(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.isStandardDescriptor(key);
        }
        logError("isStandardDescriptor");
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#isSingleValueDescriptor(java.lang.String)
     */
    @Override
    public boolean isSingleValueDescriptor(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.isSingleValueDescriptor(key);
        }
        logError("isSingleValueDescriptor");
        return false;
    }

    /**
     * Returns the repository underlying this instance or <code>null</code> if no repository is currently being
     * available.
     */
    protected abstract Repository acquireRepository();

    protected abstract BundleContext getContext();

    /**
     * Return a Repository,first find used JNDI configuration. Then used RMI configuration,otherwise Embed a repository.
     * 
     * @return
     */
    protected Repository getRepository() {
        return repository;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#getDescriptorValue(java.lang.String)
     */
    @Override
    public Value getDescriptorValue(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorValue(key);
        }
        logError("getDescriptorValue");
        return null;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#getDescriptorValues(java.lang.String)
     */
    @Override
    public Value[] getDescriptorValues(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptorValues(key);
        }
        logError("getDescriptorValues");
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#getDescriptor(java.lang.String)
     */
    @Override
    public String getDescriptor(String key) {
        Repository repo = getRepository();
        if (repo != null) {
            return repo.getDescriptor(key);
        }
        logError("getDescriptor");
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#login(javax.jcr.Credentials, java.lang.String)
     */
    @Override
    public Session login(Credentials credentials, String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        if (this.getRepository() == null) {
            throw new RepositoryException("CMS(JCR) repository is not ready");
        }
        if (this.getRepository() == null) {
            throw new RepositoryException("Repository not available");
        }
        if (credentials == null) {
            credentials = getAnonymousCredentials(getAnonymousUser());
        }
        if (workspaceName == null) {
            workspaceName = getDefaultWorkspace();
        }
        LOG.debug("Logging in workspace :{0}", workspaceName);
        Session session = getRepository().login(credentials, workspaceName);
        if (workspaceName == null) {
            String wsName = session.getWorkspace().getName();
            LOG.debug("Logging using Workspace: {0}", wsName);
            this.setDefaultWorkspace(wsName);
        }
        // TODO
        return session;
    }

    /**
     * @return anonymous credential
     */
    protected Credentials getAnonymousCredentials(String user) {
        String anonPass = this.getAnonymousPassword();
        char[] pass = anonPass.trim().toCharArray();
        return new SimpleCredentials(user, pass);
    }

    /**
     * Start Repository.
     * 
     * @return
     */
    protected boolean startRepository() {
        try {
            LOG.debug("Start Repository");
            Repository repo = acquireRepository();
            if (repo != null) {
                LOG.debug("got a new Repository ,Calling pingRepository()");
                if (pingRepository(repo)) {
                    setRepository(repo);
                    if (checkRepository()) {
                        LOG.debug("Ping and check repository successful");
                        setupRepoRepository(repo);
                        LOG.debug("Registing service");
                        repositoryService = registerService();
                        LOG.debug("regist repository service successful");
                        return true;

                    }
                } else {
                    LOG.debug("Ping repository successful but check repository login failed,call disposeRepository()");
                    setRepository(null);
                }
            } else {
                LOG.debug("Ping repository failed,call disposeRepository()");
            }
            disposeRepository(repo);
        } catch (Throwable t) {
            LOG.error("startRepository():uncaught throwable trying to access repository,Calling stopRepository()");
            stopRepository();
        }
        return false;
    }

    /**
     * 
     */
    protected void stopRepository() {
        if (repositoryService != null) {
            try {
                LOG.debug("Unregistering repository service");
                unregisterService(repositoryService);
            } catch (Exception e) {
                LOG.info("Unregister repository service unexpected Exception", e);
            }
            repositoryService = null;
        }
        if (repository != null) {
            Repository oldRepo = repository;
            repository = null;
            try {
                tearDown(oldRepo);
            } catch (Throwable t) {
                LOG.info("uncaught problem teardown the repository", t);
            }
            try {
                disposeRepository(oldRepo);
            } catch (Throwable t) {
                LOG.info("uncaught problem dispose the repository", t);
            }
        }

    }

    /**
     * @param oldRepo
     */
    protected void tearDown(Repository oldRepo) {
        this.tearDown();
    }

    /**
     * @param repo
     */
    protected void disposeRepository(Repository repo) {
        // TODO

    }

    /**
     * Subclass can overwritten to register the service with different types.
     * 
     * @return
     */
    protected ServiceRegistration registerService() {
        BundleContext context = getContext();
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.vendor", "solmix");
        String interfaces[] = new String[] { SolmixRepository.class.getName(), Repository.class.getName() };
        return context.registerService(interfaces, this, props);
    }

    protected void unregisterService(ServiceRegistration serviceRegistration) {
        serviceRegistration.unregister();
    }

    /**
     * @param repo
     */
    protected void setupRepoRepository(Repository repo) {
        setup(getContext());
    }

    protected void setup(final BundleContext bundleContex) {
        // TODO
    }

    protected void tearDown() {
        // TODO
    }

    /**
     * @return
     * @throws RepositoryException
     */
    protected boolean checkRepository() {
        if (this.getRepository() == null) {
            throw new IllegalStateException("repository is null");
        }
        boolean _return = false;
        if (pingRepository(this.getRepository())) {
            try {
                final Session session = loginAsAdmin(getDefaultWorkspace());
                session.logout();
                _return = true;
            } catch (RepositoryException re) {
                LOG.info("Login repository as admin failed", re);
            }
        }
        return _return;
    }

    /**
     * @param repo
     * @return
     */
    protected boolean pingRepository(Repository repo) {
        if (repo == null)
            return false;
        try {
            return repo.getDescriptor(Repository.SPEC_NAME_DESC) != null;
        } catch (Throwable t) {
            LOG.debug("pingRepository: repository " + repo + "does not seem to be available any more", t);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#login(javax.jcr.Credentials)
     */
    @Override
    public Session login(Credentials credentials) throws LoginException, RepositoryException {
        return login(credentials, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#login(java.lang.String)
     */
    @Override
    public Session login(String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        return login(null, workspaceName);
    }

    /**
     * Logs in as an anonymous user.
     * <p>
     * {@inheritDoc}
     * 
     * @see javax.jcr.Repository#login()
     */
    @Override
    public Session login() throws LoginException, RepositoryException {
        return this.login(null, null);
    }

    public Session loginAsAdmin(String workspace) throws RepositoryException {
        Credentials sc = getAdminCredentials(this.adminUser);
        return this.login(sc, workspace);
    }

    /**
     * @param adminUser2
     * @return
     */
    protected Credentials getAdminCredentials(String adminUser2) {
        String anonPass = this.getAdminPassword();
        char[] pass = anonPass.trim().toCharArray();
        return new SimpleCredentials(adminUser, pass);
    }

    /**
     * @return the defaultWorkspace
     */
    public String getDefaultWorkspace() {
        if (defaultWorkspace != null && defaultWorkspace.length() == 0)
            defaultWorkspace = null;
        return defaultWorkspace;
    }

    /**
     * @param defaultWorkspace the defaultWorkspace to set
     */
    public void setDefaultWorkspace(String defaultWorkspace) {
        this.defaultWorkspace = defaultWorkspace;
    }

    /**
     * @return the anonymousUser
     */
    public String getAnonymousUser() {
        return anonymousUser == null ? DEFAULT_ANONYMOUS_USER : anonymousUser;
    }

    /**
     * @param anonymousUser the anonymousUser to set
     */
    public void setAnonymousUser(String anonymousUser) {
        this.anonymousUser = anonymousUser;
    }

    /**
     * @return the anonymousPassword
     */
    public String getAnonymousPassword() {
        return anonymousPassword == null ? DEFAULT_ANONYMOUS_PASSWORD : anonymousPassword;
    }

    /**
     * @param anonymousPassword the anonymousPassword to set
     */
    public void setAnonymousPassword(String anonymousPassword) {
        this.anonymousPassword = anonymousPassword;
    }

    /**
     * @return the adminUser
     */
    public String getAdminUser() {
        return adminUser == null ? DEFAULT_ADMIN_USER : adminUser;
    }

    /**
     * @param adminUser the adminUser to set
     */
    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    /**
     * @return the adminPassword
     */
    public String getAdminPassword() {
        return adminPassword == null ? DEFAULT_ADMIN_PASSWORD : adminPassword;
    }

    /**
     * @param adminPassword the adminPassword to set
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    private static final Logger LOG = LoggerFactory.getLogger(SolmixJcrRepository.class);

    private String defaultWorkspace;

    private String anonymousUser;

    private String anonymousPassword;

    private String adminUser;

    private String adminPassword;

    // private Repository repository;

    public static final String DEFAULT_ANONYMOUS_USER = "anonymous";

    public static final String DEFAULT_ANONYMOUS_PASSWORD = "anonymous";

    public static final String DEFAULT_ADMIN_USER = "admin";

    public static final String DEFAULT_ADMIN_PASSWORD = "admin";
}
