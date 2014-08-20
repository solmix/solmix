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

package org.solmix.ds.context.support;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.solmix.ds.context.Context;
import org.solmix.runtime.Container;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-9-28
 */

public abstract class AbstractContext implements Context
{

    protected AttributeProvider attributeProvider;

    private Container systemContext;

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
    public Container getContainer() {
        return systemContext;
    }

    /**
     * @param systemContext the systemContext to set
     */
    public void setContainer(final Container systemContext) {
        this.systemContext = systemContext;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.api.context.Context#getAttribute(java.lang.String)
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
     * @see org.solmix.ds.context.api.context.Context#getAttributes()
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
     * @see org.solmix.ds.context.api.context.Context#close()
     */
    @Override
    public void close() {
           attributeProvider=null;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.api.context.Context#setAttribute(java.lang.String, java.lang.Object,
     *      org.solmix.ds.context.api.context.Context.Scope)
     */
    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        this.getAttributeProvider().setAttribute(name, value, scope);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.api.context.Context#getAttribute(java.lang.String, org.solmix.ds.context.api.context.Context.Scope)
     */
    @Override
    public Object getAttribute(String name, Scope scope) {
        return this.getAttributeProvider().getAttribute(name, scope);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.api.context.Context#getAttributes(org.solmix.ds.context.api.context.Context.Scope)
     */
    @Override
    public Map<String, Object> getAttributes(Scope scope) {
        return this.getAttributeProvider().getAttributes(scope);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.api.context.Context#removeAttribute(java.lang.String, org.solmix.ds.context.api.context.Context.Scope)
     */
    @Override
    public void removeAttribute(String name, Scope scope) {
        this.getAttributeProvider().removeAttribute(name, scope);

    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.Context#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object value) {
       setAttribute(name, value, Scope.LOCAL);
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.Context#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return getAttributeProvider().getAttributeNames();
    }
    @Override
    public void removeAttribute(String name){
        removeAttribute(name, Scope.LOCAL);
    }
}
