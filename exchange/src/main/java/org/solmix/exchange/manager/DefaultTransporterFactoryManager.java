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

package org.solmix.exchange.manager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Resource;

import org.solmix.exchange.TransporterFactory;
import org.solmix.exchange.TransporterFactoryManager;
import org.solmix.exchange.support.TypeDetector;
import org.solmix.runtime.Container;
import org.solmix.runtime.extension.ExtensionException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月11日
 */

public class DefaultTransporterFactoryManager implements
    TransporterFactoryManager {

    Map<String, TransporterFactory> transporterFactories;

    private  Container container;

    private final Set<String> failed = new CopyOnWriteArraySet<String>();

    private final Set<String> loaded = new CopyOnWriteArraySet<String>();

    public DefaultTransporterFactoryManager() {
        transporterFactories = new ConcurrentHashMap<String, TransporterFactory>(4, 0.75f, 2);
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
    public TransporterFactory getFactory(String type) {
        
        TransporterFactory factory = transporterFactories.get(type);
        if (factory == null && !failed.contains(type)) {
            TypeDetector<TransporterFactory> detector = new TypeDetector<TransporterFactory>(
                getContainer(), transporterFactories, loaded, TransporterFactory.class);
            factory = detector.detectInstanceForType(type);
        }
        if (factory == null) {
            failed.add(type);
            throw new ExtensionException(
                "No found  TransporterFactory extension with type: " + type);
        }
        return factory;
    }
    
    public Container getContainer() {
        return container;
    }

    @Resource
    public void setContainer(Container container) {
        this.container = container;
        if (container != null) {
            container.setExtension(this, TransporterFactoryManager.class);
        }
    }
   
    @Override
    public TransporterFactory getFactoryForUri(String uri) {
        return new TypeDetector<TransporterFactory>(getContainer(),
            transporterFactories, loaded, TransporterFactory.class).detectInstanceForURI(uri);
    }
}
