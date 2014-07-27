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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class ExtensionRegistry
{
    private static Set<ExtensionInfo> extensions   = new ConcurrentSkipListSet<ExtensionInfo>(new ExtensionComparator());
   
    public static Set<ExtensionInfo> getRegisteredExtensions() {
        Set<ExtensionInfo> exts = new HashSet<ExtensionInfo>(extensions.size());
        Iterator<ExtensionInfo> it= extensions.iterator();
        while(it.hasNext()){
            ExtensionInfo info= it.next();
            exts.add(info.cloneNoObject());
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
            if(!extensions.contains(e))
                extensions.add(e);
        }
    }

}
