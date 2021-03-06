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
import java.util.Map;

import org.solmix.ds.context.Context;


/**
 * 
 * Wrapped context with a decorator.
 * 
 * @version 110035 2012-9-28
 */

public class ContextDecorator extends AbstractContext
{

    protected Context ctx;

    /**
     * @param ctx the context to decorate
     */
    public ContextDecorator(Context ctx)
    {
        this.ctx = ctx;
    }
    /**
     * Delegates call to the original context.
     */
    @Override
    public Object getAttribute(String name, Scope scope) {
        return this.ctx.getAttribute(name, scope);
    }

    /**
     * Delegates call to the original context.
     */
    @Override
    public Map<String, Object> getAttributes(Scope scope) {
        return this.ctx.getAttributes(scope);
    }

    /**
     * Delegates call to the original context.
     */
    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        this.ctx.setAttribute(name, value, scope);
    }

    /**
     * Delegates call to the original context.
     */
    @Override
    public void removeAttribute(String name, Scope scope) {
        this.ctx.removeAttribute(name, scope);
    }

    /**
     * Returns the context wrapped by this decorator.
     * 
     * @return wrapped context
     */
    public Context getWrappedContext() {
        return this.ctx;
    }

    @Override
    public void close() {
        this.ctx.close();
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.Context#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return ctx.getAttributeNames();
    }
 

}
