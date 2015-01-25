/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.runtime.exchange.support;

import java.io.Closeable;
import java.io.IOException;

import javax.management.JMException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.Server;
import org.solmix.runtime.exchange.ServerLifeCycleManager;
import org.solmix.runtime.exchange.ServerRegistry;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.TransporterFactory;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.management.ComponentManager;
import org.solmix.runtime.management.ManagedEndpoint;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月16日
 */

public class DefaultServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServer.class);

    private final Container container;

    private final Endpoint endpoint;

    private final ProtocolFactory protocolFactory;

    private Transporter transporter;

    private ServerRegistry serverRegistry;

    private ManagedEndpoint managedEndpoint;

    private ServerLifeCycleManager serverLifeCycleManager;

    private ComponentManager componentManager;
    
    private  boolean stopped = true;

    /**
     * @param container server运行容器
     * @param endpoint 负责server信息收发
     * @param ptlFactory server协议
     * @param tgFactory 传输端
     * @throws IOException 
     */
    public DefaultServer(Container container, Endpoint endpoint,
        ProtocolFactory ptlFactory, TransporterFactory tgFactory) throws IOException {
        this.container = container;
        this.endpoint = endpoint;
        this.protocolFactory = ptlFactory;
        Assert.isNotNull(tgFactory);
        makeTransporterForServer(tgFactory);

    }

    /**
     * @param tgFactory
     * @throws IOException 
     */
    private void makeTransporterForServer(TransporterFactory tgFactory) throws IOException {
        EndpointInfo ei = endpoint.getEndpointInfo();
        transporter = tgFactory.getTransporter(ei, container);
        LOG.info("Server published address is " + ei.getAddress());

        serverRegistry = container.getExtension(ServerRegistry.class);

        managedEndpoint = createManagedEndpoint();

        serverLifeCycleManager = container.getExtension(ServerLifeCycleManager.class);
        if (serverLifeCycleManager != null) {
            serverLifeCycleManager.registerListener(managedEndpoint);
        }

        componentManager = container.getExtension(ComponentManager.class);
        if (componentManager != null) {
            try {
                componentManager.register(managedEndpoint);
            } catch (JMException e) {
                LOG.warn("Registering ManagedEndpoint failed.", e);
            }
        }
    }

    protected ManagedEndpoint createManagedEndpoint() {
        return new ManagedEndpoint(container, endpoint, this);
    }

    @Override
    public void start() {
        if (!stopped) {
            return;
        }
        LOG.trace("Server is starting.");

        protocolFactory.addListener(transporter, endpoint);
        if (serverRegistry != null) {
            LOG.trace("Register the server " + transporter.getAddress());
            serverRegistry.register(this);
        }

        if (serverLifeCycleManager == null) {
            serverLifeCycleManager = container.getExtension(ServerLifeCycleManager.class);
            if (serverLifeCycleManager != null) {
                serverLifeCycleManager.registerListener(managedEndpoint);
            }
        }
        if (serverLifeCycleManager != null) {
            serverLifeCycleManager.startServer(this);
        }
        stopped = false;
    }

    @Override
    public void stop() {
        if (stopped) {
            return;
        }
        LOG.trace("Server is stopping.");

        for (Closeable c : endpoint.getCleanupHooks()) {
            try {
                c.close();
            } catch (IOException e) {
                //ignore
            }
        }
        
        if (serverLifeCycleManager != null) {
            serverLifeCycleManager.stopServer(this);
        }
        getTransporter().setProcessor(null);
        stopped = true;
    }

    @Override
    public void destroy() {
        stop();
        getTransporter().shutdown();
        if (serverRegistry != null) {
            LOG.trace("unregister the server to serverRegistry.");
            serverRegistry.unregister(this);
        }

        if (componentManager != null) {
            try {
                componentManager.unregister(managedEndpoint);
            } catch (JMException e) {
                LOG.warn("Unregistering ManagedEndpoint failed.", e);
            }
            componentManager = null;
        }
    }

    @Override
    public boolean isStarted() {
        return !stopped;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public Transporter getTransporter() {
        return transporter;
    }

}
