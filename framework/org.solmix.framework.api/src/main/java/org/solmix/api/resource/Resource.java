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

package org.solmix.api.resource;

import java.util.Iterator;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-4-12
 */

public interface Resource
{

    /**
     * Returns the absolute path of this resource in the resource tree.
     */
    String getPath();

    /**
     * Returns the name of this resource. The name of a resource is the last segment of the {@link #getPath() path}.
     * 
     */
    String getName();

    /**
     * Get parent resource.
     * @return
     */
    Resource getParent();

    /**
     * List children resource.
     * @return
     */
    Iterator<Resource> listChildren();

    /**
     * Get child resource by the path.
     * @param relPath
     * @return
     */
    Resource getChild(String relPath);

    ResourceResolver getResourceResolver();
}
