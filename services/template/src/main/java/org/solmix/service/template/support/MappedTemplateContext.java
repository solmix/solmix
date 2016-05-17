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

package org.solmix.service.template.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.solmix.commons.util.Assert;
import org.solmix.service.template.TemplateContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月28日
 */

public class MappedTemplateContext implements TemplateContext
{

    private final Map<String, Object> map;

    public MappedTemplateContext()
    {
        this.map = new HashMap<String, Object>();
    }

    public MappedTemplateContext(Map<String, Object> map)
    {
        this.map = Assert.assertNotNull(map, "map");
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public void remove(String key) {
        map.remove(key);
    }
}
