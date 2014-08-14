/*
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

import static org.solmix.commons.util.DataUtils.isEmpty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.solmix.runtime.Extension;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月5日
 */

public class DefaultExtensionLoader<T> implements ExtensionLoader<T>
{

    private final  ExtensionManagerImpl extensionManager;
    private final Class<T> type;
    private String defaultName;
    private Map<String,ExtensionInfo> cached;
    private final Object cachedLock=new Object();
    public DefaultExtensionLoader(Class<T> type,ExtensionManagerImpl extensionManager){
        this.extensionManager=extensionManager;
        this.type=type;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getDefault()
     */
    @Override
    public T getDefault() {
        getExtensionInfos();
        if(isEmpty(defaultName))
            return null;
        return getExtension(defaultName);
    }

    private Map<String, ExtensionInfo> getExtensionInfos() {
        if (cached == null) {
            synchronized (cachedLock) {
                loadExtensions();
            }
        }
        return cached;
    }
    /**
     * 
     */
    private void loadExtensions() {
        Extension e = type.getAnnotation(Extension.class);
        if (e != null) {
            String defName = e.name();
            if (defName != null)
                defaultName = defName.trim();
        }
        if (cached == null) {
            Map<String, ExtensionInfo> cachedExtensions = new HashMap<String, ExtensionInfo>();
            Set<ExtensionInfo> infos = extensionManager.getExtensionInfos(type);
            for (ExtensionInfo info : infos) {
                Class<?> clazz = info.getClassObject(null);
                if (clazz != null) {
                    Extension anno = clazz.getAnnotation(Extension.class);
                    if (anno != null && anno.name() != null) {
                        String implemntor = anno.name().trim();
                        if (cachedExtensions.get(implemntor) != null) {
                            ExtensionInfo older = cachedExtensions.get(implemntor);
                            throw new IllegalStateException("Class:["
                                + clazz.getName() + "] with name:["
                                + implemntor + "],conflict and class:["
                                + older.getClassname() + "]");
                        } else {
                            cachedExtensions.put(anno.name().trim(), info);
                        }
                    }
                }
            }
            cached=cachedExtensions;
        }

    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getDefaultName()
     */
    @Override
    public String getDefaultName() {
       getExtensionInfos();
        return defaultName;
    }

  
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getExtension(java.lang.String)
     */
    @Override
    public T getExtension(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Extension name is null");
        ExtensionInfo info = getExtensionInfos().get(name);
        if (info != null) {
            if (info.getLoadedObject() == null) {
                extensionManager.createExtension(info);
            }
            return type.cast(info.getLoadedObject());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getExtensionName(java.lang.Class)
     */
    @Override
    public String getExtensionName(Class<?> clazz) {
        Extension e=clazz.getAnnotation(Extension.class);
        if(e!=null){
            return e.name().trim();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getExtensionName(java.lang.Object)
     */
    @Override
    public String getExtensionName(T t) {
        return getExtensionName(t.getClass());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#hasExtension(java.lang.String)
     */
    @Override
    public boolean hasExtension(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Extension name is null");
        try {
            return getExtensionInfos().get(name) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#getLoadedExtensions()
     */
    @Override
    public Set<String> getLoadedExtensions() {
        return Collections.unmodifiableSet(new TreeSet<String>(cached.keySet()));
    }
    
    public void addExtension(String name,ExtensionInfo info){
        synchronized (cachedLock) {
            cached.put(name, info);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.extension.ExtensionLoader#addExtension(java.lang.String, java.lang.Class)
     */
    @Override
    public void addExtension(String name, Class<T> clazz) {
        getExtensionInfos();
        if(type.isAssignableFrom(clazz)){
            throw new IllegalStateException("Input type " +
                clazz + "not implement Extension " + type);
        }
        if(clazz.isInterface()) {
            throw new IllegalStateException("Input type " +
                    clazz + "can not be interface!");
        }
        if(cached.containsKey(name)){
            throw new IllegalStateException("Extension name " +
                name + " already existed(Extension " + type + ")!");
        }
        synchronized (cachedLock) {
            ExtensionInfo info = new ExtensionInfo(Thread.currentThread().getContextClassLoader());
            info.setClassname(clazz.getName());
            info.setInterfaceName(type.getName());
            info.setDeferred(true);
            cached.put(name, info);
        }

    }

}
