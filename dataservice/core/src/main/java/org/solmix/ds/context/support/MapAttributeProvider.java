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
import java.util.Hashtable;
import java.util.Map;

import org.solmix.ds.context.Context.Scope;


/**
 * 
 * @version 110035 2012-9-28
 * @since 0.1
 */

public class MapAttributeProvider implements AttributeProvider
{

    private final Hashtable<String, Object> map = new Hashtable<String, Object>();

    @Override
    public void setAttribute(String name, Object value, Scope scope) {
        map.put(name, value);

    }

    @Override
    public Object getAttribute(String name, Scope scope) {
        return map.get(name);
    }

    @Override
    public Map<String, Object> getAttributes(Scope scope) {
        return map;
    }

    @Override
    public void removeAttribute(String name, Scope scope) {
        map.remove(name);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.ds.context.support.AttributeProvider#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames() {
        return map.keys();
    }


}
