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

import org.solmix.exchange.ProtocolFactory;
import org.solmix.exchange.ProtocolFactoryManager;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.bean.ConfiguredBeanProvider.BeanLoaderListener;
import org.solmix.runtime.extension.DefaultExtensionLoader;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月8日
 */

public class DefaultProtocolFactoryManager implements ProtocolFactoryManager {

    Map<String, ProtocolFactory> protocolFactories;

    private final Container container;

    private final Set<String> failed = new CopyOnWriteArraySet<String>();

    private final Set<String> loaded = new CopyOnWriteArraySet<String>();

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

    private ProtocolFactory loadProtocolFactory(final String type) {
        ConfiguredBeanProvider provider = container.getExtension(ConfiguredBeanProvider.class);
        provider.loadBeansOfType(ProtocolFactory.class,
            new BeanLoaderListener<ProtocolFactory>() {

                @Override
                public boolean loadBean(String name,
                    Class<? extends ProtocolFactory> clzz) {
                    return !protocolFactories.containsKey(type)
                        && !loaded.contains(name)
                        && type.equals(DefaultExtensionLoader.extensionName(clzz));
                }

                @Override
                public boolean beanLoaded(String name, ProtocolFactory bean) {
                    loaded.add(name);
                    if (!protocolFactories.containsKey(type)) {
                        register(type, bean);
                    }
                    return !protocolFactories.containsKey(type);
                }

            });
        return protocolFactories.get(type);
    }

}
