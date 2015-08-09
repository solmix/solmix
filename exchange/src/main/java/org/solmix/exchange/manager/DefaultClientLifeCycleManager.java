/**
 * Copyright (container) 2014 The Solmix Project
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

package org.solmix.exchange.manager;

import java.util.Collection;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.exchange.Client;
import org.solmix.exchange.ClientLifeCycleListener;
import org.solmix.exchange.ClientLifeCycleManager;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月6日
 */

public class DefaultClientLifeCycleManager implements ClientLifeCycleManager {

    private final CopyOnWriteArrayList<ClientLifeCycleListener> listeners = new CopyOnWriteArrayList<ClientLifeCycleListener>();

    public DefaultClientLifeCycleManager() {

    }

    public DefaultClientLifeCycleManager(Container c) {
        Collection<? extends ClientLifeCycleListener> l = c.getExtension(
            ConfiguredBeanProvider.class).getBeansOfType(
            ClientLifeCycleListener.class);
        if (l != null) {
            listeners.addAll(l);
        }
    }

    public Class<?> getRegistrationType() {
        return ClientLifeCycleManager.class;
    }

    @Override
    public void registerListener(ClientLifeCycleListener listener) {
        listeners.addIfAbsent(listener);
    }

    @Override
    public void clientCreated(Client client) {
        for (ClientLifeCycleListener listener : listeners) {
            listener.clientCreated(client);
        }
    }

    @Override
    public void clientDestroyed(Client client) {
        ListIterator<ClientLifeCycleListener> li = listeners.listIterator(listeners.size());
        while (li.hasPrevious()) {
            li.previous().clientDestroyed(client);
        }
    }

    @Override
    public void unRegisterListener(ClientLifeCycleListener listener) {
        listeners.remove(listener);
    }

}
