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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.exchange.Server;
import org.solmix.runtime.exchange.ServerRegistry;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月10日
 */
public class DefaultServerRegistry implements ServerRegistry, ContainerListener {

    private Container container;

    private final CopyOnWriteArrayList<Server> servers;

    public DefaultServerRegistry() {
        servers = new CopyOnWriteArrayList<Server>();
    }

    public DefaultServerRegistry(Container container) {
        servers = new CopyOnWriteArrayList<Server>();
        setContainer(container);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container c) {
        this.container = c;
        if (container != null) {
            container.setExtension(this, ServerRegistry.class);
            container.addListener(this);
        }
    }

    @Override
    public void register(Server server) {
        servers.addIfAbsent(server);
    }

    @Override
    public void unregister(Server server) {
        servers.remove(server);
    }

    @Override
    public List<Server> getServers() {
        return servers;
    }

    @Override
    public void handleEvent(ContainerEvent event) {
        if (event == null) {
            return;
        }
        switch (event.getType()) {
        case ContainerEvent.CREATED:

            break;
        case ContainerEvent.POSTCLOSE: 
            Server[] serverArray = servers.toArray(new Server[] {});
            for (Server server : serverArray) {
                server.destroy();
            }
            break;
        case ContainerEvent.PRECLOSE:
            servers.clear();
            break;
        default:
            break;
        }
    }

}
