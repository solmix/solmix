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

package com.solmix.api.adapter;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-12
 */

public interface AdapterManager
{

    /**
     * The name under which this service is registered with the OSGi service registry.
     */
    String SERVICE_NAME = "com.solmix.api.adapter.AdapterManager";

    /**
     * Returns an adapter object of the requested <code>AdapterType</code> for the given <code>adaptable</code> object.
     * <p>
     * The <code>adaptable</code> object may be any non-<code>null</code> object and is not required to implement the
     * <code>Adaptable</code> interface.
     * 
     * @param <AdapterType> The generic type of the adapter (target) type.
     * @param adaptable The object to adapt to the adapter type.
     * @param type The type to which the object is to be adapted.
     * @return The adapted object or <code>null</code> if no factory exists to adapt the <code>adaptable</code> to the
     *         <code>AdapterType</code> or if the <code>adaptable</code> cannot be adapted for any other reason.
     */
    <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type);

}
