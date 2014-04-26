/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.fmk.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.solmix.api.context.AttributeProvider;
import org.solmix.api.context.Context;
import org.solmix.api.context.SystemContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-9-28
 */

public abstract class AbstractContext implements Context
{

    private AttributeProvider attributeProvider;

    private SystemContext systemContext;

    protected Locale locale;


    /**
     * @return the attributeProvider
     */
    public AttributeProvider getAttributeProvider() {
        return attributeProvider;
    }

    /**
     * @param attributeProvider the attributeProvider to set
     */
    public void setAttributeProvider(AttributeProvider attributeProvider) {
        this.attributeProvider = attributeProvider;
    }

    /**
     * @return the systemContext
     */
    public SystemContext getSystemContext() {
        return systemContext;
    }

    /**
     * @param systemContext the systemContext to set
     */
    public void setSystemContext(final SystemContext systemContext) {
        this.systemContext = systemContext;
    }

    @Override
    public int size() {
        return this.getAttributes().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getAttributes().isEmpty();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(Object key) {
        return this.getAttributes().containsKey(key);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return this.getAttributes().containsValue(value);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public Object get(Object key) {
        return this.getAttribute(key.toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#put(java.lang.String, java.lang.Object)
     */
    @Override
    public Object put(String key, Object value) {
        this.setAttribute(key, value, Context.Scope.LOCAL);
        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public Object remove(Object key) {
        Object obj = this.getAttribute(key.toString());
        this.removeAttribute(key.toString(), Context.Scope.LOCAL);
        return obj;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#putAll(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void putAll(Map map) {
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Entry entry = (Entry) iter.next();
            this.setAttribute(entry.getKey().toString(), entry.getValue(), Scope.LOCAL);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        for (String key : this.getAttributes().keySet()) {
            this.removeAttribute(key, Context.Scope.LOCAL);
        }

    }

    @Override
    public Set<String> keySet() {
        return this.getAttributes().keySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#values()
     */
    @Override
    public Collection<Object> values() {
        return this.getAttributes().values();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return this.getAttributes().entrySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#getLocale()
     */
    @Override
    public Locale getLocale() {
        if (locale == null) {
            if (this == this.systemContext) {
                // do not fall in the endless loop
                return null;
            }
            locale = systemContext.getLocale();
        }
        return locale;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name) {
        Object value = this.getAttribute(name, Scope.LOCAL);
        if (null == value) {
            value = this.getAttribute(name, Scope.SESSION);
        }
        if (null == value) {
            value = this.getAttribute(name, Scope.SYSTEM);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(this.getAttributes(Scope.LOCAL));
        map.putAll(this.getAttributes(Scope.SESSION));
        map.putAll(this.getAttributes(Scope.SYSTEM));
        return map;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#release()
     */
    @Override
    public void release() {
           attributeProvider=null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#setAttribute(java.lang.String, java.lang.Object,
     *      org.solmix.api.context.Context.Scope)
     */
    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        this.getAttributeProvider().setAttribute(name, value, scope);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#getAttribute(java.lang.String, org.solmix.api.context.Context.Scope)
     */
    @Override
    public Object getAttribute(String name, Scope scope) {
        return this.getAttributeProvider().getAttribute(name, scope);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#getAttributes(org.solmix.api.context.Context.Scope)
     */
    @Override
    public Map<String, Object> getAttributes(Scope scope) {
        return this.getAttributeProvider().getAttributes(scope);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.context.Context#removeAttribute(java.lang.String, org.solmix.api.context.Context.Scope)
     */
    @Override
    public void removeAttribute(String name, Scope scope) {
        this.getAttributeProvider().removeAttribute(name, scope);

    }
}
