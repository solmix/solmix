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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.ProtocolFactoryManager;
import org.solmix.runtime.extension.ExtensionLoader;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月8日
 */

public class DefaultProtocolFactoryManager implements ProtocolFactoryManager {

    Map<String, ProtocolFactory> protocolFactories;

    private final Container container;

    private final Set<String> failed = new CopyOnWriteArraySet<String>();

    public DefaultProtocolFactoryManager(Container container) {
        this.container = container;
        protocolFactories = new ConcurrentHashMap<String, ProtocolFactory>(8,
            0.75f, 4);
    }

    @Override
    public void register(String name, ProtocolFactory factory) {
        protocolFactories.put(name, factory);

    }

    @Override
    public void unregister(String name) {
        protocolFactories.remove(name);

    }

    @Override
    public ProtocolFactory getProtocolFactory(String name) {
        ProtocolFactory pf = protocolFactories.get(name);
        if (pf == null) {
            if (!failed.contains(name)) {
                pf = loadProtocolFactory(name);
            }
            if (pf == null) {
                failed.add(name);
                throw new IllegalArgumentException(
                    "No found protocol factory named :" + name);
            }
        }
        return pf;
    }
    
    private ProtocolFactory loadProtocolFactory(String name) {
        ExtensionLoader<ProtocolFactory> loader = container.getExtensionLoader(ProtocolFactory.class);
        ProtocolFactory factory = loader.getExtension(name);
        if (factory == null) {
            failed.add(name);
        } else {
            protocolFactories.put(name, factory);
        }
        return protocolFactories.get(name);
    }

}
