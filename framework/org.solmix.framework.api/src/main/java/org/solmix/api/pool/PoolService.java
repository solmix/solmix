/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.api.pool;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.exception.SlxException;

/**
 * 
 * @version 110035
 */
public interface PoolService
{

    /**
     * Indicate this pool-service enable pool or not.
     * 
     * @return
     */
    public boolean isEnablePool();

    /**
     * IF this poolService enable pool.return {@link org.apache.commons.pool.impl.GenericObjectPool} or
     * {@link org.apache.commons.pool.impl.GenericKeyedObjectPool} Or not return null;
     * 
     * @return
     */
    public Object getPool();

    public Object borrowNewObject(Object key) throws Exception;

    public Object borrowUnpooledObject(Object key) throws SlxException;

    public Object borrowObject(Object key, DSRequest request) throws SlxException;

    public Object borrowObject(Object key) throws SlxException;

    public void returnObject(Object key, Object obj);

    void destroy();
}
