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
import org.solmix.runtime.exchange.TransporterFactory;
import org.solmix.runtime.exchange.TransporterFactoryManager;
import org.solmix.runtime.extension.ExtensionLoader;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月11日
 */

public class DefaultTransporterFactoryManager implements
    TransporterFactoryManager {

    Map<String, TransporterFactory> transporterFactories;

    private final Container container;

    private final Set<String> failed = new CopyOnWriteArraySet<String>();

    public DefaultTransporterFactoryManager(Container container) {
        this.container = container;
        transporterFactories = new ConcurrentHashMap<String, TransporterFactory>(
            4, 0.75f, 2);
    }

    @Override
    public void register(String name, TransporterFactory factory) {
        transporterFactories.put(name, factory);

    }

    @Override
    public void unregister(String name) {
        transporterFactories.remove(name);

    }

    @Override
    public TransporterFactory getFactory(String name) {
        TransporterFactory pf = transporterFactories.get(name);
        if (pf == null) {
            if (!failed.contains(name)) {
                pf = loadTransporterFactory(name);
            }
            if (pf == null) {
                failed.add(name);
                throw new IllegalArgumentException(
                    "No found protocol factory named :" + name);
            }
        }
        return pf;
    }

    private TransporterFactory loadTransporterFactory(String name) {
        ExtensionLoader<TransporterFactory> loader = container.getExtensionLoader(TransporterFactory.class);
        TransporterFactory factory = loader.getExtension(name);
        if (factory == null) {
            failed.add(name);
        } else {
            transporterFactories.put(name, factory);
        }
        return transporterFactories.get(name);
    }
}
