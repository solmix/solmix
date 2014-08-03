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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class ExtensionRegistry
{
    private static  ConcurrentMap<String, ExtensionInfo> extensions 
    = new ConcurrentHashMap<String, ExtensionInfo>(16, 0.75f, 4);
   
    public static Map<String,ExtensionInfo> getRegisteredExtensions() {
        Map<String, ExtensionInfo> exts = new HashMap<String, ExtensionInfo>(extensions.size());
        for (Map.Entry<String, ExtensionInfo> ext : extensions.entrySet()) {
            exts.put(ext.getKey(), ext.getValue().cloneNoObject());
        }
        return exts;
    }
    public static void removeExtensions(List<? extends ExtensionInfo> list) {
        for (ExtensionInfo e : list) {
            extensions.remove(e);
        }
    }

    public static void addExtensions(List<? extends ExtensionInfo> list) {
        for (ExtensionInfo e : list) {
            extensions.putIfAbsent(e.getName(), e);
        }
    }

}
