/**
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.extension;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.runtime.Extension;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年7月27日
 */

public class ExtensionObjectCache {

    private final Map<Class<?>, Object> cache;

    public ExtensionObjectCache() {
        cache = new ConcurrentHashMap<Class<?>, Object>(16, 0.75f, 4);
    }

    public ExtensionObjectCache(int initialCapacity, float loadFactor, int concurrencyLevel) {
        cache = new ConcurrentHashMap<Class<?>, Object>(initialCapacity,
            loadFactor, concurrencyLevel);
    }

    /**
     * @param beans
     */
    public ExtensionObjectCache(Map<Class<?>, Object> extensions) {
        this.cache = new ConcurrentHashMap<Class<?>, Object>(extensions);
    }

    public void putObject(Class<?> clazz, Object o) {
        if (o == null) {
            return;
        }
        if (isExtension(clazz) && isExtension(o.getClass())) {
            String name = o.getClass().getAnnotation(Extension.class).value();
            ExtensionEntry entry = (ExtensionEntry) cache.get(clazz);
            if (entry == null) {
                cache.put(clazz, new ExtensionEntry(name, o));
            } else {
                entry.put(name, o);
            }
        } else {
            cache.put(clazz, o);
        }
    }

    public Object getObject(Class<?> clazz) {
        Object o = cache.get(clazz);
        if (o instanceof ExtensionEntry) {
            String name = clazz.getAnnotation(Extension.class).value();
            return ((ExtensionEntry) o).get(name);
        } else {
            return o;
        }
    }

    public Object getObjects(Class<?> clazz) {
        Object o = cache.get(clazz);
        return o;
    }

    private boolean isExtension(Class<?> clazz) {
        return clazz.isAnnotationPresent(Extension.class);
    }

    static class ExtensionEntry {

        Map<String, Object> cache = new ConcurrentHashMap<String, Object>(4, 0.75f, 2);

        ExtensionEntry(String name, Object o) {
            cache.put(name, o);
        }

        /**
         * @param name
         * @return
         */
        public Object get(String name) {
            return cache.get(name);
        }

        /**
         * @param name
         * @param o
         */
        public void put(String name, Object o) {
            cache.put(name, o);

        }
    }

    /**
     * @return
     */
    public Set<Class<?>> keySet() {
        return cache.keySet();
    }
}
