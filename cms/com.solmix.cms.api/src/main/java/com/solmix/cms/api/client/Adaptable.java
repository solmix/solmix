/*
 * SOLMIX PROJECT
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

package com.solmix.cms.api.client;

/**
 * The <code>Adaptable</code> interface identifies objects which can be adapted to other types or representations of the
 * same object.
 * 
 * @author solmix
 */

public interface Adaptable
{

    /**
     * Adapts the adaptable to another type.
     * <p>
     * Please not that it is explicitly left as an implementation detail whether each call to this method with the same
     * <code>type</code> yields the same object or a new object on each call.
     * <p>
     * Implementations of this method should document their adapted types as well as their behaviour with respect to
     * returning newly created or not instance on each call.
     * 
     * @param <AdapterType> The generic type to which this resource is adapted to
     * @param type The Class object of the target type, such as <code>javax.jcr.Node.class</code> or
     *        <code>java.io.File.class</code>
     * @return The adapter target or <code>null</code> if the resource cannot adapt to the requested type
     */
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type);
}
