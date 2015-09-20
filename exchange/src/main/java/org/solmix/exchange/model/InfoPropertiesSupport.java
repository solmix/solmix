/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.exchange.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月24日
 */

public abstract class InfoPropertiesSupport {

    private InfoPropertiesSupport delegate;
    
    /**扩展*/
    private final AtomicReference<Object[]> extensions = new AtomicReference<Object[]>();

    private final AtomicReference<Map<String, Object>> properties = new AtomicReference<Map<String, Object>>();

    private boolean delegateProperties;

    private Map<String, Object> extensionAttributes;

    public void addExtension(Object el) {
        if (delegate != null) {
            delegate.addExtension(el);
            return;
        }
        Object exts[] = extensions.get();
        Object exts2[];
        if (exts == null) {
            exts2 = new Object[1];
        } else {
            exts2 = new Object[exts.length + 1];
            for (int i = 0; i < exts.length; i++) {
                exts2[i] = exts[i];
            }
        }
        exts2[exts2.length - 1] = el;
        if (!extensions.compareAndSet(exts, exts2)) {
            // keep trying
            addExtension(el);
        }
    }

    public AtomicReference<Object[]> getExtensors() {
        if (delegate != null) {
            return delegate.getExtensors();
        }
        return extensions;
    }

    public <T> T getExtension(Class<T> cls) {
        if (delegate != null) {
            return delegate.getExtension(cls);
        }
        Object exts[] = extensions.get();
        if (exts == null) {
            return null;
        }
        for (int x = 0; x < exts.length; x++) {
            if (cls.isInstance(exts[x])) {
                return cls.cast(exts[x]);
            }
        }
        return null;
    }

    public Object getExtensionAttribute(String name) {
        if (delegate != null) {
            return delegate.getExtensionAttribute(name);
        }
        return null == extensionAttributes ? null
            : extensionAttributes.get(name);
    }

    public Map<String, Object> getExtensionAttributes() {
        if (delegate != null) {
            return delegate.getExtensionAttributes();
        }
        return extensionAttributes;
    }

    /** no thread safe */
    public void addExtensionAttribute(String name, Object attr) {
        if (delegate != null) {
            delegate.addExtensionAttribute(name, attr);
            return;
        }
        if (null == extensionAttributes) {
            extensionAttributes = new HashMap<String, Object>();
        }
        extensionAttributes.put(name, attr);
    }

    public void setExtensionAttributes(Map<String, Object> attrs) {
        if (delegate != null) {
            delegate.setExtensionAttributes(attrs);
            return;
        }
        extensionAttributes = attrs;
    }
    
    
    
    public void setProperty(String name, Object v) {
        if (delegate != null && delegateProperties) {
            delegate.setProperty(name, v);
            return;
        }
        if (null == properties.get()) {
            properties.compareAndSet(null,
                new ConcurrentHashMap<String, Object>(4, 0.75f, 2));
        }
        if (v == null) {
            properties.get().remove(name);
        } else {
            properties.get().put(name, v);
        }
    }

    public Object getProperty(String name) {
        if (delegate != null && delegateProperties) {
            return delegate.getProperty(name);
        }
        if (null == properties.get()) {
            return null;
        }
        return properties.get().get(name);
    }

    public Object removeProperty(String name) {
        if (delegate != null && delegateProperties) {
            delegate.removeProperty(name);
        }
        if (null == properties.get()) {
            return null;
        }
        return properties.get().remove(name);
    }

    public Map<String, Object> getProperties() {
        if (delegate != null && delegateProperties) {
            return delegate.getProperties();
        }
        return properties.get();
    }

    public void setProperties(Map<String, Object> p) {
        if (delegate != null && delegateProperties) {
            delegate.setProperties(p);
        }
        properties.set(p);
    }

    /**
     * 代理InfoPropertiesSupport
     * 
     * @param p
     * @param delegateProperties
     */
    public final void setDelegate(InfoPropertiesSupport delegate,
        boolean delegateProperties) {
        this.delegate = delegate;
        this.delegateProperties = delegateProperties;
        if (delegate == null) {
            return;
        }

        if (extensionAttributes != null) {
            delegate.setExtensionAttributes(extensionAttributes);
            extensionAttributes = null;
        }
        if (extensions.get() != null) {
            for (Object el : extensions.get()) {
                delegate.addExtension(el);
            }
            extensions.set(null);
        }
        if (delegateProperties && properties.get() != null) {
            for (Map.Entry<String, Object> p2 : properties.get().entrySet()) {
                delegate.setProperty(p2.getKey(), p2.getValue());
            }
            properties.set(null);
        }
    }
    protected static final boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 != null
            || o1 != null && o2 == null) {
            return false;
        }
        return o1 == null ? true : o1.equals(o2);
    }
    

    public boolean hasProperty(String key) {
        Object value = getProperty(key);
        return value != null ;
    }
  
}
