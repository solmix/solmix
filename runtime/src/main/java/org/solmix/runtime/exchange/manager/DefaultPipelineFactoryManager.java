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
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.PipelineFactory;
import org.solmix.runtime.exchange.PipelineFactoryManager;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月14日
 */

public class DefaultPipelineFactoryManager implements PipelineFactoryManager {

    private Container container;

    private final Map<String, PipelineFactory> pipelineFactorys;

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
    public void registerFactory(String name, PipelineFactory factory) {
        pipelineFactorys.put(name, factory);
    }

    @Override
    public void unregisterFactory(String name) {
        pipelineFactorys.remove(name);
    }

   
    @Override
    public PipelineFactory getFactory(String name) {
        return pipelineFactorys.get(name);
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

}
