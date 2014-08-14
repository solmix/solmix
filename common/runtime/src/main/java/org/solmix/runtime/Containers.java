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

package org.solmix.runtime;

import org.solmix.runtime.extension.ExtensionLoader;

/**
 * Factory and utility methods for {@link Container}
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月9日
 */

public class Containers
{

    /**
     * Get the Container related current thread.
     * 
     * @return
     */
    public static Container get() {
        return ContainerFactory.getThreadDefaultContainer();
    }

    /**
     * @param createIfNeeded
     * @return
     */
    public static Container get(boolean createIfNeeded) {
        return ContainerFactory.getThreadDefaultContainer(createIfNeeded);
    }

    /**
     * @return
     */
    public static Container getDefault() {
        return ContainerFactory.getDefaultContainer();
    }

    /**
     * @param createIfNeeded
     * @return
     */
    public static Container getDefault(boolean createIfNeeded) {
        return ContainerFactory.getDefaultContainer(createIfNeeded);
    }
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        return get().getExtensionLoader(type);
    }
}
