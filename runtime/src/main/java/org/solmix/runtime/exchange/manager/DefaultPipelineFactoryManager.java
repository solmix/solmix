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

import javax.annotation.Resource;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.PipelineFactory;
import org.solmix.runtime.exchange.PipelineFactoryManager;
import org.solmix.runtime.exchange.support.TransportDetector;
import org.solmix.runtime.extension.ExtensionException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月14日
 */

public class DefaultPipelineFactoryManager implements PipelineFactoryManager {

    private Container container;

    private final Map<String, PipelineFactory> pipelineFactorys;

    private final Set<String> failed = new CopyOnWriteArraySet<String>();

    private final Set<String> loaded = new CopyOnWriteArraySet<String>();

    public DefaultPipelineFactoryManager() {
        pipelineFactorys = new ConcurrentHashMap<String, PipelineFactory>(8,
            0.75f, 4);
    }

    public DefaultPipelineFactoryManager(Container container) {
        pipelineFactorys = new ConcurrentHashMap<String, PipelineFactory>(8,
            0.75f, 4);
        setContainer(container);
    }

    @Override
    public void registerFactory(String type, PipelineFactory factory) {
        pipelineFactorys.put(type, factory);
    }

    @Override
    public void unregisterFactory(String type) {
        pipelineFactorys.remove(type);
    }

   
    @Override
    public PipelineFactory getFactory(String type) {
        PipelineFactory factory = pipelineFactorys.get(type);
        if (factory == null && !failed.contains(type)) {
            TransportDetector<PipelineFactory> detector = new TransportDetector<PipelineFactory>(
                getContainer(), pipelineFactorys, loaded, PipelineFactory.class);
            factory = detector.detectInstanceForType(type);
        }
        if (factory == null) {
            failed.add(type);
            throw new ExtensionException(
                "No found  PipelineFactory extension with type: " + type);
        }
        return factory;
    }

    /**   */
    public Container getContainer() {
        return container;
    }

    @Resource
    public void setContainer(Container container) {
        this.container = container;
        if (container != null) {
            container.setExtension(this, PipelineFactoryManager.class);
        }
    }

    @Override
    public PipelineFactory getFactoryForUri(String uri) {
        return new TransportDetector<PipelineFactory>(getContainer(),
            pipelineFactorys, loaded, PipelineFactory.class).detectInstanceForURI(uri);
    }

}
