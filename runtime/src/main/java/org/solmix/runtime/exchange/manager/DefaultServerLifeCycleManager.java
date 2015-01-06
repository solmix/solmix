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

package org.solmix.runtime.exchange.manager;

import java.util.Collection;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.exchange.Server;
import org.solmix.runtime.exchange.ServerLifeCycleListener;
import org.solmix.runtime.exchange.ServerLifeCycleManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月6日
 */

public class DefaultServerLifeCycleManager implements ServerLifeCycleManager {

    private final CopyOnWriteArrayList<ServerLifeCycleListener> listeners = new CopyOnWriteArrayList<ServerLifeCycleListener>();

    public DefaultServerLifeCycleManager() {

    }

    public DefaultServerLifeCycleManager(Container c) {
        Collection<? extends ServerLifeCycleListener> l = c.getExtension(
            ConfiguredBeanProvider.class).getBeansOfType(
            ServerLifeCycleListener.class);
        if (l != null) {
            listeners.addAll(l);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.ServerLifeCycleManager#startServer(org.solmix.runtime.exchange.Server)
     */
    @Override
    public void startServer(Server server) {
        for (ServerLifeCycleListener listener : listeners) {
            listener.startServer(server);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.ServerLifeCycleManager#stopServer(org.solmix.runtime.exchange.Server)
     */
    @Override
    public void stopServer(Server server) {
        ListIterator<ServerLifeCycleListener> li = listeners.listIterator(listeners.size());
        while (li.hasPrevious()) {
            li.previous().stopServer(server);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.ServerLifeCycleManager#registerListener(org.solmix.runtime.exchange.ServerLifeCycleListener)
     */
    @Override
    public void registerListener(ServerLifeCycleListener listener) {
        listeners.addIfAbsent(listener);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.ServerLifeCycleManager#unRegisterListener(org.solmix.runtime.exchange.ServerLifeCycleListener)
     */
    @Override
    public void unRegisterListener(ServerLifeCycleListener listener) {
        listeners.remove(listener);

    }

}
